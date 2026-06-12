package com.habitflow.repository;

import com.habitflow.infra.DatabaseConfig;
import com.habitflow.model.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC de {@link HabitRepository}.
 * SRP: responsável exclusivamente pela persistência de {@link Habit}.
 * DIP: depende de {@link DatabaseConfig} (abstração de infraestrutura).
 */
public class HabitRepositoryJdbc implements HabitRepository {

    private final DatabaseConfig databaseConfig;

    public HabitRepositoryJdbc(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
        initSchema();
    }

    /** Cria a tabela caso ainda não exista (idempotente). */
    private void initSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS habits (
                    id          BIGSERIAL PRIMARY KEY,
                    name        VARCHAR(255) NOT NULL,
                    description TEXT,
                    frequency   VARCHAR(50)  NOT NULL DEFAULT 'DAILY',
                    created_at  DATE         NOT NULL DEFAULT CURRENT_DATE
                )
                """;
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Schema verificado/criado com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar schema: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Habit> findAll() {
        String sql = "SELECT id, name, description, frequency, created_at FROM habits ORDER BY id";
        List<Habit> habits = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                habits.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hábitos: " + e.getMessage(), e);
        }
        return habits;
    }

    @Override
    public Optional<Habit> findById(Long id) {
        String sql = "SELECT id, name, description, frequency, created_at FROM habits WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar hábito por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Habit save(Habit habit) {
        String sql = "INSERT INTO habits (name, description, frequency, created_at) " +
                     "VALUES (?, ?, ?, CURRENT_DATE) RETURNING id, created_at";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, habit.getName());
            ps.setString(2, habit.getDescription());
            ps.setString(3, habit.getFrequency() != null ? habit.getFrequency() : "DAILY");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    habit.setId(rs.getLong("id"));
                    Date date = rs.getDate("created_at");
                    if (date != null) habit.setCreatedAt(date.toLocalDate());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar hábito: " + e.getMessage(), e);
        }
        return habit;
    }

    @Override
    public Habit update(Habit habit) {
        String sql = "UPDATE habits SET name = ?, description = ?, frequency = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, habit.getName());
            ps.setString(2, habit.getDescription());
            ps.setString(3, habit.getFrequency() != null ? habit.getFrequency() : "DAILY");
            ps.setLong(4, habit.getId());
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Hábito não encontrado: id=" + habit.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar hábito: " + e.getMessage(), e);
        }
        return habit;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM habits WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar hábito: " + e.getMessage(), e);
        }
    }

    private Habit mapRow(ResultSet rs) throws SQLException {
        Habit h = new Habit();
        h.setId(rs.getLong("id"));
        h.setName(rs.getString("name"));
        h.setDescription(rs.getString("description"));
        h.setFrequency(rs.getString("frequency"));
        Date date = rs.getDate("created_at");
        if (date != null) h.setCreatedAt(date.toLocalDate());
        return h;
    }
}
