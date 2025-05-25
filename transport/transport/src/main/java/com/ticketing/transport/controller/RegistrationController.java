package com.ticketing.transport.controller;

import com.ticketing.transport.dto.UserRegistrationDto;
import com.ticketing.transport.entity.User;
import com.ticketing.transport.entity.Role;
import com.ticketing.transport.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/registration";

    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") UserRegistrationDto dto, Model model) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "user/registration";

        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}
