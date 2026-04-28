package com.habitflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.habitflow.model.Habit; // Import necessário para a conversão
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object para a entidade {@code Habit}.
 * Utiliza Java Record para imutabilidade (conforme convenções).
 * OCP: novos campos expõem a API sem alterar a camada de domínio.
 */
public record HabitDTO(
        @JsonProperty("id") Long id,

        @NotBlank(message = "O nome do hábito é obrigatório")
        @JsonProperty("name") String name,

        @NotBlank(message = "A descrição é obrigatória")
        @JsonProperty("description") String description,

        @NotBlank(message = "A frequência é obrigatória")
        @JsonProperty("frequency") String frequency,

        @JsonProperty("created_at") String createdAt
) {
    /**
     * Fábrica estática para conversão rápida sem acoplamento externo.
     */
    public static HabitDTO empty() {
        return new HabitDTO(null, "", "", "", "");
    }

    /**
     * Conversão de Entidade (Model) para DTO.
     * Útil quando o Controller precisa retornar dados ao usuário.
     */
    public static HabitDTO fromEntity(Habit habit) {
        return new HabitDTO(
            habit.getId(),
            habit.getName(),
            habit.getDescription(),
            habit.getFrequency(),
            habit.getCreatedAt() != null ? habit.getCreatedAt().toString() : null
        );
    }

    /**
     * Conversão de DTO para Entidade (Model).
     * Útil quando o Controller recebe um DTO e precisa salvar no banco.
     */
    public Habit toEntity() {
    // Adicionamos LocalDate.now() para satisfazer os 5 argumentos do construtor
    return new Habit(this.id, this.name, this.description, this.frequency, java.time.LocalDate.now());
}
}