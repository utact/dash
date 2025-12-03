package com.ssafy.dash.user.domain;

import com.ssafy.dash.common.fixtures.FixtureTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 도메인 테스트")
class UserTest {

    @Test
    @DisplayName("정상적으로 생성하면 필드가 기대값으로 설정된다")
    void createInitializesFields() {
        LocalDateTime createdAt = FixtureTime.now();

        User user = User.create("tester", "test@example.com", createdAt, "github", "123", "avatar");

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEqualTo("tester");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getProvider()).isEqualTo("github");
        assertThat(user.getProviderId()).isEqualTo("123");
        assertThat(user.getAvatarUrl()).isEqualTo("avatar");
    }

    @Test
    @DisplayName("프로필을 갱신하면 전달한 값으로 교체된다")
    void updateProfileUpdatesFields() {
        User user = User.create("tester", "test@example.com", FixtureTime.now(), "github", "123", "avatar");

        user.updateProfile("new", "new@example.com", "new-avatar");

        assertThat(user.getUsername()).isEqualTo("new");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getAvatarUrl()).isEqualTo("new-avatar");
    }

    @Test
    @DisplayName("필수 값이 비거나 null이면 IllegalArgumentException 또는 NullPointerException이 발생한다")
    void createValidatesMandatoryFields() {
        LocalDateTime now = FixtureTime.now();

        assertThatThrownBy(() -> User.create("", "test@example.com", now, "github", "123", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> User.create("tester", " ", now, "github", "123", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> User.create("tester", "test@example.com", null, "github", "123", null))
                .isInstanceOf(NullPointerException.class);
    }

}
