package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private ChatService chatService;

    @MessageMapping("/send")
    @SendTo("/sub/messages")
    public String sendMessage(String message) {
        log.info("메시지 들어옴 : {}", message);
        return message;
    }
}
