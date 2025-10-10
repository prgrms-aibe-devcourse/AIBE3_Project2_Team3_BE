package com.pi.domain.admin.report.controller;

import com.pi.domain.admin.report.dto.ReportCreateReqBody;
import com.pi.domain.admin.report.dto.ReportDto;
import com.pi.domain.admin.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "ApiV1AdminReportController", description = "API 관리자 신고 컨트롤러")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 생성 (유저/게시물)")
    @PostMapping
    public ReportDto createReport(@RequestBody ReportCreateReqBody req) {
        return reportService.createReport(req.reporterId(), req);
    }

    @Operation(summary = "신고 목록 조회")
    @GetMapping
    public List<ReportDto> getReports() {
        return reportService.getAllReports();
    }

    @Operation(summary = "신고된 포스트 숨기기/보이기")
    @PatchMapping("/post/viewed")
//    /api/v1/admin/reports/post/viewed?postId={id}&isViewed={true|false}
    public void changePostViewedStatus(@RequestParam Long postId, @RequestParam boolean isViewed) {
        reportService.changePostViewedStatus(postId, isViewed);
    }
}
