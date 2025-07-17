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

    public ExerciseResponse createExercise(ExerciseRequest request) {
        ExerciseDTO exerciseToSave = ExerciseDTO.builder()
                .name(request.name())
                .MET(request.MET())
                .build();
        ExerciseDTO savedExercise = exerciseRepository.save(exerciseToSave);
        return exerciseConverter.toResponseFromEntity(savedExercise);
    }

    public List<ExerciseResponse> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseConverter::toResponseFromEntity)
                .collect(Collectors.toList());
    }

    public ExerciseResponse getExerciseById(String id) {
        return exerciseRepository.findById(id)
                .map(exerciseConverter::toResponseFromEntity)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));
    }

    public ExerciseResponse updateExercise(String id, ExerciseRequest request) {
        ExerciseDTO existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));

        existingExercise.setName(request.name());
        existingExercise.setMET(request.MET());

        ExerciseDTO updatedExercise = exerciseRepository.save(existingExercise);
        return exerciseConverter.toResponseFromEntity(updatedExercise);
    }

    public void deleteExercise(String id) {
        exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));
        exerciseRepository.deleteById(id);
    }
}