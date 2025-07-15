package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Component;

@Component
public class MealRecordConverter {
    public MealDTO fromRequest(MealRecordRequest request) {
        // TODO: introduce logic to parse model from request
        return null;
    }

    public MealRecordResponse toResponse(MealDTO mealDTO) {
        return new MealRecordResponse(
            // TODO: introduce logic to make MealRecordResponse
        );
    }
}

