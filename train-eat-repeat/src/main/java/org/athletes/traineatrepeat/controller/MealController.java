package org.athletes.traineatrepeat.controller;

import static org.athletes.traineatrepeat.common.ValidationCommon.UUID_REGEX;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.service.MealService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealController {

  private final MealService mealService;

  @PostMapping("/submit-food")
  public MealRecordResponse submitMeal(
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid,
      @Valid @RequestBody MealRecordRequest request) {
    return mealService.submitMeal(uuid, request);
  }

  @GetMapping("/meals")
  public List<MealRecordResponse> getMeals(
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid,
      @RequestParam(required = false) TimePeriod timePeriod) {
    return mealService.getMealsForUser(uuid, timePeriod);
  }

  @DeleteMapping("/{id}")
  public void deleteMeal(
      @PathVariable @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String id) {
    mealService.deleteMealById(id);
  }

  @PutMapping("/{id}")
  public MealRecordResponse updateMeal(
      @PathVariable @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String id,
      @Valid @RequestBody MealRecordRequest request) {
    return mealService.updateMeal(id, request);
  }
}
