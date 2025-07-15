package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.dto.response.MealRecordRequest;
import org.athletes.traineatrepeat.dto.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.MealRecord;
import org.athletes.traineatrepeat.service.MealRecordService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @PostMapping("/submit-food")
    public MealRecordResponse submitMeal(@RequestParam String uuid, @RequestBody MealRecordRequest request) {
        return mealRecordService.submitMeal(uuid, request);
    }
}
