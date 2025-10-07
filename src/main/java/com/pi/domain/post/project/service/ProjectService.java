package com.pi.domain.post.project.service;

import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectModifyDto;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.skill.skill.repository.SkillRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public Post create(User actor, PostWriteDto po, ProjectWriteDto pr, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        Post post = new Post(actor, po.title(), po.content());
        post.setFreelancer(Freelancer.of(post));
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
