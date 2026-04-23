// Entidade que representa uma execução (check-in) de um hábito.
// Aqui devem ser adicionados: status de conclusão (COMPLETE/SKIP), notas do usuário,
// e mapeamento com a tabela "executions" do banco (FK para habit_id).
package com.habitflow.model;

import java.time.LocalDateTime;

/**
 * Representa um evento de execução de um {@link Habit}.
 * SRP: esta classe é responsável apenas por modelar os dados de uma execução.
 */
public class Execution {

    private Long id;
    private Long habitId;
    private LocalDateTime executedAt;
    private String notes;

    public Execution() {}

    public Execution(Long id, Long habitId, LocalDateTime executedAt, String notes) {
        this.id = id;
        this.habitId = habitId;
        this.executedAt = executedAt;
        this.notes = notes;
    }

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public String getNotes() {
        return notes;
    }

    // --- Setters ---

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Execution{id=" + id + ", habitId=" + habitId + ", executedAt=" + executedAt + "}";
    }
}
