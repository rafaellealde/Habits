package com.habitflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.habitflow.controller.HabitController;
import com.habitflow.infra.DatabaseConfig;
import com.habitflow.repository.HabitRepository;
import com.habitflow.repository.HabitRepositoryJdbc;
import com.habitflow.service.HabitService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Inicializando HabitFlow...");

        DatabaseConfig databaseConfig = new DatabaseConfig();
        HabitRepository habitRepository = new HabitRepositoryJdbc(databaseConfig);
        HabitService habitService = new HabitService(habitRepository);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        HabitController habitController = new HabitController(habitService, objectMapper);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/habits", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String response = "";

            try {
                // Roteamento seguro
                if (method.equals("GET")) {
                    response = habitController.getAll();
                } else if (method.equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    response = habitController.create(body);
                } else if (method.equals("PUT")) {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    String[] parts = path.split("/");
                    Long id = (parts.length > 2) ? Long.parseLong(parts[2]) : 0L;
                    response = habitController.update(id, body);
                } else if (method.equals("DELETE")) {
                    String[] parts = path.split("/");
                    Long id = (parts.length > 2) ? Long.parseLong(parts[2]) : 0L;
                    response = habitController.delete(id);
                }

                // A CORREÇÃO MÁGICA: Convertemos em bytes primeiro!
                byte[] responseBytes = response.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (Exception e) {
                e.printStackTrace();
                String error = "{\"error\": \"Internal Server Error\"}";
                byte[] errorBytes = error.getBytes("UTF-8");
                exchange.sendResponseHeaders(500, errorBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBytes);
                }
            }
        });

        server.start();
        System.out.println("Servidor rodando em http://localhost:8080/habits");
    }
}