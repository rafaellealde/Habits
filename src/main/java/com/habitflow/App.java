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

/**
 * Classe principal da aplicação HabitFlow.
 * Realiza a composição/wiring manual de todas as dependências (Composition Root).
 * DIP: toda a árvore de dependências é montada aqui, sem uso de framework de injeção.
 */
public class App {

    public static void main(String[] args) {
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

        // Aqui deve entrar: registro das rotas em um servidor HTTP embutido.
        // Exemplo de fluxo para validação da cadeia de chamadas:
        System.out.println("\n--- Simulando chamadas de endpoints ---");
        System.out.println("GET /habits         → " + habitController.getAll());
        System.out.println("GET /habits/1       → " + habitController.getById(1L));
        System.out.println("POST /habits        → " + habitController.create("{\"name\":\"Meditar\",\"description\":\"10 minutos\",\"frequency\":\"DAILY\",\"created_at\":null}"));
        System.out.println("PUT /habits/1       → " + habitController.update(1L, "{\"name\":\"Meditar Atualizado\",\"description\":\"15 minutos\",\"frequency\":\"DAILY\",\"created_at\":null}"));
        System.out.println("DELETE /habits/1    → " + habitController.delete(1L));
        System.out.println("\n--- HabitFlow inicializado com sucesso ---");
    }
}
