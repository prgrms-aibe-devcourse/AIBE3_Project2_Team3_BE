package com.pi.global.initData;

import com.pi.domain.report.report.dto.ReportCreateReqBody;
import com.pi.domain.report.report.entity.ReportType;
import com.pi.domain.report.report.service.ReportService;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class ProdReportInitData {
    private final ReportService reportService;
    private final UserRepository userRepository;

    @Bean
    ApplicationRunner prodReportInitDataRunner() {
        return args -> {
            initReports();
        };
    }

    @Transactional
    public void initReports() {
        if (reportService.count() > 0) {
            return;
        }
//        리뷰 신고
        reportService.createReport(4L, new ReportCreateReqBody(
                4L, // reporterId
                3L, // targetUserId
                null, // postId
                1L, // reviewId
                "리뷰 내용이 허위 사실을 포함하고 있습니다.", // content
                ReportType.REVIEW // type
        ));

//        포스트 신고
        reportService.createReport(5L, new ReportCreateReqBody(
                5L,
                null,
                1L,
                null,
                "비방성 표현이 있어 신고합니다.",
                ReportType.POST
        ));
    }
}