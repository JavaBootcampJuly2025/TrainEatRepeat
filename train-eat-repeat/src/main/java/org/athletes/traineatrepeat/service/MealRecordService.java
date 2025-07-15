package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.dto.response.MealRecordRequest;
import org.athletes.traineatrepeat.dto.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.MealRecord;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
        MealRecord mealRecord = MealRecordConverter.fromRequest(request);
        MealRecord saved = mealRecordRepository.save(mealRecord);
        return MealRecordConverter.toResponse(saved);
    }
}
