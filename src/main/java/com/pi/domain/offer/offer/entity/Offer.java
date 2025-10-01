package com.pi.domain.offer.offer.entity;

import com.pi.domain.post.freelancer.entity.Freelancer;
import com.pi.domain.user.user.entity.User;
import com.pi.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Offer extends BaseEntity {
    @ManyToOne
    private Freelancer freelancer;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;
}
