// Exceção de domínio para violações de regra de negócio.
// Aqui devem ser adicionados: códigos de erro, lista de campos inválidos,
// e um mapeamento para status HTTP 422 (Unprocessable Entity) no controller.
package com.habitflow.exception;

/**
 * Lançada quando os dados de um {@code Habit} violam uma regra de validação.
 * Estende {@link RuntimeException} para ser uma unchecked exception (não obriga try/catch).
 */
public class HabitValidationException extends RuntimeException {

    private final String field;

    public HabitValidationException(String message) {
        super(message);
        this.field = null;
    }

    public HabitValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    /** Retorna o campo que originou a violação, ou {@code null} se não aplicável. */
    public String getField() {
        return field;
    }
}
