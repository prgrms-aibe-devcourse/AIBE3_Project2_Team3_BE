package com.pi.domain.report.report.dto;

import com.pi.domain.report.report.entity.ReportType;

public record ReportCreateReqBody(
        Long reporterId,
        Long targetUserId,
        Long postId,
        Long reviewId,
        String comment,
        ReportType reportType
) {}
