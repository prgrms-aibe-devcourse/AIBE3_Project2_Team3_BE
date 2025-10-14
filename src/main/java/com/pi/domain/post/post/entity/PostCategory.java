package com.pi.domain.post.post.entity;

import com.pi.domain.category.category.entity.Category;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "post_categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_category", columnNames = {"post_id", "category_id"})
)
@Getter
@Setter
public class PostCategory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
