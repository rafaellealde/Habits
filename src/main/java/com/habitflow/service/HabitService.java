package com.habitflow.service;

import com.habitflow.dto.HabitDTO;
import com.habitflow.exception.HabitValidationException;
import com.habitflow.model.Habit;
import com.habitflow.repository.HabitRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fachada de negócio para {@link Habit}.
 * SRP: coordena as regras de negócio e delega a persistência ao repositório.
 */
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Retorna todos os hábitos ordenados por data de criação.
     */
    public List<Habit> findAll() {
        System.out.println("Chamando: HabitService.findAll()");
        // Lógica simples de ordenação em memória (exemplo de manipulação de coleção)
        return habitRepository.findAll().stream()
                .sorted(Comparator.comparing(Habit::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Habit findById(Long id) {
        System.out.println("Chamando: HabitService.findById(" + id + ")");
        return habitRepository.findById(id)
                .orElseThrow(() -> new HabitValidationException("id", "Hábito não encontrado com ID: " + id, "NOT_FOUND"));
    }

    /**
     * Cria um novo hábito com validação de negócio.
     */
    public Habit create(HabitDTO dto) {
        System.out.println("Chamando: HabitService.create()");
        
        // Mapeamento DTO -> Model
        Habit habit = new Habit(null, dto.name(), dto.description(), dto.frequency(), LocalDate.now());
        
        // Validação da regra de negócio (Self-validation)
        habit.validate(); 
        
        return habitRepository.save(habit);
    }

    /**
     * Atualiza dados após verificar existência.
     */
    public Habit update(Long id, HabitDTO dto) {
        System.out.println("Chamando: HabitService.update(" + id + ")");
        
        // Verificação de existência
        Habit existingHabit = findById(id);
        
        // Merge dos novos dados
        existingHabit.setName(dto.name());
        existingHabit.setDescription(dto.description());
        existingHabit.setFrequency(dto.frequency());
        
        // Nova validação após alteração
        existingHabit.validate();
        
        return habitRepository.update(existingHabit);
    }

    public void delete(Long id) {
        System.out.println("Chamando: HabitService.delete(" + id + ")");
        // Verificação de existência antes de deletar
        findById(id); 
        habitRepository.deleteById(id);
    }
}