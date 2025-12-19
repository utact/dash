package com.ssafy.dash.ai.application;

import com.ssafy.dash.ai.client.AiServerClient;
import com.ssafy.dash.ai.client.dto.HintRequest;
import com.ssafy.dash.ai.client.dto.HintResponse;
import com.ssafy.dash.analytics.application.UserSkillAnalysisService;
import com.ssafy.dash.analytics.application.dto.TagWeaknessDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HintService 테스트")
class HintServiceTest {

    @Mock
    private AiServerClient aiClient;

    @Mock
    private UserSkillAnalysisService skillAnalysisService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.ssafy.dash.acorn.application.AcornService acornService;

    private HintService hintService;

    @BeforeEach
    void setUp() {
        hintService = new HintService(aiClient, skillAnalysisService, userRepository, acornService);
    }

    @Test
    @DisplayName("힌트 생성 시 사용자 컨텍스트 포함 및 도토리 차감")
    void generateHint_shouldIncludeUserContext() {
        // given
        Long userId = 1L;
        Long studyId = 10L;
        String problemNumber = "1000";
        String problemTitle = "A+B";
        int level = 1;

        User user = createUser(userId, 11, 150); // Gold V
        user.updateStudy(studyId); // Set studyId

        TagWeaknessDto weakness = TagWeaknessDto.builder()
                .tagKey("dp")
                .tagName("다이나믹 프로그래밍")
                .solved(5)
                .build();

        HintResponse mockResponse = new HintResponse();
        mockResponse.setLevel(level);
        mockResponse.setHint("테스트 힌트");
        mockResponse.setEncouragement("잘하고 있어요!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillAnalysisService.getWeaknessTags(userId)).thenReturn(List.of(weakness));
        when(aiClient.generateHint(any(HintRequest.class))).thenReturn(mockResponse);

        // when
        HintResponse result = hintService.generateHint(userId, problemNumber, problemTitle, level);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getHint()).isEqualTo("테스트 힌트");

        // Verify Acorn Usage
        verify(acornService).use(eq(studyId), eq(userId), eq(5), anyString());

        ArgumentCaptor<HintRequest> captor = ArgumentCaptor.forClass(HintRequest.class);
        verify(aiClient).generateHint(captor.capture());

        HintRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getProblemNumber()).isEqualTo(problemNumber);
        assertThat(capturedRequest.getLevel()).isEqualTo(level);
        assertThat(capturedRequest.getUserContext()).isNotNull();
        assertThat(capturedRequest.getUserContext().getTier()).isEqualTo(11);
    }

    @Test
    @DisplayName("스터디 미가입 사용자는 힌트 사용 불가 (예외 발생)")
    void generateHint_shouldThrowExceptionStartingWithoutStudy() {
        // given
        Long userId = 1L;
        User user = createUser(userId, 0, 0); 
        // No studyId set

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            hintService.generateHint(userId, "1000", "A+B", 1);
        });
    }

    private User createUser(Long id, Integer tier, Integer rating) {
        User user = new User();
        user.setId(id);
        user.setSolvedacTier(tier);
        user.setSolvedacRating(rating);
        return user;
    }
}
