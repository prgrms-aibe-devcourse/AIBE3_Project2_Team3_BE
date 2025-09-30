package com.pi.domain.project.project.dto;

import com.pi.domain.project.project.entity.Project;

import java.time.LocalDateTime;

public record ProjectDto(
        LocalDateTime deadlineDate,
        LocalDateTime startedDate,
        LocalDateTime endedDate,
        String hirerType,
        String employmentType,
        String salary,
        Integer personnel,
        String skillLevel) {
    public ProjectDto(Project project) {
        this(
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
