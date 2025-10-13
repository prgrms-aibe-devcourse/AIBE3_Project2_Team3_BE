package com.pi.domain.post.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 612749545L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final com.pi.domain.post.freelancer.entity.QFreelancer freelancer;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isViewed = createBoolean("isViewed");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final ListPath<PostCategory, QPostCategory> postCategories = this.<PostCategory, QPostCategory>createList("postCategories", PostCategory.class, QPostCategory.class, PathInits.DIRECT2);

    public final ListPath<PostRegion, QPostRegion> postRegions = this.<PostRegion, QPostRegion>createList("postRegions", PostRegion.class, QPostRegion.class, PathInits.DIRECT2);

    public final ListPath<PostSkill, QPostSkill> postSkills = this.<PostSkill, QPostSkill>createList("postSkills", PostSkill.class, QPostSkill.class, PathInits.DIRECT2);

    public final com.pi.domain.post.project.entity.QProject project;

    public final EnumPath<com.pi.domain.post.project.entity.ProjectStatus> status = createEnum("status", com.pi.domain.post.project.entity.ProjectStatus.class);

    public final StringPath title = createString("title");

    public final com.pi.domain.user.user.entity.QUser user;

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.freelancer = inits.isInitialized("freelancer") ? new com.pi.domain.post.freelancer.entity.QFreelancer(forProperty("freelancer"), inits.get("freelancer")) : null;
        this.project = inits.isInitialized("project") ? new com.pi.domain.post.project.entity.QProject(forProperty("project"), inits.get("project")) : null;
        this.user = inits.isInitialized("user") ? new com.pi.domain.user.user.entity.QUser(forProperty("user")) : null;
    }

}

