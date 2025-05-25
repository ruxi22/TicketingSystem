package com.ticketing.transport.controller;

import com.ticketing.transport.entity.TravelCard;
import com.ticketing.transport.service.TravelCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
public class TravelCardController {
    @Autowired
    private TravelCardService travelCardService;


    @PostMapping("/user/travelcard/create")
    public String createTravelCard(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        travelCardService.createForUsername(username);
        return "redirect:/user/travelcard";
    }

    @PostMapping("/user/travelcard/add-money")
    public String addMoney(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("amount") BigDecimal amount) {
        String username = userDetails.getUsername();
        travelCardService.addMoney(username, amount);
        return "redirect:/user/travelcard";
    }

    @GetMapping("/user/travelcard")
    public String viewTravelCard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        TravelCard card = travelCardService.getByUsername(username)
                .orElseGet(() -> travelCardService.createForUsername(username));
        model.addAttribute("travelCard", card);
        return "user/travelcard";
    }
}