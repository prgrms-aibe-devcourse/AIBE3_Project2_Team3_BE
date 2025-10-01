package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.exception.ServiceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void delete(User user) { userRepository.delete(user); }
    public void modify(User user, String nickname) { user.modify(nickname); }
    public String genAccessToken(User user) {
        return authTokenService.genAccessToken(user);
    }
    public String genRefreshToken(User user) {
        return authTokenService.genRefreshToken(user);
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        // 1. refreshToken 검증 (JWT 토큰인지 확인)
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ServiceException("401-3", "refreshToken이 비어있습니다.");
        }

        // JWT 토큰에서 회원 정보를 추출 (예: id, username, nickname 등)
        long userId = authTokenService.getUserIdFromToken(refreshToken); // JWT에서 사용자 ID 추출

        // 2. 데이터베이스에서 해당 사용자 조회
        Optional<User> userOptional = userRepository.findById(userId);

        // 3. 사용자 조회 실패 시 예외 처리
        if (userOptional.isEmpty()) {
            throw new ServiceException("401-3", "회원을 찾을 수 없습니다.");
        }

        // 4. 사용자가 존재하면 반환
        return userOptional;
    }
    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("401-1", "비밀번호가 일치 하지 않습니다.");
        }
    }

    public User join(String username, String password, String nickname, String email) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
            throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
        });
        password = passwordEncoder.encode(password);
        User user = new User(username, password, nickname, email);
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
            long actorId,
            String oldPassword,
            String newPassword,
            String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new ServiceException("400-1", "새 비밀번호와 일치하지 않습니다.");
        }
        User user = userRepository.findById(actorId)
                .orElseThrow(() -> new ServiceException("404-1", "일치하는 유저를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ServiceException("400-2", "현재 비밀번호가 일치하지 않습니다.");
        }

        String newEncodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(newEncodedPassword);
        userRepository.save(user);
    }
}
