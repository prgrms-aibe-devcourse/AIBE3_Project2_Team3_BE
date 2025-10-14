package com.pi.domain.post.freelancer.entity;

import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
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
}
