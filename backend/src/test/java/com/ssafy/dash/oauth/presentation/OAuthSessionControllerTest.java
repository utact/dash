package com.ssafy.dash.oauth.presentation;

import com.ssafy.dash.common.TestFixtures;
import com.ssafy.dash.oauth.application.dto.result.OAuthLoginResult;
import com.ssafy.dash.oauth.domain.AuthFlowType;
import com.ssafy.dash.oauth.presentation.security.CustomOAuth2User;
import com.ssafy.dash.user.application.UserService;
import com.ssafy.dash.user.application.dto.result.UserResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(OAuthSessionControllerTest.TestConfig.class)
@DisplayName("OAuthSessionController 테스트")
class OAuthSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("세션을 조회하면 로그인 흐름 정보를 반환한다")
    void sessionReturnsFlowType() throws Exception {
        UserResult userResult = TestFixtures.createUserResult();
        given(userService.findById(TestFixtures.TEST_USER_ID)).willReturn(userResult);

        OAuth2User delegate = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("id", TestFixtures.TEST_PROVIDER_ID),
                "id");
        OAuthLoginResult loginResult = new OAuthLoginResult(TestFixtures.createUser(), AuthFlowType.SIGN_UP);
        CustomOAuth2User customUser = new CustomOAuth2User(delegate, loginResult);

        AbstractAuthenticationToken authentication = new OAuth2AuthenticationToken(customUser,
                customUser.getAuthorities(), TestFixtures.TEST_PROVIDER);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/api/oauth/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flowType").value("SIGN_UP"))
                .andExpect(jsonPath("$.user.id").value(TestFixtures.TEST_USER_ID));
    }

    @Test
    @DisplayName("로그인하지 않고 세션을 조회하면 401을 반환한다")
    void sessionReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/oauth/session"))
                .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(userService);
    }

}
