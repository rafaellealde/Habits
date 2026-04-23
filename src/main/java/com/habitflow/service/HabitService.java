// Camada de serviço (fachada) para operações de Habit.
// Aqui devem ser implementados: validações de negócio (ex: nome duplicado, frequência inválida),
// ocultação da lógica de persistência para os controllers,
// e disparo de HabitValidationException quando as regras forem violadas.
package com.habitflow.service;

import com.habitflow.dto.HabitDTO;
import com.habitflow.exception.HabitValidationException;
import com.habitflow.model.Habit;
import com.habitflow.repository.HabitRepository;

import java.util.List;
import java.util.Optional;

/**
 * Fachada de negócio para {@link Habit}.
 * SRP: coordena as regras de negócio e delega a persistência ao repositório.
 * DIP: depende da interface {@link HabitRepository}, não da implementação JDBC.
 */
public class HabitService {

    private final HabitRepository habitRepository;

    /** Injeção de dependência via construtor (DIP). */
    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Retorna todos os hábitos cadastrados.
     * Aqui deve entrar a lógica de paginação e ordenação.
     */
    public List<Habit> findAll() {
        System.out.println("Chamando: HabitService.findAll()");
        return habitRepository.findAll();
    }

    /**
     * Busca um hábito pelo ID.
     * Aqui deve entrar o lançamento de NotFoundException quando o hábito não for encontrado.
     */
    public Optional<Habit> findById(Long id) {
        System.out.println("Chamando: HabitService.findById(" + id + ")");
        return habitRepository.findById(id);
    }

    /**
     * Cria um novo hábito a partir de um DTO.
     * Aqui deve entrar: validação de campos obrigatórios e mapeamento DTO → Model.
     *
     * @throws HabitValidationException se os dados do DTO forem inválidos.
     */
    public Habit create(HabitDTO dto) {
        System.out.println("Chamando: HabitService.create(" + dto + ")");
        // Aqui deve entrar: validação e mapeamento real.
        Habit habit = new Habit();
        habit.setName(dto.name());
        habit.setDescription(dto.description());
        habit.setFrequency(dto.frequency());
        return habitRepository.save(habit);
    }

    /**
     * Atualiza os dados de um hábito existente.
     * Aqui deve entrar: verificação de existência antes do update e merge seletivo de campos.
     *
     * @throws HabitValidationException se os dados forem inválidos.
     */
    public Habit update(Long id, HabitDTO dto) {
        System.out.println("Chamando: HabitService.update(" + id + ", " + dto + ")");
        // Aqui deve entrar: buscar o hábito existente, aplicar as mudanças e persistir.
        Habit habit = new Habit();
        habit.setId(id);
        habit.setName(dto.name());
        habit.setDescription(dto.description());
        habit.setFrequency(dto.frequency());
        return habitRepository.update(habit);
    }

    /**
     * Remove um hábito pelo ID.
     * Aqui deve entrar: verificação de existência antes de deletar.
     */
    public void delete(Long id) {
        System.out.println("Chamando: HabitService.delete(" + id + ")");
        habitRepository.deleteById(id);
    }
}
