package com.ssafy.dash.onboarding.presentation.dto.response;

import com.ssafy.dash.onboarding.application.dto.result.RepositorySetupResult;

public record RepositorySetupResponse(Long userId, String repositoryName, boolean webhookConfigured) {

    public static RepositorySetupResponse from(RepositorySetupResult result) {
        return new RepositorySetupResponse(result.userId(), result.repositoryName(), result.webhookConfigured());
    }
    
}
