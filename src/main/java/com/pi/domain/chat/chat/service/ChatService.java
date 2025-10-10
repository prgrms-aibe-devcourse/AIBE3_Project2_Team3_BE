package com.pi.domain.chat.chat.service;

import com.pi.domain.chat.chat.dto.ChatMessageRes;
import com.pi.domain.chat.chat.entity.ChatMember;
import com.pi.domain.chat.chat.entity.ChatMessage;
import com.pi.domain.chat.chat.entity.ChatRoom;
import com.pi.domain.chat.chat.repository.ChatMemberRepository;
import com.pi.domain.chat.chat.repository.ChatMessageRepository;
import com.pi.domain.chat.chat.repository.ChatRoomRepository;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @Transactional
    public ChatMessageRes sendMessage(Long userId, Long roomId, String content) {
        ChatMember member = chatMemberRepository.findByChatRoomIdAndUser_IdAndEndedDateIsNull(roomId, userId)
                .orElseThrow(() -> new ServiceException("401-1", "방 참여자가 아닙니다."));
        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatMember(member)
                        .chatRoom(member.getChatRoom())
                        .content(content)
                        .build()
        );
        ChatMessageRes dto = new ChatMessageRes(
                saved.getId(),
                roomId,
                member.getUser().getId(),
                saved.getContent(),
                saved.getCreatedDate()
        );
        messagingTemplate.convertAndSend("/sub/rooms" + roomId, dto);
        return dto;
    }

    @Transactional
    public void join(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        chatMemberRepository.findByChatRoomIdAndUser_IdAndEndedDateIsNull(roomId, userId)
                .ifPresent(m -> { throw new IllegalStateException("이미 참여 중입니다."); });

        ChatMember m = ChatMember.builder()
                .chatRoom(room)
                .user(room.getMembers().isEmpty()
                        ? userService.getReferenceById(userId) // 예시: 필요 시 로딩. 실제로는 userService.getReferenceById(userId) 형태로 주입
                        : null)
                .startedDate(LocalDateTime.now())
                .role(com.pi.domain.chat.chat.entity.ChatRole.MEMBER)
                .build();

        // user 참조는 반드시 userService.getReferenceById(userId)로 대체하십시오.
        chatMemberRepository.save(m);
    }

    @Transactional
    public void leave(Long userId, Long roomId) {
        ChatMember m = chatMemberRepository
                .findByChatRoomIdAndUser_IdAndEndedDateIsNull(roomId, userId)
                .orElseThrow(() -> new ServiceException("401-1", "참여 기록이 없습니다."));
        m.setEndedDate(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageRes> history(Long roomId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedDateAsc(roomId, pageable)
                .map(m -> new ChatMessageRes(
                        m.getId(), roomId, m.getChatMember().getUser().getId(),
                        m.getContent(), m.getCreatedDate()
                ));
    }
}
