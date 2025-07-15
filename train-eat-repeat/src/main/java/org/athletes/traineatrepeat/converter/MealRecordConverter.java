package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.dto.response.MealRecordRequest;
import org.athletes.traineatrepeat.dto.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.MealRecord;

public class MealRecordConverter {

    public static MealRecord fromRequest(MealRecordRequest request) {
        // TODO: introduce logic to parse model from request
        return new MealRecord();
    }

    public static MealRecordResponse toResponse(MealRecord mealRecord) {
        return new MealRecordResponse(
            // TODO: introduce logic to make MealRecordResponse
        );
    }
}

