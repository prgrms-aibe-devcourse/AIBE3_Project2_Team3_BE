package com.pi.domain.post.post.repository;

import com.pi.domain.post.freelancer.entity.QFreelancer;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.entity.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<Post> searchFreelancers(Pageable pageable,
                                        Long categoryId,
                                        Long regionId,
                                        List<Long> skillIds,
                                        String title,
                                        Long minSalary,
                                        Long maxSalary) {

        QPost post = QPost.post;
        QFreelancer freelancer = QFreelancer.freelancer;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.freelancer.isNotNull());

        if (categoryId != null) builder.and(post.postCategories.any().category.id.eq(categoryId));
        if (regionId != null) builder.and(post.postRegions.any().region.id.eq(regionId));
        if (skillIds != null && !skillIds.isEmpty()) builder.and(post.postSkills.any().skill.id.in(skillIds));
        if (title != null && !title.isBlank()) builder.and(post.title.containsIgnoreCase(title));
        if (minSalary != null) builder.and(freelancer.salary.goe(minSalary));
        if (maxSalary != null) builder.and(freelancer.salary.loe(maxSalary));

        List<Post> content = query
                .selectFrom(post)
                .join(post.freelancer, freelancer).fetchJoin()
                .where(builder)
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
