package com.pi.domain.post.project.service;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectModifyDto;
import com.pi.domain.post.project.dto.ProjectResponse;
import com.pi.domain.post.project.dto.ProjectWriteDto;
import com.pi.domain.post.project.entity.Project;
import com.pi.domain.post.project.entity.ProjectStatus;
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

    //TODO: 현재 개수,id로 찾기, 페이지 가져오기, 생성, 수정 메서드 추가 기능 :프로젝트 조회수 관리,프로젝트 상태 관리,프로젝트 필터링
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

        addRelations(post, regionIds, categoryIds, skillIds);

        return postRepository.save(post);
    }

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

    public void delete(Post post) {
        if (post.getProject() != null) {
            projectRepository.delete(post.getProject());
        }
        postRepository.delete(post);
    }

    // 테스트 용도
    public Post findLatestPost() {
        return postRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    }

    @Transactional
    public void changeStatus(Long id, ProjectStatus status) throws NotFoundException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        project.changeStatus(status);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> searchProjects(ProjectStatus status, String region, String keyword) {
        List<Post> posts = postRepository.findByProjectIsNotNull();

        return posts.stream()
                .filter(post -> (status == null || post.getProject().getStatus() == status) &&
                        (region == null || post.getPostRegions().stream().anyMatch(pr -> pr.getRegion().getName().equalsIgnoreCase(region))) &&
                        (keyword == null || post.getTitle().toLowerCase().contains(keyword.toLowerCase()) || post.getContent().toLowerCase().contains(keyword.toLowerCase()))
                )
                .map(ProjectResponse::fromPost)
                .toList();
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