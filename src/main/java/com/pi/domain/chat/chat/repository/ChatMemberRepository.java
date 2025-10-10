package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    Optional<ChatMember> findByChatRoomIdAndUserIdAndEndedDateIsNull(Long roodId, Long userId);
    Optional<ChatMember> findByChatRoomIdAndUser_IdAndEndedDateIsNull(Long chatId, Long userId);
}
