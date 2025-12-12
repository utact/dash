<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-950 text-slate-100 p-6">
    <div class="max-w-md w-full space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div class="text-center space-y-2">
        <h1 class="text-3xl font-bold tracking-tight bg-gradient-to-r from-indigo-400 to-cyan-400 bg-clip-text text-transparent">
          Solved.ac 연동
        </h1>
        <p class="text-slate-400">
          알고리즘 실력을 분석하기 위해 아이디를 입력해주세요.
        </p>
      </div>

      <div class="bg-white/5 border border-white/10 rounded-2xl p-6 shadow-2xl backdrop-blur-sm">
        <form @submit.prevent="submitHandle" class="space-y-6">
          <div class="space-y-2">
            <label class="text-sm font-medium text-slate-300">Solved.ac ID</label>
            <input
              v-model="handle"
              type="text"
              placeholder="백준 핸들을 입력하세요"
              class="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all font-mono"
              :disabled="loading"
              required
            />
          </div>

          <button
            type="submit"
            class="w-full bg-indigo-500 hover:bg-indigo-400 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold py-3.5 rounded-xl transition-all shadow-[0_0_20px_rgba(99,102,241,0.3)] hover:shadow-[0_0_30px_rgba(99,102,241,0.5)] flex items-center justify-center gap-2"
            :disabled="loading || !handle"
          >
            <svg v-if="loading" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ loading ? '연동 중...' : '다음으로' }}</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { onboardingApi } from '../../api/onboarding';

const router = useRouter();
const handle = ref('');
const loading = ref(false);

const submitHandle = async () => {
  if (!handle.value) return;
  
  loading.value = true;
  try {
    await onboardingApi.registerSolvedac(handle.value);
    router.push('/onboarding/analysis');
  } catch (error) {
    console.error(error);
    alert('아이디 연동에 실패했습니다. 올바른 아이디인지 확인해주세요.');
  } finally {
    loading.value = false;
  }
};
</script>
