package com.pi.domain.post.project.repository;

import com.pi.domain.post.project.entity.Project;
import com.pi.domain.post.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    Collection<Object> search(ProjectStatus status, String region, String keyword);
}
