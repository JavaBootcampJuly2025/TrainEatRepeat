package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.dto.response.UserResponseDto;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.model.User;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository useRepository;
    private final UserConverter userConverter;

    public UserResponseDto getUser(String uuid, String jwtToken) {
        // TODO: implement logic related to JWT token in scope of TER-3
        User userDto = useRepository.getUserById(Long.parseLong(uuid));
        return userConverter.convertToUserResponse(userDto);
    }
}
