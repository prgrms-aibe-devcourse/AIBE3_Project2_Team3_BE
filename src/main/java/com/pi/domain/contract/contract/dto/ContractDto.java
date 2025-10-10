package com.pi.domain.contract.contract.dto;

import com.pi.domain.contract.contract.entity.Contract;
import com.pi.domain.contract.contract.entity.ContractStatus;

import java.time.LocalDateTime;

public record ContractDto(
        long id,
        long postId,
        long hirerUserId,
        long talentUserId,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        ContractStatus status
) {
    public ContractDto(Contract contract) {
        this(
                contract.getId(),
                contract.getPost().getId(),
                contract.getHirerUser().getId(),
                contract.getTalentUser().getId(),
                contract.getCreatedDate(),
                contract.getModifiedDate(),
                contract.getStatus()
        );
    }
}
