package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.MealRecord;
import org.athletes.traineatrepeat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {

    List<MealRecord> findByUserUuid(String uuid);
    Optional<MealRecord> findById(Long id);
}