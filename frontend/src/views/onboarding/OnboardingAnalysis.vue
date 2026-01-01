<template>
  <div class="min-h-screen bg-slate-50 text-slate-800 p-6 flex flex-col items-center">

    <!-- Loading State -->
    <div v-if="loading" class="flex-1 flex items-center justify-center">
      <div class="text-center space-y-6">
        <div class="relative w-24 h-24 mx-auto">
          <div class="absolute inset-0 border-4 border-brand-200 rounded-full animate-ping opacity-75"></div>
          <div class="absolute inset-0 border-4 border-brand-500 rounded-full animate-spin"></div>
          <div class="absolute inset-3 bg-white rounded-full flex items-center justify-center shadow-lg">
            <span class="text-4xl">🧠</span>
          </div>
        </div>
        <div class="animate-pulse">
          <h2 class="text-2xl font-bold text-slate-800">AI 분석 중...</h2>
          <p class="text-slate-500 mt-2">알고리즘 실력을 정밀하게 측정하고 있습니다</p>
        </div>
      </div>
    </div>

    <!-- Main Content (Bento Grid) -->
    <div v-else class="w-full max-w-6xl py-8 space-y-8 animate-fade-in-up">
      
      <!-- Header -->
      <div class="flex items-center justify-between px-2">
        <div>
          <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight flex items-center gap-3">
             <span class="bg-brand-500 text-white w-10 h-10 rounded-xl flex items-center justify-center text-xl shadow-lg shadow-brand-200">📊</span>
             AI 종합 분석 리포트
          </h1>
          <p class="text-slate-500 mt-2 pl-1">
            <span class="font-bold text-brand-600">{{ user?.solvedacId || userContext?.handle || 'User' }}</span>님의 현재 실력과 성장 가능성을 분석했습니다.
          </p>
        </div>
        <button 
          @click="nextStep"
          class="hidden md:flex items-center gap-2 px-6 py-3 bg-slate-900 hover:bg-slate-800 text-white rounded-xl font-bold transition-all shadow-lg hover:shadow-xl hover:-translate-y-0.5"
        >
          <span>스터디 찾기</span>
          <ArrowRight class="w-4 h-4" />
        </button>
      </div>

      <!-- Bento Grid Layout -->
      <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
        
        <!-- 1. AI Summary (Top Left - Spans 8) -->
        <div class="md:col-span-8 bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
           <div class="absolute top-0 right-0 w-32 h-32 bg-brand-50 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 group-hover:bg-brand-100 transition-colors"></div>
           <div class="relative z-10">
              <div class="flex items-center gap-2 mb-4">
                 <Sparkles class="w-5 h-5 text-brand-500" fill="currentColor" />
                 <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">ANALYSIS SUMMARY</h3>
              </div>
              <p class="text-lg text-slate-700 leading-relaxed font-medium">
                {{ analysisData?.aiAnalysis?.analysisSummary || '분석 데이터를 불러오는 중입니다...' }}
              </p>
           </div>
        </div>

        <!-- 2. Days to Goal (Top Right - Spans 4) -->
        <div class="md:col-span-4 bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-center items-center relative overflow-hidden">
           <div class="absolute inset-0 bg-gradient-to-br from-slate-50 to-white"></div>
           <div class="relative z-10 text-center w-full">
              <div class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2">ESTIMATED TIME</div>
              <div class="text-5xl font-black text-brand-600 tracking-tight flex items-baseline justify-center gap-1">
                 {{ analysisData?.aiAnalysis?.estimatedDaysToGoal || '-' }}
                 <span class="text-lg font-bold text-slate-400">Days</span>
              </div>
              <p class="text-xs text-slate-400 mt-2">목표 달성까지 예상 소요 시간</p>
           </div>
        </div>

        <!-- 3. Radar Chart (Middle Left - Spans 4) -->
        <div class="md:col-span-4 bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-md transition-shadow flex flex-col">
           <div class="flex items-center gap-2 mb-4">
              <Radar class="w-4 h-4 text-slate-400" />
              <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">COMPETENCE</h3>
           </div>
           <div class="flex-1 flex items-center justify-center p-2">
              <div class="w-full aspect-square">
                 <AlgorithmRadarChart :stats="chartTags" :max-tags="8" class="w-full h-full" />
              </div>
           </div>
           <div class="mt-4 flex flex-wrap gap-2 justify-center">
              <span v-for="tag in analysisData?.aiAnalysis?.strengthTags?.slice(0,2)" :key="tag.tagKey" class="px-2 py-1 bg-emerald-50 text-emerald-600 text-[10px] font-bold rounded-md">
                + {{ tag.tagName }}
              </span>
           </div>
        </div>

        <!-- 4. Goals & Insights (Middle Center - Spans 4) -->
        <div class="md:col-span-4 flex flex-col gap-6">
           <!-- Weekly Goal -->
           <div class="bg-brand-50 rounded-3xl p-6 border border-brand-100 shadow-sm flex-1">
              <div class="flex items-center gap-2 mb-3">
                 <Target class="w-4 h-4 text-brand-600" />
                 <h3 class="text-xs font-black text-brand-600 uppercase tracking-widest">WEEKLY GOAL</h3>
              </div>
              <p class="text-slate-800 font-bold text-lg leading-snug">
                {{ analysisData?.aiAnalysis?.weeklyGoal || '목표를 설정중입니다...' }}
              </p>
              <div v-if="analysisData?.aiAnalysis?.difficultySuggestion" class="mt-4 pt-4 border-t border-brand-100/50">
                 <span class="text-xs font-bold text-brand-500 bg-white px-2 py-1 rounded-lg">
                    권장 난이도: {{ analysisData?.aiAnalysis?.difficultySuggestion }}
                 </span>
              </div>
           </div>

           <!-- Growth & Strategy -->
           <div class="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm flex-1">
              <div class="flex items-center gap-2 mb-3">
                 <TrendingUp class="w-4 h-4 text-violet-500" />
                 <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">GROWTH</h3>
              </div>
              <p class="text-sm text-slate-600 leading-relaxed">
                 {{ analysisData?.aiAnalysis?.growthPrediction || '성장 데이터를 분석중입니다...' }}
              </p>
              <p v-if="analysisData?.aiAnalysis?.strategicAdvice" class="mt-3 text-xs font-medium text-violet-600 bg-violet-50 p-2 rounded-lg">
                💡 {{ analysisData?.aiAnalysis?.strategicAdvice }}
              </p>
           </div>
        </div>

        <!-- 5. Roadmap (Middle Right - Spans 4, Row Span 2) -->
        <div class="md:col-span-4 bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-md transition-shadow relative md:row-span-2 flex flex-col">
           <div class="flex items-center gap-2 mb-6">
              <Map class="w-4 h-4 text-slate-400" />
              <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">ROADMAP</h3>
           </div>
           
           <!-- Roadmap Component in a scrollable container if needed -->
           <div class="flex-1 overflow-y-auto no-scrollbar pr-1 -mr-1">
              <LearningRoadmap :phases="analysisData?.aiAnalysis?.phases || []" class="w-full" />
           </div>

           <!-- Overlay Fade for overflow indication (optional) -->
           <div class="absolute bottom-0 left-0 w-full h-8 bg-gradient-to-t from-white to-transparent pointer-events-none rounded-b-3xl"></div>
        </div>
        
        <!-- 6. Bottom Banner: Recommended Tags (Spans 8) -->
        <div class="md:col-span-8 bg-gradient-to-r from-slate-50 to-white border border-slate-200 rounded-3xl p-6 shadow-sm flex flex-col md:flex-row items-center justify-between gap-6 relative overflow-hidden group">
           <!-- Decorative Background -->
           <div class="absolute top-0 right-0 w-64 h-64 bg-slate-100 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 opacity-50"></div>
           
           <div class="relative z-10 flex-1">
              <h3 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-2">RECOMMENDED FOCUS</h3>
              <div class="flex flex-wrap gap-2">
                <span 
                   v-for="(tag, idx) in analysisData?.aiAnalysis?.recommendedTags" 
                   :key="idx"
                   class="px-3 py-1.5 bg-white border border-slate-200 shadow-sm rounded-lg text-sm font-bold text-slate-700 transition-colors cursor-default"
                >
                   #{{ tag }}
                </span>
                <span v-if="!analysisData?.aiAnalysis?.recommendedTags?.length" class="text-sm text-slate-400">
                   추천 태그 분석 중...
                </span>
              </div>
           </div>
           
           <div class="relative z-10">
              <p class="text-sm text-slate-600 font-medium max-w-xs text-right hidden md:block">
                 {{ analysisData?.aiAnalysis?.motivationMessage || '꾸준함이 가장 빠른 지름길입니다.' }}
              </p>
           </div>
        </div>

      </div>

      <!-- Mobile Action Button -->
      <div class="md:hidden pt-4 pb-12">
        <button 
          @click="nextStep"
          class="w-full flex items-center justify-center gap-2 px-6 py-4 bg-slate-900 text-white rounded-2xl font-bold shadow-lg"
        >
          <span>스터디 찾기</span>
          <ArrowRight class="w-4 h-4" />
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
// Icons
import { Sparkles, ArrowRight, Target, TrendingUp, Map, Radar } from 'lucide-vue-next';
// Components
import AlgorithmRadarChart from '@/components/charts/AlgorithmRadarChart.vue';
import LearningRoadmap from '@/components/skill/LearningRoadmap.vue';
// API
import { aiApi } from '@/api/ai';
import { useAuth } from '@/composables/useAuth';
import { onboardingApi } from '@/api/onboarding';

const router = useRouter();
const { user, refresh } = useAuth();

const loading = ref(true);
const analysisData = ref(null);
const familyStats = ref([]);
const classStats = ref([]);
const userContext = ref(null);

const chartTags = computed(() => {
  // 1. If Family Stats exist (Ideal), use them
  if (familyStats.value && familyStats.value.length > 0) {
    return familyStats.value.map(stat => ({
      tagKey: stat.familyKey?.toLowerCase(),
      solved: stat.solved || 0,
      total: stat.total || 0,
      label: stat.familyName === '다이나믹 프로그래밍' ? 'DP' : stat.familyName
    }));
  }

  // 2. Fallback: Use Tags from AI Analysis (Like TrainingView)
  if (analysisData.value) {
      const strong = analysisData.value.strengthTags || [];
      const weak = analysisData.value.weaknessTags || [];
      const merged = [...strong, ...weak];
      
      // Remove duplicates by tagKey
      const uniqueMap = new Map();
      merged.forEach(t => uniqueMap.set(t.tagKey, t));
      
      return Array.from(uniqueMap.values()).map(t => ({
          tagKey: t.tagKey,
          solved: t.solved,
          total: t.total,
          label: t.tagName // Use tagName from AI response
      }));
  }
  
  return [];
});

// Solved.ac Tier Names
const TIER_NAMES = [
  "Unrated",
  "Bronze V", "Bronze IV", "Bronze III", "Bronze II", "Bronze I",
  "Silver V", "Silver IV", "Silver III", "Silver II", "Silver I",
  "Gold V", "Gold IV", "Gold III", "Gold II", "Gold I",
  "Platinum V", "Platinum IV", "Platinum III", "Platinum II", "Platinum I",
  "Diamond V", "Diamond IV", "Diamond III", "Diamond II", "Diamond I",
  "Ruby V", "Ruby IV", "Ruby III", "Ruby II", "Ruby I",
  "Master"
];

const getTierName = (tier) => {
  if (tier >= 0 && tier < TIER_NAMES.length) {
    return TIER_NAMES[tier];
  }
  return 'Unrated';
};

onMounted(async () => {
  try {
    await refresh();
    
    const userId = user.value?.id;
    const handle = user.value?.solvedacHandle;
    
    if (!handle) console.warn("No Solved.ac handle found after refresh");

    const [learningPathRes, familyRes, classRes, userRes] = await Promise.allSettled([
      userId ? aiApi.getLearningPath(userId) : Promise.resolve({ data: null }),
      userId ? aiApi.getFamilyStats(userId) : Promise.resolve({ data: [] }),
      userId ? aiApi.getClassStats(userId) : Promise.resolve({ data: [] }),
      handle ? onboardingApi.verifySolvedac(handle) : Promise.reject('No handle')
    ]);
    
    if (learningPathRes.status === 'fulfilled') {
      analysisData.value = learningPathRes.value.data;
    }
    if (familyRes.status === 'fulfilled') {
      familyStats.value = familyRes.value.data || [];
    }
    if (classRes.status === 'fulfilled') {
      classStats.value = classRes.value.data || [];
    }

    if (userRes.status === 'fulfilled') {
        userContext.value = userRes.value.data;
    } else if (user.value?.solvedacTier) {
        userContext.value = {
            tierName: getTierName(user.value.solvedacTier),
            tier: user.value.solvedacTier
        };
    } else {
        userContext.value = { 
            tierName: 'Unrated',
            tier: 0
        };
    }

  } catch (e) {
    console.error('Analysis error:', e);
  } finally {
    setTimeout(() => {
        loading.value = false;
    }, 800);
  }
});

const emit = defineEmits(['next']);

const nextStep = () => {
  emit('next');
};
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');
* { font-family: 'Pretendard', sans-serif; }

.animate-fade-in-up {
  animation: fade-in-up 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes fade-in-up {
  to { opacity: 1; transform: translateY(0); }
}

.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
