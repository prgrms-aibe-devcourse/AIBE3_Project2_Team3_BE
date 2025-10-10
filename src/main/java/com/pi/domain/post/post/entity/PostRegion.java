package com.pi.domain.post.post.entity;

import com.pi.domain.admin.region.entity.Region;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "post_regions",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_region", columnNames = {"post_id", "region_id"})
)
@Getter
@Setter
public class PostRegion extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
}
