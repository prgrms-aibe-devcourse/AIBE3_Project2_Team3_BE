package com.pi.domain.freelancer.repository;

import com.pi.domain.category.category.entity.QCategory;
import com.pi.domain.post.freelancer.entity.Freelancer
import com.pi.domain.post.post.entity.QPost;
import com.pi.domain.post.freelancer.entity.QFreelancer;
import com.pi.domain.post.freelancer.repository.FreelancerRepositoryCustom;
import com.pi.domain.region.region.entity.QRegion;
import com.pi.domain.skill.skill.entity.QSkill;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class FreelancerRepositoryImpl implements FreelancerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Freelancer> searchFreelancers(
            String category,
            String region,
            String skill,
            String title,
            Integer minSalary,
            Integer maxSalary,
            Pageable pageable
    ) {
        QFreelancer freelancer = QFreelancer.freelancer;
        QPost post = QPost.post;
        QCategory categoryEntity = QCategory.category;
        QRegion regionEntity = QRegion.region;
        QSkill skillEntity = QSkill.skill;

        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && !category.isEmpty()) builder.and(categoryEntity.name.containsIgnoreCase(category));
        if (region != null && !region.isEmpty()) builder.and(regionEntity.name.containsIgnoreCase(region));
        if (skill != null && !skill.isEmpty()) builder.and(skillEntity.name.containsIgnoreCase(skill));
        if (title != null && !title.isEmpty()) builder.and(post.title.containsIgnoreCase(title));
        if (minSalary != null) builder.and(freelancer.salary.goe(minSalary));
        if (maxSalary != null) builder.and(freelancer.salary.loe(maxSalary));

        List<Freelancer> results = queryFactory
                .selectFrom(freelancer)
                .join(freelancer.post, post).fetchJoin()
                .leftJoin(freelancer.category, categoryEntity).fetchJoin()
                .leftJoin(freelancer.region, regionEntity).fetchJoin()
                .leftJoin(freelancer.skill, skillEntity).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(freelancer.id.desc())
                .fetch();

        long total = queryFactory
                .select(freelancer.count())
                .from(freelancer)
                .join(freelancer.post, post)
                .leftJoin(freelancer.category, categoryEntity)
                .leftJoin(freelancer.region, regionEntity)
                .leftJoin(freelancer.skill, skillEntity)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
