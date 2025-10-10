package com.pi.domain.post.project.service;

import com.pi.domain.admin.category.repository.CategoryRepository;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectModifyDto;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.post.project.repository.ProjectRepository;
import com.pi.domain.admin.region.repository.RegionRepository;
import com.pi.domain.admin.skill.skill.repository.SkillRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public long count() {
        return projectRepository.count();
    }

    public Post findById(Long id) {
        return postRepository.findByProjectIsNotNullAndId(id).get();
    }

    public Page<Post> getPage(Pageable pageable, String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return postRepository.findByProjectIsNotNull(pageable);
        }
        return postRepository.findByProjectIsNotNullAndTitleContainingIgnoreCase(pageable, searchKeyword);
    }

    public Post create(User actor, PostWriteDto po, ProjectWriteDto pr, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        Post post = new Post(actor, po.title(), po.content());
        post.setProject(Project.of(post));
        post.getProject().modify(pr.deadlineDate(), pr.startedDate(), pr.endedDate(), pr.hirerType(), pr.employmentType(), pr.salary(), pr.personnel(), pr.skillLevel());

        if (regionIds != null) {
            for (Long regionId : regionIds) {
                post.addRegion(regionRepository.findById(regionId).get());
            }
        }

        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                post.addCategory(categoryRepository.findById(categoryId).get());
            }
        }

        if (skillIds != null) {
            for (Long skillId : skillIds) {
                post.addSkill(skillRepository.findById(skillId).get());
            }
        }

        return postRepository.save(post);
    }

    public Post modify(Post post, PostModifyDto po, ProjectModifyDto pr, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        post.modify(po.title(), po.content(), po.isViewed());
        post.getProject().modify(pr.deadlineDate(), pr.startedDate(), pr.endedDate(), pr.hirerType(), pr.employmentType(), pr.salary(), pr.personnel(), pr.skillLevel());

        post.getPostRegions().clear();
        if (regionIds != null) {
            for (Long regionId : regionIds) {
                post.addRegion(regionRepository.findById(regionId).get());
            }
        }

        post.getPostCategories().clear();
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                post.addCategory(categoryRepository.findById(categoryId).get());
            }
        }

        post.getPostSkills().clear();
        if (skillIds != null) {
            for (Long skillId : skillIds) {
                post.addSkill(skillRepository.findById(skillId).get());
            }
        }

        return postRepository.save(post);
    }
}
