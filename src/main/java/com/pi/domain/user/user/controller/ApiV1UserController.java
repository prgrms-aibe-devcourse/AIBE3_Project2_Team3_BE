package com.pi.domain.user.user.controller;

import com.pi.domain.user.user.dto.*;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.AuthTokenService;
import com.pi.domain.user.user.service.RefreshTokenStore;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import com.pi.global.security.SecurityUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1UserController {
    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final RefreshTokenStore refreshTokenStore;
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
        userService.modify(user, reqBody.nickname(), reqBody.email());
        String newAccess = authTokenService.genAccessToken(user);
        rq.setCookie("accessToken", newAccess);

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
        String refreshPlain = rq.getCookieValue("refreshToken", null);
        if (refreshPlain != null && !refreshPlain.isBlank()) {
            refreshTokenStore.revoke(refreshPlain);
        }

        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

    @DeleteMapping("/logout/all")
    public RsData<Void> logoutAll() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser su) {
            refreshTokenStore.revokeAllForUser(su.getId());
            refreshTokenStore.bumpAuthVersion(su.getId()); // 남아있는 AT도 즉시 무효 (선택)
        }
        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");
        return new RsData<>("200-2", "모든 기기에서 로그아웃 되었습니다.");
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
        refreshTokenStore.revokeAllForUser(actor.getId());
        refreshTokenStore.bumpAuthVersion(actor.getId());
        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");
        return new RsData<>(
                "200-1",
                "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요."
        );
    }

    @DeleteMapping("/me")
    public void deleteMe(@RequestBody UserDeleteReqBody body) {
        SecurityUser su = (SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long userId = su.getId();

        userService.deleteMe(userId, body.password());

        // 1) 이 사용자의 모든 리프레시 토큰 무효화
        refreshTokenStore.revokeAllForUser(userId);

        // 2) authVersion 증가 → 남은 AT 즉시 무효
        refreshTokenStore.bumpAuthVersion(userId);

        // 3) 쿠키 삭제
        rq.deleteCookie("accessToken");
        rq.deleteCookie("refreshToken");
    }

    @PostMapping("/searchToInvite")
    public RsData<List<UserInviteDto>> invite(
            @RequestBody UserInviteSearchReqBody reqBody
    ) {
        String q = Optional.ofNullable(reqBody.username()).orElse("").trim();
        if (q.isEmpty()) {
            return new RsData<>("200-1", "검색어는 1글자 이상으로 검색해주세요.", List.of());
        }
        List<User> searchedUsers = userService.getInvitedUsers(q);
        List<UserInviteDto> dtoList = searchedUsers.stream()
                .map(UserInviteDto::new)
                .toList();
        return new RsData<>(
                "200-2",
                "유저 목록을 성공적으로 가져왔습니다.",
                dtoList);
    }
}
