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

    public long count() {
        return userRepository.count();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void modify(User user, String nickname) {
        user.modify(nickname);
    }

    public String genAccessToken(User user) {
        return authTokenService.genAccessToken(user);
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

    public User getReferenceById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));
    }
}
