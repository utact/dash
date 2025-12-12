package com.ssafy.dash.ai.presentation;

import com.ssafy.dash.ai.application.CodeReviewService;
import com.ssafy.dash.ai.domain.CodeAnalysisResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI 코드 분석 API 컨트롤러
 */
@Tag(name = "AI Code Review", description = "AI 기반 코드 분석 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final CodeReviewService codeReviewService;

    @Operation(summary = "코드 분석 요청", description = "알고리즘 풀이 코드를 AI로 분석합니다")
    @PostMapping("/review")
    public ResponseEntity<CodeAnalysisResult> analyzeCode(@RequestBody CodeReviewRequest request) {
        CodeAnalysisResult result = codeReviewService.analyzeAndSave(
                request.algorithmRecordId(),
                request.code(),
                request.language(),
                request.problemNumber());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "분석 결과 조회", description = "저장된 분석 결과를 조회합니다")
    @GetMapping("/review/{algorithmRecordId}")
    public ResponseEntity<CodeAnalysisResult> getAnalysisResult(@PathVariable Long algorithmRecordId) {
        return codeReviewService.getAnalysisResult(algorithmRecordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CodeReviewRequest(
            Long algorithmRecordId,
            String code,
            String language,
            String problemNumber) {
    }
}
