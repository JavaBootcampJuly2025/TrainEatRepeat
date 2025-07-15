package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRecordRepository mealRecordRepository;
    private final UserConverter userConverter;

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
//        MealDTO mealDTO = MealRecordConverter.fromRequest(request);
//        MealDTO saved = mealRecordRepository.save(mealDTO);
//        return MealRecordConverter.toResponse(saved);
        return null;
    }
}
