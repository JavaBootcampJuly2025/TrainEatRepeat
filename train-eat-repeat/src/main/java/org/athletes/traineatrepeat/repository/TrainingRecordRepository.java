package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.entity.Training;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRecordRepository extends JpaRepository<TrainingDTO, String> {
    Training getTrainingByTrainingId(String trainingId);
}