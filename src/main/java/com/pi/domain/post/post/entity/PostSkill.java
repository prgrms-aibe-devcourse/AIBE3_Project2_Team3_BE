package com.pi.domain.post.post.entity;

import com.pi.domain.admin.skill.entity.Skill;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "post_skills",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_skill",
                columnNames = {"post_id", "skill_id"}
        )
)
@Getter
@Setter
public class PostSkill extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
