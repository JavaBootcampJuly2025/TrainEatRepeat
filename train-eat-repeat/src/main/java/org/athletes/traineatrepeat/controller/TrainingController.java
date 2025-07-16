package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.service.TrainingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/training-data")
    public TrainingDTO getTrainingData(@RequestParam String uuid, @RequestBody TrainingRecordRequest request) {
        return trainingService.getTrainingRecord(uuid, request);
    }

    //TODO:

}