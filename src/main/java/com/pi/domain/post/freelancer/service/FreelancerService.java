package com.pi.domain.post.freelancer.service;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyDto;
import com.pi.domain.post.freelancer.dto.FreelancerWriteDto;
import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.entity.FreelancerFile;
import com.pi.domain.post.freelancer.repository.FreelancerFileRepository;
import com.pi.domain.post.freelancer.repository.FreelancerQueryRepository;
import com.pi.domain.post.post.dto.PostModifyDto;
import com.pi.domain.post.post.dto.PostWriteDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.region.region.entity.Region;
import com.pi.domain.region.region.repository.RegionRepository;
import com.pi.domain.skill.skill.entity.Skill;
import com.pi.domain.skill.skill.repository.SkillRepository;
import com.pi.domain.user.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final FreelancerQueryRepository freelancerQueryRepository;
    private final FreelancerFileRepository freelancerFileRepository;
    private final String fileDir = "./uploads/freelancer/";

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
        Page<Post> posts = postRepository.findByFreelancerIsNotNullAndUser_Id(user.getId(), pageable);
        return posts.map(FreelancerDto::new);
    }

    public Post create(User actor,
                       PostWriteDto p,
                       FreelancerWriteDto f,
                       List<Long> regionIds,
                       List<Long> categoryIds,
                       List<Long> skillIds,
                       List<MultipartFile> files) {
        Post post = new Post(actor, p.title(), p.content(), p.isViewed());
        post.setFreelancer(Freelancer.of(post));
        post.getFreelancer().modify(f.salary(), f.period());
        addRelations(post, regionIds, categoryIds, skillIds);


        if (files != null && !files.isEmpty()) {
            uploadFiles(post, files);
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post modify(Post post,
                       PostModifyDto p,
                       FreelancerModifyDto f,
                       List<Long> regionIds,
                       List<Long> categoryIds,
                       List<Long> skillIds,
                       List<MultipartFile> files) {
        post.modify(p.title(), p.content(), p.isViewed());
        post.getFreelancer().modify(f.salary(), f.period());

        post.getPostRegions().clear();
        post.getPostCategories().clear();
        post.getPostSkills().clear();
        postRepository.flush();
        addRelations(post, regionIds, categoryIds, skillIds);

        if (files != null && !files.isEmpty()) {
            uploadFiles(post, files);
        }

        return postRepository.save(post);
    }

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

    @Transactional(readOnly = true)
    public Page<FreelancerDto> searchFreelancers(ProjectSearchParams condition, Pageable pageable) {
        return freelancerQueryRepository.searchFreelancers(condition, pageable);
    }

    private void uploadFiles(Post post, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            try {
                String savedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(fileDir + savedName);
                dest.getParentFile().mkdirs();
                file.transferTo(dest);

                String url = "/uploads/freelancer/" + savedName;
                freelancerFileRepository.save(new FreelancerFile(url, post));
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
            }
        }
    }

    @Transactional
    public void deleteAllFiles(Post post) {
        List<FreelancerFile> files = freelancerFileRepository.findByPost(post);
        for (FreelancerFile file : files) {
            new File(fileDir + file.getUrl().substring(file.getUrl().lastIndexOf("/") + 1)).delete();
        }
        freelancerFileRepository.deleteByPost(post);
    }

    @Transactional(readOnly = true)
    public List<FreelancerFile> getFilesByPost(Post post) {
        return freelancerFileRepository.findByPost(post);
    }
}
