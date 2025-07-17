package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping("/submit-exercise")
    public ResponseEntity<ExerciseResponse> createExercise(@RequestBody ExerciseRequest request) {
        ExerciseResponse response = exerciseService.createExercise(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
        List<ExerciseResponse> responses = exerciseService.getAllExercises();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}") // Path variable remains {id} as per standard REST conventions
    public ResponseEntity<ExerciseResponse> updateExercise(@PathVariable String id, @RequestBody ExerciseRequest request) {
        ExerciseResponse response = exerciseService.updateExercise(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}") // Path variable remains {id} as per standard REST conventions
    public ResponseEntity<Void> deleteExercise(@PathVariable String id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}