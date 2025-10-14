package com.pi.global.rsData;

import java.util.List;

public record PagePayload<T>(
        List<T> content,
        PageMeta page
) {
}
