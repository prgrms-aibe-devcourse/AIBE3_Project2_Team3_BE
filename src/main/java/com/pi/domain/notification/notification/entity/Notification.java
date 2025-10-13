package com.pi.domain.notification.notification.entity;

import com.pi.domain.chat.chat.entity.ChatMessage;
import com.pi.domain.offer.offer.entity.Offer;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    //    테이블 추가 필요
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Apply apply;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Contract contract;
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Setter
    private String content;

    public Notification(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public void addOffer(Offer offer) {
        this.offer = offer;
    }

    public void addChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void addReview(Review review) {
        this.review = review;
    }

//    public void addContract(Contract contract) {
//        this.contract = contract;
//    }
}
