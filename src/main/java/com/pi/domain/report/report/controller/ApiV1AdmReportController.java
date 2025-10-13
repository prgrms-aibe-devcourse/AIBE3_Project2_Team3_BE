package com.pi.domain.report.report.controller;

import com.pi.domain.report.report.dto.ReportDto;
import com.pi.domain.report.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "ApiV1AdminReportController", description = "API 관리자 신고 컨트롤러")
public class ApiV1AdmReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 목록 조회")
    @GetMapping
    public Page<ReportDto> getReports(Pageable pageable) {
        return reportService.getAllReports(pageable);
    }

    @Operation(summary = "신고된 포스트 숨기기/보이기")
    @PatchMapping("/post/viewed")
//    /api/v1/admin/reports/post/viewed?postId={id}&isViewed={true|false}
    public void changePostViewedStatus(@RequestParam Long postId, @RequestParam boolean isViewed) {
        reportService.changePostViewedStatus(postId, isViewed);
    }
}
