<template>
  <div class="relative w-full h-full">
    <!-- Loading State -->
    <div v-if="loading" class="min-h-screen flex items-center justify-center bg-slate-50">
      <div class="text-center">
        <div class="w-12 h-12 border-4 border-brand-200 border-t-brand-600 rounded-full animate-spin mx-auto mb-4"></div>
        <p class="text-slate-500 font-medium">온보딩 상태 확인 중...</p>
      </div>
    </div>

    <transition v-else name="fade-slide" mode="out-in">
      <component 
        :is="currentStepComponent" 
        @next="nextStep"
        @finish="finishOnboarding"
      />
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '../../composables/useAuth';
import OnboardingWelcome from './OnboardingWelcome.vue';
import OnboardingAnalysis from './OnboardingAnalysis.vue';
import OnboardingStudy from './OnboardingStudy.vue';
import OnboardingRepository from './OnboardingRepository.vue';

const router = useRouter();
const { user, refresh } = useAuth();
const loading = ref(true);
const steps = ['welcome', 'analysis', 'study', 'repo'];
const currentStepIndex = ref(0);
const hasNavigatedManually = ref(false); // 사용자가 '다음'을 클릭했는지 추적

const currentStepComponent = computed(() => {
  switch (steps[currentStepIndex.value]) {
    case 'welcome': return OnboardingWelcome;
    case 'analysis': return OnboardingAnalysis;
    case 'study': return OnboardingStudy;
    case 'repo': return OnboardingRepository;
    default: return OnboardingWelcome;
  }
});

// 초기 마운트 시에만 호출되어 백엔드 상태 복원
const determineInitialStep = () => {
  if (!user.value) {
    currentStepIndex.value = 0; // welcome
    return;
  }

  // 사용자가 모든 단계를 완료한 경우 -> 대시보드로 리다이렉트
  if (user.value.solvedacId && user.value.studyId && user.value.repositoryName) {
    router.replace('/dashboard');
    return;
  }

  // solvedacId 없음 -> 환영 단계
  if (!user.value.solvedacId) {
    currentStepIndex.value = 0; // welcome
    return;
  }

  // solvedacId 있음, studyId 없음 -> 스터디 단계 (새로고침 시 분석 생략)
  if (!user.value.studyId) {
    currentStepIndex.value = 2; // study
    return;
  }

  // studyId 있음, repositoryName 없음 -> 저장소 단계
  if (!user.value.repositoryName) {
    currentStepIndex.value = 3; // repo
    return;
  }
};

onMounted(async () => {
  await refresh();
  determineInitialStep();
  loading.value = false;
});

// 오직 승인(studyId 업데이트) 감지를 위해서만 사용자 변경 감시
// 이는 RESET이 아니라 ADVANCE(진행)만 해야 함
watch(() => user.value?.studyId, (newStudyId, oldStudyId) => {
  if (!loading.value && !oldStudyId && newStudyId) {
    // 사용자가 스터디 승인을 받음 -> 저장소 단계로 진행
    currentStepIndex.value = 3;
  }
});

const nextStep = () => {
  hasNavigatedManually.value = true;
  if (currentStepIndex.value < steps.length - 1) {
    currentStepIndex.value++;
  }
};

const finishOnboarding = () => {
  router.replace('/dashboard');
};
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
