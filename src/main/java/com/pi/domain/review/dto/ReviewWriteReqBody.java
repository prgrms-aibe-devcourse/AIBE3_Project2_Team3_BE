package com.pi.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewWriteReqBody(
        @NotNull Long contractId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank String comment
) {}
