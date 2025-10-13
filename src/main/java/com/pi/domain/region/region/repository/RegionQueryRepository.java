package com.pi.domain.region.region.repository;

import com.pi.domain.region.region.entity.QRegion;
import com.pi.domain.region.region.entity.Region;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegionQueryRepository {
    private final JPAQueryFactory query;

    public List<Region> findParentsWithChildrenAll() {
        QRegion p = QRegion.region;
        QRegion c = new QRegion("child");

        return query.selectFrom(p)
                .leftJoin(p.children, c).fetchJoin()
                .where(p.parent.isNull())
                .distinct()
                .orderBy(p.id.desc())
                .fetch();
    }
}
