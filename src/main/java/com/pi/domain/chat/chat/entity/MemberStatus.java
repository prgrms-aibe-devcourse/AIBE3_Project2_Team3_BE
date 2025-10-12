package com.pi.domain.chat.chat.entity;

public enum MemberStatus {
    ACTIVE("수락"), PENDING("대기"), LEFT("떠남"), NONE("없음"), ALL("전체");

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }
    public String getDescription() { return description; }

    public static MemberStatus normalize(String raw) {
        if (raw == null) return ALL;
        String s = raw.trim().toUpperCase();
        // 영문 코드와 한글 설명 둘 다 허용
        for (MemberStatus ms : values()) {
            if (ms.name().equals(s) || ms.description.equals(raw)) return ms;
        }
        return ALL; // 기본값
    }
}
