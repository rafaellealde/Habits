package com.habitflow.exception;

/**
 * Lançada quando os dados de um {@code Habit} violam uma regra de validação.
 * Estende {@link RuntimeException} para ser uma unchecked exception.
 */
public class HabitValidationException extends RuntimeException {

    private final String field;
    private final String errorCode; // Adicionado para controle programático

    public HabitValidationException(String message, String errorCode) {
        super(message);
        this.field = null;
        this.errorCode = errorCode;
    }

    public HabitValidationException(String field, String message, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }

    /** Retorna o campo que originou a violação. */
    public String getField() {
        return field;
    }

    /** Retorna o código de erro para o cliente identificar o tipo de falha. */
    public String getErrorCode() {
        return errorCode;
    }
    
    /** Retorna o status HTTP sugerido para esta exceção. */
    public int getHttpStatus() {
        return 422; // Unprocessable Entity
    }
}