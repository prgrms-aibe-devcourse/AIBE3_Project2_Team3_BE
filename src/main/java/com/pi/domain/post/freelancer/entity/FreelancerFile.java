package com.pi.domain.post.freelancer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "freelancer_files")
public class FreelancerFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Freelancer freelancer;

    public FreelancerFile(String url, Freelancer freelancer) {
        this.url = url;
        this.freelancer = freelancer;
    }
}
