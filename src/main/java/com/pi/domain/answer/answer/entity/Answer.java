package com.pi.domain.answer.answer.entity;

import com.pi.domain.question.question.entity.Question;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pi.global.exception.ServiceException;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Column(nullable = false, length = 1000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer(String comment, User user, Question question) {
        this.comment = comment;
        this.user = user;
        this.question = question;
    }

    public void setContent(String comment) {
        this.comment = comment;
    }

    public static void checkActorCanCreate(User actor) {

        if (!actor.isAdmin()) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanModify(User actor) {

        if (!actor.isAdmin() && actor.getId() != this.user.getId()) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanDelete(User actor) {

        if (!actor.isAdmin() && actor.getId() != this.user.getId()) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

}