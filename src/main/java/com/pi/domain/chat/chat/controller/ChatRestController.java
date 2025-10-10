package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.dto.ChatMessageRes;
import com.pi.domain.chat.chat.dto.JoinRoomReq;
import com.pi.domain.chat.chat.service.ChatService;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/join")
    public void join(@RequestBody JoinRoomReq req, Authentication auth) {
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        chatService.join(user.getId(), req.roomId());
    }

    @PostMapping("/leave")
    public void leave(@RequestBody JoinRoomReq req, Authentication auth) {
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        chatService.leave(user.getId(), req.roomId());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessageRes> history(@PathVariable Long roomId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "50") int size) {
        return chatService.history(roomId, PageRequest.of(page, size));
    }
}
