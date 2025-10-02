package com.pi.domain.question.question.entity;

import com.pi.domain.answer.answer.entity.Answer;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "questions")
public class Question extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Question(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void checkActorCanDelete(User actor) {
        if (actor == null || !actor.equals(this.user)) {
            throw new ServiceException("403-1", "삭제 권한이 없습니다.");
        }
    }

    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void checkActorCanModify(User actor) {
        if (actor == null || !actor.equals(this.user)) {
            throw new ServiceException("403-1", "수정 권한이 없습니다.");
        }
    }


}