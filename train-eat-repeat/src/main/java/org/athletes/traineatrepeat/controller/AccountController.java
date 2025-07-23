package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.RegisterRequest;
import org.athletes.traineatrepeat.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @GetMapping("/register")
  public String register(Model model) {
      return accountService.prepareRegistrationForm(model);
  }

  @PostMapping("/register")
  public String register(@ModelAttribute("registerRequest") RegisterRequest registerDto, BindingResult result, Model model) {
    return accountService.registerUser(registerDto, result, model);
  }
}