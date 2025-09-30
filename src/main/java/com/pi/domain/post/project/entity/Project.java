package com.pi.domain.post.project.entity;

import com.pi.domain.post.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Project extends Post {
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
