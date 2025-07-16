package org.athletes.traineatrepeat.repository;


import org.athletes.traineatrepeat.model.entity.Meal;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRecordRepository extends JpaRepository<Meal, String> {

    List<Meal> findAllByUserUuidAndDate(String userUuid, LocalDate date);

    @Query("SELECT m FROM Meal m WHERE m.userUuid = :userUuid AND m.date BETWEEN :start AND :end")
    List<Meal> findMealsByUserUuidAndDateBetween(String userUuid, LocalDate start, LocalDate end);
}