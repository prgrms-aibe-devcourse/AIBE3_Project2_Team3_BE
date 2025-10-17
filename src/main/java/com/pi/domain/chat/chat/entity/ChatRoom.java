package com.pi.domain.chat.chat.entity;

import com.pi.domain.application.application.entity.Application;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_rooms",
        indexes = {
                @Index(name = "idx_chat_rooms_name", columnList = "name")
        })
public class ChatRoom extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100) // 채팅방 이름
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Offer offer;

    @OneToOne(fetch = FetchType.LAZY)
    private Application application;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatRoom(String name) {
        setName(name);
    }

    public static ChatRoom create(String name) {
        return new ChatRoom(name);
    }

    public void rename(String newName) {
        setName(newName);
    }

    public void addMember(ChatMember member) {
        // 중복 방지 등 도메인 규칙 체크 가능
        if (!this.members.contains(member)) {
            this.members.add(member);
            member.setChatRoom(this); // 역방향 세팅
        }
    }

    public void removeMember(ChatMember member) {
        if (this.members.remove(member)) {
            member.setChatRoom(null);
        }
    }

    // 편의 규칙
    public boolean hasMember(Long userId) {
        return members.stream().anyMatch(m -> m.getUser().getId().equals(userId)
                && m.getEndedDate() == null);
    }

    public long getActiveMemberCount() {
        return members.stream().filter(m -> m.getEndedDate() == null).count();
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) throw new ServiceException("400-1", "name은 필수입니다.");
        if (name.length() > 100) throw new ServiceException("400-2", "name은 100자 이내로 입력해주세요.");
        this.name = name;
    }
}
