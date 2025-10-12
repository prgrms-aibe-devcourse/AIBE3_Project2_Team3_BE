package com.pi.domain.report.report.dto;

import com.pi.domain.report.report.entity.Report;
import com.pi.domain.report.report.entity.ReportType;

public record ReportDto(
        Long id,
        Long reporterId,
        Long targetUserId,
        Long postId,
//        Long reviewId,
        String comment,
        ReportType reportType
) {
    public static ReportDto from(Report report) {
        return new ReportDto(
                report.getId(),
                report.getReporter().getId(),
                report.getTargetUser() != null ? report.getTargetUser().getId() : null,
                report.getPost() != null ? report.getPost().getId() : null,
//                report.getReview() != null ? report.getReview().getId() : null,
                report.getComments(),
                report.getReportType()
        );
    }
}
