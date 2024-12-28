package com.linh.identity_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received message: {}", message);
        return "Hello, " + message + "!"; // Gửi thông điệp dưới dạng chuỗi
    }
}
