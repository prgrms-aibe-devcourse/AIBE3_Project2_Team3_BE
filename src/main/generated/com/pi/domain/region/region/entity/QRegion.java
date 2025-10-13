package com.pi.domain.region.region.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRegion is a Querydsl query type for Region
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRegion extends EntityPathBase<Region> {

    private static final long serialVersionUID = -1869828099L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRegion region = new QRegion("region");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    public final ListPath<Region, QRegion> children = this.<Region, QRegion>createList("children", Region.class, QRegion.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final QRegion parent;

    public final ListPath<com.pi.domain.post.post.entity.PostRegion, com.pi.domain.post.post.entity.QPostRegion> postRegions = this.<com.pi.domain.post.post.entity.PostRegion, com.pi.domain.post.post.entity.QPostRegion>createList("postRegions", com.pi.domain.post.post.entity.PostRegion.class, com.pi.domain.post.post.entity.QPostRegion.class, PathInits.DIRECT2);

    public QRegion(String variable) {
        this(Region.class, forVariable(variable), INITS);
    }

    public QRegion(Path<? extends Region> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRegion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRegion(PathMetadata metadata, PathInits inits) {
        this(Region.class, metadata, inits);
    }

    public QRegion(Class<? extends Region> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QRegion(forProperty("parent"), inits.get("parent")) : null;
    }

}

