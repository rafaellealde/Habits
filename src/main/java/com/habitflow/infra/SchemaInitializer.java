package com.habitflow.infra;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Garante que o schema do banco existe antes da aplicação servir requisições.
 * Usa CREATE TABLE IF NOT EXISTS — idempotente, seguro em múltiplos restarts.
 */
public class SchemaInitializer {

    private final DatabaseConfig databaseConfig;

    public SchemaInitializer(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void initialize() {
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS habits (
                        id          BIGSERIAL    PRIMARY KEY,
                        name        VARCHAR(255) NOT NULL,
                        description TEXT,
                        frequency   VARCHAR(50)  NOT NULL,
                        created_at  DATE         NOT NULL DEFAULT CURRENT_DATE
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS executions (
                        id          BIGSERIAL  PRIMARY KEY,
                        habit_id    BIGINT     NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
                        executed_at TIMESTAMP  NOT NULL DEFAULT NOW()
                    )
                    """);

            System.out.println("Schema initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inicializar o schema do banco de dados.", e);
        }
    }
}
