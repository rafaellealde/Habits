package com.habitflow.repository;

import com.habitflow.model.Habit;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência para a entidade {@link Habit}.
 * DIP: as camadas superiores dependem desta abstração, não da implementação JDBC.
 * ISP: define apenas as operações CRUD e de consulta necessárias para este agregado.
 */
public interface HabitRepository {

    // --- CRUD Básico ---
    List<Habit> findAll();

    Optional<Habit> findById(Long id);

    Habit save(Habit habit);

    Habit update(Habit habit);

    void deleteById(Long id);

    // --- Queries Especializadas (Conforme solicitado) ---

    /** Busca hábitos filtrando pela frequência (ex: "DAILY", "WEEKLY"). */
    List<Habit> findByFrequency(String frequency);

    /** Busca todos os hábitos que possuem alguma execução registrada no dia de hoje. */
    List<Habit> findAllWithExecutionsToday();
}