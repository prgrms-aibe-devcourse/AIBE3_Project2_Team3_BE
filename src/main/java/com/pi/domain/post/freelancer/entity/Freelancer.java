package com.pi.domain.post.freelancer.entity;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
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
    private boolean isOwner(User actor) {
        return actor.getUsername().equals(post.getUser().getUsername());
    }

    public void checkActorCanReadOffer(User actor) {
        if (!isOwner(actor)) {
            throw new ServiceException("403-1", "구인 조회 권한이 없습니다.");
        }
    }
}
