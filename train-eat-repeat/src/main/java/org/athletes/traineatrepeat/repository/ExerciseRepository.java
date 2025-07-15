package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    public Exercise getExerciseById(Long id);
    Optional<Exercise> getExerciseByName(String name);

}
