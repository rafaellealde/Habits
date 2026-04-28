package com.habitflow.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private final String url = "jdbc:postgresql://localhost:5432/habitflow";
    private final String user = "postgres";
    private final String password = "admin123"; // Altere se a sua senha for outra!

    public DatabaseConfig() {}

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            System.err.println("ERRO NO BANCO: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}