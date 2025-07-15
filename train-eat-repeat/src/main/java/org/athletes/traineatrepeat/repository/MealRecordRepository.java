package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface MealRecordRepository extends JpaRepository<MealDTO, Long> {

    List<MealDTO> findByUserUuid(String uuid);
    Optional<MealDTO> findById(Long id);
}