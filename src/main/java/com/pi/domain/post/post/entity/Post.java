package com.pi.domain.post.post.entity;

import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    boolean isViewed;
    private String title;
    private String content;
}
