package com.pi.domain.notification.notification.entity;

import com.pi.domain.chat.chat.entity.ChatMessage;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Offer offer;
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMessage chatMessage;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Apply apply;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Contract contract;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Review review;

    private String content;

}
