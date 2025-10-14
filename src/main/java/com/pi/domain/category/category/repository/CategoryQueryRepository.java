package com.pi.domain.category.category.repository;


import com.pi.domain.category.category.entity.Category;
import com.pi.domain.category.category.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository{
    private final JPAQueryFactory query;

    public List<Category> findParentsWithChildrenAll() {
        QCategory p = QCategory.category;
        QCategory c = new QCategory("child");

        return query.selectFrom(p)
                .leftJoin(p.children, c).fetchJoin()
                .where(p.parent.isNull())
                .distinct()
                .orderBy(p.id.desc())
                .fetch();
    }
}
