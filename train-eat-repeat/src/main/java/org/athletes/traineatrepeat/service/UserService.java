package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository useRepository;
    private final UserConverter userConverter;

    public UserResponse getUser(String uuid, String jwtToken) {
        // TODO: JWT validation
        var user = useRepository.getUserByUuid(uuid);
        return userConverter.convertToUserDTO(user);
    }
    public List<UserResponse> getAllUsers() {
        var users = useRepository.findAll();
        return users.stream()
                .map(userConverter::convertToUserDTO)
                .collect(Collectors.toList());
    }

}