package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.stereotype.Component;

@Component
public class TrainingRecordConverter {

    public TrainingDTO fromRequest(TrainingRecordRequest request) {
        //TODO: dto logic
        return null;
    }

    public TrainingRecordResponse toResponse(TrainingDTO trainingDTO){
        return new TrainingRecordResponse(
                //
        );
    }
}