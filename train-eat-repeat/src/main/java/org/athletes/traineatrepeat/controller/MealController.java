package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/todays-meals")
    public List<MealRecordResponse> getTodaysMeals(@RequestParam String uuid) {
        return mealService.getTodaysMealsForUser(uuid);
    }

    @GetMapping("/week-meals")
    public List<MealRecordResponse> getThisWeeksMeals(@RequestParam String uuid) {
        return mealService.getThisWeeksMeals(uuid);
    }

    @GetMapping("/month-meals")
    public List<MealRecordResponse> getThisMonthsMeals(@RequestParam String uuid) {
        return mealService.getThisMonthsMeals(uuid);
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
