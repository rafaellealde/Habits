package com.habitflow.model;

import com.habitflow.exception.HabitValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Representa um Hábito no sistema.
 */
public class Habit {

    private Long id;
    private String name;
    private String description;
    private String frequency;
    private LocalDate createdAt;
    private final List<Execution> executions;

    public Habit() {
        this.executions = new ArrayList<>();
    }

    public Habit(Long id, String name, String description, String frequency, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = createdAt;
        this.executions = new ArrayList<>();
    }

    // --- Validação (Domain Logic) ---
    
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new HabitValidationException("name", "O nome do hábito não pode ser vazio.", "INVALID_NAME");
        }
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new HabitValidationException("frequency", "A frequência é obrigatória.", "INVALID_FREQUENCY");
        }
    }

    // --- Lógica de Streaks ---

    public int calculateCurrentStreak() {
        if (executions.isEmpty()) return 0;

        List<Execution> sorted = new ArrayList<>(executions);
        sorted.sort(Comparator.comparing(Execution::getExecutedAt).reversed());

        int streak = 0;
        LocalDate lastDate = LocalDate.now();

        for (Execution e : sorted) {
            LocalDate execDate = e.getExecutedAt().toLocalDate();
            if (execDate.equals(lastDate) || execDate.equals(lastDate.minusDays(1))) {
                streak++;
                lastDate = execDate;
            } else {
                break;
            }
        }
        return streak;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }

    // --- AQUI ESTAVA A FALTA! ---
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public List<Execution> getExecutions() {
        return Collections.unmodifiableList(executions);
    }

    public void addExecution(Execution execution) { this.executions.add(execution); }
    public void removeExecution(Execution execution) { this.executions.remove(execution); }
}