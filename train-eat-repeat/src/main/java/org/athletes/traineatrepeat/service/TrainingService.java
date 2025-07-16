package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordConverter trainingRecordConverter;


    public TrainingRecordResponse submitTraining(TrainingRecordRequest request) {
        TrainingDTO dtoToSave = trainingRecordConverter.fromRequest(request);
        TrainingDTO savedDto = trainingRecordRepository.save(dtoToSave);
        return trainingRecordConverter.toResponse(savedDto);
    }

    public TrainingRecordResponse getTrainingById(String id) {
        TrainingDTO dto = trainingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found with ID: " + id));
        return trainingRecordConverter.toResponse(dto);
    }

    public TrainingRecordResponse updateTraining(String id, TrainingRecordRequest request) {
        TrainingDTO trainingDTOToSave = trainingRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found with ID: " + id));

        TrainingDTO trainingDTOWithUuid = TrainingDTO.builder()
                .id(trainingDTOToSave.id())
                .exercise(request.exercise())
                .duration(request.duration())
                .caloriesLost(request.caloriesLost())
                .date(request.date())
                .build();

        TrainingDTO savedTrainingDTO = trainingRecordRepository.save(trainingDTOWithUuid);
        return trainingRecordConverter.toResponse(savedTrainingDTO);
    }

    public void deleteTraining(String id) {
        if (!trainingRecordRepository.existsById(id)) {
            throw new RuntimeException("Training record not found with ID: " + id);
        }
        trainingRecordRepository.deleteById(id);
    }
}