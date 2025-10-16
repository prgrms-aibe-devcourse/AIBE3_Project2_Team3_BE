package com.pi.domain.post.freelancer.service;

import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.repository.CategoryRepository;
import com.pi.domain.post.file.dto.FreelancerFileDto;
import com.pi.domain.post.file.entity.FreelancerFile;
import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.freelancer.dto.FreelancerModifyDto;
import com.pi.domain.post.freelancer.dto.FreelancerWriteDto;
import com.pi.domain.post.freelancer.entity.Freelancer;
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
import com.pi.domain.user.user.dto.UserDto;
import com.pi.domain.user.user.entity.User;
import com.pi.global.s3.AwsS3Service;
import com.pi.global.s3.S3KeyParser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final PostRepository postRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final FreelancerQueryRepository freelancerQueryRepository;
    private final AwsS3Service awsS3Service;
    private static final String AWS_S3_DIRECTORY = "freelancer";
    private final S3KeyParser s3KeyParser;

    public Post findById(Long id) {
        return postRepository.findByFreelancerIsNotNullAndId(id).get();
    }
    
    @Transactional(readOnly = true)
    public Page<FreelancerDto> getMyFreelancers(User user, Pageable pageable) {
        Page<Post> posts = postRepository.findByFreelancerIsNotNullAndUser_Id(user.getId(), pageable);
        return posts.map(FreelancerDto::new);
    }

    @Transactional
    public FreelancerDto create(User actor, PostWriteDto p, FreelancerWriteDto f, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds, List<MultipartFile> files) {
        Post post = new Post(actor, p.title(), p.content(), p.isViewed());
        post.setFreelancer(Freelancer.of(post));
        post.getFreelancer().modify(f.salary(), f.period());
        addRelations(post, regionIds, categoryIds, skillIds);

        createFiles(post.getFreelancer(), files);
        Post savedPost = postRepository.save(post);

        List<FreelancerFileDto> fileDtos =
                savedPost.getFreelancer() == null ? List.of() :
                        savedPost.getFreelancer().getFiles().stream()
                                .map(ff -> new FreelancerFileDto(
                                        ff.getId(),
                                        ff.getUrl(),
                                        s3KeyParser.getDecodedFileName(ff.getUrl())
                                ))
                                .toList();

        return new FreelancerDto(savedPost, fileDtos);
    }

    private void createFiles(Freelancer freelancer, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        List<String> fileUrls = awsS3Service.uploadFiles(files, AWS_S3_DIRECTORY);
        fileUrls.forEach(freelancer::addFreelancerFile);
    }

    private void deleteFiles(Freelancer freelancer, List<Long> removeIds) {
        if (removeIds != null && !removeIds.isEmpty()) {
            List<FreelancerFile> toRemove = freelancer.getFiles().stream()
                    .filter(f -> removeIds.contains(f.getId()))
                    .toList();

            for(FreelancerFile file : toRemove) {
                String fileKey = awsS3Service.getDecodedFileKey(file.getUrl());
                awsS3Service.deleteFile(fileKey);
                freelancer.getFiles().remove(file);
            }
        }
    }

    @Transactional
    public FreelancerDto modify(Post post, PostModifyDto p, FreelancerModifyDto f, List<Long> regionIds, List<Long> categoryIds, List<Long> skillIds, List<MultipartFile> files, List<Long> removeFileIds) {
        post.modify(p.title(), p.content(), p.isViewed());
        post.getFreelancer().modify(f.salary(), f.period());

        post.getPostRegions().clear();
        post.getPostCategories().clear();
        post.getPostSkills().clear();
        postRepository.flush();
        addRelations(post, regionIds, categoryIds, skillIds);

        deleteFiles(post.getFreelancer(), removeFileIds);
        createFiles(post.getFreelancer(), files);

        Post savedPost = postRepository.save(post);

        List<FreelancerFileDto> fileDtos =
                savedPost.getFreelancer() == null ? List.of() :
                        savedPost.getFreelancer().getFiles().stream()
                                .map(ff -> new FreelancerFileDto(
                                        ff.getId(),
                                        ff.getUrl(),
                                        s3KeyParser.getDecodedFileName(ff.getUrl())
                                ))
                                .toList();

        return new FreelancerDto(savedPost, fileDtos);
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

    @Transactional(readOnly = true)
    public FreelancerDto getItem(Long id) {
        Post p = freelancerQueryRepository.findDetailBase(id).get();

        var filesMap = freelancerQueryRepository.fetchFreelancerFiles(id);
        var regionsMap = freelancerQueryRepository.fetchRegions(List.of(id));
        var categoriesMap = freelancerQueryRepository.fetchCategories(List.of(id));
        var skillsMap = freelancerQueryRepository.fetchSkills(List.of(id));

        return new FreelancerDto(
                p.getId(),
                p.getCreatedDate(),
                p.getModifiedDate(),
                p.getTitle(),
                p.getContent(),
                p.isViewed(),
                new UserDto(p.getUser()),
                regionsMap.getOrDefault(id, List.of()),
                categoriesMap.getOrDefault(id, List.of()),
                skillsMap.getOrDefault(id, List.of()),
                p.getFreelancer() != null ? p.getFreelancer().getSalary() : null,
                p.getFreelancer() != null ? p.getFreelancer().getPeriod() : null,
                p.getViewCount(),
                p.getLikeCount(),
                filesMap.getOrDefault(id, List.of())
        );
    }
}
