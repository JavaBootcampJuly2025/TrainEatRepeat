package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseDTO, String> {

}