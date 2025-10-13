package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    long countByChatRoomIdAndEndedDateIsNull(Long id);

    boolean existsByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(Long roomId, Long userId);

    Optional<ChatMember> findTopByChatRoom_IdAndUser_IdOrderByIdDesc(Long roomId, Long userId);

    Optional<ChatMember> findByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(Long roomId, Long id);

    Optional<ChatMember> findByChatRoom_IdAndUser_IdAndEndedDateIsNull(Long roomId, Long userId);

    Optional<ChatMember> findByChatRoom_IdAndUser_IdAndStartedDateIsNullAndEndedDateIsNull(Long roomId, Long id);

    List<ChatMember> findByChatRoom_IdAndUser_IdInAndEndedDateIsNull(Long roomId, Set<Long> foundIds);
}
