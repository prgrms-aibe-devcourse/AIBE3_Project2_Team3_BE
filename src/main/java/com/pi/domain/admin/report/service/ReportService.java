package com.pi.domain.admin.report.service;

import com.pi.domain.admin.report.dto.ReportCreateReqBody;
import com.pi.domain.admin.report.dto.ReportDto;
import com.pi.domain.admin.report.entity.Report;
import com.pi.domain.admin.report.entity.ReportType;
import com.pi.domain.admin.report.repository.ReportRepository;
import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public ReportDto createReport(Long reporterId, ReportCreateReqBody req) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("신고자 정보가 없습니다."));

        User targetUser = null;
        Post post = null;

        if (req.reportType() == ReportType.USER) {
            targetUser = userRepository.findById(req.targetUserId())
                    .orElseThrow(() -> new IllegalArgumentException("대상 유저 정보가 없습니다."));
        } else if (req.reportType() == ReportType.POST) {
            post = postRepository.findById(req.postId())
                    .orElseThrow(() -> new IllegalArgumentException("게시물 정보가 없습니다."));
            targetUser = post.getUser();
        }

        Report report = new Report(
                reporter,
                targetUser,
                post,
                req.comment(),
                req.reportType()
        );

        return ReportDto.from(reportRepository.save(report));
    }

    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream()
                .map(ReportDto::from)
                .toList();
    }
}
