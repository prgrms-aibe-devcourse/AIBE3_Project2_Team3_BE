package com.pi.domain.review.entity;

import com.pi.domain.contract.entity.Contract;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition ="TEXT", nullable = false)
    private String comment;

    public Review(Contract contract, User user, Integer rating, String comment) {
        this.contract = contract;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    public void modify(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void checkActorCanModify(User actor) {
        if (!actor.isAdmin() && actor.getId() != this.user.getId()) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }
    public void checkActorCanDelete(User actor) {
        if (!actor.isAdmin() && actor.getId() != this.user.getId()) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }
}
