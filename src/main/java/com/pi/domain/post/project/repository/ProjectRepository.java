package com.pi.domain.post.project.repository;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT pr.post FROM Project pr " +
            "JOIN pr.post p " +
            "LEFT JOIN p.postRegions prg " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " + //  키워드 검색 (Post의 title/content)
            "AND (:region IS NULL OR :region = '' OR prg.region.name = :region) " + //  지역 필터링
            "AND (" + //  상태 필터링
            "    (:isOngoing = TRUE AND pr.deadlineDate > :now) " + // 모집중은 Project의 deadlineDate 사용
            " OR (:isOngoing = FALSE AND pr.deadlineDate <= :now) " +
            " OR (:isOngoing IS NULL) " +
            ")" +
            "ORDER BY p.id DESC")
    List<Post> search(
            @Param("isOngoing") Boolean isOngoing,
            @Param("region") String region,
            @Param("keyword") String keyword,
            @Param("now") LocalDateTime now
    );
}
