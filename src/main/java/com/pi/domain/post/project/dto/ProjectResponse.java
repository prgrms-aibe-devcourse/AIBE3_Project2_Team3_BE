package com.pi.domain.post.project.dto;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long id,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String title,
        String content,
        boolean isViewed,
        UserDto author,
        List<RegionDto> regions,
        List<CategoryDto> categories,
        List<SkillDto> skills,
        LocalDateTime deadlineDate,
        LocalDateTime startedDate,
        LocalDateTime endedDate,
        String hirerType,
        String employmentType,
        Long salary,
        Integer personnel,
        Integer skillLevel
) {
    public static ProjectResponse fromPost(Post post) {
        Project project = post.getProject();

        return new ProjectResponse(
                post.getId(),
                post.getCreatedDate(),
                post.getModifiedDate(),
                post.getTitle(),
                post.getContent(),
                post.isViewed(),
                UserDto.from(post.getUser()),
                post.getPostRegions().stream()
                        .map(pr -> new RegionDto(pr.getRegion())).toList(),
                post.getPostCategories().stream()
                        .map(pc -> new CategoryDto(pc.getCategory())).toList(),
                post.getPostSkills().stream()
                        .map(ps -> new SkillDto(ps.getSkill())).toList(),

                // Project 정보
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
