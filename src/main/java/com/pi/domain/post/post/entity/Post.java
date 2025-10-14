package com.pi.domain.post.post.entity;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Slf4j
public class Post extends BaseEntity {
    @ManyToOne
    private User user;
    boolean isViewed;
    private String title;
    private String content;

    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Project project;
    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Freelancer freelancer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostRegion> postRegions = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCategory> postCategories = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostSkill> postSkills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private int viewCount = 0;
    private int likeCount = 0;

    public Post(User user, String title, String content, boolean isViewed) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isViewed = isViewed;
    }

    public void modify(String title, String content, Boolean isViewed) {
        this.title = title;
        this.content = content;
        this.isViewed = isViewed;
    }

    public void addRegion(Region region) {
        PostRegion link = new PostRegion();
        link.setPost(this);
        link.setRegion(region);
        postRegions.add(link);
    }

    public void removeRegion(Region region) {
        postRegions.removeIf(pr -> {
            boolean match = pr.getRegion().equals(region);
            if (match) pr.setPost(null); // 양방향 정리
            return match;
        });
    }

    public void addCategory(Category category) {
        PostCategory link = new PostCategory();
        link.setPost(this);
        link.setCategory(category);
        postCategories.add(link);
    }

    public void removeCategory(Category category) {
        postCategories.removeIf(pr -> {
            boolean match = pr.getCategory().equals(category);
            if (match) pr.setPost(null); // 양방향 정리
            return match;
        });
    }

    public void addSkill(Skill skill) {
        PostSkill link = new PostSkill();
        link.setPost(this);
        link.setSkill(skill);
        postSkills.add(link);
    }

    public void removeSkill(Skill skill) {
        postSkills.removeIf(pr -> {
            boolean match = pr.getSkill().equals(skill);
            if (match) pr.setPost(null); // 양방향 정리
            return match;
        });
    }

    private boolean isOwner(User actor) {
        return actor.getUsername().equals(user.getUsername());
    }

    private boolean isNotOwner(User actor) {
        return !actor.getUsername().equals(user.getUsername());
    }

    public void checkActorIsNotOwner(User actor) {
        if (isOwner(actor)) {
            log.warn("본인({})이 등록한 게시글({})에 지원 불가", actor.getUsername(), id);
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanReadApplication(User actor) {
        if (isNotOwner(actor)) {
            log.warn("{}번 게시글의 구직 조회 권한 없음. 사용자: {}", id, actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanReadOffer(User actor) {
        if (isNotOwner(actor)) {
            log.warn("{}번 게시글의 구인 조회 권한 없음. 사용자: {}", id, actor.getUsername());
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }


    public void checkActorCanModify(User actor) {
        if (isNotOwner(actor)) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanDelete(User actor) {
        if (isNotOwner(actor)) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

}
