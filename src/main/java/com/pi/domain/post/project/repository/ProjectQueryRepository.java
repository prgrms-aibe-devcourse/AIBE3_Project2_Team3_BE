package com.pi.domain.post.project.repository;

import com.pi.domain.category.category.dto.CategoryDto;
import com.pi.domain.category.category.entity.QCategory;
import com.pi.domain.post.project.dto.ProjectDto;
import com.pi.domain.post.project.dto.ProjectSearchReqDto;
import com.pi.domain.region.region.dto.RegionDto;
import com.pi.domain.region.region.entity.QRegion;
import com.pi.domain.skill.skill.dto.SkillDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.stream.Collectors;

import static com.pi.domain.category.category.entity.QCategory.category;
import static com.pi.domain.post.post.entity.QPost.post;
import static com.pi.domain.post.post.entity.QPostCategory.postCategory;
import static com.pi.domain.post.post.entity.QPostRegion.postRegion;
import static com.pi.domain.post.post.entity.QPostSkill.postSkill;
import static com.pi.domain.post.project.entity.QProject.project;
import static com.pi.domain.region.region.entity.QRegion.region;
import static com.pi.domain.skill.skill.entity.QSkill.skill;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 1단계: 단일 엔티티 필드만 가진 내부 DTO (fetch join 금지용)
     */
    @Getter
    @AllArgsConstructor
    public static class ProjectSimpleDto {
        private Long id;
        private String title;
        private String content;
        private Long salary;
        private LocalDateTime deadlineDate;
        private LocalDateTime startedDate;
        private LocalDateTime endedDate;
        private String hirerType;
        private String employmentType;
    }

    public Page<ProjectDto> searchProjects(ProjectSearchReqDto condition, Pageable pageable) {

        // [1] Step 1 — 단순 필드만 SELECT
        List<ProjectSimpleDto> basicList = queryFactory
                .select(Projections.constructor(ProjectSimpleDto.class,
                        post.id,
                        post.title,
                        post.content,
                        project.salary,
                        project.deadlineDate,
                        project.startedDate,
                        project.endedDate,
                        project.hirerType,
                        project.employmentType
                ))
                .from(post)
                .join(post.project, project)
                .where(
                        keywordContains(condition.keyword()),
                        salaryBetween(condition.minSalary(), condition.maxSalary())
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (basicList.isEmpty()) {
            return Page.empty(pageable);
        }

        // [2] Step 2 — ID 리스트로 연관 데이터 batch 조회
        List<Long> postIds = basicList.stream().map(ProjectSimpleDto::getId).toList();

        Map<Long, List<RegionDto>> regionsMap = fetchRegions(postIds);
        Map<Long, List<CategoryDto>> categoriesMap = fetchCategories(postIds);
        Map<Long, List<SkillDto>> skillsMap = fetchSkills(postIds);

        // [3] Step 3 — ProjectDto 조립
        List<ProjectDto> result = basicList.stream()
                .map(p -> new ProjectDto(
                        p.getId(),
                        null, null, // createdDate, modifiedDate (원하면 post에서 조인해서 포함 가능)
                        p.getTitle(),
                        p.getContent(),
                        false,
                        null, // author
                        regionsMap.getOrDefault(p.getId(), List.of()),
                        categoriesMap.getOrDefault(p.getId(), List.of()),
                        skillsMap.getOrDefault(p.getId(), List.of()),
                        p.getDeadlineDate(),
                        p.getStartedDate(),
                        p.getEndedDate(),
                        p.getHirerType(),
                        p.getEmploymentType(),
                        p.getSalary(),
                        null,
                        null
                ))
                .toList();

        // [4] Step 4 — count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(post.countDistinct())
                .from(post)
                .join(post.project, project)
                .where(
                        keywordContains(condition.keyword()),
                        salaryBetween(condition.minSalary(), condition.maxSalary())
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    // 🔹 서브 조회: 지역 리스트
    private Map<Long, List<RegionDto>> fetchRegions(List<Long> postIds) {
        QRegion parentRegion = new QRegion("parentRegion");
        List<Tuple> tuples = queryFactory
                .select(postRegion.post.id, region.id, region.name, region.parent.id)
                .from(postRegion)
                .join(postRegion.region, region)
                .leftJoin(region.parent, parentRegion)
                .where(postRegion.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(postRegion.post.id),
                        Collectors.mapping(
                                t -> new RegionDto(t.get(region.id), t.get(region.name), t.get(region.parent.id)),
                                Collectors.toList()
                        )
                ));
    }

    // 🔹 서브 조회: 카테고리 리스트
    private Map<Long, List<CategoryDto>> fetchCategories(List<Long> postIds) {
        QCategory parentCategory = new QCategory("parentCategory");
        List<Tuple> tuples = queryFactory
                .select(postCategory.post.id, category.id, category.name, category.parent.id)
                .from(postCategory)
                .join(postCategory.category, category)
                .leftJoin(category.parent, parentCategory)
                .where(postCategory.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(postCategory.post.id),
                        Collectors.mapping(
                                t -> new CategoryDto(t.get(category.id), t.get(category.name), t.get(category.parent.id)),
                                Collectors.toList()
                        )
                ));
    }

    // 🔹 서브 조회: 스킬 리스트
    private Map<Long, List<SkillDto>> fetchSkills(List<Long> postIds) {
        List<Tuple> tuples = queryFactory
                .select(postSkill.post.id, skill.id, skill.name)
                .from(postSkill)
                .join(postSkill.skill, skill)
                .where(postSkill.post.id.in(postIds))
                .fetch();

        return tuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(postSkill.post.id),
                        Collectors.mapping(
                                t -> new SkillDto(t.get(skill.id), t.get(skill.name)),
                                Collectors.toList()
                        )
                ));
    }

    // 🔸 조건 메서드들
    private BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null
                : post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression salaryBetween(Long min, Long max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return project.salary.between(min, max);
        if (min != null) return project.salary.goe(min);
        return project.salary.loe(max);
    }
}