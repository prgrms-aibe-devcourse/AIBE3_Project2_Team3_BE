package com.pi.domain.skill.skill.entity;

import com.pi.domain.post.post.entity.PostSkill;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "skills")
public class Skill extends BaseEntity {
    private String name;

    @OneToMany(mappedBy = "skill")
    private List<PostSkill> postSkills = new ArrayList<>();

    public Skill(String name) {
        this.name = name;
    }
}
