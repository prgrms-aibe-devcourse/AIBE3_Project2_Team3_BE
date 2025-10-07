package com.pi.domain.category.category.entity;

import com.pi.domain.post.post.entity.PostCategory;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Category extends BaseEntity {
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<PostCategory> postCategories = new ArrayList<>();

    public void addChild(Category child) {
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

    private boolean isAncestorOf(Category node) {
        for (Category p = this.parent; p != null; p = p.parent) {
            if (p == node) return true;
        }
        return false;
    }
}
