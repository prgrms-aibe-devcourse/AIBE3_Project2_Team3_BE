package com.pi.domain.chat.chat.controller;

import com.pi.domain.chat.chat.dto.*;
import com.pi.domain.chat.chat.entity.MemberStatus;
import com.pi.domain.chat.chat.service.ChatService;
import com.pi.domain.user.user.entity.User;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.RsData;
import com.pi.global.util.Ut;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "ApiV1ChatController", description = "API 채팅 컨트롤러")
public class ApiV1ChatRestController {
    private final ChatService chatService;
    private final Rq rq;
    
    @PostMapping("/rooms")
    public RsData<ChatRoomDto> create(
        @Valid @RequestBody ChatCreateReqBody reqBody
    ){
        User actor = rq.getActor();
        ChatRoomDto dto = chatService.createRoom(actor, reqBody);
        return new RsData<>("201-1", "채팅방이 생성되었습니다.", dto);
    }

    @GetMapping("/rooms")
    public PagePayload<ChatRoomDto> list(
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        return Ut.pageMapper.of(chatService.findRoomsForUser(actor, pageable));
    }

    @GetMapping("/rooms/{roomId}")
    public ChatRoomDetailDto get(@PathVariable Long roomId) {
        User actor = rq.getActor();
        return chatService.getRoomDetail(actor, roomId);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public PagePayload<ChatMessageDto> getMessages(
            @PathVariable Long roomId,
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        return Ut.pageMapper.of(chatService.getMessages(actor, roomId, pageable));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public RsData<ChatMessageDto> sendMessage(
            @PathVariable Long roomId,
            @Valid @RequestBody ChatSendReqBody reqBody
    ){
        User actor = rq.getActor();
        ChatMessageDto dto = chatService.sendMessage(actor, roomId, reqBody.content());
        return new RsData<>("201-1", "메시지가 전송되었습니다.", dto);
    }

    @PostMapping("/rooms/{roomId}/invites")
    public RsData<ChatInviteResBody> invite(@PathVariable Long roomId, @RequestBody ChatInviteReqBody reqbody) {
        User actor = rq.getActor();
        return new RsData<>("200-1", "채팅방 초대가 전송되었습니다.", chatService.invite(actor.getId(), roomId, reqbody.inviteeIds()));
    }

    @PostMapping("/rooms/{roomId}/invites/accept")
    public RsData<Void> accept(@PathVariable Long roomId) {
        User actor = rq.getActor();
        chatService.accept(actor.getId(), roomId);
        return new RsData<>("200-1", "채팅방 초대를 수락했습니다.");
    }

    @PostMapping("/rooms/{roomId}/leave")
    public RsData<Void> leave(@PathVariable Long roomId) {
        User actor = rq.getActor();
        chatService.leave(actor.getId(), roomId);
        return new RsData<>("200-1", "채팅방을 나갔습니다.");
    }

    @GetMapping("/rooms/{roomId}/members")
    public PagePayload<ChatMemberDto> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "ALL") String status,
            @ParameterObject @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User actor = rq.getActor();
        MemberStatus filter = MemberStatus.normalize(status);
        return Ut.pageMapper.of(chatService.listMembers(actor, roomId, filter,pageable));
    }
}
