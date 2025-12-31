# 프론트엔드 리팩토링 로그

## 2025-12-05

### 인프라 구축
- `axios` 및 `pinia` 패키지 설치 완료.
- `main.js`에 Pinia 플러그인 설정 및 적용.

### API 계층 분리 (API Layer Separation)
- **`src/api/http.js`**: `axios` 인스턴스 생성 (Base URL 설정, 인터셉터 준비).
- **`src/api/auth.js`**: 인증 관련 API (세션 체크, 로그아웃) 모듈화.
- **`src/api/onboarding.js`**: 온보딩(저장소 등록) 관련 API 모듈화.
- **`src/api/user.js`**: 사용자 정보 조회 API 모듈화.

### 마이그레이션 (Migration)
직접적인 `fetch` 호출을 API 모듈 호출로 변경하였습니다.
- **`useAuth.js`**: `userApi.getMyProfile()`을 사용하여 사용자 정보 조회.
- **`OauthRedirect.vue`**: `authApi.getSession()` 및 `onboardingApi.submitRepository()` 사용.
- **`CommonHeader.vue`**: `authApi.logout()` 사용하여 로그아웃 처리.
- **`Landing.vue`**: `authApi.logout()` 사용 및 하드코딩된 로그인 URL을 상대 경로로 변경.

## 2025-12-31

### 뷰 구조 재편 (View Structure Restructure)
프로젝트 구조의 유지보수성을 높이기 위해 루트 레벨에 있던 뷰 파일들을 도메인별 하위 디렉토리로 이동하였습니다.

- **Dashboard**: `views/Dashboard.vue` -> `views/dashboard/DashboardView.vue`
- **Training**: `views/TrainingView.vue` -> `views/training/TrainingView.vue`
- **Defense**: `views/DefenseView.vue` -> `views/defense/DefenseView.vue`
- **Landing**: `views/LandingView.vue` -> `views/landing/LandingView.vue`
- **User**: `views/Profile.vue` -> `views/user/ProfileView.vue`
- **MockExam**: `views/MockExamView.vue` -> `views/mockexam/MockExamView.vue`

### 문서화 (Documentation)
- **`DESIGN_SYSTEM.md`**: 루트에서 `frontend/docs/`로 이동하여 문서 관리 일원화.
- **`PAGES.md`**: 이동된 파일 경로 반영하여 최신화.
