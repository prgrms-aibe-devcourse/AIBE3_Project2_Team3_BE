package com.pi.domain.chat.chat.entity;

import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Chat_messages")
public class ChatMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_member_id", nullable = false)
    private ChatMember chatMember;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    @Lob
    @Column(name = "content", nullable = false)
    private String content;
}
