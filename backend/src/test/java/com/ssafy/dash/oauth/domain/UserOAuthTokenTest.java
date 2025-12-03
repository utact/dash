package com.ssafy.dash.oauth.domain;

import com.ssafy.dash.common.TestFixtures;
import com.ssafy.dash.common.fixtures.FixtureTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserOAuthTokenTest {

    private static final Long USER_ID = TestFixtures.TEST_USER_ID;

    @Test
    @DisplayName("토큰을 발급하면 필드가 초기화된다")
    void issued_ShouldInitializeFields() {
        var issuedAt = FixtureTime.now();
        UserOAuthToken token = TestFixtures.userOAuthTokenFixture(USER_ID)
                .withIssuedAt(issuedAt)
                .toDomain(issuedAt.plusHours(1));

        assertThat(token.getUserId()).isEqualTo(USER_ID);
        assertThat(token.getAccessToken()).isEqualTo(TestFixtures.TEST_ACCESS_TOKEN);
        assertThat(token.getTokenType()).isEqualTo("Bearer");
        assertThat(token.getExpiresAt()).isEqualTo(issuedAt.plusHours(1));
        assertThat(token.getCreatedAt()).isEqualTo(issuedAt);
        assertThat(token.getUpdatedAt()).isEqualTo(issuedAt);
    }

    @Test
    @DisplayName("엑세스 토큰을 갱신하면 값과 시간이 함께 변경된다")
    void updateAccessToken_ShouldRefreshFields() {
        var issuedAt = FixtureTime.now();
        UserOAuthToken token = TestFixtures.userOAuthTokenFixture(USER_ID)
                .withIssuedAt(issuedAt)
                .toDomain(issuedAt.plusHours(1));

        var updatedAt = issuedAt.plusHours(2);
        var newExpiry = issuedAt.plusHours(3);
        token.updateAccessToken(TestFixtures.UPDATED_ACCESS_TOKEN, token.getTokenType(), newExpiry, updatedAt);

        assertThat(token.getAccessToken()).isEqualTo(TestFixtures.UPDATED_ACCESS_TOKEN);
        assertThat(token.getExpiresAt()).isEqualTo(newExpiry);
        assertThat(token.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("만료 시간 이전과 이후를 구분해 만료 여부를 판단한다")
    void isAccessTokenExpired_ShouldCheckTimestamp() {
        var now = FixtureTime.now();
        UserOAuthToken token = TestFixtures.userOAuthTokenFixture(USER_ID)
                .withIssuedAt(now)
                .toDomain(now.plusMinutes(10));

        assertThat(token.isAccessTokenExpired(now)).isFalse();
        assertThat(token.isAccessTokenExpired(now.plusMinutes(20))).isTrue();
    }

}
