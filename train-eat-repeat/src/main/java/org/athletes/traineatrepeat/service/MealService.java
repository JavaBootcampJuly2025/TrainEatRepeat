package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRecordRepository mealRecordRepository;
    private final MealRecordConverter mealRecordConverter;

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
        MealDTO mealDTOToSave = mealRecordConverter.fromRequest(request);

        MealDTO mealDTOWithUuid = MealDTO.builder()
                .id(mealDTOToSave.id())
                .userUuid(uuid)
                .foodName(mealDTOToSave.foodName())
                .caloriesConsumed(mealDTOToSave.caloriesConsumed())
                .carbs(mealDTOToSave.carbs())
                .protein(mealDTOToSave.protein())
                .fat(mealDTOToSave.fat())
                .date(mealDTOToSave.date())
                .build();

        MealDTO savedMealDTO = mealRecordRepository.save(mealDTOWithUuid);

        return mealRecordConverter.toResponse(savedMealDTO);
    }
}