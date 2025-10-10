package com.pi.domain.chat.chat.entity;

import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_members",
        uniqueConstraints = {
                // 한 유저가 같은 방에 동시에 두 번 입장 레코드를 만들지 않도록(퇴장 전 중복 방지)
                @UniqueConstraint(name = "uk_chat_members_room_user_active", columnNames = {"chat_room_id", "user_id", "ended_date"})
        },
        indexes = {
                @Index(name = "idx_chat_members_room", columnList = "chat_room_id"),
                @Index(name = "idx_chat_members_user", columnList = "user_id")
        })
public class ChatMember extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "started_date", nullable = false) // 입장시간
    private LocalDateTime startedDate;

    @Column(name = "ended_date") // 퇴장시간(현재 참여 중이면 null)
    private LocalDateTime endedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20) // 권한
    private ChatRole role;

    public ChatMember(ChatRoom chatRoom, User user, ChatRole role, LocalDateTime startedDate) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.role = role;
        this.startedDate = startedDate;
        chatRoom.getMembers().add(this);
    }
}
