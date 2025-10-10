package com.pi.domain.contract.contract.entity;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Contract extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User hirerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private User talentUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    public Contract(Post post, User hirerUser, User talentUser, ContractStatus status) {
        this.post = post;
        this.hirerUser = hirerUser;
        this.talentUser = talentUser;
        this.status = status;
    }

    public void changeStatus(ContractStatus newStatus) {
        this.status = newStatus;
    }
}
