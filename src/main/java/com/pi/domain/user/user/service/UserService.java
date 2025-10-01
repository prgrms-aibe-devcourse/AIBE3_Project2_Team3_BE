package com.pi.domain.user.user.service;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepositoy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoy userRepositoy;

    public Optional<User> findByUsername(String username) {
        return userRepositoy.findByUsername(username);
    }
    public void delete(User user) { userRepositoy.delete(user); }
    public void modify(User user, String nickname) { user.modify(nickname); }
}
