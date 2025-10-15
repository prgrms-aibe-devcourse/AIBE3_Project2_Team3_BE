package com.pi.domain.post.file.entity;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.post.entity.Post;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "freelancer_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerFile extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Freelancer freelancer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String url;

    private String originalName;
}

