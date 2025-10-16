package com.pi.domain.post.file.service;

import com.pi.domain.post.file.entity.FreelancerFile;
import com.pi.domain.post.file.repository.FreelancerFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreelancerFileService {
    private final FreelancerFileRepository freelancerFileRepository;

    public FreelancerFile findById(long id) {
        return freelancerFileRepository.findById(id).get();
    }
}