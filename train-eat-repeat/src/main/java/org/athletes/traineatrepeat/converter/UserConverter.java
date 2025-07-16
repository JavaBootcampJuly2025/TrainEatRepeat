package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserResponse convertToUserResponse(UserDTO userDTO) {
        // TODO: introduce logic to parse model from DTO to rest
        return null;
    }
}
