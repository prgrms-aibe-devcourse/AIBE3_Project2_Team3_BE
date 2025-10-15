package com.pi.domain.post.freelancer.repository;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.post.freelancer.entity.FreelancerFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerFileRepository extends JpaRepository<FreelancerFile, Long> {
    List<FreelancerFile> findByFreelancer(Freelancer freelancer);
    void deleteByFreelancer(Freelancer freelancer);
}
