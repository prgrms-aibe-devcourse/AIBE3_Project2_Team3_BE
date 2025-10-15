package com.pi.domain.post.freelancer.repository;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.category.category.entity.QCategory;
import com.pi.domain.post.file.dto.FreelancerFileDto;
import com.pi.domain.post.freelancer.dto.FreelancerDto;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.dto.ProjectSearchParams;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.region.region.entity.QRegion;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.pi.domain.user.user.dto.UserDto;
import com.pi.global.s3.S3KeyParser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pi.domain.category.category.entity.QCategory.category;
import static com.pi.domain.post.file.entity.QFreelancerFile.freelancerFile;
import static com.pi.domain.post.freelancer.entity.QFreelancer.freelancer;
import static com.pi.domain.post.post.entity.QPost.post;
import static com.pi.domain.post.post.entity.QPostCategory.postCategory;
import static com.pi.domain.post.post.entity.QPostRegion.postRegion;
import static com.pi.domain.post.post.entity.QPostSkill.postSkill;
import static com.pi.domain.region.region.entity.QRegion.region;
import static com.pi.domain.skill.skill.entity.QSkill.skill;
import static com.pi.domain.user.user.entity.QUser.user;
import static java.util.stream.Collectors.groupingBy;

@Repository
@RequiredArgsConstructor
public class FreelancerQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final S3KeyParser s3KeyParser;

    @Getter
    @AllArgsConstructor
    public static class FreelancerSimpleDto {
        private Long id;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        private String title;
        private String content;
        private boolean viewed;

        private Long salary;
        private Long period;
        private Integer viewCount;
        private Integer likeCount;

        // author (user)
        private Long authorId;
        private LocalDateTime authorCreatedDate;
        private LocalDateTime authorModifiedDate;
        private String authorNickname;
        private String authorEmail;
        private String authorRole;
        private String authorProfileImageUrl;
    }

    public Page<FreelancerDto> searchFreelancers(ProjectSearchParams condition, Pageable pageable) {

        // 1) 평평한 DTO로 1차 조회 (post + freelancer + user)
        List<FreelancerSimpleDto> basicList = queryFactory
                .select(Projections.constructor(FreelancerSimpleDto.class,
                        post.id,
                        post.createdDate,
                        post.modifiedDate,

                        post.title,
                        post.content,
                        post.isViewed,

                        freelancer.salary,
                        freelancer.period,
                        post.viewCount,
                        post.likeCount,

                        user.id,
                        user.createdDate,
                        user.modifiedDate,
                        user.nickname,
                        user.email,
                        user.role.stringValue(),
                        user.profileImageUrl
                ))
                .from(post)
                .join(post.freelancer, freelancer) // 프리랜서 글만 대상
                .join(post.user, user)
                .where(
                        post.isViewed.isTrue(),
                        keywordContains(condition.keyword()),
                        salaryBetween(condition.minSalary(), condition.maxSalary()), // freelancer.salary 기준
                        anyRegion(condition.regionIds()),
                        anyCategory(condition.categoryIds()),
                        anySkill(condition.skillIds())
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (basicList.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2) 배치 조회용 ID 목록
        List<Long> postIds = basicList.stream().map(FreelancerSimpleDto::getId).toList();

        Map<Long, List<RegionDto>> regionsMap = fetchRegions(postIds);
        Map<Long, List<CategoryDto>> categoriesMap = fetchCategories(postIds);
        Map<Long, List<SkillDto>> skillsMap = fetchSkills(postIds);
        Map<Long, List<FreelancerFileDto>> filesMap = fetchFreelancerFiles(postIds);

        // 3) 최종 DTO 조립
        List<FreelancerDto> result = basicList.stream()
                .map(p -> new FreelancerDto(
                        p.getId(),
                        p.getCreatedDate(),
                        p.getModifiedDate(),
                        p.getTitle(),
                        p.getContent(),
                        p.isViewed(),
                        new UserDto(
                                p.getAuthorId(),
                                p.getAuthorCreatedDate(),
                                p.getAuthorModifiedDate(),
                                p.getAuthorNickname(),
                                p.getAuthorEmail(),
                                p.getAuthorRole(),
                                p.getAuthorProfileImageUrl()
                        ),
                        regionsMap.getOrDefault(p.getId(), List.of()),
                        categoriesMap.getOrDefault(p.getId(), List.of()),
                        skillsMap.getOrDefault(p.getId(), List.of()),
                        p.getSalary(),
                        p.getPeriod(),
                        p.getViewCount(),
                        p.getLikeCount(),
                        filesMap.getOrDefault(p.getId(), List.of())
                ))
                .toList();

        // 4) count 쿼리 (조건 반드시 동일)
        JPAQuery<Long> countQuery = queryFactory
                .select(post.countDistinct())
                .from(post)
                .join(post.freelancer, freelancer)
                .where(
                        post.isViewed.isTrue(),
                        keywordContains(condition.keyword()),
                        salaryBetween(condition.minSalary(), condition.maxSalary()),
                        anyRegion(condition.regionIds()),
                        anyCategory(condition.categoryIds()),
                        anySkill(condition.skillIds())
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    // ---------- 배치 서브쿼리들 ----------

    public Map<Long, List<RegionDto>> fetchRegions(List<Long> postIds) {
        QRegion parentRegion = new QRegion("parentRegion");
        List<Tuple> tuples = queryFactory
                .select(postRegion.post.id, region.id, region.name, region.parent.id)
                .from(postRegion)
                .join(postRegion.region, region)
                .leftJoin(region.parent, parentRegion)
                .where(postRegion.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(groupingBy(
                        t -> t.get(postRegion.post.id),
                        Collectors.mapping(
                                t -> new RegionDto(t.get(region.id), t.get(region.name), t.get(region.parent.id)),
                                Collectors.toList()
                        )
                ));
    }

    public Map<Long, List<CategoryDto>> fetchCategories(List<Long> postIds) {
        QCategory parentCategory = new QCategory("parentCategory");
        List<Tuple> tuples = queryFactory
                .select(postCategory.post.id, category.id, category.name, category.parent.id)
                .from(postCategory)
                .join(postCategory.category, category)
                .leftJoin(category.parent, parentCategory)
                .where(postCategory.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(groupingBy(
                        t -> t.get(postCategory.post.id),
                        Collectors.mapping(
                                t -> new CategoryDto(t.get(category.id), t.get(category.name), t.get(category.parent.id)),
                                Collectors.toList()
                        )
                ));
    }

    public Map<Long, List<SkillDto>> fetchSkills(List<Long> postIds) {
        List<Tuple> tuples = queryFactory
                .select(postSkill.post.id, skill.id, skill.name)
                .from(postSkill)
                .join(postSkill.skill, skill)
                .where(postSkill.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(groupingBy(
                        t -> t.get(postSkill.post.id),
                        Collectors.mapping(
                                t -> new SkillDto(t.get(skill.id), t.get(skill.name)),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<FreelancerFileDto>> fetchFreelancerFiles(List<Long> postIds) {
        List<Tuple> tuples = queryFactory
                .select(post.id, freelancerFile.id, freelancerFile.url)
                .from(post)
                .join(post.freelancer, freelancer)
                .join(freelancer.files, freelancerFile)
                .where(post.id.in(postIds))
                .orderBy(freelancerFile.id.asc())
                .fetch();

        return tuples.stream().collect(groupingBy(
                t -> t.get(post.id),
                Collectors.mapping(t -> new FreelancerFileDto(
                        t.get(freelancerFile.id),
                        t.get(freelancerFile.url),
                        s3KeyParser.getDecodedFileName(t.get(freelancerFile.url))
                ), Collectors.toList())
        ));
    }

    public Map<Long, List<FreelancerFileDto>> fetchFreelancerFiles(Long postId) {
        List<Tuple> tuples = queryFactory
                .select(post.id, freelancerFile.id, freelancerFile.url)
                .from(post)
                .join(post.freelancer, freelancer)
                .join(freelancer.files, freelancerFile)
                .where(post.id.eq(postId))
                .orderBy(freelancerFile.id.asc())
                .fetch();

        return tuples.stream().collect(groupingBy(
                t -> t.get(post.id),
                Collectors.mapping(t -> new FreelancerFileDto(
                        t.get(freelancerFile.id),
                        t.get(freelancerFile.url),
                        s3KeyParser.getDecodedFileName(t.get(freelancerFile.url))
                ), Collectors.toList())
        ));
    }



    // ---------- 조건 메서드 ----------

    private BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null
                : post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression salaryBetween(Long min, Long max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return freelancer.salary.between(min, max);
        if (min != null) return freelancer.salary.goe(min);
        return freelancer.salary.loe(max);
    }

    private BooleanExpression anyRegion(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return JPAExpressions.selectOne()
                .from(postRegion)
                .where(
                        postRegion.post.id.eq(post.id),
                        postRegion.region.id.in(ids)
                )
                .exists();
    }

    private BooleanExpression anyCategory(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return JPAExpressions.selectOne()
                .from(postCategory)
                .where(
                        postCategory.post.id.eq(post.id),
                        postCategory.category.id.in(ids)
                )
                .exists();
    }

    private BooleanExpression anySkill(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return JPAExpressions.selectOne()
                .from(postSkill)
                .where(
                        postSkill.post.id.eq(post.id),
                        postSkill.skill.id.in(ids)
                )
                .exists();
    }
    public Optional<Post> findDetailBase(Long id) {
        Post p = queryFactory
                .selectFrom(post)
                .join(post.freelancer, freelancer).fetchJoin() // toOne
                .join(post.user, user).fetchJoin()             // toOne
                .where(post.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(p);
    }
}
