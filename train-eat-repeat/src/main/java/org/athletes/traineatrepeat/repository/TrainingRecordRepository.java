package org.athletes.traineatrepeat.repository;

import java.time.LocalDate;
import java.util.List;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRecordRepository extends JpaRepository<TrainingDTO, String> {

  List<TrainingDTO> findAllByUuidAndDate(String uuid, LocalDate date);

  List<TrainingDTO> findAllByUuid(String uuid);

  @Query("SELECT t FROM TrainingDTO t WHERE t.uuid = :uuid AND t.date BETWEEN :start AND :end")
  List<TrainingDTO> findTrainingsByUuidAndDateBetween(String uuid, LocalDate start, LocalDate end);
}
