package com.pi.domain.user.user.controller;

import com.pi.domain.user.user.dto.UserDto;
import com.pi.domain.user.user.dto.UserModifyReqBody;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ApiV1UserController {
    private final UserService userService;

    @GetMapping("/me")
    public RsData<UserDto> me() {
        User user = userService.findByUsername("user1").get();

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
        User user = userService.findByUsername("user1").get();
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
        User user = userService.findByUsername("user1").get();

        user.checkActorCanDelete(user);
        userService.delete(user);
        return new RsData<>("200-1", "회원 탈퇴가 완료되었습니다.");
    }
}
