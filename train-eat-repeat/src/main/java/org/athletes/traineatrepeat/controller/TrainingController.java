package org.athletes.traineatrepeat.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.service.TrainingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

  private final TrainingService trainingService;

  @PostMapping("/submit-training")
  public TrainingRecordResponse submitTraining(
      @RequestParam String uuid, @Valid @RequestBody TrainingRecordRequest request) {
    return trainingService.submitTraining(uuid, request);
  }

  @GetMapping("/trainings")
  public List<TrainingRecordResponse> getTrainings(
      @RequestParam String uuid, @RequestParam(required = false) TimePeriod timePeriod) {
    return trainingService.getTrainingsForUser(uuid, timePeriod);
  }

  @DeleteMapping("/{id}")
  public void deleteTraining(@PathVariable String id) {
    trainingService.deleteTrainingById(id);
  }

  @PutMapping("/{id}")
  public TrainingRecordResponse updateTraining(
      @PathVariable String id, @Valid @RequestBody TrainingRecordRequest request) {
    return trainingService.updateTrainingById(id, request);
  }
}
