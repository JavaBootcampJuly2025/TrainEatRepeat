package org.athletes.traineatrepeat.controller;

import org.athletes.traineatrepeat.converter.ExerciseConverter;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseRepository repository;
    private final ExerciseConverter converter;

    public ExerciseController(ExerciseRepository repository, ExerciseConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @PostMapping("/submit-exercise")
    public ResponseEntity<ExerciseResponse> submitExercise(@RequestBody ExerciseRequest request) {
        ExerciseDTO dto = converter.fromRequest(request);
        ExerciseDTO saved = repository.save(dto);
        ExerciseResponse response = converter.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}