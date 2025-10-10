package com.pi.domain.admin.report.dto;

import com.pi.domain.admin.report.entity.ReportType;

public record ReportCreateReqBody(
        Long reporterId,
        Long targetUserId,
        Long postId,
//        Long reviewId,
        String comment,
        ReportType reportType
) {}
