package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.dto.ChatMessageResBody;
import com.pi.domain.chat.chat.dto.JoinRoomReqBody;
import com.pi.domain.chat.chat.service.ChatService;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ApiV1ChatRestController {
    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/join")
    public void join(@RequestBody JoinRoomReqBody req, Authentication auth) {
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        chatService.join(user.getId(), req.roomId());
    }

    @PostMapping("/leave")
    public void leave(@RequestBody JoinRoomReqBody req, Authentication auth) {
        SecurityUser user = (SecurityUser) auth.getPrincipal();
        chatService.leave(user.getId(), req.roomId());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessageResBody> history(@PathVariable Long roomId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size) {
        return chatService.history(roomId, PageRequest.of(page, size));
    }
}
