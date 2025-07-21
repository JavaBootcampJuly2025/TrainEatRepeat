package org.athletes.traineatrepeat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.RegisterRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
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
        RegisterRequest registerRequest = RegisterRequest.builder().build();
        model.addAttribute("registerRequest", registerRequest);
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerDto,
            BindingResult result
    ) {
        accountService.registerUser(registerDto, result);

        if (result.hasErrors()) {
            return "register";
        }

        return "redirect:/login";
    }
}