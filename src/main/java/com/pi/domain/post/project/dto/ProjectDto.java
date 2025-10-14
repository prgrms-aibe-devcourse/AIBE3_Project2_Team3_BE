package com.pi.domain.post.project.dto;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDto(
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
    public ProjectDto(Post post) {
        this(
                post.getId(),
                post.getCreatedDate(),
                post.getModifiedDate(),
                post.getTitle(),
                post.getContent(),
                post.isViewed(),
                new UserDto(post.getUser()),
                post.getPostRegions().stream().map(pr -> new RegionDto(pr.getRegion())).toList(),
                post.getPostCategories().stream().map(pc -> new CategoryDto(pc.getCategory())).toList(),
                post.getPostSkills().stream().map(ps -> new SkillDto(ps.getSkill())).toList(),
                post.getProject().getDeadlineDate(),
                post.getProject().getStartedDate(),
                post.getProject().getEndedDate(),
                post.getProject().getHirerType(),
                post.getProject().getEmploymentType(),
                post.getProject().getSalary(),
                post.getProject().getPersonnel(),
                post.getProject().getSkillLevel()
        );
    }


}
