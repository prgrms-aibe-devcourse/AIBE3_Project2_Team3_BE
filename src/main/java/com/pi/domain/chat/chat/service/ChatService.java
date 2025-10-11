package com.pi.domain.chat.chat.service;

import com.pi.domain.chat.chat.dto.*;
import com.pi.domain.chat.chat.entity.*;
import com.pi.domain.chat.chat.guard.ChatMemberGuard;
import com.pi.domain.chat.chat.repository.ChatMemberRepository;
import com.pi.domain.chat.chat.repository.ChatMessageRepository;
import com.pi.domain.chat.chat.repository.ChatRoomQueryRepository;
import com.pi.domain.chat.chat.repository.ChatRoomRepository;
import com.pi.domain.user.user.dto.UserDto;
import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.repository.UserRepository;
import com.pi.global.exception.ServiceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;
    private final ChatMemberGuard chatMemberGuard;

    @Transactional
    public ChatRoomDto createRoom(User actor, @Valid ChatCreateReqBody reqBody) {
        String name = reqBody.roomName().trim();
        if (name.length() > 100) throw new IllegalArgumentException("채팅방 이름이 너무 깁니다.");
        List<Long> inviteeIds = Optional.ofNullable(reqBody.inviteeIds()).orElseGet(List::of)
                .stream().filter(id -> !id.equals(actor.getId())) // 본인 제거
                .distinct().toList();
        // 1) 방 생성
        ChatRoom room = ChatRoom.create(reqBody.roomName());
        chatRoomRepository.save(room);

        // 2) 멤버 추가: 나 = ACTIVE(즉시 입장), 초대 대상 = PENDING(입장 전)
        User me = userRepository.findByUsername(actor.getUsername()).get();
        ChatMember owner = ChatMember.joined(room, me, ChatRole.OWNER);
        room.addMember(owner);

        List<User> invitees = inviteeIds.isEmpty() ? List.of() : userRepository.findAllById(inviteeIds);
        for (User u : invitees) room.addMember(ChatMember.invited(room, u, ChatRole.MEMBER));

        // 3) 방 생성 시간
        LocalDateTime lastTime = room.getCreatedDate();

        // 4) 참여 중 기준 카운트
        long memberCount = chatMemberRepository.countByChatRoomIdAndEndedDateIsNull(room.getId());

        // 5) 초대된 유저 미리보기 필요
        List<UserDto> preview = Stream.concat(
                        Stream.of(me),
                        invitees.stream()
                )
                .limit(3)
                .map(UserDto::new)
                .toList();
        return new ChatRoomDto(room.getId(), room.getName(), lastTime, memberCount, "ACTIVE", preview);
    }

    @Transactional
    public long nextSeq(Long roomId) {
        // TODO: Redis INCR or 별도 Counter 테이블/시퀀스
        // 임시로는 마지막 메시지 seq + 1 조회(경합 주의)
        return Optional.ofNullable(chatMessageRepository.findMaxSeqByChatRoom_Id(roomId)).orElse(0L) + 1;
    }

    @Transactional(readOnly = true)
    public Page<ChatRoomDto> findRoomsForUser(User actor, Pageable pageable) {
        return chatRoomQueryRepository.findRoomListForActiveUser(actor.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public ChatRoomDetailDto getRoomDetail(User actor, Long roomId) {
        var dto = chatRoomQueryRepository.findRoomDetail(actor.getId(), roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        // 단건 조회이므로 avatarPreview는 별도 쿼리로 안전하게(성능 OK)
        var preview = chatRoomQueryRepository.findAvatarPreview(roomId, 3);
        return new ChatRoomDetailDto(
                dto.roomId(),
                dto.roomName(),
                dto.lastMessageId(),
                dto.lastMessageAt(),
                dto.memberCount(),
                dto.membershipStatus(),
                preview
        );
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getMessages(User actor, Long roomId, Pageable pageable) {
        // 멤버십 가드 (ACTIVE만 허용 등)
        chatMemberGuard.ensureActive(actor.getId(), roomId);

        // 방별 메시지 페이지 조회 (id DESC 권장)
        Page<ChatMessage> page = chatMessageRepository.findByChatRoom_Id(roomId, pageable);

        // 무한스크롤 표시용: 클라에서 위로 쌓을거면 오름차순이 편함
        // -> 여기서 역순 변환해 보내거나, 클라에서 정렬 변경
        List<ChatMessageDto> content = page.getContent().stream()
                .map(m -> new ChatMessageDto(
                        m.getId(),
                        m.getChatMember().getUser().getId(),
                        m.getChatMember().getUser().getNickname(),
                        m.getChatMember().getUser().getProfileImageUrl(),
                        m.getContent(),
                        m.getCreatedDate()
                ))
                .toList();

        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    public ChatMessageDto sendMessage(User actor, Long roomId, String content) {
        if (content.isBlank()) throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");

        // 1) 방 존재 확인 (지연 로딩만 필요하면 getReferenceById도 가능)
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ServiceException("404-1", "채팅방을 찾을 수 없습니다."));

        // 2) ACTIVE 멤버 가드
        ChatMember me = chatMemberRepository
                .findByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(roomId, actor.getId())
                .orElseThrow(() -> new ServiceException("403-2", "방에 참여 중인 멤버만 메시지를 보낼 수 있습니다."));

        // 3) 저장
        long seq = nextSeq(roomId);
        ChatMessage chatMessage = new ChatMessage(me, room, content, seq);
        chatMessageRepository.save(chatMessage);

        return new ChatMessageDto(
                chatMessage.getId(),
                me.getUser().getId(),
                me.getUser().getNickname(),
                me.getUser().getProfileImageUrl(),
                chatMessage.getContent(),
                chatMessage.getCreatedDate()
        );
    }

    /** 초대 수락 */
    @Transactional
    public void accept(Long userId, Long roomId) {
        ChatMember m = chatMemberRepository.findByChatRoom_IdAndUser_IdAndEndedDateIsNull(roomId, userId)
                .orElseThrow(() -> new ServiceException("404-1","초대/멤버십을 찾을 수 없습니다."));

        if (m.getStartedDate() == null) {
            m.accept(); // startedDate = now
        } // 이미 ACTIVE면 멱등 처리
        // 이벤트(선택): 수락 알림
//        eventPublisher.publishEvent(new InviteAcceptedEvent(roomId, userId));
    }

    /** 방 떠나기 */
    @Transactional
    public void leave(Long userId, Long roomId) {
        ChatMember m = chatMemberRepository.findByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(roomId, userId)
                .orElseThrow(() -> new ServiceException("403-4","참여 중이 아닙니다."));

        // OWNER 정책: 지금은 그냥 나가도록. 필요 시 OWNER 위임/차단 로직 추가.
        m.leave(); // endedDate = now

        // 이벤트(선택): 퇴장 알림
//        eventPublisher.publishEvent(new MemberLeftEvent(roomId, userId));
    }

    @Transactional
    public ChatInviteResBody invite(Long inviterId, Long roomId, List<Long> inviteeIds) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ServiceException("404-1","채팅방을 찾을 수 없습니다."));

        // 권한: ACTIVE 멤버만 초대 가능(OWNER만으로 제한하려면 role 체크 추가)
        boolean inviterActive = chatMemberRepository
                .existsByChatRoom_IdAndUser_IdAndStartedDateIsNotNullAndEndedDateIsNull(roomId, inviterId);
        if (!inviterActive) throw new ServiceException("403-2","초대 권한이 없습니다.");

        List<Long> userIds = Optional.ofNullable(inviteeIds).orElseGet(List::of).stream()
                .filter(id -> !id.equals(inviterId))   // 본인 초대 금지
                .distinct()
                .toList();
        if (userIds.isEmpty()) return new ChatInviteResBody(List.of(), List.of());

        // 대상 유저 로드/존재 확인
        List<User> users = userRepository.findAllById(userIds);
        Set<Long> foundIds = users.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
        List<ChatInviteResBody.Skip> skipped = new ArrayList<>();
        for (Long id : userIds) {
            if (!foundIds.contains(id)) skipped.add(new ChatInviteResBody.Skip(id, "NOT_FOUND"));
        }

        // 현재 상태 미리 조회 (ACTIVE/PENDING)
        Map<Long, ChatMember> existing = chatMemberRepository
                .findByChatRoom_IdAndUser_IdInAndEndedDateIsNull(roomId, foundIds)
                .stream().collect(java.util.stream.Collectors.toMap(cm -> cm.getUser().getId(), cm -> cm));

        List<Long> invited = new ArrayList<>();
        for (User u : users) {
            ChatMember cur = existing.get(u.getId());
            if (cur != null) {
                if (cur.getStartedDate() != null) {
                    skipped.add(new ChatInviteResBody.Skip(u.getId(), "ALREADY_ACTIVE"));
                } else {
                    skipped.add(new ChatInviteResBody.Skip(u.getId(), "ALREADY_INVITED"));
                }
                continue;
            }
            // 새 초대(PENDING)
            ChatMember invitedMember = ChatMember.invited(room, u, ChatRole.MEMBER); // startedDate=null
            // room.addMember(...) 내부에서 역방향 세팅이 되도록 구현되어 있어야 함
            // 부모만 save 해도 되지만 안전하게 명시 persist:
            chatMemberRepository.save(invitedMember);
            invited.add(u.getId());
        }

        // 이벤트(선택): 초대 알림
        if (!invited.isEmpty()) {
//            eventPublisher.publishEvent(new RoomInvitedEvent(roomId, inviterId, invited));
        }

        return new ChatInviteResBody(invited, skipped);
    }

    public Page<ChatMemberDto> listMembers(User actor, Long roomId, MemberStatus status, Pageable pageable) {
        chatMemberGuard.ensureMember(actor.getId(), roomId);
        return chatRoomQueryRepository.findMembers(roomId, status, pageable);
    }
}
