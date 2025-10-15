package com.pi.domain.post.freelancer.dto;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.post.freelancer.entity.FreelancerFile;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.user.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record FreelancerDto(
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
        Long salary,
        Long period,
        List<String> fileUrls
) {
    public FreelancerDto(
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
            Long salary,
            Long period
    ) {
        this(
                id,
                createdDate,
                modifiedDate,
                title,
                content,
                isViewed,
                author,
                regions,
                categories,
                skills,
                salary,
                period,
                List.of()
        );
    }

    public FreelancerDto(Post post) {
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
                post.getFreelancer().getSalary(),
                post.getFreelancer().getPeriod(),
                List.of()
        );
    }

    public FreelancerDto(Post post, List<FreelancerFile> files) {
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
                post.getFreelancer().getSalary(),
                post.getFreelancer().getPeriod(),
                files != null
                        ? files.stream().map(FreelancerFile::getUrl).toList()
                        : List.of()
        );
    }
}
