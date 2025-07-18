package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseDTO, String> {
    Optional<ExerciseDTO> findByNameIgnoreCase(String name);
}