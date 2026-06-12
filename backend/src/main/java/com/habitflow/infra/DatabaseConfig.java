// Configuração de infraestrutura JDBC.
// Aqui devem ser implementados: leitura das credenciais de um arquivo .properties ou variáveis
// de ambiente, uso de um Connection Pool (ex: HikariCP), e tratamento de falhas de conexão.
package com.habitflow.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a criação de conexões com o banco de dados.
 * SRP: responsável exclusivamente pelo fornecimento de {@link Connection}.
 * OCP: a URL e credenciais podem ser externalizadas sem modificar esta classe.
 */
public class DatabaseConfig {

    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5432/habitflow";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "postgres";

    /**
     * Cria e retorna uma nova conexão com o banco de dados.
     * Aqui deve entrar a lógica de pool de conexões (ex: HikariCP).
     *
     * @return uma {@link Connection} aberta.
     * @throws RuntimeException se a conexão falhar.
     */
    public Connection getConnection() {
        System.out.println("Chamando: DatabaseConfig.getConnection()");
        try {
            // Aqui deve entrar: devolução de conexão de um pool, não uma conexão nova a cada chamada.
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // Aqui deve entrar: log estruturado e exceção de infraestrutura específica.
            throw new RuntimeException("Falha ao obter conexão com o banco de dados.", e);
        }
    }
}
