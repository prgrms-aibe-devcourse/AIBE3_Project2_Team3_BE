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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = @UniqueConstraint(name = "uk_application", columnNames = {"post_id", "user_id"}),
        indexes = @Index(name = "idx_application_created_date", columnList = "createdDate")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Application extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Post post;

    @ManyToOne(fetch = LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;

    private long salary;

    private int period;

    @OneToMany(mappedBy = "application", fetch = LAZY, cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<ApplicationFile> files = new ArrayList<>();

    public Application(Post post, User user, ApplicationStatus status, String content) {
        this.post = post;
        this.user = user;
        this.status = status;
        this.content = content;
    }

    public ApplicationFile addApplicationFile(String fileUrl) {
        ApplicationFile applicationFile = new ApplicationFile(this, fileUrl);
        files.add(applicationFile);

        return applicationFile;
    }

    public Optional<ApplicationFile> findApplicationFileById(long fileId) {
        return files.stream()
                .filter(file -> file.getId() == fileId)
                .findFirst();
    }

    public boolean deleteApplicationFile(ApplicationFile applicationFile) {
        if (applicationFile == null) return false;
        return files.remove(applicationFile);
    }

    public void modify(ApplicationStatus status, String content) {
        modifyStatus(status, true);
        this.content = content;
    }

    public void modifyStatus(ApplicationStatus status, boolean isApplicant) {
        if (!this.status.canTransitionTo(status, isApplicant)) {
            log.warn("{} 상태에서 {}(으)로 변경 불가", this.status, status);
            throw new ServiceException("400-1", "잘못된 요청입니다.");
        }
        this.status = status;
    }

    public void checkActorCanRead(User actor, User user) {
        if (isDifferentUser(actor, user)) {
            log.warn("구직({}) 읽기 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanModify(User actor, User user) {
        if (isDifferentUser(actor, user)) {
            log.warn("구직({}) 수정 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    public void checkActorCanDelete(User actor) {
        if (isNotOwner(actor)) {
            log.warn("구직({}) 삭제 권한 없음. 사용자: {}", getId(), actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.".formatted(getId()));
        }
    }

    private boolean isNotOwner(User actor) {
        return !actor.getUsername().equals(this.user.getUsername());
    }

    private boolean isDifferentUser(User actor, User user) {
        return !actor.getUsername().equals(user.getUsername());
    }
}
