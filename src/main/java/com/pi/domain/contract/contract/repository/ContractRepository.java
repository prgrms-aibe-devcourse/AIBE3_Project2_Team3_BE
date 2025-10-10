package com.pi.domain.contract.contract.repository;

import com.pi.domain.contract.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
