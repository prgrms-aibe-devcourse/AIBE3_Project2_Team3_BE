package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.entity.UserRole;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.exception.ServiceException;
import com.pi.global.s3.AwsS3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final String AWS_S3_DIRECTORY = "user";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AwsS3Service awsS3Service;

    public long count() {
        return userRepository.count();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void modify(User user, String nickname, String email, MultipartFile file) {
        user.modify(nickname, email);
        if (file != null && !file.isEmpty()) {
            String oldUrl = user.getProfileImageUrl();
            String newUrl = awsS3Service.uploadFile(file, AWS_S3_DIRECTORY);
            user.setProfileImageUrl(newUrl);

            if (oldUrl != null && !oldUrl.isBlank()) {
                try {
                    awsS3Service.deleteFileByUrl(oldUrl); // 아래 유틸 메서드 참고
                } catch (Exception e) {
                    // 삭제 실패는 치명적이지 않으니 로그만 남기고 무시
                    log.warn("Failed to delete old profile image: {}", oldUrl, e);
                }
            }
        }
    }

    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("401-1", "비밀번호가 일치 하지 않습니다.");
        }
    }

    public User join(String username, String password, String nickname, String email) {
        return join(username, password, nickname, email, UserRole.ROLE_USER);
    }

    public User join(String username, String password, String nickname, String email, UserRole role) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
                });
        password = passwordEncoder.encode(password);
        User user = new User(username, password, nickname, email, role);
        return userRepository.save(user);
    }

    @Transactional
    public void findPassword(String username, String email) {
        User user = userRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new ServiceException("404-1", ""));
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        emailService.sendTemporaryPasswordEmail(email, temporaryPassword);
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    @Transactional
    public void updatePassword(
            User actor,
            String oldPassword,
            String newPassword
    ) {
        if (!passwordEncoder.matches(oldPassword, actor.getPassword())) {
            throw new ServiceException("400-2", "현재 비밀번호가 일치하지 않습니다.");
        }
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        actor.setPassword(newEncodedPassword);
        userRepository.save(actor);
        // 더티 체킹으로 저장은 되지만 명시적으로 작성
        // 지워도 작동은 동일하게 동작
    }

    public User getById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));
    }

    @Transactional
    public void deleteMe(long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("404-1", "회원이 존재하지 않습니다."));

        // 이미 탈퇴 처리된 계정 방지
        if (user.isDeleted()) {
            throw new ServiceException("400-9", "이미 탈퇴 처리된 계정입니다.");
        }

        // 비밀번호 재확인
        if (password == null || password.isBlank() || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
        }

        // 도메인 참조 검토:
        // - 작성글/댓글 등은 유지(작성자 표시만 “탈퇴회원”)
        // - 강제 삭제 필요하면 cascade/on delete 세팅 먼저 확인
        user.deleteSoft();
        userRepository.save(user);
    }
}
