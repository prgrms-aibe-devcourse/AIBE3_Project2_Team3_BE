package com.pi.domain.user.user.repository;

import com.pi.domain.user.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    @Query("""
        select rt from RefreshToken rt
        join fetch rt.user u
        where rt.tokenHash = :hash and rt.revoked = false
    """)
    Optional<RefreshToken> findActiveWithUserByTokenHash(@Param("hash") String hash);
}
