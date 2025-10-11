package com.pi.domain.post.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostSkill is a Querydsl query type for PostSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostSkill extends EntityPathBase<PostSkill> {

    private static final long serialVersionUID = 1761161960L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostSkill postSkill = new QPostSkill("postSkill");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final QPost post;

    public final com.pi.domain.skill.skill.entity.QSkill skill;

    public QPostSkill(String variable) {
        this(PostSkill.class, forVariable(variable), INITS);
    }

    public QPostSkill(Path<? extends PostSkill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostSkill(PathMetadata metadata, PathInits inits) {
        this(PostSkill.class, metadata, inits);
    }

    public QPostSkill(Class<? extends PostSkill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.skill = inits.isInitialized("skill") ? new com.pi.domain.skill.skill.entity.QSkill(forProperty("skill")) : null;
    }

}

