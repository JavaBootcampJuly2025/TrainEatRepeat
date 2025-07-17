package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/user-data")
    public UserResponse getUserData(@RequestParam String uuid, @RequestHeader (value = "jwtToken", required = false) String jwtToken) {
        return userService.getUser(uuid, jwtToken);
    }
    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/test")
    public String test() {
        return "UserController is working";
    }
}