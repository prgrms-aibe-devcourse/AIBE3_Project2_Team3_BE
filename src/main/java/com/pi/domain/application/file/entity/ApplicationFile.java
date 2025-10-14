package com.pi.domain.application.file.entity;

import com.pi.domain.application.application.entity.Application;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFile extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    private String url;
}
