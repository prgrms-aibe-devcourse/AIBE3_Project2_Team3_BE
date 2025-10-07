package com.pi.domain.offer.offer.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResBody<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last
) {
    public PagedResBody(List<T> content, Page<?> page) {
        this(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }
}
