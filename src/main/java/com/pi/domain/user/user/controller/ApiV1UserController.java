package com.pi.domain.user.user.controller;

import com.pi.domain.user.user.dto.*;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.AuthTokenService;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1UserController {
    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    @GetMapping("/me")
    public RsData<UserDto> me() {
        User actor = rq.getActor();
        User user = userService.findByUsername(actor.getUsername()).get();

        return new RsData<>(
                "200-1",
                "%s님 정보입니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @PutMapping
    @Transactional
    public RsData<UserDto> modify(
            @Valid @RequestBody UserModifyReqBody reqBody
    ) {
        User actor = rq.getActor();
        User user = userService.findByUsername(actor.getUsername()).get();
        user.checkActorCanModify(actor);
        userService.modify(user, reqBody.nickname());

        return new RsData<>(
                "200-1",
                "%s님 정보가 수정되었습니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @DeleteMapping
    public RsData<Void> delete() {
        User actor = rq.getActor();
        User user = userService.findByUsername(actor.getUsername()).get();
        user.checkActorCanDelete(actor);
        userService.delete(user);
        return new RsData<>("200-1", "회원 탈퇴가 완료되었습니다.");
    }

    @Transactional
    @PostMapping("/join")
    public RsData<UserDto> join(
            @Valid @RequestBody UserJoinReqBody reqBody
    ) {
        User user = userService.join(reqBody.username(), reqBody.password(), reqBody.nickname(), reqBody.email());

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @Transactional
    @PostMapping("/login")
    public RsData<UserDto> login(
            @Valid @RequestBody UserLoginReqBody reqBody
    ) {
        User user = userService.findByUsername(reqBody.username())
                .orElseThrow(() -> new ServiceException("401-1", "사용자를 찾을 수 없습니다."));
        userService.checkPassword(
                user,
                reqBody.password()
        );
        String accessToken = authTokenService.genAccessToken(user);
        String refreshToken = authTokenService.issueRefresh(user);

        rq.setCookie("accessToken", accessToken);
        rq.setCookie("refreshToken", refreshToken);

        return new RsData<>(
                "200-1",
                "%s님, 로그인 성공".formatted(user.getNickname()),
                new UserDto(user)
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

    @PostMapping("/findPw")
    public RsData<Void> findPassword(
            @Valid @RequestBody UserFindPasswordReqBody reqBody
    ) {
        userService.findPassword(reqBody.username(), reqBody.email());
        return new RsData<>(
                "200-1",
                "임시 비밀번호가 **" + reqBody.email() + "** 로 발송되었습니다. 확인 후 로그인하여 비밀번호를 변경해 주세요."
        );
    }

    @PatchMapping("/password")
    public RsData<Void> updatePassword(
            @Valid @RequestBody UserPasswordUpdateReqBody reqBody
    ) {
        User actor = userService.findByUsername(rq.getActor().getUsername())
                .orElseThrow(() -> new ServiceException("404-1", "사용자를 찾을 수 없습니다."));
        userService.updatePassword(
                actor,
                reqBody.oldPassword(),
                reqBody.newPassword()
        );
        return new RsData<>(
                "200-1",
                "비밀번호가 성공적으로 변경되었습니다."
        );
    }
}
