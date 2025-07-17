package org.athletes.traineatrepeat.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.entity.User;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserDTO> getAllUsers() {
        List<User> users = useRepository.findAll();
        return users.stream()
                .map(userConverter::convertToUserDTO)
                .collect(Collectors.toList());
    }

}