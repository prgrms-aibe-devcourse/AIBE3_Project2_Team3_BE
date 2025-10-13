package com.pi.domain.post.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostRegion is a Querydsl query type for PostRegion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostRegion extends EntityPathBase<PostRegion> {

    private static final long serialVersionUID = -1272786627L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostRegion postRegion = new QPostRegion("postRegion");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final QPost post;

    public final com.pi.domain.region.region.entity.QRegion region;

    public QPostRegion(String variable) {
        this(PostRegion.class, forVariable(variable), INITS);
    }

    public QPostRegion(Path<? extends PostRegion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostRegion(PathMetadata metadata, PathInits inits) {
        this(PostRegion.class, metadata, inits);
    }

    public QPostRegion(Class<? extends PostRegion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.region = inits.isInitialized("region") ? new com.pi.domain.region.region.entity.QRegion(forProperty("region"), inits.get("region")) : null;
    }

}

