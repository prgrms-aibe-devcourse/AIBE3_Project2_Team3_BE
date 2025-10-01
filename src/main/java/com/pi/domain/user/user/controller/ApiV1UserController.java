package com.pi.domain.user.user.controller;

import com.pi.domain.user.user.dto.*;
import com.pi.domain.user.user.entity.Users;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import com.pi.global.security.SecurityUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1UserController {
    private final UserService userService;
    private final Rq rq;

    @GetMapping("/me")
    public RsData<UserDto> me() {
        SecurityUser securityUser = rq.getSecurityUser();
        String currentUsername = securityUser.getUsername();
        Users user = userService.findByUsername(currentUsername)
                .orElseThrow(() -> new ServiceException("404-1", "로그인된 사용자를 찾을 수 없습니다."));

        return new RsData<>(
                "200-1",
                "%s님 정보입니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @PutMapping
    public RsData<UserDto> modify(
            @Valid @RequestBody UserModifyReqBody reqBody
    ) {
        Users user = userService.findByUsername("user1").get();
        user.checkActorCanModify(user);
        userService.modify(user, reqBody.nickname());

        return new RsData<>(
                "200-1",
                "%s님 정보가 수정되었습니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @DeleteMapping
    public RsData<Void> delete() {
        Users user = userService.findByUsername("user1").get();

        user.checkActorCanDelete(user);
        userService.delete(user);
        return new RsData<>("200-1", "회원 탈퇴가 완료되었습니다.");
    }

    @Transactional
    @PostMapping("/join")
    public RsData<UserDto> join(
            @Valid @RequestBody UserJoinReqBody reqBody
    ) {
        Users user = userService.join(reqBody.username(), reqBody.password(), reqBody.nickname());

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @Transactional
    @PostMapping("/login")
    public RsData<UserLoginResBody> login(
            @Valid @RequestBody UserLoginReqBody reqBody
    ) {
        Users user = userService.findByUsername(reqBody.username())
                .orElseThrow(() -> new ServiceException("401-1", "사용자를 찾을 수 없습니다."));
        userService.checkPassword(
                user,
                reqBody.password()
        );
        String accessToken = userService.genAccessToken(user);
        String refreshToken = userService.genRefreshToken(user);

        rq.setCookie("accessToken", accessToken);
        rq.setCookie("refreshToken", refreshToken);

        return new RsData<>(
                "200-1",
                "%s님, 로그인 성공".formatted(user.getNickname()),
                new UserLoginResBody(new UserDto(user), accessToken, refreshToken)
        );
    }

    @Transactional
    @DeleteMapping("/logout")
    public RsData<Void> logout() {

        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }
}
