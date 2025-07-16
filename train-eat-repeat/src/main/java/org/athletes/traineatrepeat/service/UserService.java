package org.athletes.traineatrepeat.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.entity.User;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserDTO getUser(String uuid, String jwtToken) {
        // TODO: JWT validation
        User user = userRepository.getUserByUuid(uuid);
        return userConverter.convertToUserDTO(user);
    }

}