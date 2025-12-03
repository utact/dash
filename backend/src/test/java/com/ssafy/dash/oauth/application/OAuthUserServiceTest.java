package com.ssafy.dash.oauth.application;

import com.ssafy.dash.common.TestFixtures;
import com.ssafy.dash.oauth.application.dto.result.OAuthLoginResult;
import com.ssafy.dash.oauth.domain.AuthFlowType;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuthUserService 단위 테스트")
class OAuthUserServiceTest {

    private static final String PROVIDER = TestFixtures.TEST_PROVIDER;
    private static final String PROVIDER_ID = TestFixtures.TEST_PROVIDER_ID;
    private static final String USERNAME = TestFixtures.TEST_USERNAME;
    private static final String EMAIL = TestFixtures.TEST_EMAIL;
    private static final String AVATAR_URL = TestFixtures.TEST_AVATAR_URL;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OAuthUserService oauthUserService;

    @Test
    @DisplayName("신규 OAuth 유저면 생성하고 SIGN_UP 흐름을 반환한다")
    void createOrUpdateOAuthUser_createsNewUser() {
        when(userRepository.findByProviderAndProviderId(PROVIDER, PROVIDER_ID))
                .thenReturn(Optional.empty());

        OAuthLoginResult result = oauthUserService.createOrUpdateOAuthUser(PROVIDER, PROVIDER_ID,
                USERNAME, EMAIL, AVATAR_URL);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(userRepository, never()).update(any());

        User saved = userCaptor.getValue();
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getProvider()).isEqualTo(PROVIDER);
        assertThat(saved.getProviderId()).isEqualTo(PROVIDER_ID);
        assertThat(result.flowType()).isEqualTo(AuthFlowType.SIGN_UP);
        assertThat(result.user()).isEqualTo(saved);
    }

    @Test
    @DisplayName("기존 OAuth 유저면 프로필을 갱신하고 LOGIN 흐름을 반환한다")
    void createOrUpdateOAuthUser_updatesExistingUser() {
        User existing = TestFixtures.createUser();
        when(userRepository.findByProviderAndProviderId(PROVIDER, PROVIDER_ID))
                .thenReturn(Optional.of(existing));

        OAuthLoginResult result = oauthUserService.createOrUpdateOAuthUser(PROVIDER, PROVIDER_ID,
                TestFixtures.UPDATED_USERNAME, TestFixtures.UPDATED_EMAIL, TestFixtures.TEST_AVATAR_URL);

        verify(userRepository).update(existing);
        verify(userRepository, never()).save(any());

        assertThat(existing.getUsername()).isEqualTo(TestFixtures.UPDATED_USERNAME);
        assertThat(existing.getEmail()).isEqualTo(TestFixtures.UPDATED_EMAIL);
        assertThat(result.flowType()).isEqualTo(AuthFlowType.LOGIN);
        assertThat(result.user()).isEqualTo(existing);
    }

}
