package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.entity.User;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.model.response.UserResponse;

import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                //.chronicDiseases(user.getChronicDiseases())
                //.foodPreferences(user.getFoodPreferences())
                .weight(user.getWeight())
                .height(user.getHeight())
                .BMI(user.getBMI())
                .BMR(user.getBMR())
                .role(user.getRole())
                .build();
    }

}