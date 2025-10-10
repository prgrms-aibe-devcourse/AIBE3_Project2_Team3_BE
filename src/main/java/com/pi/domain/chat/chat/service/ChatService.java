package com.pi.domain.chat.chat.service;

import com.pi.domain.chat.chat.dto.ChatMessageRes;
import com.pi.domain.chat.chat.entity.ChatMember;
import com.pi.domain.chat.chat.entity.ChatMessage;
import com.pi.domain.chat.chat.entity.ChatRole;
import com.pi.domain.chat.chat.entity.ChatRoom;
import com.pi.domain.chat.chat.repository.ChatMemberRepository;
import com.pi.domain.chat.chat.repository.ChatMessageRepository;
import com.pi.domain.chat.chat.repository.ChatRoomRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
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
    private final Rq rq;

    @Transactional
    public ChatMessageRes sendMessage(Long userId, Long roomId, String content) {
        ChatMember member = chatMemberRepository.findByChatRoom_IdAndUser_IdAndEndedDateIsNull(roomId, userId)
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
        messagingTemplate.convertAndSend("/sub/rooms." + roomId, dto);
        return dto;
    }

    @Transactional
    public Long join(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        chatMemberRepository.findByChatRoom_IdAndUser_IdAndEndedDateIsNull(roomId, userId)
                .ifPresent(m -> { throw new IllegalStateException("이미 참여 중입니다."); });
        User actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-0", "로그인이 필요합니다.");
        }


        return chatMemberRepository
                .findByChatRoom_IdAndUser_IdAndEndedDateIsNull(roomId, actor.getId())
                .map(ChatMember::getId)
                .orElseGet(() -> {
                    ChatMember m = ChatMember.builder()
                            .chatRoom(room)
                            .user(actor)                    // ★ 반드시 세팅!
                            .role(ChatRole.MEMBER)
                            .startedDate(LocalDateTime.now())
                            .build();
                    chatMemberRepository.save(m);
                    return m.getId();
                });
    }

    @Transactional
    public void leave(Long userId, Long roomId) {
        ChatMember m = chatMemberRepository
                .findByChatRoom_IdAndUser_IdAndEndedDateIsNull(roomId, userId)
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
