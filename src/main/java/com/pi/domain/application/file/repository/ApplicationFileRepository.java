package com.pi.domain.application.file.repository;

import com.pi.domain.application.file.entity.ApplicationFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationFileRepository extends JpaRepository<ApplicationFile, Long> {
}
