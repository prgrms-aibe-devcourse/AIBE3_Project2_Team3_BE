package com.pi.domain.chat.chat.repository;

import com.pi.domain.chat.chat.dto.ChatMemberDto;
import com.pi.domain.chat.chat.dto.ChatRoomDto;
import com.pi.domain.chat.chat.entity.MemberStatus;
import com.pi.domain.chat.chat.entity.QChatMember;
import com.pi.domain.chat.chat.entity.QChatMessage;
import com.pi.domain.chat.chat.entity.QChatRoom;
import com.pi.domain.user.user.dto.UserDto;
import com.pi.domain.user.user.entity.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ChatRoomDto> findRoomListForActiveUser(Long userId, Pageable pageable) {
        QChatRoom r = QChatRoom.chatRoom;
        QChatMember mu = QChatMember.chatMember; // "me" in room
        QChatMember cm = new QChatMember("cm");  // count members
        QChatMessage m = QChatMessage.chatMessage;

        // 서브쿼리: 방의 마지막 메시지 시각 (없으면 null)
        var lastMsgAtExpr =
                JPAExpressions.select(m.createdDate.max())
                        .from(m)
                        .where(m.chatRoom.eq(r));

        // 서브쿼리: 활성 멤버 수
        var memberCountExpr =
                JPAExpressions.select(cm.count())
                        .from(cm)
                        .where(cm.chatRoom.eq(r)
                                .and(cm.endedDate.isNull()));

        // (선택) 아바타 프리뷰는 추후 조인/서브쿼리로 확장. 일단 빈 리스트.
        var content = queryFactory
                .select(Projections.constructor(ChatRoomDto.class,
                        r.id,
                        r.name,
                        Expressions.dateTimeTemplate(LocalDateTime.class, "coalesce({0}, {1})", lastMsgAtExpr, r.createdDate),
                        memberCountExpr,
                        Expressions.constant("ACTIVE"),
                        // ✅ null 대신 빈 리스트 상수 (제네릭 명시)
                        Expressions.constant(Collections.<UserDto>emptyList())
                ))
                .from(r)
                .join(r.members, mu)
                .where(mu.user.id.eq(userId)
                        .and(mu.startedDate.isNotNull())
                        .and(mu.endedDate.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        Expressions.dateTimeTemplate(LocalDateTime.class, "coalesce({0}, {1})", lastMsgAtExpr, r.createdDate).desc(),
                        r.id.desc()
                )
                .fetch();

        Long total = queryFactory
                .select(r.id.countDistinct())
                .from(r)
                .join(r.members, mu)
                .where(mu.user.id.eq(userId)
                        .and(mu.startedDate.isNotNull())
                        .and(mu.endedDate.isNull()))
                .fetchOne();

        // avatarPreview null → 빈 리스트로 변환
        List<ChatRoomDto> normalized = content.stream()
                .map(it -> new ChatRoomDto(
                        it.roomId(),
                        it.roomName(),
                        it.LastMessageSendedDate(),
                        it.memberCount(),
                        it.membershipStatus(), // 이미 "ACTIVE" 들어옴
                        it.avatarPreview() == null ? List.of() : it.avatarPreview()
                ))
                .toList();

        return new PageImpl<>(normalized, pageable, total == null ? 0 : total);
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

        return Optional.ofNullable(core);
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
