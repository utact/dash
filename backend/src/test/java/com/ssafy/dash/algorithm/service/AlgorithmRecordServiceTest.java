package com.ssafy.dash.algorithm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.ssafy.dash.algorithm.domain.AlgorithmRecord;
import com.ssafy.dash.algorithm.dto.AlgorithmRecordCreateRequest;
import com.ssafy.dash.algorithm.dto.AlgorithmRecordResponse;
import com.ssafy.dash.algorithm.dto.AlgorithmRecordUpdateRequest;
import com.ssafy.dash.algorithm.mapper.AlgorithmRecordMapper;
import com.ssafy.dash.common.TestFixtures;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlgorithmRecordService 단위 테스트")
class AlgorithmRecordServiceTest {

    @Mock
    private AlgorithmRecordMapper algorithmRecordMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AlgorithmRecordService algorithmRecordService;

    private User user;
    private AlgorithmRecord record;

    @BeforeEach
    void setUp() {
        user = TestFixtures.createUser();
        record = TestFixtures.createAlgorithmRecord(user);
    }

    @Test
    @DisplayName("알고리즘 기록 생성 성공")
    void createRecord_Success() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "Main.java", "text/plain", TestFixtures.TEST_ALGORITHM_CODE.getBytes(StandardCharsets.UTF_8));
        AlgorithmRecordCreateRequest req = TestFixtures.createAlgorithmRecordCreateRequest(file);
        
        given(userMapper.selectById(TestFixtures.TEST_USER_ID)).willReturn(user);
        
        // when
        AlgorithmRecordResponse response = algorithmRecordService.create(TestFixtures.TEST_USER_ID, req);

        // then
        verify(algorithmRecordMapper).insert(any(AlgorithmRecord.class));
        assertThat(response.getProblemNumber()).isEqualTo(req.getProblemNumber());
        assertThat(response.getCode()).isEqualTo(TestFixtures.TEST_ALGORITHM_CODE);
    }

    @Test
    @DisplayName("알고리즘 기록 단건 조회 성공")
    void findById_Success() {
        // given
        given(algorithmRecordMapper.selectById(TestFixtures.TEST_ALGORITHM_RECORD_ID)).willReturn(record);

        // when
        AlgorithmRecordResponse response = algorithmRecordService.findById(TestFixtures.TEST_ALGORITHM_RECORD_ID);

        // then
        assertThat(response.getId()).isEqualTo(TestFixtures.TEST_ALGORITHM_RECORD_ID);
        assertThat(response.getTitle()).isEqualTo(TestFixtures.TEST_ALGORITHM_TITLE);
    }

    @Test
    @DisplayName("알고리즘 기록 전체 조회 성공")
    void findAll_Success() {
        // given
        given(algorithmRecordMapper.selectAll()).willReturn(List.of(record));

        // when
        List<AlgorithmRecordResponse> responses = algorithmRecordService.findAll();

        // then
        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("알고리즘 기록 수정 성공")
    void updateRecord_Success() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "Main.java", "text/plain", "updated code".getBytes(StandardCharsets.UTF_8));
        AlgorithmRecordUpdateRequest req = TestFixtures.createAlgorithmRecordUpdateRequest(file);
        
        given(algorithmRecordMapper.selectById(TestFixtures.TEST_ALGORITHM_RECORD_ID)).willReturn(record);

        // when
        AlgorithmRecordResponse response = algorithmRecordService.update(TestFixtures.TEST_ALGORITHM_RECORD_ID, req);

        // then
        verify(algorithmRecordMapper).update(any(AlgorithmRecord.class));
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.getCode()).isEqualTo("updated code");
    }

    @Test
    @DisplayName("알고리즘 기록 삭제 성공")
    void deleteRecord_Success() {
        // given
        given(algorithmRecordMapper.delete(TestFixtures.TEST_ALGORITHM_RECORD_ID)).willReturn(1);

        // when
        algorithmRecordService.delete(TestFixtures.TEST_ALGORITHM_RECORD_ID);

        // then
        verify(algorithmRecordMapper).delete(TestFixtures.TEST_ALGORITHM_RECORD_ID);
    }
    
}
