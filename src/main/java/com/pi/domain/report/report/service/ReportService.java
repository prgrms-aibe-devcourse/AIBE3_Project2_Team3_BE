package com.pi.domain.report.report.service;

import com.pi.domain.report.report.dto.ReportCreateReqBody;
import com.pi.domain.report.report.dto.ReportDto;
import com.pi.domain.report.report.entity.Report;
import com.pi.domain.report.report.entity.ReportType;
import com.pi.domain.report.report.repository.ReportRepository;
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
                .get();

        User targetUser = null;
        Post post = null;

        if (req.reportType() == ReportType.USER) {
            targetUser = userRepository.findById(req.targetUserId())
                    .get();
        } else if (req.reportType() == ReportType.POST) {
            post = postRepository.findById(req.postId())
                    .get();
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

    public void changePostViewedStatus(Long postId, boolean isViewed) {
        if (reportRepository.existsByPostId(postId)) {
            Post post = postRepository.findById(postId)
                    .get();
            post.modify(post.getTitle(), post.getContent(), isViewed);
            postRepository.save(post);
        }
    }
}
