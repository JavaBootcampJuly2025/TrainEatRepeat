package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Component;

@Component
public class MealRecordConverter {
  public MealRecordResponse toResponse(MealDTO meal) {
    return MealRecordResponse.builder()
        .id(meal.getId())
        .uuid(meal.getUuid())
        .foodName(meal.getFoodName())
        .calories(meal.getCalories())
        .carbs(meal.getCarbs())
        .protein(meal.getProtein())
        .fat(meal.getFat())
        .weightInGrams(meal.getWeightInGrams())
        .date(meal.getDate())
        .build();
  }
}
