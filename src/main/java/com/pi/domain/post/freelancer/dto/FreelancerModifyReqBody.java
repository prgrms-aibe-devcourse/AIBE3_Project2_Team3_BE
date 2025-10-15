package com.pi.domain.post.freelancer.dto;

import com.pi.domain.post.post.dto.PostModifyDto;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FreelancerModifyReqBody(
        @Valid
        PostModifyDto post,
        @Valid
        FreelancerModifyDto freelancer,
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds,
        List<MultipartFile> files
) {
}
