package com.pi.domain.offer.offer.entity;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "offers", indexes = {
        @Index(name = "idx_offers_created_date", columnList = "createdDate")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Offer extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Post post;

    @ManyToOne(fetch = LAZY)
    private User user;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'ACCEPTED'")
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @Column(columnDefinition = "INT UNSIGNED DEFAULT 1")
    @Setter
    private int amount = 1;

    public Offer(Post post, User user, int amount) {
        this.post = post;
        this.user = user;
        this.status = OfferStatus.ACCEPTED;
        this.amount = amount;
    }

    public void modifyStatus(OfferStatus status, boolean isOfferUser) {
        if (!this.status.canTransitionTo(status, isOfferUser)) {
            log.warn("{} 상태에서 {}(으)로 변경 불가", this.status, status);
            throw new ServiceException("400-1", "잘못된 요청입니다.");
        }
        this.status = status;
    }

    public void checkActorCanModify(User actor) {
        if (isNotOwner(actor)) {
            log.warn("구인({}) 수정 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanModifyStatus(User actor, User postUser) {
        if (isDifferentUser(actor, postUser)) {
            log.warn("구인({}) 상태 수정 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanDelete(User actor) {
        if (isNotOwner(actor)) {
            log.warn("구인({}) 삭제 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    private boolean isNotOwner(User actor) {
        return !actor.getUsername().equals(this.user.getUsername());
    }

    public boolean isDifferentUser(User actor, User user) {
        return !actor.getUsername().equals(user.getUsername());
    }

    public boolean isSameUser(User actor, User user) {
        return actor.getUsername().equals(user.getUsername());
    }
}
