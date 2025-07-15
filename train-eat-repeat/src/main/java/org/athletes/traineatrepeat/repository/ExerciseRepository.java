package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<ExerciseDTO, Long> {

    public ExerciseDTO getExerciseById(Long id);
    Optional<ExerciseDTO> getExerciseByName(String name);

}
