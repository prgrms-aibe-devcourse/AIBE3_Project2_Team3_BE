package com.pi.domain.report.report.entity;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    private String comments;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    public Report(User reporter, User targetUser, Post post, Review review, String comments, ReportType reportType) {
        this.reporter = reporter;
        this.targetUser = targetUser;
        this.post = post;
        this.review = review;
        this.comments = comments;
        this.reportType = reportType;
    }
}
