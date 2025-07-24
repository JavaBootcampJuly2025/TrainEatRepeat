package org.athletes.traineatrepeat.controller;

import static org.athletes.traineatrepeat.common.ValidationCommon.UUID_REGEX;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.service.TrainingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

  private final TrainingService trainingService;

  @PostMapping("/submit-training")
  public TrainingRecordResponse submitTraining(
      @RequestParam
          @NotBlank
          @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          @Valid
          @RequestBody
          TrainingRecordRequest request) {
    return trainingService.submitTraining(request);
  }

  @GetMapping("/trainings")
  public List<TrainingRecordResponse> getTrainings(
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid,
      @RequestParam(required = false) TimePeriod timePeriod) {
    return trainingService.getTrainingsForUser(uuid, timePeriod);
  }

  @DeleteMapping("/{id}")
  public void deleteTraining(
      @PathVariable @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String id) {
    trainingService.deleteTrainingById(id);
  }

  @PutMapping("/{id}")
  public TrainingRecordResponse updateTraining(
      @PathVariable @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String id,
      @Valid @RequestBody TrainingRecordRequest request) {
    return trainingService.updateTrainingById(id, request);
  }
}
