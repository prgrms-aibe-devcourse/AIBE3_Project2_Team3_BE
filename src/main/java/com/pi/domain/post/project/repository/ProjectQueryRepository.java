package com.pi.domain.post.project.repository;

import com.pi.domain.post.project.dto.ProjectSearchDto;
import com.pi.domain.post.project.dto.ProjectSearchReqDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pi.domain.post.post.entity.QPost.post;
import static com.pi.domain.post.post.entity.QPostCategory.postCategory;
import static com.pi.domain.post.post.entity.QPostRegion.postRegion;
import static com.pi.domain.post.post.entity.QPostSkill.postSkill;
import static com.pi.domain.post.project.entity.QProject.project;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ProjectSearchDto> searchProjects(ProjectSearchReqDto condition, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 조건 누적
        if (condition.regionIds() != null && !condition.regionIds().isEmpty()) {
            builder.and(postRegion.region.id.in(condition.regionIds()));
        }
        if (condition.categoryIds() != null && !condition.categoryIds().isEmpty()) {
            builder.and(postCategory.category.id.in(condition.categoryIds()));
        }
        if (condition.skillIds() != null && !condition.skillIds().isEmpty()) {
            builder.and(postSkill.skill.id.in(condition.skillIds()));
        }
        if (condition.minSalary() != null) {
            builder.and(project.salary.goe(condition.minSalary()));
        }
        if (condition.maxSalary() != null) {
            builder.and(project.salary.loe(condition.maxSalary()));
        }
        if (condition.keyword() != null && !condition.keyword().isEmpty()) {
            builder.and(
                    post.title.containsIgnoreCase(condition.keyword())
                            .or(post.content.containsIgnoreCase(condition.keyword()))
            );
        }

        // 1️⃣ 실제 데이터 조회
        List<ProjectSearchDto> content = queryFactory
                .select(Projections.constructor(ProjectSearchDto.class,
                        post.id,
                        post.createdDate,
                        post.modifiedDate,
                        post.title,
                        post.content,
                        post.isViewed,
                        project.salary,
                        project.deadlineDate
                ))
                .from(post)
                .join(post.project, project)
                .leftJoin(post.postRegions, postRegion)
                .leftJoin(post.postCategories, postCategory)
                .leftJoin(post.postSkills, postSkill)
                .where(builder)
                .groupBy(post.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        // 2️⃣ Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(post.countDistinct())
                .from(post)
                .join(post.project, project)
                .leftJoin(post.postRegions, postRegion)
                .leftJoin(post.postCategories, postCategory)
                .leftJoin(post.postSkills, postSkill)
                .where(builder);

        // 3️⃣ 결과 반환
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
