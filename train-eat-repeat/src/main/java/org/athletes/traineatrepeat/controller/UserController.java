package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.dto.response.UserResponseDto;
import org.athletes.traineatrepeat.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/user-data")
    public UserResponseDto getUserData(@RequestParam String uuid, @RequestHeader String jwtToken) {
        return userService.getUser(uuid, jwtToken);
    }
}
