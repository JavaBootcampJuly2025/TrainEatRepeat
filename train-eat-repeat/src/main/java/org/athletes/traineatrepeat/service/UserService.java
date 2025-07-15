package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository useRepository;
    private final UserConverter userConverter;

    public UserResponse getUser(String uuid, String jwtToken) {
        // TODO: implement logic related to JWT token in scope of TER-3
        UserDTO userDto = useRepository.getUserById(Long.parseLong(uuid));
        return userConverter.convertToUserResponse(userDto);
    }
}
