package com.pi.domain.post.freelancer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFreelancer is a Querydsl query type for Freelancer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFreelancer extends EntityPathBase<Freelancer> {

    private static final long serialVersionUID = 885951535L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFreelancer freelancer = new QFreelancer("freelancer");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> period = createNumber("period", Long.class);

    public final com.pi.domain.post.post.entity.QPost post;

    public final NumberPath<Long> salary = createNumber("salary", Long.class);

    public QFreelancer(String variable) {
        this(Freelancer.class, forVariable(variable), INITS);
    }

    public QFreelancer(Path<? extends Freelancer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFreelancer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFreelancer(PathMetadata metadata, PathInits inits) {
        this(Freelancer.class, metadata, inits);
    }

    public QFreelancer(Class<? extends Freelancer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.pi.domain.post.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
    }

}

