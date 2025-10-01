package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.Users;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void delete(Users user) { userRepository.delete(user); }
    public void modify(Users user, String nickname) { user.modify(nickname); }
    public String genAccessToken(Users user) {
        return authTokenService.genAccessToken(user);
    }
    public String genRefreshToken(Users user) {
        return authTokenService.genRefreshToken(user);
    }

    public Optional<Users> findByRefreshToken(String refreshToken) {
        // 1. refreshToken 검증 (JWT 토큰인지 확인)
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ServiceException("401-3", "refreshToken이 비어있습니다.");
        }

        // JWT 토큰에서 회원 정보를 추출 (예: id, username, nickname 등)
        long userId = authTokenService.getUserIdFromToken(refreshToken); // JWT에서 사용자 ID 추출

        // 2. 데이터베이스에서 해당 사용자 조회
        Optional<Users> userOptional = userRepository.findById(userId);

        // 3. 사용자 조회 실패 시 예외 처리
        if (userOptional.isEmpty()) {
            throw new ServiceException("401-3", "회원을 찾을 수 없습니다.");
        }

        // 4. 사용자가 존재하면 반환
        return userOptional;
    }
    public void checkPassword(Users user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("401-1", "비밀번호가 일치 하지 않습니다.");
        }
    }

    public Users join(String username, String password, String nickname) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
            throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
        });
        password = passwordEncoder.encode(password);
        Users user = new Users(username, password, nickname);
        return userRepository.save(user);
    }
}
