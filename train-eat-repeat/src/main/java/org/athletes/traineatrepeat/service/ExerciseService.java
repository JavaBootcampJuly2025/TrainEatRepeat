package org.athletes.traineatrepeat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.ExerciseConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
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
    if (exerciseRepository.findByNameIgnoreCase(request.name()).isPresent()) {
      throw new TrainEatRepeatException("Exercise with name '" + request.name() + "' already exists");
    }

    var exerciseToSave = ExerciseDTO.builder().name(request.name()).MET(request.met()).build();
    var savedExercise = exerciseRepository.save(exerciseToSave);
    return exerciseConverter.toResponseFromEntity(savedExercise);
  }

  public List<ExerciseResponse> getAllExercises() {
    var exercises = exerciseRepository.findAll();
    if (exercises.isEmpty()) {
      throw new TrainEatRepeatException("No exercises found");
    }
    return exercises.stream().map(exerciseConverter::toResponseFromEntity).toList();
  }

  private ExerciseDTO getExerciseEntityById(String id) {
    return exerciseRepository
            .findById(id)
            .orElseThrow(() -> new TrainEatRepeatException("Exercise not found with ID: " + id));
  }

  public ExerciseResponse updateExercise(String id, ExerciseRequest request) {
    ExerciseDTO existingExercise = getExerciseEntityById(id);

    var exerciseWithSameName = exerciseRepository.findByNameIgnoreCase(request.name());
    if (exerciseWithSameName.isPresent() && !exerciseWithSameName.get().getId().equals(id)) {
      throw new TrainEatRepeatException("Exercise with name '" + request.name() + "' already exists");
    }

    existingExercise.setName(request.name());
    existingExercise.setMET(request.met());

    var updatedExercise = exerciseRepository.save(existingExercise);
    return exerciseConverter.toResponseFromEntity(updatedExercise);
  }

  public void deleteExercise(String id) {
    if (id == null || id.isEmpty()) {
      throw new TrainEatRepeatException("Exercise ID cannot be null or empty");
    }
    if (!exerciseRepository.existsById(id)) {
      throw new TrainEatRepeatException("Exercise not found with ID: " + id);
    }
    exerciseRepository.deleteById(id);
  }
}
