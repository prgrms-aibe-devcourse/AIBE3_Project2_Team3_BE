package com.pi.domain.post.freelancer.entity;

import com.pi.domain.post.file.entity.FreelancerFile;
import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "freelancers")
public class Freelancer {
    @Id
    private Long id;

    private Long salary;
    private Long period;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreelancerFile> files = new ArrayList<>();

    private Freelancer(Post post) {
        setPost(post);
    }

    public static Freelancer of(Post post) {
        return new Freelancer(post);
    }

    public void modify(Long salary, Long period) {
        this.salary = salary;
        this.period = period;
    }

    public void addFreelancerFile(String fileUrl) {
        FreelancerFile freelancerFile = new FreelancerFile(this, fileUrl);
        files.add(freelancerFile);
    }
}
