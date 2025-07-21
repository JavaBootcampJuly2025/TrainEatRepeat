package org.athletes.traineatrepeat.repository;

import java.util.Optional;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseDTO, String> {
  Optional<ExerciseDTO> findByNameIgnoreCase(String name);
}
