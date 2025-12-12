package com.ssafy.dash.ai.application;

import com.ssafy.dash.ai.client.AiServerClient;
import com.ssafy.dash.ai.client.dto.CodingStyleRequest;
import com.ssafy.dash.ai.client.dto.CodingStyleResponse;
import com.ssafy.dash.algorithm.domain.AlgorithmRecord;
import com.ssafy.dash.algorithm.infrastructure.mapper.AlgorithmRecordMapper;
import com.ssafy.dash.analytics.domain.UserTagStat;
import com.ssafy.dash.analytics.infrastructure.persistence.UserTagStatMapper;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CodingStyleService 테스트")
class CodingStyleServiceTest {

    @Mock
    private AiServerClient aiClient;

    @Mock
    private AlgorithmRecordMapper recordMapper;

    @Mock
    private UserTagStatMapper tagStatMapper;

    @Mock
    private UserRepository userRepository;

    private CodingStyleService codingStyleService;

    @BeforeEach
    void setUp() {
        codingStyleService = new CodingStyleService(aiClient, recordMapper, tagStatMapper, userRepository);
    }

    @Test
    @DisplayName("코딩 스타일 분석 시 코드 샘플 포함")
    void analyzeCodingStyle_shouldIncludeCodeSamples() {
        // given
        Long userId = 1L;
        User user = createUser(userId, 11, 150);

        AlgorithmRecord record = new AlgorithmRecord();
        record.setUserId(userId);
        record.setCode("public class Solution {}");
        record.setLanguage("java");
        record.setProblemNumber("1000");
        record.setRuntimeMs(100);
        record.setMemoryKb(15000);

        UserTagStat tagStat = new UserTagStat();
        tagStat.setTagKey("dp");
        tagStat.setSolved(30);

        CodingStyleResponse mockResponse = createMockResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recordMapper.selectByUserId(userId)).thenReturn(List.of(record));
        when(tagStatMapper.findByUserId(userId)).thenReturn(List.of(tagStat));
        when(aiClient.analyzeCodingStyle(any(CodingStyleRequest.class))).thenReturn(mockResponse);

        // when
        CodingStyleResponse result = codingStyleService.analyzeCodingStyle(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMbtiCode()).isEqualTo("INTJ");

        ArgumentCaptor<CodingStyleRequest> captor = ArgumentCaptor.forClass(CodingStyleRequest.class);
        verify(aiClient).analyzeCodingStyle(captor.capture());

        CodingStyleRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getCodeSamples()).hasSize(1);
        assertThat(capturedRequest.getUserStats()).isNotNull();
        assertThat(capturedRequest.getUserStats().getTier()).contains("Gold");
    }

    @Test
    @DisplayName("코드 샘플 없으면 예외 발생")
    void analyzeCodingStyle_shouldThrowWhenNoCodeSamples() {
        // given
        Long userId = 1L;
        User user = createUser(userId, 11, 150);

        AlgorithmRecord recordWithoutCode = new AlgorithmRecord();
        recordWithoutCode.setUserId(userId);
        recordWithoutCode.setCode(null); // 코드 없음

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recordMapper.selectByUserId(userId)).thenReturn(List.of(recordWithoutCode));

        // when & then
        assertThatThrownBy(() -> codingStyleService.analyzeCodingStyle(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("분석할 코드가 없습니다");
    }

    @Test
    @DisplayName("사용자 없으면 예외 발생")
    void analyzeCodingStyle_shouldThrowWhenUserNotFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codingStyleService.analyzeCodingStyle(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    private User createUser(Long id, Integer tier, Integer rating) {
        User user = new User();
        user.setId(id);
        user.setSolvedacTier(tier);
        user.setSolvedacRating(rating);
        return user;
    }

    private CodingStyleResponse createMockResponse() {
        CodingStyleResponse response = new CodingStyleResponse();
        response.setMbtiCode("INTJ");
        response.setNickname("논리적 설계자");
        response.setSummary("테스트 요약");
        response.setAxes(List.of());
        response.setStrengths(List.of("강점1"));
        response.setImprovements(List.of("개선점1"));
        response.setCompatibleStyles("ENTJ");
        response.setAdvice("조언");
        return response;
    }
}
