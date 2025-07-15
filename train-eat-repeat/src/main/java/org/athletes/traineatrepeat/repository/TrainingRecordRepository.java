package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface TrainingRecordRepository extends JpaRepository<TrainingDTO, String> {

    public TrainingDTO getTrainingRecordById(String trainingRecordId);
}
