package com.pi.domain.user.user.repository;

import com.pi.domain.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoy extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
