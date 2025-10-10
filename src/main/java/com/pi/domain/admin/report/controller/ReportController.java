package com.pi.domain.admin.report.controller;

import com.pi.domain.admin.report.dto.ReportCreateReqBody;
import com.pi.domain.admin.report.dto.ReportDto;
import com.pi.domain.admin.report.entity.ReportType;
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

    @Operation(summary = "유저 신고 생성")
    @PostMapping("/user")
    public ReportDto reportUser(@RequestBody ReportCreateReqBody req) {
        ReportCreateReqBody userReportReq = new ReportCreateReqBody(
                req.reporterId(),
                req.targetUserId(),
                null,
                req.comment(),
                ReportType.USER
        );
        return reportService.createReport(req.reporterId(), userReportReq);
    }

    @Operation(summary = "게시물 신고 생성")
    @PostMapping("/post")
    public ReportDto reportPost(@RequestBody ReportCreateReqBody req) {
        ReportCreateReqBody postReportReq = new ReportCreateReqBody(
                req.reporterId(),
                null,
                req.postId(),
                req.comment(),
                ReportType.POST
        );
        return reportService.createReport(req.reporterId(), postReportReq);
    }

    @Operation(summary = "신고 목록 조회")
    @GetMapping
    public List<ReportDto> getReports() {
        return reportService.getAllReports();
    }
}
