package com.pi.domain.post.project.dto;

import com.pi.domain.post.project.entity.Project;

import java.time.LocalDateTime;

public record ProjectDto(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String title,
        String content,
        boolean isViewed,
        LocalDateTime deadlineDate,
        LocalDateTime startedDate,
        LocalDateTime endedDate,
        String hirerType,
        String employmentType,
        String salary,
        Integer personnel,
        String skillLevel
) {
    public ProjectDto(Project project) {
        this(
                project.getId(),
                project.getPost().getCreatedDate(),
                project.getPost().getModifiedDate(),
                project.getPost().getTitle(),
                project.getPost().getContent(),
                project.getPost().isViewed(),
                project.getDeadlineDate(),
                project.getStartedDate(),
                project.getEndedDate(),
                project.getHirerType(),
                project.getEmploymentType(),
                project.getSalary(),
                project.getPersonnel(),
                project.getSkillLevel()
        );
    }
}
