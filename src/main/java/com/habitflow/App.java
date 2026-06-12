// Ponto de entrada da aplicação.
// Aqui devem ser implementados: inicialização de um servidor HTTP embutido (ex: com.sun.net.httpserver
// ou Javalin/Undertow), registro das rotas do HabitController, e shutdown gracioso (ShutdownHook).
package com.habitflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.habitflow.controller.HabitController;
import com.habitflow.infra.DatabaseConfig;
import com.habitflow.repository.HabitRepository;
import com.habitflow.repository.HabitRepositoryJdbc;
import com.habitflow.service.HabitService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Classe principal da aplicação HabitFlow.
 * Realiza a composição/wiring manual de todas as dependências (Composition Root).
 * DIP: toda a árvore de dependências é montada aqui, sem uso de framework de injeção.
 */
public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("Chamando: App.main() — inicializando HabitFlow...");

        // --- Infraestrutura ---
        DatabaseConfig databaseConfig = new DatabaseConfig();

        // --- Repositórios ---
        // DIP: o serviço recebe a interface HabitRepository, não a implementação JDBC.
        HabitRepository habitRepository = new HabitRepositoryJdbc(databaseConfig);

        // --- Serviços ---
        HabitService habitService = new HabitService(habitRepository);

        // --- Jackson ObjectMapper com suporte a Java 8 Date/Time ---
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // --- Controllers ---
        HabitController habitController = new HabitController(habitService, objectMapper);

        // --- Servidor HTTP ---
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/habits", (HttpExchange exchange) -> {
            // Headers obrigatórios de CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                Long id = null;

                if (pathParts.length > 2) {
                    try {
                        id = Long.parseLong(pathParts[2]);
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid ID format\"}");
                        return;
                    }
                }

                String response = "";
                int statusCode = 200;

                if ("GET".equalsIgnoreCase(method)) {
                    if (id == null) {
                        response = habitController.getAll();
                    } else {
                        response = habitController.getById(id);
                        if (response.contains("Habit not found")) {
                            statusCode = 404;
                        }
                    }
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readBody(exchange.getRequestBody());
                    response = habitController.create(body);
                    if (response.contains("error")) {
                        statusCode = 400;
                    } else {
                        statusCode = 201;
                    }
                } else if ("PUT".equalsIgnoreCase(method)) {
                    if (id == null) {
                        statusCode = 400;
                        response = "{\"error\": \"ID is required for update\"}";
                    } else {
                        String body = readBody(exchange.getRequestBody());
                        response = habitController.update(id, body);
                        if (response.contains("error")) {
                            statusCode = 400;
                        }
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    if (id == null) {
                        statusCode = 400;
                        response = "{\"error\": \"ID is required for deletion\"}";
                    } else {
                        response = habitController.delete(id);
                        statusCode = 204;
                    }
                } else {
                    statusCode = 405;
                    response = "{\"error\": \"Method not allowed\"}";
                }

                sendResponse(exchange, statusCode, response);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\": \"Internal server error\"}");
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("\n--- HabitFlow inicializado com sucesso na porta 8080 ---");
    }

    private static String readBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (statusCode == 204 || response.isEmpty()) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
