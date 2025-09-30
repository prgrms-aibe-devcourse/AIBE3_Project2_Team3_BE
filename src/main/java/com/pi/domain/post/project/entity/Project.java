package com.pi.domain.post.project.entity;

import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Project {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Post post;

    private LocalDateTime deadlineDate;
    private LocalDateTime startedDate;
    private LocalDateTime endedDate;
    private String hirerType;
    private String employmentType;
    private String salary;
    private Integer personnel;
    private String skillLevel;
}
