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
const hasNavigatedManually = ref(false); // Track if user has clicked 'next'

const currentStepComponent = computed(() => {
  switch (steps[currentStepIndex.value]) {
    case 'welcome': return OnboardingWelcome;
    case 'analysis': return OnboardingAnalysis;
    case 'study': return OnboardingStudy;
    case 'repo': return OnboardingRepository;
    default: return OnboardingWelcome;
  }
});

// Only called on initial mount to restore state from backend
const determineInitialStep = () => {
  if (!user.value) {
    currentStepIndex.value = 0; // welcome
    return;
  }

  // If user has everything -> redirect to dashboard
  if (user.value.solvedacId && user.value.studyId && user.value.repositoryName) {
    router.replace('/dashboard');
    return;
  }

  // No solvedacId -> Welcome step
  if (!user.value.solvedacId) {
    currentStepIndex.value = 0; // welcome
    return;
  }

  // Has solvedacId but no studyId -> Study step (skip analysis on refresh)
  if (!user.value.studyId) {
    currentStepIndex.value = 2; // study
    return;
  }

  // Has studyId but no repositoryName -> Repository step
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

// Watch for user changes ONLY to detect approval (studyId update)
// This should only ADVANCE, never RESET
watch(() => user.value?.studyId, (newStudyId, oldStudyId) => {
  if (!loading.value && !oldStudyId && newStudyId) {
    // User just got approved for a study -> advance to repo step
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
