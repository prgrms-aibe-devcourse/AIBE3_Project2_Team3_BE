package com.pi.domain.post.file.repository;

import com.pi.domain.post.file.entity.FreelancerFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerFileRepository extends JpaRepository<FreelancerFile, Long> {
    List<FreelancerFile> findByPostId(Long postId);
}
