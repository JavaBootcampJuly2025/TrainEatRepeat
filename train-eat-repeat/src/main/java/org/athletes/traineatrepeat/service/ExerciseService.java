package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.ExerciseConverter;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseConverter exerciseConverter;

    public ExerciseResponse submitExercise(ExerciseRequest request) {
        ExerciseDTO dtoToSave = exerciseConverter.fromRequest(request);
        ExerciseDTO savedDto = exerciseRepository.save(dtoToSave);
        return exerciseConverter.toResponse(savedDto);
    }

    public List<ExerciseResponse> getAllExercises() {
        List<ExerciseDTO> dtos = exerciseRepository.findAll();
        return dtos.stream()
                .map(exerciseConverter::toResponse)
                .collect(Collectors.toList());
    }

    public ExerciseResponse getExerciseById(String id) {
        return exerciseRepository.findById(id)
                .map(exerciseConverter::toResponse)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));
    }
}