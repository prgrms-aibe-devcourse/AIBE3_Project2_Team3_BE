package com.pi.domain.post.freelancer.entity;

import com.pi.domain.post.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Freelancer {

    @Id
    private Long id;

    private String salary;
    private String period;

    @OneToOne
    @MapsId
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
