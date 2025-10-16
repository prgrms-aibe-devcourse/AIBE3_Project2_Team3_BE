package com.pi.domain.post.file.entity;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    private String url;
}

