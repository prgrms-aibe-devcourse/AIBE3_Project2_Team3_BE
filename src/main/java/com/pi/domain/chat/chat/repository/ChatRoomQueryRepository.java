package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.dto.ChatMemberDto;
import com.pi.domain.chat.chat.dto.ChatMessageDto;
import com.pi.domain.chat.chat.dto.ChatRoomDto;
import com.pi.domain.chat.chat.entity.MemberStatus;
import com.pi.domain.chat.chat.entity.QChatMember;
import com.pi.domain.chat.chat.entity.QChatMessage;
import com.pi.domain.chat.chat.entity.QChatRoom;
import com.pi.domain.user.user.dto.UserDto;
import com.pi.domain.user.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Getter
    public static class RoomRow {
        private final Long id;
        private final String name;
        private final LocalDateTime createdAt; // 방 생성시각 (마지막 메시지 없을 때 fallback)
        private final Long memberCount;

        public RoomRow(Long id, String name, LocalDateTime createdAt, Long memberCount) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.memberCount = memberCount;
        }
    }

    public Page<ChatRoomDto> findRoomListForActiveUser(Long userId, Pageable pageable) {
        QChatRoom r = QChatRoom.chatRoom;
        QChatMember mu = QChatMember.chatMember;      // me in room
        QChatMember cm = new QChatMember("cm");       // count members
        QChatMessage m  = QChatMessage.chatMessage;
        QUser u = QUser.user;

        // 1) 페이지로 "내가 ACTIVE인" 방 목록 기본 메타만 가져오기
        var memberCountExpr = JPAExpressions
                .select(cm.count())
                .from(cm)
                .where(cm.chatRoom.eq(r)
                        .and(cm.endedDate.isNull()));

        List<RoomRow> roomRows = queryFactory
                .select(Projections.constructor(RoomRow.class,
                        r.id,
                        r.name,
                        r.createdDate,
                        memberCountExpr
                ))
                .from(r)
                .join(r.members, mu)
                .where(
                        mu.user.id.eq(userId),
                        mu.startedDate.isNotNull(),
                        mu.endedDate.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(r.id.desc()) // 정렬 기준은 필요에 맞게 교체(예: 마지막 메시지 시각 DESC)
                .fetch();

        if (roomRows.isEmpty()) {
            // total 계산만 해서 빈 페이지 반환
            long total0 = ofNullable(queryFactory
                    .select(r.id.countDistinct())
                    .from(r)
                    .join(r.members, mu)
                    .where(
                            mu.user.id.eq(userId),
                            mu.startedDate.isNotNull(),
                            mu.endedDate.isNull()
                    )
                    .fetchOne()).orElse(0L);
            return new PageImpl<>(List.of(), pageable, total0);
        }

        // 방 ID들
        List<Long> roomIds = roomRows.stream().map(RoomRow::getId).toList();

        // 2) 방별 마지막 메시지 id (groupBy)
        List<Tuple> lastIdTuples = queryFactory
                .select(m.chatRoom.id, m.id.max())
                .from(m)
                .where(m.chatRoom.id.in(roomIds))
                .groupBy(m.chatRoom.id)
                .fetch();

        Map<Long, Long> lastMsgIdByRoom = lastIdTuples.stream()
                .collect(Collectors.toMap(
                        t -> t.get(0, Long.class),
                        t -> t.get(1, Long.class)
                ));

        // 3) 마지막 메시지 상세(보낸 유저 포함) 배치 조회 → roomId → ChatMessageDto 매핑
        Map<Long, ChatMessageDto> lastMsgByRoom;
        if (!lastMsgIdByRoom.isEmpty()) {
            List<Long> lastIds = new ArrayList<>(new HashSet<>(lastMsgIdByRoom.values()));
            // roomId도 함께 뽑아 매핑하기
            List<Tuple> lastRows = queryFactory
                    .select(m.chatRoom.id, m.id, u.id, u.nickname, u.profileImageUrl, m.content, m.createdDate)
                    .from(m)
                    .join(m.chatMember, cm)
                    .join(cm.user, u)
                    .where(m.id.in(lastIds))
                    .fetch();

            lastMsgByRoom = lastRows.stream().collect(Collectors.toMap(
                    t -> t.get(m.chatRoom.id),
                    t -> new ChatMessageDto(
                            t.get(m.id),
                            t.get(u.id),
                            t.get(u.nickname),
                            t.get(u.profileImageUrl),
                            t.get(m.content),
                            t.get(m.createdDate)
                    )
            ));
        } else {
            lastMsgByRoom = Collections.emptyMap();
        }

        // 4) 방별 미읽음 개수(unreadCount) 배치 조회
        // 전제: ChatMessage.messageSeq, ChatMember.lastReadSeq 컬럼이 존재한다고 가정
        Map<Long, Long> unreadByRoom = queryFactory
                .select(m.chatRoom.id, m.id.count())
                .from(m)
                .join(mu).on(
                        mu.chatRoom.eq(m.chatRoom)
                                .and(mu.user.id.eq(userId))
                                .and(mu.endedDate.isNull())
                )
                .where(
                        m.chatRoom.id.in(roomIds)
                                .and(mu.lastReadMessageId.isNull().or(m.id.gt(mu.lastReadMessageId)))
                )
                .groupBy(m.chatRoom.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(0, Long.class),
                        t -> t.get(1, Long.class)
                ));

        // 5) DTO 조립 (마지막 메시지가 없으면 createdDate=방 생성시각으로 fallback)
        List<ChatRoomDto> content = roomRows.stream().map(row -> {
            ChatMessageDto last = lastMsgByRoom.get(row.getId());
            if (last == null) {
                // 메시지가 하나도 없으면: createdDate만 방 생성시각으로 채운 placeholder
                last = new ChatMessageDto(
                        null,          // id
                        null,          // senderId
                        null,          // senderNickname
                        null,          // senderProfileImageUrl
                        null,          // content
                        row.getCreatedAt() // createdDate = 방 생성시간
                );
            }
            long unread = unreadByRoom.getOrDefault(row.getId(), 0L);

            return new ChatRoomDto(
                    row.getId(),
                    row.getName(),
                    last,
                    ofNullable(row.getMemberCount()).orElse(0L),
                    "ACTIVE",
                    Collections.<UserDto>emptyList(),  // avatarPreview는 추후 확장
                    unread
            );
        }).toList();

        // 6) total 계산
        Long total = queryFactory
                .select(r.id.countDistinct())
                .from(r)
                .join(r.members, mu)
                .where(
                        mu.user.id.eq(userId),
                        mu.startedDate.isNotNull(),
                        mu.endedDate.isNull()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    public record _RoomDetailCore(
            Long roomId, String roomName,
            Long lastMessageId, LocalDateTime lastMessageAt,
            long memberCount, String membershipStatus
    ) {}

    public Optional<_RoomDetailCore> findRoomDetail(Long userId, Long roomId) {
        QChatRoom r = QChatRoom.chatRoom;
        QChatMember mu = new QChatMember("mu"); // me in room
        QChatMember cm = new QChatMember("cm"); // count members
        QChatMessage m = QChatMessage.chatMessage;

        // 마지막 메시지 (id, createdAt)
        var lastMsgIdExpr = JPAExpressions
                .select(m.id.max())
                .from(m)
                .where(m.chatRoom.eq(r));

        // createdAt은 max(id)기준과 다를 수 있어서 별도 서브쿼리
        QChatMessage m2 = new QChatMessage("m2");
        var lastMsgAtExpr = JPAExpressions
                .select(m2.createdDate)
                .from(m2)
                .where(m2.id.eq(lastMsgIdExpr));

        // 멤버 수(활성)
        var memberCountExpr = JPAExpressions
                .select(cm.count())
                .from(cm)
                .where(cm.chatRoom.eq(r)
                        .and(cm.endedDate.isNull()));

        // 내 멤버십 상태
        var statusExpr = new com.querydsl.core.types.dsl.CaseBuilder()
                .when(mu.endedDate.isNotNull()).then("LEFT")
                .when(mu.startedDate.isNull()).then("PENDING")
                .otherwise("ACTIVE");

        _RoomDetailCore core = queryFactory
                .select(Projections.constructor(_RoomDetailCore.class,
                        r.id,
                        r.name,
                        lastMsgIdExpr,
                        // 메시지가 없으면 방 생성시각 사용
                        Expressions.dateTimeTemplate(LocalDateTime.class, "coalesce({0}, {1})",
                                lastMsgAtExpr, r.createdDate),
                        memberCountExpr,
                        statusExpr
                ))
                .from(r)
                .join(r.members, mu)
                .where(r.id.eq(roomId)
                        .and(mu.user.id.eq(userId))
                        .and(mu.endedDate.isNull())) // 방에 ‘현재’ 속해 있어야 조회 허용(PENDING 포함)
                .fetchOne();

        return ofNullable(core);
    }

    // avatarPreview: 활성 멤버 상위 N명(예: 최근 입장순/유저ID순 등 단순 정렬)
    public List<UserDto> findAvatarPreview(Long roomId, int limit) {
        QChatMember cm = QChatMember.chatMember;
        com.pi.domain.user.user.entity.QUser u = com.pi.domain.user.user.entity.QUser.user;

        return queryFactory.select(Projections.constructor(
                        UserDto.class,
                        u.id,
                        u.createdDate,
                        u.modifiedDate,
                        u.nickname,
                        u.email,
                        u.role,
                        u.profileImageUrl
                ))
                .from(cm)
                .join(cm.user, u)
                .where(cm.chatRoom.id.eq(roomId)
                        .and(cm.endedDate.isNull()))
                .orderBy(u.id.asc()) // 필요 시 입장시각/최근 발언자 기준으로 변경
                .limit(limit)
                .fetch();
    }

    public Page<ChatMemberDto> findMembers(Long roomId, MemberStatus status, Pageable pageable) {
        QChatMember cm = QChatMember.chatMember;
        QUser u = QUser.user;

        // where 기본절
        BooleanExpression where = cm.chatRoom.id.eq(roomId).and(cm.endedDate.isNull()); // 활성(퇴장 X)만

        // status 필터
        if (status == MemberStatus.ACTIVE) {
            where = where.and(cm.startedDate.isNotNull());
        } else if (status == MemberStatus.PENDING) {
            where = where.and(cm.startedDate.isNull());
        }

        // 정렬 매핑 (Pageable의 sort를 QueryDSL로 적용)
        List<OrderSpecifier<?>> orders = toOrderSpecifiers(pageable.getSort(), u, cm);

        // content
        List<ChatMemberDto> content = queryFactory
                .select(Projections.constructor(ChatMemberDto.class,
                        u.id,
                        u.nickname,
                        u.profileImageUrl,
                        cm.role.stringValue(),
                        new CaseBuilder().when(cm.startedDate.isNull()).then("PENDING").otherwise("ACTIVE"),
                        cm.startedDate,
                        cm.endedDate
                ))
                .from(cm)
                .join(cm.user, u)
                .where(where)
                .orderBy(
                        orders.isEmpty()
                                ? new OrderSpecifier<?>[] { u.id.asc() }
                                : orders.toArray(OrderSpecifier[]::new)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // total
        Long total = queryFactory.select(cm.id.count())
                .from(cm)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private List<OrderSpecifier<?>> toOrderSpecifiers(Sort sort, QUser u, QChatMember cm) {
        List<OrderSpecifier<?>> list = new ArrayList<>();
        for (Sort.Order o : sort) {
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;
            switch (o.getProperty()) {
                case "nickname" -> list.add(new OrderSpecifier<>(dir, u.nickname));
                case "role"     -> list.add(new OrderSpecifier<>(dir, cm.role));
                case "startedDate" -> list.add(new OrderSpecifier<>(dir, cm.startedDate));
                default         -> list.add(new OrderSpecifier<>(dir, u.id)); // fallback
            }
        }
        return list;
    }
}
