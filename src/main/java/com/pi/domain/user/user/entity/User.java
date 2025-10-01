package com.pi.domain.user.user.entity;

import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private String password;
    private String nickname;
    private String role;

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = "ROLE_USER";
    }

    public User(long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = "ROLE_USER";
    }

    public void modify(String nickname){
        this.nickname = nickname;
    }

    public void checkActorCanModify(User actor) {
        if(!actor.getUsername().equals(getUsername())){
            throw new ServiceException("403-1", "권한이 없습니다.");
        }
    }

    public void checkActorCanDelete(User actor) {
        if(!actor.getUsername().equals(getUsername())){
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
        if ("admin".equals(username)) return true;
        return false;
    }

    public List<String> getAuthoritiesStringList() {
        List<String> authorities = new ArrayList<>();
        if (isAdmin()) {
            authorities.add("ROLE_ADMIN");
        }
        return authorities;
    }
}
