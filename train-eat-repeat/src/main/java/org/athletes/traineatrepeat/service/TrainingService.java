package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordConverter trainingRecordConverter;

    public TrainingDTO getTrainingRecord(String uuid, TrainingRecordRequest request) {
        //TODO:
        //TrainingDTO trainingDTODto = trainingRepository.getTrainingRecordById(String.valueOf(request));
        //TrainingDTO saved = trainingRepository.save(trainingDTO);
        //return trainingConverter.convertToTrainingRecord(trainingDTODto);
        return null;
    }
}