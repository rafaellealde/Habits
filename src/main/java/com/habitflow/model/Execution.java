package com.habitflow.model;

import java.time.LocalDateTime;

/**
 * Representa um evento de execução de um {@link Habit}.
 * SRP: esta classe é responsável apenas por modelar os dados de uma execução.
 */
public class Execution {

    // Enum para garantir segurança de tipos (Type Safety)
    public enum ExecutionStatus {
        COMPLETE, SKIP
    }

    private Long id;
    private Long habitId; // FK para tabela habits
    private LocalDateTime executedAt;
    private String notes;
    private ExecutionStatus status; // Adicionado conforme solicitado

    public Execution() {}

    public Execution(Long id, Long habitId, LocalDateTime executedAt, String notes, ExecutionStatus status) {
        this.id = id;
        this.habitId = habitId;
        this.executedAt = executedAt;
        this.notes = notes;
        this.status = status;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public Long getHabitId() { return habitId; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public String getNotes() { return notes; }
    public ExecutionStatus getStatus() { return status; }

    // --- Setters ---

    public void setId(Long id) { this.id = id; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStatus(ExecutionStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Execution{id=" + id + ", habitId=" + habitId + ", status=" + status + "}";
    }
}