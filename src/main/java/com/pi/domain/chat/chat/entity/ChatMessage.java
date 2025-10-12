package com.pi.domain.chat.chat.entity;

import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(name = "message_seq", nullable = false)
    private Long messageSeq;

    public ChatMessage(ChatMember sender, ChatRoom room, String content, Long seq) {
        this.chatMember = sender;
        this.chatRoom = room;
        this.content = content;
        this.messageSeq = seq;
    }

    /** 생성 팩토리: 멤버 상태/방 일치 검증 포함 */
    public static ChatMessage create(ChatMember sender, ChatRoom room, String content, long nextSeq) {
        if (sender == null || room == null) throw new ServiceException("400-1", "필수 파라미터가 누락되었습니다.");
        if (sender.getEndedDate() != null) throw new ServiceException("400-2", "채팅방에서 퇴장한 멤버는 메시지를 보낼 수 없습니다.");
        if (!sender.getChatRoom().getId().equals(room.getId()))
            throw new ServiceException("400-3", "멤버가 속한 채팅방과 메시지 채팅방이 일치하지 않습니다.");
        if (content == null || content.isBlank())
            throw new ServiceException("400-4", "메시지 내용은 필수입니다.");
        return new ChatMessage(sender, room, content, nextSeq);
    }
}
