package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  @GetMapping({"", "/"})
  public String home(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    var user = userRepository.findByEmail(email);
    if (user != null) {
      UserResponse userResponse = userConverter.convertToUserDTO(user);
      model.addAttribute("user", userResponse);
    }
    return "index";
  }
}
