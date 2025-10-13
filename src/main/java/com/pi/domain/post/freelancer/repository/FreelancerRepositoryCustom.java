package com.pi.domain.post.freelancer.repository;

import com.pi.domain.post.freelancer.entity.Freelancer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreelancerRepositoryCustom {
    Page<Freelancer> searchFreelancers(
            String category,
            String region,
            String skill,
            String title,
            Integer minSalary,
            Integer maxSalary,
            Pageable pageable
    );
}

}
