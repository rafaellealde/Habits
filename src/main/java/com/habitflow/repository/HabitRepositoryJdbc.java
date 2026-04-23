// Implementação JDBC do repositório de Habit.
// Aqui devem ser implementados: os PreparedStatements para cada operação CRUD,
// o mapeamento de ResultSet para objetos Habit (RowMapper),
// e o tratamento de SQLException (conversão para exceções de domínio).
package com.habitflow.repository;

import com.habitflow.infra.DatabaseConfig;
import com.habitflow.model.Habit;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC de {@link HabitRepository}.
 * SRP: responsável exclusivamente pela persistência de {@link Habit}.
 * DIP: depende de {@link DatabaseConfig} (abstração de infraestrutura), não de uma Connection direta.
 */
public class HabitRepositoryJdbc implements HabitRepository {

    private final DatabaseConfig databaseConfig;

    /** Injeção de dependência via construtor (DIP). */
    public HabitRepositoryJdbc(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public List<Habit> findAll() {
        System.out.println("Chamando: HabitRepositoryJdbc.findAll()");
        // Aqui deve entrar: SELECT * FROM habits com mapeamento de ResultSet.
        return new ArrayList<>();
    }

    @Override
    public Optional<Habit> findById(Long id) {
        System.out.println("Chamando: HabitRepositoryJdbc.findById(" + id + ")");
        // Aqui deve entrar: SELECT * FROM habits WHERE id = ? com PreparedStatement.
        return Optional.empty();
    }

    @Override
    public Habit save(Habit habit) {
        System.out.println("Chamando: HabitRepositoryJdbc.save(" + habit + ")");
        // Aqui deve entrar: INSERT INTO habits (name, description, frequency, created_at)
        // VALUES (?, ?, ?, ?) e recuperação do ID gerado (Statement.RETURN_GENERATED_KEYS).
        return habit;
    }

    @Override
    public Habit update(Habit habit) {
        System.out.println("Chamando: HabitRepositoryJdbc.update(" + habit + ")");
        // Aqui deve entrar: UPDATE habits SET name=?, description=?, frequency=? WHERE id=?
        return habit;
    }

    @Override
    public void deleteById(Long id) {
        System.out.println("Chamando: HabitRepositoryJdbc.deleteById(" + id + ")");
        // Aqui deve entrar: DELETE FROM habits WHERE id=?
    }
}
