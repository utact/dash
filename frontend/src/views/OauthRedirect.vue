<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-950 text-slate-100">
    <div class="flex flex-col items-center justify-center space-y-4">
      <div v-if="error" class="text-center">
         <h2 class="text-xl font-bold text-red-400">로그인 실패</h2>
        <p class="text-sm text-slate-400">{{ error }}</p>
        <button @click="goHome" class="mt-4 px-6 py-2 rounded-full bg-slate-700 hover:bg-slate-600 text-white text-sm">
          홈으로 돌아가기
        </button>
      </div>
      <div v-else class="text-center space-y-4">
        <div class="relative w-16 h-16 mx-auto">
          <div class="absolute inset-0 border-4 border-indigo-500/30 rounded-full"></div>
          <div class="absolute inset-0 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
        </div>
        <h2 class="text-lg font-medium text-slate-200">
          {{ isSignUp ? '온보딩 페이지로 이동합니다...' : '로그인 중입니다...' }}
        </h2>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { authApi } from "../api/auth";

const router = useRouter();
const error = ref(null);
const flowType = ref(null);
const isSignUp = computed(() => flowType.value === "SIGN_UP");

const goHome = () => {
  window.location.href = "/";
};

onMounted(async () => {
  try {
    const res = await authApi.getSession();
    const data = res.data;
    flowType.value = data.flowType;

    setTimeout(() => {
        if (data.flowType === "SIGN_UP") {
            router.push("/onboarding/welcome");
        } else {
            window.location.href = "/";
        }
    }, 800);
    
  } catch (err) {
    error.value = err.message || "로그인 처리 중 오류가 발생했습니다.";
  }
});
</script>
