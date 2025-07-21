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

    /**
     * COMMENT: You can use just .toList() instead of .collect(Collectors.toList()) in Java 21
     */
    public List<ExerciseResponse> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exerciseConverter::toResponseFromEntity)
                .collect(Collectors.toList());
    }

    /** COMMENT: I see you have implemented a method to get an exercise by ID. Yet, you are still retrieving exercises by id and handle exception multiple times.
    * This function is not used, even-though it is very useful. Use it instead of calling exerciseRepository.findById(id)
    * .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id)); multiple times
    */
    public ExerciseResponse getExerciseById(String id) {
        return exerciseRepository.findById(id)
                .map(exerciseConverter::toResponseFromEntity)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));
    }

    public ExerciseResponse updateExercise(String id, ExerciseRequest request) {
        ExerciseDTO existingExercise = exerciseRepository.findById(id)
                /**
                * COMMENT: I see that you are using RuntimeException everywhere. Instead, you can use custom exceptions
                * in which you can also define HTTP error codes. Example : TrainEatRepeatException class. You can also go a step further and
                * create custom Error Handling for HTTP errors.
                */
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