package com.pi.domain.post.project.entity;

import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    private Long id;

    @OneToOne
    @Setter
    @MapsId
    @JoinColumn(name = "id")
    private Post post;

    private LocalDateTime deadlineDate;
    private LocalDateTime startedDate;
    private LocalDateTime endedDate;
    private String hirerType;
    private String employmentType;
    private Long salary;
    private Integer personnel;
    private Integer skillLevel;

    private Project(Post post) {
        setPost(post);
    }

    public static Project of(Post post) {
        return new Project(post);
    }

    public void modify(LocalDateTime deadlineDate, LocalDateTime startedDate, LocalDateTime endedDate, String hirerType, String employmentType, Long salary, Integer personnel, Integer skillLevel) {
        this.deadlineDate = deadlineDate;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.hirerType = hirerType;
        this.employmentType = employmentType;
        this.salary = salary;
        this.personnel = personnel;
        this.skillLevel = skillLevel;
    }
}
