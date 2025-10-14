package com.pi.domain.post.freelancer.service;

import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyDto;
import com.pi.domain.post.freelancer.dto.FreelancerWriteDto;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.repository.FreelancerQueryRepository;
import com.pi.domain.post.freelancer.repository.FreelancerRepository;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.skill.skill.repository.SkillRepository;
import com.pi.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final FreelancerQueryRepository freelancerQueryRepository;

    public long count() {
        return freelancerRepository.count();
    }

    public Post findById(Long id) {
        return postRepository.findByFreelancerIsNotNullAndId(id).get();
    }

    public Page<Post> getPage(Pageable pageable, String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return postRepository.findByFreelancerIsNotNull(pageable);
        }
        return postRepository.findByFreelancerIsNotNullAndTitleContainingIgnoreCase(pageable, searchKeyword);
    }

    @Transactional(readOnly = true)
    public Page<FreelancerDto> getMyFreelancers(User user, Pageable pageable) {
        Page<Freelancer> freelancers = freelancerRepository.findAllByPost_User(user, pageable);
        return freelancers.map(f -> new FreelancerDto(f.getPost()));
    }

    public Post create(User actor, PostWriteDto p, FreelancerWriteDto f, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        Post post = new Post(actor, p.title(), p.content(), p.isViewed());
        post.setFreelancer(Freelancer.of(post));
        post.getFreelancer().modify(f.salary(), f.period());
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

    public Post modify(Post post, PostModifyDto p, FreelancerModifyDto f, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds) {
        post.modify(p.title(), p.content(), p.isViewed());
        post.getFreelancer().modify(f.salary(), f.period());

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

    @Transactional(readOnly = true)
    public Page<Post> search(Pageable pageable,
                             Long categoryId,
                             Long regionId,
                             List<Long> skillIds,
                             String title,
                             Long minSalary,
                             Long maxSalary) {
        return postRepository.searchFreelancers(pageable, categoryId, regionId, skillIds, title, minSalary, maxSalary);
    }

    @Transactional(readOnly = true)
    public Page<FreelancerDto> searchFreelancers(ProjectSearchParams condition, Pageable pageable) {
        return freelancerQueryRepository.searchFreelancers(condition, pageable);
    }
}
