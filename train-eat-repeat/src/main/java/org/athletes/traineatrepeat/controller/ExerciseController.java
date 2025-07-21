package org.athletes.traineatrepeat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

  private final ExerciseService exerciseService;

  @PostMapping("/submit-exercise")
  public ResponseEntity<ExerciseResponse> createExercise(@RequestBody ExerciseRequest request) {
    var response = exerciseService.createExercise(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
    var responses = exerciseService.getAllExercises();
    return ResponseEntity.ok(responses);
  }

  /**
   * COMMENT: Usually, for POST and PUT methods, endpoints are supported with annotation
   * {@code @Produces(MediaType.APPLICATION_JSON)}. If these methods are expected to handle Request
   * Body, endpoints are also supported with {@code @Consumes(MediaType.APPLICATION_JSON)} to
   * specify JSON as media type.
   */
  @PutMapping(
          value = "/{id}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ExerciseResponse> updateExercise(
          @PathVariable String id, @RequestBody ExerciseRequest request) {
    var response = exerciseService.updateExercise(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExercise(@PathVariable String id) {
    exerciseService.deleteExercise(id);
    return ResponseEntity.noContent().build();
  }
}
