package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.dto.ChatSendReqBody;
import com.pi.domain.chat.chat.service.ChatService;
import com.pi.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ApiV1ChatStompController {
    private final ChatService chatService;

    @MessageMapping("/rooms.{roomId}.send")
    public void send(@DestinationVariable Long roomId,
                     ChatSendReqBody req,
                     Authentication auth) {
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        chatService.sendMessage(user.getId(), roomId, req.content());
    }
}
