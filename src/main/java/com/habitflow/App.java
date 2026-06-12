package com.habitflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.habitflow.controller.HabitController;
import com.habitflow.infra.DatabaseConfig;
import com.habitflow.infra.SchemaInitializer;
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
import java.util.concurrent.Executors;

/**
 * Ponto de entrada da aplicação HabitFlow.
 * Composition Root: toda a árvore de dependências é montada aqui.
 * O servidor HTTP é o com.sun.net.httpserver nativo do JDK — sem dependências externas.
 */
public class App {

    public static void main(String[] args) throws IOException {

        // --- Infraestrutura ---
        DatabaseConfig databaseConfig = new DatabaseConfig();
        new SchemaInitializer(databaseConfig).initialize();

        // --- Repositórios ---
        HabitRepository habitRepository = new HabitRepositoryJdbc(databaseConfig);

        // --- Serviços ---
        HabitService habitService = new HabitService(habitRepository);

        // --- Serialização ---
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // --- Controllers ---
        HabitController habitController = new HabitController(habitService, objectMapper);

        // --- Servidor HTTP ---
        int port = resolvePort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/habits", exchange -> {
            setCorsHeaders(exchange);

            String method = exchange.getRequestMethod().toUpperCase();
            String path   = exchange.getRequestURI().getPath();

            // Responde ao preflight CORS sem tocar na camada de negócio
            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // /habits           → segments = ["", "habits"]       → hasId = false
            // /habits/42        → segments = ["", "habits", "42"] → hasId = true
            String[] segments = path.split("/");
            boolean hasId = segments.length >= 3 && !segments[2].isBlank();

            try {
                if (!hasId) {
                    switch (method) {
                        case "GET"  -> respond(exchange, 200, habitController.getAll());
                        case "POST" -> respond(exchange, 201, habitController.create(readBody(exchange)));
                        default     -> respond(exchange, 405, "{\"error\":\"Method not allowed\"}");
                    }
                } else {
                    long id;
                    try {
                        id = Long.parseLong(segments[2]);
                    } catch (NumberFormatException e) {
                        respond(exchange, 400, "{\"error\":\"Invalid ID format\"}");
                        return;
                    }

                    switch (method) {
                        case "GET"    -> respond(exchange, 200, habitController.getById(id));
                        case "PUT"    -> respond(exchange, 200, habitController.update(id, readBody(exchange)));
                        case "DELETE" -> {
                            habitController.delete(id);
                            exchange.sendResponseHeaders(204, -1);
                        }
                        default -> respond(exchange, 405, "{\"error\":\"Method not allowed\"}");
                    }
                }
            } catch (RuntimeException e) {
                System.err.println("[ERROR] Unhandled exception: " + e.getMessage());
                respond(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        });

        // CachedThreadPool: threads não-daemon, mantém a JVM viva após main() retornar
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("HabitFlow API running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(5);
            System.out.println("HabitFlow API stopped.");
        }));
    }

    // Lê a porta da variável PORT (injetada por Render/Railway) ou usa 8080 como padrão
    private static int resolvePort() {
        String port = System.getenv("PORT");
        if (port != null && !port.isBlank()) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException ignored) {}
        }
        return 8080;
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type",                 "application/json; charset=UTF-8");
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
