package com.ssafy.dash.oauth.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.dash.oauth.domain.UserOAuthToken;
import com.ssafy.dash.oauth.mapper.UserOAuthTokenMapper;

@Service
public class OAuthTokenService {

    private static final Logger log = LoggerFactory.getLogger(OAuthTokenService.class);

    private final UserOAuthTokenMapper tokenMapper;

    public OAuthTokenService(UserOAuthTokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Transactional
    public void saveAccessToken(Long userId, OAuth2AccessToken accessToken) {
        if (userId == null || accessToken == null) {
            log.debug("saveAccessToken called with null userId or accessToken (userId={}, accessTokenPresent={})", userId, accessToken != null);
            return;
        }

        String tokenValue = accessToken.getTokenValue();
        String masked = tokenValue == null ? "null" : (tokenValue.length() > 8 ? tokenValue.substring(0, 6) + "..." : "***");
        log.debug("Persisting OAuth token for userId={} tokenMasked={}", userId, masked);

        UserOAuthToken token = tokenMapper.selectByUserId(userId);
        if (token == null) {
            token = new UserOAuthToken();
            token.setUserId(userId);
            applyAccessToken(token, accessToken);
            tokenMapper.insert(token);
            log.debug("Inserted new UserOAuthToken for userId={}", userId);
        } else {
            applyAccessToken(token, accessToken);
            tokenMapper.update(token);
            log.debug("Updated UserOAuthToken for userId={}", userId);
        }
    }

    @Transactional(readOnly = true)
    public UserOAuthToken findByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        return tokenMapper.selectByUserId(userId);
    }

    private void applyAccessToken(UserOAuthToken token, OAuth2AccessToken accessToken) {
        token.setAccessToken(accessToken.getTokenValue());
        token.setTokenType(accessToken.getTokenType() != null ? accessToken.getTokenType().getValue() : null);
        token.setExpiresAt(toLocalDateTime(accessToken.getExpiresAt()));
        token.setRefreshToken(null);
        token.setRefreshTokenExpiresAt(null);
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
