package com.pi.domain.review.review.entity;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "reviews")
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition ="TEXT", nullable = false)
    private String comment;

    public Review(Post post, User user, int rating, String comment) {
        this.post = post;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    public void modify(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public boolean isOwnedBy(User actor) {
        return this.user.getId().equals(actor.getId());
    }
}
