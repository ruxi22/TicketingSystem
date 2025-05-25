package com.ticketing.transport.controller;

import com.ticketing.transport.entity.TransportRoute;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/route-update")
    @SendTo("/topic/routes")
    public TransportRoute routeUpdate(TransportRoute route) {
        // This method handles route update messages from clients
        // and broadcasts the updated route to all subscribers
        return route;
    }
}