package com.pi.domain.offer.offer.entity;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offers", indexes = {
        @Index(name = "idx_offers_created_date", columnList = "createdDate")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Offer extends BaseEntity {
    @ManyToOne
    private Freelancer freelancer;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    @Setter
    private OfferStatus status;

    private boolean isOwner(User actor) {
        return actor.getUsername().equals(user.getUsername());
    }

    public void checkActorCanRead(User actor) {
        if (!isOwner(actor)) {
            throw new ServiceException("403-1", "%d번 구인 읽기 권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanModify(User actor, User freelancerUser) {
        if (!actor.getUsername().equals(freelancerUser.getUsername())) {
            throw new ServiceException("403-1", "%d번 구인 상태 수정 권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanDelete(User actor) {
        if (!isOwner(actor)) {
            throw new ServiceException("403-1", "%d번 구인 삭제 권한이 없습니다.".formatted(getId()));
        }
    }
}
