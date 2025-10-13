package com.pi.domain.report.report.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = -1325771203L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final com.pi.global.jpa.entity.QBaseEntity _super = new com.pi.global.jpa.entity.QBaseEntity(this);

    public final StringPath comments = createString("comments");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final com.pi.domain.post.post.entity.QPost post;

    public final com.pi.domain.user.user.entity.QUser reporter;

    public final EnumPath<ReportType> reportType = createEnum("reportType", ReportType.class);

    public final com.pi.domain.user.user.entity.QUser targetUser;

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.pi.domain.post.post.entity.QPost(forProperty("post"), inits.get("post")) : null;
        this.reporter = inits.isInitialized("reporter") ? new com.pi.domain.user.user.entity.QUser(forProperty("reporter")) : null;
        this.targetUser = inits.isInitialized("targetUser") ? new com.pi.domain.user.user.entity.QUser(forProperty("targetUser")) : null;
    }

}

