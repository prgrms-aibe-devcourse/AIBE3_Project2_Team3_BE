package com.pi.domain.post.post.entity;

import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    @ManyToOne
    private User user;
    boolean isViewed;
    private String title;
    private String content;

    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
