package com.pi.domain.post.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 2080000943L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final DateTimePath<java.time.LocalDateTime> deadlineDate = createDateTime("deadlineDate", java.time.LocalDateTime.class);

    public final StringPath employmentType = createString("employmentType");

    public final DateTimePath<java.time.LocalDateTime> endedDate = createDateTime("endedDate", java.time.LocalDateTime.class);

    public final StringPath hirerType = createString("hirerType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> personnel = createNumber("personnel", Integer.class);

    public final com.pi.domain.post.post.entity.QPost post;

    public final NumberPath<Long> salary = createNumber("salary", Long.class);

    public final NumberPath<Integer> skillLevel = createNumber("skillLevel", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startedDate = createDateTime("startedDate", java.time.LocalDateTime.class);

    public final EnumPath<ProjectStatus> status = createEnum("status", ProjectStatus.class);

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.pi.domain.post.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
    }

}

