package com.pi.domain.report.report.controller;

import com.pi.domain.report.report.dto.ReportCreateReqBody;
import com.pi.domain.report.report.dto.ReportDto;
import com.pi.domain.report.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "ApiV1AdminReportController", description = "API 신고 컨트롤러")
public class ApiV1ReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 생성 (유저/게시물)")
    @PostMapping
    public ReportDto createReport(@RequestBody ReportCreateReqBody req) {
        return reportService.createReport(req.reporterId(), req);
    }
}
