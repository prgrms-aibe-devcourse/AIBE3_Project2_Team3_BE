package com.pi.domain.review.entity;

import com.pi.domain.user.user.entity.User;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition ="TEXT", nullable = false)
    private String comment;

    public Review(User user, Integer rating, String comment) {
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }
}
