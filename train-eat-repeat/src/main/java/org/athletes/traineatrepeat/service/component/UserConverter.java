package org.athletes.traineatrepeat.service.converter;

import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserResponse convertToUserResponse(UserDto userDto) {
        // TODO: introduce logic to parse model from DTO to rest
        return null;
    }
}
