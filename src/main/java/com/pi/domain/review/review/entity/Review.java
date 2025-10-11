package com.pi.domain.review.review.entity;

import com.pi.domain.contract.contract.entity.Contract;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition ="TEXT", nullable = false)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Review(Contract contract, User user, int rating, String comment) {
        this.contract = contract;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    public void modify(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public boolean isOwnedBy(User actor) {
        return this.user.equals(actor);
    }
}
