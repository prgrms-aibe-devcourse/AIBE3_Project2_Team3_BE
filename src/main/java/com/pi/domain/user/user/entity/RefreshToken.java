package com.pi.domain.user.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    private String tokenHash;

    @Column(nullable = false)
    private Instant expires_date;

    @Column(nullable = false)
    private boolean revoked = false;

    public static RefreshToken of(User user, String hash, Instant exp) {
        RefreshToken rt = new RefreshToken();
        rt.id = UUID.randomUUID().toString();
        rt.user = user;
        rt.tokenHash = hash;
        rt.expires_date = exp;
        return rt;
    }

    public boolean isActive(Instant now) {
        return !revoked && expires_date.isAfter(now);
    }

    public void revoke() {
        this.revoked = true;
    }
}
