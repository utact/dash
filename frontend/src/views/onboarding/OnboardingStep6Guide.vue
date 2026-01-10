<template>
  <div class="min-h-screen bg-slate-50 text-slate-900 p-6 flex flex-col items-center justify-center relative overflow-hidden font-['Pretendard']">
    
    <!-- Background -->
    <div class="absolute inset-0 bg-white pointer-events-none"></div>
    <div class="absolute top-[-20%] right-[-10%] w-[800px] h-[800px] bg-brand-50 rounded-full blur-[100px] pointer-events-none opacity-60"></div>

    <div class="max-w-4xl w-full relative z-10 text-center animate-fade-up px-4">

      <!-- Header -->
      <h1 class="text-3xl md:text-5xl font-black mb-16 text-slate-900 tracking-tight leading-tight">
        모든 설정이 완료되었습니다.
      </h1>

      <!-- Visual Workflow Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16 relative">
          <!-- Connecting Line (Desktop) -->
          <div class="hidden md:block absolute top-[2.5rem] left-[10%] right-[10%] h-0.5 bg-gradient-to-r from-slate-200 via-brand-200 to-slate-200 -z-10"></div>

          <!-- Step 1 -->
          <div class="flex flex-col items-center">
              <div class="w-20 h-20 bg-white rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center justify-center mb-6 relative z-10">
                  <Code2 class="w-8 h-8 text-slate-700" />
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">자유로운 문제 풀이</h3>
              <p class="text-sm text-slate-500 font-medium leading-relaxed max-w-[200px]">
                <span class="font-bold text-slate-700">백준</span>, <span class="font-bold text-slate-700">SWEA</span> 어디서든<br>
                평소처럼 문제를 푸세요
              </p>
          </div>

          <!-- Step 2 -->
          <div class="flex flex-col items-center">
             <div class="w-20 h-20 bg-white rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center justify-center mb-6 relative z-10">
                  <RefreshCw class="w-8 h-8 text-brand-500 animate-spin-slow" />
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">자동 기록 & 동기화</h3>
              <p class="text-sm text-slate-500 font-medium leading-relaxed max-w-[200px]">
                제출한 코드는 자동으로<br>
                Git 저장소에 푸시됩니다
              </p>
          </div>

          <!-- Step 3 -->
          <div class="flex flex-col items-center">
              <div class="w-20 h-20 bg-white rounded-2xl border border-slate-100 shadow-xl shadow-slate-200/50 flex items-center justify-center mb-6 relative z-10">
                  <BarChart3 class="w-8 h-8 text-slate-700" />
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">실시간 분석</h3>
              <p class="text-sm text-slate-500 font-medium leading-relaxed max-w-[200px]">
                대시보드에서 내 성장을<br>
                데이터로 확인하세요
              </p>
          </div>
      </div>

      <!-- Action Button -->
      <button 
        @click="emit('finish')"
        class="group relative inline-flex items-center gap-3 px-12 py-5 bg-slate-900 text-white rounded-2xl font-bold text-xl hover:bg-brand-600 hover:-translate-y-1 transition-all duration-300 shadow-xl shadow-slate-900/10 hover:shadow-brand-600/20"
      >
        <span>대시보드 시작하기</span>
        <ArrowRight class="w-6 h-6 group-hover:translate-x-1 transition-transform" />
      </button>

    </div>
  </div>
</template>

<script setup>
import { ArrowRight, Code2, RefreshCw, BarChart3 } from 'lucide-vue-next';
import { useAuth } from '@/composables/useAuth';

const emit = defineEmits(['finish']);
const { user } = useAuth();
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');
* { font-family: 'Pretendard', sans-serif; }

.animate-fade-up {
  animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

.animate-spin-slow {
    animation: spin 8s linear infinite;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}
</style>
