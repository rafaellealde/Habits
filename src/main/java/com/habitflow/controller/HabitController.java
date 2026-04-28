package com.habitflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitflow.dto.HabitDTO;
import com.habitflow.exception.HabitValidationException;
import com.habitflow.model.Habit;
import com.habitflow.service.HabitService;

import java.util.List;

/**
 * Ponto de entrada HTTP para recursos de {@link Habit}.
 */
public class HabitController {

    private final HabitService habitService;
    private final ObjectMapper objectMapper;

    public HabitController(HabitService habitService, ObjectMapper objectMapper) {
        this.habitService = habitService;
        this.objectMapper = objectMapper;
    }

    public String getAll() {
        System.out.println("Chamando: HabitController.getAll()");
        try {
            List<Habit> habits = habitService.findAll();
            return objectMapper.writeValueAsString(habits);
        } catch (Exception e) {
            return "{\"status\": 500, \"error\": \"Internal server error\"}";
        }
    }

    public String getById(Long id) {
        System.out.println("Chamando: HabitController.getById(" + id + ")");
        try {
            Habit habit = habitService.findById(id);
            // Segurança: se não achar o hábito, retorna 404 em vez de null
            if (habit == null) {
                return "{\"status\": 404, \"error\": \"Habit not found\"}";
            }
            return objectMapper.writeValueAsString(habit);
        } catch (HabitValidationException e) {
            return "{\"status\": 404, \"error\": \"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"status\": 500, \"error\": \"Internal server error\"}";
        }
    }

    public String create(String requestBody) {
        System.out.println("Chamando: HabitController.create(body=" + requestBody + ")");
        try {
            HabitDTO dto = objectMapper.readValue(requestBody, HabitDTO.class);
            Habit created = habitService.create(dto);
            return objectMapper.writeValueAsString(created);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return "{\"status\": 400, \"error\": \"Invalid JSON format\"}";
        } catch (Exception e) {
            return "{\"status\": 422, \"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String update(Long id, String requestBody) {
        System.out.println("Chamando: HabitController.update(id=" + id + ", body=" + requestBody + ")");
        try {
            HabitDTO dto = objectMapper.readValue(requestBody, HabitDTO.class);
            Habit updated = habitService.update(id, dto);
            return objectMapper.writeValueAsString(updated);
        } catch (Exception e) {
            return "{\"status\": 422, \"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String delete(Long id) {
        System.out.println("Chamando: HabitController.delete(" + id + ")");
        try {
            habitService.delete(id);
            return "{\"status\": 204, \"message\": \"Deleted successfully\"}";
        } catch (Exception e) {
            return "{\"status\": 404, \"error\": \"Habit not found or could not be deleted\"}";
        }
    }
}