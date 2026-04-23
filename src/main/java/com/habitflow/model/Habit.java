// Entidade principal do domínio.
// Aqui devem ser adicionados: validações de campos (ex: nome não nulo/vazio),
// lógica de cálculo de streaks, e mapeamento completo com a tabela "habits" do banco.
package com.habitflow.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um Hábito no sistema.
 * SRP: esta classe é responsável apenas por modelar os dados de um hábito.
 */
public class Habit {

    private Long id;
    private String name;
    private String description;
    private String frequency; // ex: "DAILY", "WEEKLY"
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

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /** Retorna visão imutável da lista interna (encapsulamento de coleção). */
    public List<Execution> getExecutions() {
        return Collections.unmodifiableList(executions);
    }

    // --- Setters (apenas para campos escalares, não para coleções) ---

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    // --- Mutadores de coleção (sem setList!) ---

    public void addExecution(Execution execution) {
        this.executions.add(execution);
    }

    public void removeExecution(Execution execution) {
        this.executions.remove(execution);
    }

    @Override
    public String toString() {
        return "Habit{id=" + id + ", name='" + name + "', frequency='" + frequency + "'}";
    }
}
