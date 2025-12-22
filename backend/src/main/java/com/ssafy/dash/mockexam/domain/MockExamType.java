package com.ssafy.dash.mockexam.domain;

/**
 * 모의고사/코딩테스트 타입 정의
 */
public enum MockExamType {
    // 모의고사 타입 (IM/A/B)
    IM("IM 모의고사", 1, 2, "모의고사"),
    A("A형 모의고사", 2, 2, "모의고사"),
    B("B형 모의고사", 1, 4, "모의고사"),

    // 코딩테스트 타입 (삼성/카카오)
    SAMSUNG("삼성 코딩테스트", 3, 2, "코딩테스트"),
    KAKAO("카카오 코딩테스트", 3, 2, "코딩테스트");

    private final String displayName;
    private final int problemCount;
    private final int timeLimitHours;
    private final String category; // "모의고사" 또는 "코딩테스트"

    MockExamType(String displayName, int problemCount, int timeLimitHours, String category) {
        this.displayName = displayName;
        this.problemCount = problemCount;
        this.timeLimitHours = timeLimitHours;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getProblemCount() {
        return problemCount;
    }

    public int getTimeLimitHours() {
        return timeLimitHours;
    }

    public String getCategory() {
        return category;
    }
}
