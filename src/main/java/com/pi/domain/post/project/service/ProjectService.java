package com.pi.domain.post.project.service;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectDto;
import com.pi.domain.post.project.dto.ProjectModifyDto;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.post.project.entity.ProjectStatus;
import com.pi.domain.post.project.repository.ProjectQueryRepository;
import com.pi.domain.post.project.repository.ProjectRepository;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
import com.pi.domain.user.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final ProjectQueryRepository projectQueryRepository;

    @Transactional(readOnly = true)
    public long count() {
        return projectRepository.count();
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findByProjectIsNotNullAndId(id).get();
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> searchProjects(ProjectSearchParams condition, Pageable pageable) {
        return projectQueryRepository.searchProjects(condition, pageable);
    }

    @Transactional
    public Post create(User actor, PostWriteDto po, ProjectWriteDto pr, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        Post post = new Post(actor, po.title(), po.content(), po.isViewed());
        postRepository.save(post);  // ID 생성

        Project project = new Project();
        project.setPost(post); // @MapsId로 post.id 복사
        project.changeStatus(ProjectStatus.ONGOING);
        post.setProject(project);

        post.getProject().modify(pr.deadlineDate(), pr.startedDate(), pr.endedDate(), pr.hirerType(), pr.employmentType(), pr.salary(), pr.personnel(), pr.skillLevel());

        addRelations(post, regionIds, categoryIds, skillIds);

        return postRepository.save(post);
    }

    @Transactional
    public Post modify(Post post, PostModifyDto po, ProjectModifyDto pr, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        post.modify(po.title(), po.content(), po.isViewed());
        post.getProject().modify(pr.deadlineDate(), pr.startedDate(), pr.endedDate(), pr.hirerType(), pr.employmentType(), pr.salary(), pr.personnel(), pr.skillLevel());

        post.getPostRegions().clear();
        post.getPostCategories().clear();
        post.getPostSkills().clear();
        postRepository.flush();

        addRelations(post, regionIds, categoryIds, skillIds);

        return postRepository.save(post);
    }

    @Transactional
    public void delete(Post post) {
        if (post.getProject() != null) {
            projectRepository.delete(post.getProject());
        }
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> getMyProjects(User actor, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserAndProjectIsNotNull(actor.getId(), pageable);
        return posts.map(ProjectDto::new);
    }

    // 테스트 용도
    @Transactional(readOnly = true)
    public Post findLatestPost() {
        return postRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException());
    }

    @Transactional
    public void changeStatus(Long id, ProjectStatus status) throws NotFoundException {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        project.changeStatus(status);
    }


    // 연관관계
    private void addRelations(Post post, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {

        post.getPostRegions().clear();
        if (regionIds != null) {
            for (Long regionId : regionIds) {
                Region region = regionRepository.findById(regionId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역 ID: " + regionId));
                post.addRegion(region);
            }
        }

        post.getPostCategories().clear();
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리 ID: " + categoryId));
                post.addCategory(category);
            }
        }

        post.getPostSkills().clear();
        if (skillIds != null) {
            for (Long skillId : skillIds) {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 스킬 ID: " + skillId));
                post.addSkill(skill);
            }
        }

    }
}