// src/main/java/com/ticketing/transport/controller/UserController.java
package com.ticketing.transport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import com.ticketing.transport.dto.UserDto;
import com.ticketing.transport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

//    @GetMapping("/user/registration")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("userDto", new UserDto());
//        return "user/registration";
//    }

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute("userDto") UserDto userDto,
//                               BindingResult result,
//                               Model model) {
//        // Check for validation errors
//        if (result.hasErrors()) {
//            return "user/registration";
//        }
//
//        try {
//            // Call your user service to register the user
//            userService.registerUser(userDto);
//            return "redirect:/login?registered";
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//            return "user/registration";
//        }
//    }
}