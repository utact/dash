<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 text-slate-800 p-6 relative overflow-hidden font-[Pretendard]">
    
    <!-- Background Decor -->
    <div class="absolute top-[-10%] left-[-5%] w-[40vw] h-[40vw] bg-purple-200/40 rounded-full blur-[100px] animate-blob mix-blend-multiply"></div>
    <div class="absolute bottom-[-10%] right-[-5%] w-[40vw] h-[40vw] bg-indigo-200/40 rounded-full blur-[100px] animate-blob animation-delay-2000 mix-blend-multiply"></div>

    <div class="max-w-2xl w-full text-center space-y-12 relative z-10">
      <!-- Loading State -->
      <div v-if="loading" class="space-y-8 animate-in fade-in duration-700">
        <div class="relative w-24 h-24 mx-auto">
          <div class="absolute inset-0 border-4 border-indigo-200/50 rounded-full animate-ping"></div>
          <div class="absolute inset-0 border-4 border-indigo-500 rounded-full animate-[spin_3s_linear_infinite]"></div>
          <div class="absolute inset-2 bg-indigo-500/10 rounded-full backdrop-blur-md flex items-center justify-center">
            <span class="text-3xl">🧠</span>
          </div>
        </div>
        <div class="space-y-4">
          <h2 class="text-3xl font-extrabold bg-gradient-to-r from-indigo-600 to-purple-500 bg-clip-text text-transparent animate-pulse">
            AI가 알고리즘 성향을 분석 중입니다...
          </h2>
          <p class="text-slate-500 font-medium">
            풀이 스타일, 자주 사용하는 언어, 취약 유형을 파악하고 있습니다.
          </p>
        </div>
      </div>

      <!-- Result State -->
      <div v-else class="space-y-8 animate-in zoom-in-95 duration-500">
        <h1 class="text-4xl font-extrabold text-slate-800 tracking-tight">분석 완료!</h1>
        
        <div class="bg-white/80 border border-white/60 rounded-3xl p-8 backdrop-blur-xl relative overflow-hidden shadow-xl shadow-indigo-500/10 group hover:border-indigo-500/30 transition-all">
          <div class="absolute top-0 right-0 p-32 bg-indigo-500/10 blur-[80px] rounded-full group-hover:bg-indigo-500/20 transition-all"></div>
          
          <div class="relative z-10 grid grid-cols-1 md:grid-cols-2 gap-8 text-left">
            <div class="space-y-4">
              <h3 class="text-lg font-bold text-slate-500 uppercase tracking-widest">나의 코딩 스타일</h3>
              <div class="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600 leading-tight">
                {{ styleResult?.codingStyle || '전략적인 설계자' }}
              </div>
              <p class="text-slate-600 text-sm leading-relaxed font-medium">
                {{ styleResult?.description || '문제의 핵심을 빠르게 파악하고 효율적인 자료구조를 선택하는 능력이 탁월합니다.' }}
              </p>
            </div>
            
            <div class="space-y-4">
              <h3 class="text-lg font-bold text-slate-500 uppercase tracking-widest">추천 학습 경로</h3>
              <ul class="space-y-2">
                <li v-for="tag in styleResult?.recommendedTags || ['Dynamic Programming', 'Graph Theory', 'Greedy']" :key="tag" 
                    class="flex items-center gap-3 text-slate-700 bg-white/60 border border-white/50 rounded-xl px-4 py-3 shadow-sm">
                  <span class="w-2 h-2 rounded-full bg-cyan-500 shadow-[0_0_10px_rgba(6,182,212,0.5)]"></span>
                  <span class="font-semibold">{{ tag }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <button 
          @click="nextStep"
          class="px-10 py-4 bg-slate-900 text-white font-bold rounded-2xl hover:bg-slate-800 hover:scale-105 transition-all shadow-lg hover:shadow-xl"
        >
          나에게 맞는 스터디 찾기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { aiApi } from '../../api/ai';
import { useAuth } from '../../composables/useAuth';

const router = useRouter();
const { user } = useAuth();
const loading = ref(true);
const styleResult = ref(null);

  onMounted(async () => {
    if (!user.value) {
      // 사용자가 아직 로드되지 않은 경우, 대기하거나 리다이렉트합니다.
      // 보통 라우터 가드나 부모 컴포넌트에서 인증이 처리됩니다.
      // 사용자가 존재한다고 가정하거나 대기합니다.
      // 먼저 사용자 존재 여부를 확인합니다.
    }
    
    try {
       // 인증 컨텍스트에서 userId를 사용합니다.
       // 사용할 수 없는 경우 모의 ID로 대체합니다 (실제 흐름에서는 발생하지 않아야 함).
       const userId = user.value?.id || 1; 
       
       const res = await aiApi.getCodingStyle(userId);
       const data = res.data; // CodingStyleResponse
       
       styleResult.value = {
          codingStyle: `${data.nickname} (${data.mbtiCode})`,
          description: data.summary,
          recommendedTags: data.strengths?.slice(0, 3) || ['Algorithm', 'Data Structure']
       };
    } catch (e) {
        console.error("AI Analysis failed", e);
        // 대체 UI 또는 오류 시각화
        styleResult.value = {
          codingStyle: '분석 실패',
          description: 'AI 서버에 연결할 수 없습니다. 나중에 다시 시도해주세요.',
          recommendedTags: []
        };
    } finally {
        loading.value = false;
    }
  });

const nextStep = () => {
  router.push('/onboarding/study');
};
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');

.animate-blob {
  animation: blob 10s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}
</style>
