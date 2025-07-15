package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.service.MealService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealController {

    private final MealService mealRecordService;

    @PostMapping("/submit-food")
    public MealRecordResponse submitMeal(@RequestParam String uuid, @RequestBody MealRecordRequest request) {
        return mealRecordService.submitMeal(uuid, request);
    }
}
