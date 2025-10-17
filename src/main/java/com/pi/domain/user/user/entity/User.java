package com.pi.domain.user.user.entity;

import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "Users")
public class User extends BaseEntity {
    @Column(unique = true)
    private String username;
    @Setter
    private String password;
    private String nickname;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(unique = true)
    private String email;
    @Setter
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    public User(String username, String password, String nickname, String email) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = UserRole.ROLE_USER;
    }

    public User(String username, String password, String nickname, String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public User(long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = UserRole.ROLE_USER;
    }

    public User(long id, String username, String nickname, UserRole role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public void modify(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public void checkActorCanModify(User actor) {
        if (!actor.getUsername().equals(getUsername())) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanDelete(User actor) {
        if (!actor.getUsername().equals(getUsername())) {
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public boolean isAdmin() {
        return UserRole.ROLE_ADMIN.equals(role);
    }

    public List<String> getAuthoritiesStringList() {
        List<String> authorities = new ArrayList<>();
        // 기본 사용자 권한은 항상 포함
        authorities.add("ROLE_USER");
        if (isAdmin()) {
            authorities.add("ROLE_ADMIN");
        }
        return authorities;
    }

    public void deleteSoft() {
        this.deleted = true;
        this.deletedDate = LocalDateTime.now();
    }
}
