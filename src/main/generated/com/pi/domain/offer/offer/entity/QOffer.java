package com.pi.domain.offer.offer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOffer is a Querydsl query type for Offer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOffer extends EntityPathBase<Offer> {

    private static final long serialVersionUID = -549809897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOffer offer = new QOffer("offer");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final com.pi.domain.post.freelancer.entity.QFreelancer freelancer;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final EnumPath<OfferStatus> status = createEnum("status", OfferStatus.class);

    public final com.pi.domain.user.user.entity.QUser user;

    public QOffer(String variable) {
        this(Offer.class, forVariable(variable), INITS);
    }

    public QOffer(Path<? extends Offer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOffer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOffer(PathMetadata metadata, PathInits inits) {
        this(Offer.class, metadata, inits);
    }

    public QOffer(Class<? extends Offer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.freelancer = inits.isInitialized("freelancer") ? new com.pi.domain.post.freelancer.entity.QFreelancer(forProperty("freelancer"), inits.get("freelancer")) : null;
        this.user = inits.isInitialized("user") ? new com.pi.domain.user.user.entity.QUser(forProperty("user")) : null;
    }

}

