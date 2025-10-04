package com.pi.domain.post.freelancer.entity;

import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Freelancer {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private Post post;

    private String salary;
    private String period;

    public Freelancer(Post post, String salary, String period) {
        this.post = post;
        this.salary = salary;
        this.period = period;
    }

    public void modify(String salary, String period) {
        this.salary = salary;
        this.period = period;
    }
}
