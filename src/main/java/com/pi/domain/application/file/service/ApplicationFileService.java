package com.pi.domain.application.file.service;

import com.pi.domain.application.file.entity.ApplicationFile;
import com.pi.domain.application.file.repository.ApplicationFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationFileService {
    private final ApplicationFileRepository applicationFileRepository;

    public ApplicationFile findById(long id) {
        return applicationFileRepository.findById(id).get();
    }
}
