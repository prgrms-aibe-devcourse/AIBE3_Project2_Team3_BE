package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByOfferId(Long offerId);

    Optional<ChatRoom> findByApplicationId(Long applicationId);
}
