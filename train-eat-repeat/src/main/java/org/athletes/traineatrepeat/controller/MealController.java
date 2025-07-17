package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.service.MealService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealController {

    private final MealService mealService;

    @PostMapping("/submit-food")
    public MealRecordResponse submitMeal(@RequestParam String uuid, @RequestBody MealRecordRequest request) {
        return mealService.submitMeal(uuid, request);
    }

    @GetMapping("/meals")
    public List<MealRecordResponse> getTodaysMeals(@RequestParam String uuid, @RequestParam (required = false) TimePeriod timePeriod) {
        return mealService.getMealsForUser(uuid, timePeriod);
    }

    @DeleteMapping("/{id}")
    public void deleteMeal(@PathVariable String id) {
        mealService.deleteMealById(id);
    }

    @PutMapping("/{id}")
    public MealRecordResponse updateMeal(@PathVariable String id, @RequestBody MealRecordRequest request) {
        return mealService.updateMeal(id, request);
    }
}