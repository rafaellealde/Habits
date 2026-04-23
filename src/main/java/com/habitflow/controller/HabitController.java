// Camada de controle HTTP para Habit.
// Aqui devem ser implementados: roteamento real de requisições HTTP (parse de URL e método),
// desserialização do corpo da requisição via Jackson ObjectMapper,
// serialização da resposta em JSON, e tratamento de erros com status HTTP corretos
// (200 OK, 201 Created, 404 Not Found, 422 Unprocessable Entity).
package com.habitflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitflow.dto.HabitDTO;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;

import java.util.List;
import java.util.Optional;

/**
 * Ponto de entrada HTTP para recursos de {@link Habit}.
 * SRP: responsável exclusivamente por receber requisições e devolver respostas.
 * DIP: depende de {@link HabitService} (abstração de negócio) e {@link ObjectMapper} injetados.
 */
public class HabitController {

    private final HabitService habitService;
    private final ObjectMapper objectMapper;

    /** Injeção de dependência via construtor (DIP). */
    public HabitController(HabitService habitService, ObjectMapper objectMapper) {
        this.habitService = habitService;
        this.objectMapper = objectMapper;
    }

    /**
     * GET /habits — lista todos os hábitos.
     * Aqui deve entrar: serialização da lista para JSON e status 200.
     */
    public String getAll() {
        System.out.println("Chamando: HabitController.getAll()");
        List<Habit> habits = habitService.findAll();
        try {
            return objectMapper.writeValueAsString(habits);
        } catch (Exception e) {
            // Aqui deve entrar: resposta de erro padronizada em JSON.
            return "[]";
        }
    }

    /**
     * GET /habits/{id} — busca um hábito pelo ID.
     * Aqui deve entrar: retorno 404 quando o hábito não existir.
     */
    public String getById(Long id) {
        System.out.println("Chamando: HabitController.getById(" + id + ")");
        Optional<Habit> habit = habitService.findById(id);
        try {
            if (habit.isPresent()) {
                return objectMapper.writeValueAsString(habit.get());
            }
            // Aqui deve entrar: resposta padronizada de 404.
            return "{\"error\": \"Habit not found\"}";
        } catch (Exception e) {
            return "{\"error\": \"Internal server error\"}";
        }
    }

    /**
     * POST /habits — cria um novo hábito.
     * Aqui deve entrar: desserialização do body, status 201 Created e URI do novo recurso.
     */
    public String create(String requestBody) {
        System.out.println("Chamando: HabitController.create(body=" + requestBody + ")");
        try {
            HabitDTO dto = objectMapper.readValue(requestBody, HabitDTO.class);
            Habit created = habitService.create(dto);
            return objectMapper.writeValueAsString(created);
        } catch (Exception e) {
            // Aqui deve entrar: distinção entre erro de parsing (400) e validação (422).
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * PUT /habits/{id} — atualiza um hábito existente.
     * Aqui deve entrar: verificação de existência antes do update e status 200.
     */
    public String update(Long id, String requestBody) {
        System.out.println("Chamando: HabitController.update(id=" + id + ", body=" + requestBody + ")");
        try {
            HabitDTO dto = objectMapper.readValue(requestBody, HabitDTO.class);
            Habit updated = habitService.update(id, dto);
            return objectMapper.writeValueAsString(updated);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * DELETE /habits/{id} — remove um hábito.
     * Aqui deve entrar: retorno 204 No Content após remoção bem-sucedida.
     */
    public String delete(Long id) {
        System.out.println("Chamando: HabitController.delete(" + id + ")");
        habitService.delete(id);
        // Aqui deve entrar: resposta com status HTTP 204 e body vazio.
        return "";
    }
}
