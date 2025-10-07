package com.pi.domain.post.freelancer.dto;

import com.pi.domain.post.post.dto.PostWriteDto;
import jakarta.validation.Valid;

import java.util.List;

public record FreelancerWriteReqBody(
        @Valid
        PostWriteDto postWriteDto,
        @Valid
        FreelancerWriteDto freelancerWriteDto,
        List<Long> regionIds,
        List<Long> categoryIds,
        List<Long> skillIds
) {
}
