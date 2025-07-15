package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.model.TrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, String> {

    public TrainingRecord getTrainingRecordById(String trainingRecordId);
}
