package com.pi.domain.user.user.repository;

import com.pi.domain.user.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
