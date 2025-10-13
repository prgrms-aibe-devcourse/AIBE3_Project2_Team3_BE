package com.pi.domain.chat.chat.entity;

import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "started_date") // 입장시간
    private LocalDateTime startedDate;

    @Column(name = "ended_date") // 퇴장시간(현재 참여 중이면 null)
    private LocalDateTime endedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20) // 권한
    private ChatRole role;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    public ChatMember(User user, ChatRole role, LocalDateTime startedDate) {
        this.user = user;
        this.role = role;
        this.startedDate = startedDate;
    }

    /** 초대(PENDING): 아직 입장 안 함 */
    public static ChatMember invited(ChatRoom room, User user, ChatRole role) {
        ChatMember m = new ChatMember(user, role, null);
        room.addMember(m);                // 양방향 일관성 보장
        return m;
    }

    /** 즉시 참여(ACTIVE) */
    public static ChatMember joined(ChatRoom room, User user, ChatRole role) {
        ChatMember m = new ChatMember(user, role, LocalDateTime.now());
        room.addMember(m);                // 양방향 일관성 보장
        return m;
    }

    @Transient
    public MemberStatus getStatus() {
        if (endedDate == null) {
            return (startedDate == null) ? MemberStatus.PENDING : MemberStatus.ACTIVE;
        } return startedDate == null ? MemberStatus.REFUSE : MemberStatus.LEFT;
    }

    /** 수락(초대 → 입장) */
    public void accept() {
        if (this.startedDate == null) this.startedDate = LocalDateTime.now();
    }

    /** 거절 */
    public void refuse() { if (this.endedDate == null) this.endedDate = LocalDateTime.now(); }

    /** 퇴장 */
    public void leave() {
        if (this.endedDate == null) this.endedDate = LocalDateTime.now();
    }

    void setChatRoom(ChatRoom room) { this.chatRoom = room; }
}
