package com.pi.domain.chat.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatMember is a Querydsl query type for ChatMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMember extends EntityPathBase<ChatMember> {

    private static final long serialVersionUID = 811563163L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatMember chatMember = new QChatMember("chatMember");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    public final QChatRoom chatRoom;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> endedDate = createDateTime("endedDate", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final EnumPath<ChatRole> role = createEnum("role", ChatRole.class);

    public final DateTimePath<java.time.LocalDateTime> startedDate = createDateTime("startedDate", java.time.LocalDateTime.class);

    public final com.pi.domain.user.user.entity.QUser user;

    public QChatMember(String variable) {
        this(ChatMember.class, forVariable(variable), INITS);
    }

    public QChatMember(Path<? extends ChatMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatMember(PathMetadata metadata, PathInits inits) {
        this(ChatMember.class, metadata, inits);
    }

    public QChatMember(Class<? extends ChatMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new com.pi.domain.user.user.entity.QUser(forProperty("user")) : null;
    }

}

