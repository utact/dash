package com.ssafy.dash.ai.client;

import com.ssafy.dash.ai.client.dto.CodeReviewRequest;
import com.ssafy.dash.ai.client.dto.CodeReviewResponse;

/**
 * AI 서버 통신 클라이언트 인터페이스
 */
public interface AiServerClient {

    /**
     * 코드 분석 요청
     * 
     * @param request 분석할 코드 정보
     * @return 분석 결과
     */
    CodeReviewResponse analyzeCode(CodeReviewRequest request);
}
