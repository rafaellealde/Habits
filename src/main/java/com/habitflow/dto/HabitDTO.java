// DTO de transferência de dados para Habit.
// Aqui devem ser adicionados: anotações Jackson (@JsonProperty) para controle do nome dos campos JSON,
// validações (ex: @NotBlank via Bean Validation), e conversão bidirecional para/de Habit.
package com.habitflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object para a entidade {@code Habit}.
 * Utiliza Java Record para imutabilidade (conforme convenções).
 * OCP: novos campos expõem a API sem alterar a camada de domínio.
 */
public record HabitDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("frequency") String frequency,
        @JsonProperty("created_at") String createdAt
) {
    /**
     * Fábrica estática para conversão rápida sem acoplamento externo.
     * Aqui deve entrar a lógica de formatação de datas e mapeamento de campos extras.
     */
    public static HabitDTO empty() {
        return new HabitDTO(null, "", "", "", "");
    }
}
