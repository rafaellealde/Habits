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

    // Aqui devem entrar: leitura de application.properties ou variáveis de ambiente.
    private static final String URL      = "jdbc:postgresql://localhost:5432/habitflow";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres";

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
