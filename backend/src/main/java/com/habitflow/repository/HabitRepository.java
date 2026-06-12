// Interface do repositório de Habit.
// ISP: define apenas as operações CRUD necessárias para este agregado.
// Aqui devem ser adicionadas: assinaturas de queries especializadas
// (ex: findByFrequency, findAllWithExecutionsToday).
package com.habitflow.repository;

import com.habitflow.model.Habit;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência para a entidade {@link Habit}.
 * DIP: as camadas superiores dependem desta abstração, não da implementação JDBC.
 */
public interface HabitRepository {

    List<Habit> findAll();

    Optional<Habit> findById(Long id);

    Habit save(Habit habit);

    Habit update(Habit habit);

    void deleteById(Long id);
}
