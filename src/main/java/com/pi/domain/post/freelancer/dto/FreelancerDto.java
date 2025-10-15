package com.pi.domain.post.freelancer.dto;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.post.file.dto.FreelancerFileDto;
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
        Integer viewCount,
        Integer likeCount,
        List<FreelancerFileDto> files
) {
    public FreelancerDto(Post post) {
        this(
                post.getId(),
                post.getCreatedDate(),
                post.getModifiedDate(),
                post.getTitle(),
                post.getContent(),
                post.isViewed(),
                new UserDto(post.getUser()),
                safeList(post.getPostRegions()).stream().map(pr -> new RegionDto(pr.getRegion())).toList(),
                safeList(post.getPostCategories()).stream().map(pc -> new CategoryDto(pc.getCategory())).toList(),
                safeList(post.getPostSkills()).stream().map(ps -> new SkillDto(ps.getSkill())).toList(),
                post.getFreelancer() != null ? post.getFreelancer().getSalary() : null,   // ✅ null 방어
                post.getFreelancer() != null ? post.getFreelancer().getPeriod() : null,   // ✅ null 방어
                post.getViewCount(),
                post.getLikeCount(),
                List.of() // ✅ 파일은 Service에서 주입
        );
    }

    public FreelancerDto(Post post, List<FreelancerFileDto> files) {
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
                post.getViewCount(),
                post.getLikeCount(),
                safeList(files)
        );
    }

    private static <T> List<T> safeList(List<T> src) {
        return src == null ? List.of() : src;
    }
}
