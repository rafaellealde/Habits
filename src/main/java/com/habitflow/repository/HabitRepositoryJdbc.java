package com.habitflow.repository;

import com.habitflow.infra.DatabaseConfig;
import com.habitflow.model.Habit;

import java.sql.*;
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

    public HabitRepositoryJdbc(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public List<Habit> findAll() {
        String sql = "SELECT id, name, description, frequency, created_at FROM habits ORDER BY created_at DESC";
        List<Habit> habits = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                habits.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hábitos.", e);
        }

        return habits;
    }

    @Override
    public Optional<Habit> findById(Long id) {
        String sql = "SELECT id, name, description, frequency, created_at FROM habits WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hábito com id " + id + ".", e);
        }

        return Optional.empty();
    }

    @Override
    public Habit save(Habit habit) {
        String sql = "INSERT INTO habits (name, description, frequency, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, habit.getName());
            stmt.setString(2, habit.getDescription());
            stmt.setString(3, habit.getFrequency());
            stmt.setDate(4, habit.getCreatedAt() != null
                    ? Date.valueOf(habit.getCreatedAt())
                    : Date.valueOf(java.time.LocalDate.now()));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    habit.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar hábito.", e);
        }

        return habit;
    }

    @Override
    public Habit update(Habit habit) {
        String sql = "UPDATE habits SET name = ?, description = ?, frequency = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, habit.getName());
            stmt.setString(2, habit.getDescription());
            stmt.setString(3, habit.getFrequency());
            stmt.setLong(4, habit.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar hábito com id " + habit.getId() + ".", e);
        }

        return habit;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM habits WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar hábito com id " + id + ".", e);
        }
    }

    private Habit mapRow(ResultSet rs) throws SQLException {
        return new Habit(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("frequency"),
                rs.getDate("created_at").toLocalDate()
        );
    }
}
