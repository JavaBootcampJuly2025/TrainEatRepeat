package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/submit-training")
    public ResponseEntity<TrainingRecordResponse> submitTraining(@RequestBody TrainingRecordRequest request) {
        TrainingRecordResponse response = trainingService.submitTraining(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingRecordResponse> getTrainingById(@PathVariable String id) {
        TrainingRecordResponse response = trainingService.getTrainingById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingRecordResponse> updateTraining(@PathVariable String id, @RequestBody TrainingRecordRequest request) {
        TrainingRecordResponse response = trainingService.updateTraining(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TrainingRecordResponse> deleteTraining(@PathVariable String id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}