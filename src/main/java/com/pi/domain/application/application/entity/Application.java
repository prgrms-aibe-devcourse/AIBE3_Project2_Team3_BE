package com.pi.domain.application.application.entity;

import com.pi.domain.application.file.entity.ApplicationFile;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}),
        indexes = @Index(name = "idx_applications_created_date", columnList = "createdDate")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Post post;

    @ManyToOne(fetch = LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "application", fetch = LAZY, cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<ApplicationFile> files = new ArrayList<>();

    public Application(Post post, User user, ApplicationStatus status, String content) {
        this.post = post;
        this.user = user;
        this.status = status;
        this.content = content;
    }

    public void modify(ApplicationStatus status, String content) {
        modifyStatus(status, true);
        this.content = content;
    }

    public void modifyStatus(ApplicationStatus status, boolean isApplicant) {
        if (!this.status.canTransitionTo(status, isApplicant)) {
            throw new ServiceException("400-1", "현재 상태(%s)에서 %s로 변경할 수 없습니다.".formatted(this.status, status));
        }
        this.status = status;
    }

    public void checkActorCanRead(User actor, User user) {
        if (isDifferentUser(actor, user)) {
            throw new ServiceException("403-1", "%d번 구직 읽기 권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanModify(User actor, User user) {
        if (isDifferentUser(actor, user)) {
            throw new ServiceException("403-3", "%d번 구직 수정 권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanDelete(User actor) {
        if (isNotOwner(actor)) {
            throw new ServiceException("403-4", "%d번 구직 삭제 권한이 없습니다.".formatted(getId()));
        }
    }

    private boolean isNotOwner(User actor) {
        return !actor.getUsername().equals(this.user.getUsername());
    }

    private boolean isDifferentUser(User actor, User user) {
        return !actor.getUsername().equals(user.getUsername());
    }
}
