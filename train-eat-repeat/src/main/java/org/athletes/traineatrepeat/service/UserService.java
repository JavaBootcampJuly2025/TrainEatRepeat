package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UseRepository;
import org.athletes.traineatrepeat.service.converter.UserConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UseRepository useRepository;
    private final UserConverter userConverter;

    public UserResponse getUser(String uuid, String jwtToken) {
        // TODO: implement logic related to JWT token in scope of TER-3
        var userDto = useRepository.getUserById(uuid);
        return userConverter.convertToUserResponse(userDto);
    }
}
