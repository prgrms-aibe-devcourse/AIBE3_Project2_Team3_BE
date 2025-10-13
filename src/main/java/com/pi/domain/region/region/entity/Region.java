package com.pi.domain.region.region.entity;

import com.pi.domain.post.post.entity.PostRegion;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Region extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    @OneToMany(mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Region> children = new ArrayList<>();

    @OneToMany(mappedBy = "region")
    private final List<PostRegion> postRegions = new ArrayList<>();

    public Region(String name, Region parent) {
        this.name = name;
        this.parent = parent;
    }

    public void addChild(Region child) {
        if (child == null) return;
        // 사이클 방지: 자신이나 자신의 하위로는 이동 불가
        if (child == this || isAncestorOf(child)) {
            throw new IllegalArgumentException("순환 참조 불가");
        }
        child.detachFromParent();
        child.parent = this;
        this.children.add(child);
    }

    public void detachFromParent() {
        if (this.parent != null) {
            this.parent.children.remove(this);
            this.parent = null;
        }
    }

    private boolean isAncestorOf(Region node) {
        for (Region p = this.parent; p != null; p = p.parent) {
            if (p == node) return true;
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }
}
