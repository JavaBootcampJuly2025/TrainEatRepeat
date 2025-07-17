package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MealRecordConverter {

    public MealDTO fromRequestToEntity(MealRecordRequest request, String uuid) {
        return MealDTO.builder()
                .id(UUID.randomUUID().toString())
                .uuid(uuid)
                .foodName(request.foodName())
                .calories(request.caloriesConsumed())
                .carbs(request.carbs())
                .protein(request.protein())
                .fat(request.fat())
                .date(request.date())
                .build();
    }

    public MealRecordResponse toResponse(MealDTO meal) {
        return  MealRecordResponse.builder()
                .id(meal.getId())
                .foodName(meal.getFoodName())
                .calories(meal.getCalories())
                .carbs(meal.getCarbs())
                .protein(meal.getProtein())
                .fat(meal.getFat())
                .date(meal.getDate())
                .build();
    }
}