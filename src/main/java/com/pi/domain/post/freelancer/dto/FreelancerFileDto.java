package com.pi.domain.post.freelancer.dto;

import com.pi.domain.post.freelancer.entity.FreelancerFile;

public record FreelancerFileDto(Long id, String url) {
    public FreelancerFileDto(FreelancerFile file) {
        this(file.getId(), file.getUrl());
    }
}
