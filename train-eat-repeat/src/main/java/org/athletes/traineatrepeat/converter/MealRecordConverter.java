package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MealRecordConverter {

    public MealDTO fromRequest(MealRecordRequest request) {
        return MealDTO.builder()
                .foodName(request.foodName())
                .caloriesConsumed(request.caloriesConsumed())
                .carbs(request.carbs())
                .protein(request.protein())
                .fat(request.fat())
                .date(LocalDate.now())
                .build();
    }

    public MealRecordResponse toResponse(MealDTO mealDTO) {
        return new MealRecordResponse(
                mealDTO.id(),
                mealDTO.foodName(),
                mealDTO.caloriesConsumed(),
                mealDTO.carbs(),
                mealDTO.protein(),
                mealDTO.fat(),
                mealDTO.date()
        );
    }
}