package org.athletes.traineatrepeat.service;

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

    private final UserRepository useRepository;
    private final UserConverter userConverter;

    public UserDTO getUser(String uuid, String jwtToken) {
        // TODO: JWT validation
        User user = useRepository.getUserByUuid(uuid);
        return userConverter.convertToUserDTO(user);
    }

}