package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.entity.Meal;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class MealRecordConverter {

    public Meal fromRequestToEntity(MealRecordRequest request, String uuid) {
        return Meal.builder()
                .id(UUID.randomUUID().toString())
                .userUuid(uuid)
                .foodName(request.foodName())
                .calories(request.caloriesConsumed())
                .carbs(request.carbs())
                .protein(request.protein())
                .fat(request.fat())
                .date(request.date())
                .build();
    }

    public MealRecordResponse toResponse(Meal meal) {
        return new MealRecordResponse(
                meal.getId(),
                meal.getFoodName(),
                meal.getCalories(),
                meal.getCarbs(),
                meal.getProtein(),
                meal.getFat(),
                meal.getDate()
        );
    }
}