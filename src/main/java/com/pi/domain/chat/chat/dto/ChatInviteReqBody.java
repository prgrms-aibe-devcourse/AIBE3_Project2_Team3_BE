package com.pi.domain.chat.chat.dto;

import java.util.List;

public record ChatInviteReqBody(
        List<Long> inviteeIds
) {
}
