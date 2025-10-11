package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Long findMaxSeqByChatRoom_Id(Long roomId);
    Page<ChatMessage> findByChatRoom_Id(Long roomId, Pageable pageable);
}
