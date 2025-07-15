package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.dto.response.TrainingRecordResponseDto;
import org.athletes.traineatrepeat.service.TrainingRecordService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training")
public class TrainRecordController {

    private final TrainingRecordService trainingRecordService;

    @GetMapping("/training-data")
    public TrainingRecordResponseDto getTrainingData(@RequestParam String foodId, @RequestParam String uuid){
        return trainingRecordService.getTrainingRecord(Long.valueOf(foodId));
    }

    //TODO:

}
