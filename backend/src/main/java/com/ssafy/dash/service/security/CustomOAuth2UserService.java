package com.ssafy.dash.service.security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ssafy.dash.dto.CustomOAuth2User;
import com.ssafy.dash.dto.GitHubResponse;
import com.ssafy.dash.dto.OAuth2Response;
import com.ssafy.dash.entity.OAuthToken;
import com.ssafy.dash.repository.OAuthTokenRepository;
import com.ssafy.dash.entity.UserEntity;
import com.ssafy.dash.repository.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final OAuthTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public CustomOAuth2UserService(OAuthTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2AccessToken accessToken = userRequest.getAccessToken();

        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2Response response;
        if ("github".equalsIgnoreCase(registrationId)) {
            response = new GitHubResponse(attributes);
        }
        else {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }

        // 액세스 토큰을 나중에 사용하기 위해 영속화 (웹훅 처리)
        String provider = response.getProvider();
        String providerId = response.getProviderId();
        String providerLogin = null;
        Object loginObj = attributes.get("login");
        if (loginObj != null) providerLogin = loginObj.toString();
        if (provider != null && providerId != null && accessToken != null) {
            OAuthToken t = null;
            if (providerLogin != null) {
                t = tokenRepository.findByProviderAndProviderLogin(provider, providerLogin).orElseGet(OAuthToken::new);
            }
            else {
                t = tokenRepository.findByProviderAndProviderId(provider, providerId).orElseGet(OAuthToken::new);
            }
            t.setProvider(provider);
            t.setProviderId(providerId);
            t.setProviderLogin(providerLogin);
            t.setAccessToken(accessToken.getTokenValue());
            tokenRepository.save(t);
        }

        // 유저 정보도 저장/갱신
        String username = response.getProvider() + "_" + response.getProviderId();
        UserEntity userEntity = null;
        if (userRepository != null) {
            userEntity = userRepository.findByUsername(username)
                .map(entity -> {
                    entity.setEmail(response.getEmail());
                    return userRepository.save(entity);
                }).orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setUsername(username);
                    newUser.setEmail(response.getEmail());
                    newUser.setRole("ROLE_USER");
                    return userRepository.save(newUser);
                });
        }

        String role = (userEntity != null && userEntity.getRole() != null) ? userEntity.getRole() : "ROLE_USER";
        return new CustomOAuth2User(response, role);
    }

}
