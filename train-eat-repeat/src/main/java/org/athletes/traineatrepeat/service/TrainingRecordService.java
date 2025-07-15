package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.dto.response.TrainingRecordDto;
import org.athletes.traineatrepeat.model.TrainingRecord;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingRecordService {

    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordConverter trainingRecordConverter;

    public TrainingRecordDto getTrainingRecord(Long trainingRecordId) {
        //TODO:
        TrainingRecord trainingRecordDto = trainingRecordRepository.getTrainingRecordById(String.valueOf(trainingRecordId));
        return trainingRecordConverter.convertToTrainingRecord(trainingRecordDto);
    }
}
