package org.athletes.traineatrepeat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.ExerciseConverter;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;
  private final ExerciseConverter exerciseConverter;

  public ExerciseResponse createExercise(ExerciseRequest request) {
    var exerciseToSave = ExerciseDTO.builder().name(request.name()).MET(request.met()).build();
    var savedExercise = exerciseRepository.save(exerciseToSave);
    return exerciseConverter.toResponseFromEntity(savedExercise);
  }

  public List<ExerciseResponse> getAllExercises() {
    var exercises = exerciseRepository.findAll();
    return exercises.stream().map(exerciseConverter::toResponseFromEntity).toList();
  }

  private ExerciseDTO getExerciseEntityById(String id) {
    return exerciseRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));
  }

  public ExerciseResponse updateExercise(String id, ExerciseRequest request) {
    ExerciseDTO existingExercise = getExerciseEntityById(id);

    existingExercise.setName(request.name());
    existingExercise.setMET(request.met());

    var updatedExercise = exerciseRepository.save(existingExercise);
    return exerciseConverter.toResponseFromEntity(updatedExercise);
  }

  public void deleteExercise(String id) {
    exerciseRepository.deleteById(id);
  }
}
