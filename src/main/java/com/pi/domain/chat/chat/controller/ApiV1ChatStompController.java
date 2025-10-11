package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ApiV1ChatStompController {
    private final ChatService chatService;
//
//    @MessageMapping("/rooms.{roomId}.send")
//    public void send(@DestinationVariable Long roomId,
//                     ChatSendReqBody req,
//                     Authentication auth) {
//        SecurityUser user = (SecurityUser) auth.getPrincipal();
//        chatService.sendMessage(user.getId(), roomId, req.content());
//    }
}
