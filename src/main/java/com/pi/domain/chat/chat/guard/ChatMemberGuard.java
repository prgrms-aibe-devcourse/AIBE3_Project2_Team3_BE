package com.pi.domain.chat.chat.guard;

import com.pi.domain.chat.chat.entity.ChatMember;
import com.pi.domain.chat.chat.entity.ChatRole;
import com.pi.domain.chat.chat.entity.ChatRoom;
import com.pi.domain.chat.chat.entity.MemberStatus;
import com.pi.domain.chat.chat.repository.ChatMemberRepository;
import com.pi.domain.user.user.entity.User;
import com.pi.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMemberGuard {
    private final ChatMemberRepository ChatMemberRepository;

    /** 상태 값 조회 */
    public MemberStatus getStatus(Long userId, Long roomId) {
        return ChatMemberRepository.findTopByChatRoom_IdAndUser_IdOrderByIdDesc(roomId, userId)
                .map(m -> {
                    if (m.getEndedDate() != null) return MemberStatus.LEFT;      // 퇴장 기록
                    if (m.getStartedDate() == null) return MemberStatus.PENDING; // 초대 상태
                    return MemberStatus.ACTIVE;
                })
                .orElse(MemberStatus.NONE);
    }

    /** ACTIVE가 아니면 예외 */
    public void ensureActive(Long userId, Long roomId) {
        if (!ChatMemberRepository.existsByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(roomId, userId)) {
            throw new ServiceException("403-2", "방에 참여 중인 멤버만 이용할 수 있습니다.");
        }
    }

    /** PENDING 이상(=방에 속한 기록만 있으면) 허용할 때 */
    public void ensureMember(Long userId, Long roomId) {
        MemberStatus s = getStatus(userId, roomId);
        if (s == MemberStatus.NONE || s == MemberStatus.LEFT) {
            throw new ServiceException("403-3", "해당 방의 멤버가 아닙니다.");
        }
    }

    // 재참여 및 수락 처리
    public void reactivateMember(ChatRoom room, User user, ChatRole chatRole) {
        Optional<ChatMember> optionalChatMember = ChatMemberRepository.findTopByChatRoom_IdAndUser_IdOrderByIdDesc(room.getId(), user.getId());

        if (optionalChatMember.isEmpty()) {
            ChatMember newMember = ChatMember.joined(room, user, chatRole);
            room.addMember(newMember);
            return;
        }

        ChatMember member = optionalChatMember.get();
        if (member.getEndedDate() != null) {
            member.setEndedDate(null);
            member.setStartedDate(LocalDateTime.now());
        } else if (member.getStartedDate() == null) {
            member.accept();
        }
    }
}
