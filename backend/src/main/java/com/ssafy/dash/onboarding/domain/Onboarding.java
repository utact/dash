package com.ssafy.dash.onboarding.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Onboarding {

    private Long id;
    private Long userId;
    private String repositoryName;
    private boolean webhookConfigured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Onboarding(Long id, Long userId, String repositoryName, boolean webhookConfigured, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = requirePositive(userId);
        this.repositoryName = requireText(repositoryName);
        this.webhookConfigured = webhookConfigured;
        this.createdAt = requireTimestamp(createdAt, "createdAt");
        this.updatedAt = requireTimestamp(updatedAt, "updatedAt");
    }

    public static Onboarding create(Long userId, String repositoryName, LocalDateTime createdAt) {
        return new Onboarding(null, userId, repositoryName, false, createdAt, createdAt);
    }

    public void updateRepository(String repositoryName, LocalDateTime updatedAt) {
        this.repositoryName = requireText(repositoryName);
        this.updatedAt = requireTimestamp(updatedAt, "updatedAt");
        this.webhookConfigured = false;
    }

    public void markWebhookConfigured(boolean configured, LocalDateTime updatedAt) {
        this.webhookConfigured = configured;
        this.updatedAt = requireTimestamp(updatedAt, "updatedAt");
    }

    private static Long requirePositive(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("userId" + " must be positive");
        }
        return value;
    }

    private static String requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("repositoryName" + " must not be blank");
        }
        return value;
    }

    private static LocalDateTime requireTimestamp(LocalDateTime value, String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }

}
