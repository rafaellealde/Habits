package com.habitflow.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a criação de conexões com o banco de dados.
 * SRP: responsável exclusivamente pelo fornecimento de {@link Connection}.
 *
 * Ordem de resolução das credenciais:
 *   1. DATABASE_URL  — URL única no formato postgresql://user:pass@host:port/db
 *                      injetada automaticamente pelo Render (Blueprint) e Railway.
 *   2. DB_URL + DB_USER + DB_PASSWORD — variáveis separadas para outras plataformas.
 *   3. Fallback localhost — ambiente de desenvolvimento sem variáveis definidas.
 */
public class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isBlank()) {
            // Render/Railway fornecem postgresql://...; o driver JDBC exige jdbc:postgresql://
            this.url      = databaseUrl.startsWith("jdbc:") ? databaseUrl : "jdbc:" + databaseUrl;
            this.user     = null;   // credenciais já embutidas na URL
            this.password = null;
        } else {
            this.url      = resolveEnv("DB_URL",      "jdbc:postgresql://localhost:5432/habitflow");
            this.user     = resolveEnv("DB_USER",     "postgres");
            this.password = resolveEnv("DB_PASSWORD", "postgres");
        }
    }

    /**
     * Cria e retorna uma nova conexão com o banco de dados.
     *
     * @return uma {@link Connection} aberta.
     * @throws RuntimeException se a conexão falhar.
     */
    public Connection getConnection() {
        try {
            return (user != null)
                    ? DriverManager.getConnection(url, user, password)
                    : DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao obter conexão com o banco de dados.", e);
        }
    }

    private static String resolveEnv(String key, String fallback) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
