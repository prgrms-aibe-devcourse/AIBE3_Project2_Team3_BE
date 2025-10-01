package com.pi.domain.user.user.entity;

import com.pi.global.exception.ServiceException;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    private String role;

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
}
