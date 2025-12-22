package com.ssafy.dash.problem.presentation;

import com.ssafy.dash.problem.application.SkillTreeService;
import com.ssafy.dash.problem.presentation.dto.SkillTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 태그 및 스킬 트리 API 컨트롤러
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final SkillTreeService skillTreeService;

    /**
     * 사용자 스킬 트리 조회
     * 모든 태그 Family와 하위 태그를 트리 구조로 반환
     */
    @GetMapping("/user/{userId}/skill-tree")
    public ResponseEntity<SkillTreeResponse> getUserSkillTree(@PathVariable Long userId) {
        SkillTreeResponse response = skillTreeService.getSkillTree(userId);
        return ResponseEntity.ok(response);
    }
}
