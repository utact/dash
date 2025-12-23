import http from './http';

export const tagApi = {
    // 사용자 스킬 트리 조회 (카드 뷰)
    getSkillTree: (userId) => http.get(`/tags/user/${userId}/skill-tree`),

    // 그래프 스킬 트리 조회 (노드/엣지)
    getSkillGraph: (userId) => http.get(`/tags/graph/${userId}`),

    // 선수 관계 초기화 (개발용)
    initPrerequisites: () => http.post('/tags/prerequisites/init'),
};
