package org.athletes.traineatrepeat.repository;

import java.time.LocalDate;
import java.util.List;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRecordRepository extends JpaRepository<MealDTO, String> {

  List<MealDTO> findAllByUuidAndDate(String uuid, LocalDate date);

  List<MealDTO> findAllByUuid(String uuid);

  @Query("SELECT m FROM MealDTO m WHERE m.uuid = :uuid AND m.date BETWEEN :start AND :end")
  List<MealDTO> findMealsByUuidAndDateBetween(String uuid, LocalDate start, LocalDate end);
}
