package com.pi.domain.report.report.service;

import com.pi.domain.post.post.entity.Post;
import com.pi.domain.post.post.repository.PostRepository;
import com.pi.domain.report.report.dto.ReportCreateReqBody;
import com.pi.domain.report.report.dto.ReportDto;
import com.pi.domain.report.report.entity.Report;
import com.pi.domain.report.report.entity.ReportType;
import com.pi.domain.report.report.repository.ReportRepository;
import com.pi.domain.review.review.entity.Review;
import com.pi.domain.review.review.repository.ReviewRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;

    public Page<ReportDto> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(ReportDto::from);
    }

    public void changePostViewedStatus(Long postId, boolean isViewed) {
        if (reportRepository.existsByPostId(postId)) {
            Post post = postRepository.findById(postId)
                    .get();
            post.modify(post.getTitle(), post.getContent(), isViewed);
            postRepository.save(post);
        }
    }

    @Transactional
    public ReportDto createReport(Long reporterId, ReportCreateReqBody req) {
        User reporter = userRepository.findById(reporterId)
                .get();

        User targetUser = null;
        Post post = null;
        Review review = null;


        if (req.reportType() == ReportType.USER) {
            targetUser = userRepository.findById(req.targetUserId())
                    .get();
        } else if (req.reportType() == ReportType.POST) {
            post = postRepository.findById(req.postId())
                    .get();
            targetUser = post.getUser();
        } else if (req.reportType() == ReportType.REVIEW) {
            review = reviewRepository.findById(req.reviewId()).get();
            targetUser = review.getUser();
        }

        Report report = new Report(
                reporter,
                targetUser,
                post,
                review,
                req.comment(),
                req.reportType()
        );

        return ReportDto.from(reportRepository.save(report));
    }
}
