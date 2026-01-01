package com.ssafy.dash.ai.infrastructure.client.dto.request;

import lombok.Builder;
import lombok.Getter;

import org.apache.ibatis.type.Alias;

/**
 * 코드 분석 요청 DTO
 */
@Getter
@Builder
@Alias("AiCodeReviewRequest")
public class CodeReviewRequest {
    private String code;
    private String language;
    private String problemNumber;
    private String platform;
    private String problemTitle;
}
