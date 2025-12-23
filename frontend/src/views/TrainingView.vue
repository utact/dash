<template>
  <div class="min-h-screen bg-slate-50 relative pb-20">
    <!-- 배경 효과 -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden">
      <div class="absolute top-0 left-1/4 w-96 h-96 bg-indigo-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-rose-200/20 rounded-full blur-3xl"></div>
    </div>

    <!-- 헤더 영역 -->


    <div class="container mx-auto px-6 py-8 relative z-10">
      <!-- 탭 네비게이션 -->
      <div class="flex justify-center mb-8">
        <div class="flex gap-1 bg-white/50 backdrop-blur-sm p-1.5 rounded-2xl border border-white/60 shadow-sm">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="currentTab = tab.id"
            class="px-6 py-2.5 rounded-xl text-sm font-bold transition-all duration-200"
            :class="currentTab === tab.id ? 'bg-white text-indigo-600 shadow-sm ring-1 ring-black/5' : 'text-slate-500 hover:text-slate-700'"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>
      
      <!-- 1. 로드맵 (스킬 트리) -->
      <div v-if="currentTab === 'roadmap'" class="animate-fade-in-up">
        
        <!-- 히어로 카드 2개 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <!-- 오늘의 복습 -->
          <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-12 h-12 bg-indigo-100 rounded-xl flex items-center justify-center">
                <RefreshCw :size="24" class="text-indigo-600" />
              </div>
              <div>
                <span class="text-xs font-bold text-indigo-600 uppercase">Today's Review</span>
                <h3 class="text-lg font-bold text-slate-800">{{ dailyReview?.title || '복습할 문제가 없습니다' }}</h3>
              </div>
            </div>
            <p class="text-sm text-slate-500 mb-4 line-clamp-2">{{ dailyReview?.reason || '꾸준한 학습으로 실력을 키워보세요!' }}</p>
            <button 
              v-if="dailyReview"
              @click="goToProblem(dailyReview.problemNumber, dailyReview.link)"
              class="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold transition-colors flex items-center justify-center gap-2"
            >
              <Play :size="16" />
              다시 풀기
            </button>
          </div>

          <!-- 오늘의 도전 -->
          <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-12 h-12 bg-rose-100 rounded-xl flex items-center justify-center">
                <Trophy :size="24" class="text-rose-500" />
              </div>
              <div>
                <span class="text-xs font-bold text-rose-500 uppercase">Daily Challenge</span>
                <h3 class="text-lg font-bold text-slate-800">{{ learningPath?.goalLevel || '목표 설정 중...' }}</h3>
              </div>
            </div>
            <p class="text-sm text-slate-500 mb-4">다음 단계로 나아가기 위한 도전입니다.</p>
            <button class="w-full py-3 border-2 border-slate-200 hover:border-rose-400 text-slate-700 hover:text-rose-500 rounded-xl font-bold transition-colors flex items-center justify-center gap-2">
              <Swords :size="16" />
              도전하기
            </button>
          </div>
        </div>

        <!-- 현재 실력 분석 (OnboardingAnalysis Style) -->
        <section class="mb-8">
          <h2 class="text-xl font-bold text-slate-800 mb-5 flex items-center gap-2 pl-2 border-l-4 border-indigo-500">
            현재 실력 분석
          </h2>
          
          <div class="grid grid-cols-1 lg:grid-cols-12 gap-5">
            
            <!-- Left Column: Tier + Radar + Strength/Weakness (5 cols) -->
            <div class="lg:col-span-5 flex flex-col gap-4">
              
              <!-- Tier Card (Compact) -->
              <div class="bg-white/80 backdrop-blur-xl border border-slate-200 rounded-2xl p-4 shadow-sm relative overflow-hidden group">
                <div class="absolute inset-0 bg-gradient-to-br from-indigo-50/50 to-purple-50/50 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
                <div class="relative z-10 flex items-center gap-4">
                  <div class="relative flex-shrink-0">
                    <div class="absolute inset-0 bg-indigo-500/20 blur-xl rounded-full"></div>
                    <img 
                      v-if="userTier"
                      :src="`https://static.solved.ac/tier_small/${userTier}.svg`" 
                      class="w-14 h-14 relative drop-shadow-lg"
                      alt="Tier Badge"
                    />
                  </div>
                  <div>
                    <div class="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1">
                      <Zap :size="12" />
                      현재 티어
                    </div>
                    <div class="text-xl font-black text-indigo-900">{{ userTierName || 'Unranked' }}</div>
                  </div>
                </div>
              </div>

              <!-- Radar Chart Card -->
              <div class="bg-white/80 backdrop-blur-xl border border-slate-200 rounded-2xl p-4 shadow-sm flex-1">
                <h3 class="text-sm font-bold text-slate-600 mb-3 flex items-center gap-2">
                   <LayoutGrid :size="14" class="text-indigo-500" />
                   알고리즘 역량
                </h3>
                <div class="flex items-center justify-center">
                   <div class="w-full aspect-square max-w-[240px]">
                      <AlgorithmRadarChart :stats="allTagStats" :max-tags="8" />
                   </div>
                </div>
              </div>

              <!-- Strength & Weakness Cards -->
              <div class="grid grid-cols-2 gap-3">
                <!-- Strength Card -->
                <div class="bg-emerald-50/80 border border-emerald-200 rounded-xl p-3">
                  <div class="flex items-center gap-2 mb-1.5">
                    <div class="w-6 h-6 rounded-lg bg-emerald-100 flex items-center justify-center text-emerald-600">
                      <Zap :size="12" />
                    </div>
                    <span class="text-xs font-bold text-emerald-700 uppercase">강점</span>
                  </div>
                  <div class="text-slate-700 text-xs font-medium leading-relaxed line-clamp-2">
                    {{ learningPath?.aiAnalysis?.keyStrength || '-' }}
                  </div>
                </div>

                <!-- Weakness Card -->
                <div class="bg-rose-50/80 border border-rose-200 rounded-xl p-3">
                  <div class="flex items-center gap-2 mb-1.5">
                     <div class="w-6 h-6 rounded-lg bg-rose-100 flex items-center justify-center text-rose-500">
                        <AlertTriangle :size="12" />
                     </div>
                    <span class="text-xs font-bold text-rose-600 uppercase">약점</span>
                  </div>
                  <div class="text-slate-700 text-xs font-medium leading-relaxed line-clamp-2">
                    {{ learningPath?.aiAnalysis?.primaryWeakness || '-' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column: Learning Roadmap (7 cols) -->
            <div class="lg:col-span-7 bg-white/80 backdrop-blur-xl border border-slate-200 rounded-2xl p-5 shadow-sm">
              <h3 class="text-sm font-bold text-slate-600 mb-4 flex items-center gap-2">
                 <MapIcon :size="14" class="text-indigo-500" />
                 맞춤형 학습 로드맵
              </h3>
              <LearningRoadmap :phases="learningPath?.aiAnalysis?.phases || []" class="w-full" />
            </div>
          </div>
        </section>

        <!-- AI 종합 분석 리포트 -->
        <section class="mb-8">
          <h2 class="text-xl font-bold text-slate-800 mb-5 flex items-center gap-2 pl-2 border-l-4 border-indigo-500">
            AI 종합 분석 리포트
          </h2>
          
          <div class="space-y-4">
            <!-- Analysis Summary Banner -->
            <div class="relative overflow-hidden rounded-2xl bg-indigo-900 shadow-lg text-white p-6">
               <div class="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20"></div>
               <div class="absolute top-0 right-0 w-64 h-64 bg-indigo-500 rounded-full blur-3xl opacity-20 -translate-y-1/2 translate-x-1/2"></div>
               
               <div class="relative z-10 flex gap-4 items-start">
                 <div class="hidden md:flex p-2.5 bg-indigo-500/30 rounded-xl backdrop-blur-md border border-indigo-400/30 flex-shrink-0">
                    <FileText :size="20" class="text-indigo-200" />
                 </div>
                 <div class="flex-1">
                   <h3 class="text-xs font-bold text-indigo-300 uppercase tracking-widest mb-2">Analysis Summary</h3>
                   <p class="text-base text-indigo-50 leading-relaxed font-medium">
                     {{ learningPath?.aiAnalysis?.analysisSummary || 'AI가 학습 데이터를 분석하고 있습니다...' }}
                   </p>
                 </div>
               </div>
            </div>

            <!-- 3-Column Insights -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
               <!-- Growth Prediction -->
               <div class="bg-white/80 border border-slate-200 rounded-xl p-4 min-h-[120px]">
                 <div class="flex items-center gap-2 mb-2">
                   <div class="w-8 h-8 rounded-full bg-violet-100 flex items-center justify-center text-violet-600">
                     <TrendingUp :size="16" />
                   </div>
                   <h4 class="font-bold text-slate-700 text-sm">성장 예측</h4>
                 </div>
                 <p class="text-slate-600 text-xs leading-relaxed">
                   {{ learningPath?.aiAnalysis?.growthPrediction || '데이터 분석 중...' }}
                 </p>
               </div>

               <!-- Strategic Advice -->
               <div class="bg-indigo-50/80 border border-indigo-200 rounded-xl p-4 min-h-[120px]">
                 <div class="flex items-center gap-2 mb-2">
                   <div class="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600">
                     <Target :size="16" />
                   </div>
                   <h4 class="font-bold text-indigo-900 text-sm">전략적 조언</h4>
                 </div>
                 <p class="text-indigo-800 text-xs leading-relaxed">
                   {{ learningPath?.aiAnalysis?.strategicAdvice || '전략 수립 중...' }}
                 </p>
               </div>

               <!-- Efficiency Analysis -->
               <div class="bg-white/80 border border-slate-200 rounded-xl p-4 min-h-[120px]">
                 <div class="flex items-center gap-2 mb-2">
                   <div class="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600">
                     <Activity :size="16" />
                   </div>
                   <h4 class="font-bold text-slate-700 text-sm">효율성 분석</h4>
                 </div>
                 <p class="text-slate-600 text-xs leading-relaxed">
                   {{ learningPath?.aiAnalysis?.efficiencyAnalysis || '효율성 분석 중...' }}
                 </p>
               </div>
            </div>
          </div>
        </section>
        
      </div>

      <!-- 2. 스킬 트리 -->
      <div v-if="currentTab === 'skilltree'" class="animate-fade-in-up">
        <SkillTreeView />
      </div>

      <!-- 2. 강의실 (Youtube) -->
      <div v-if="currentTab === 'videos'" class="animate-fade-in-up">
        <!-- 검색 및 필터 -->
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 mb-6">
          <h2 class="text-lg font-bold text-slate-800 mb-4">📺 알고리즘 강의</h2>
          <div class="flex flex-wrap gap-2 mb-4">
            <button v-for="tag in recommendedKeywords" :key="tag" 
               @click="searchVideos(tag)"
               class="px-4 py-2 rounded-full border transition-colors"
               :class="searchKeyword === tag ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-white text-slate-600 border-slate-200 hover:border-indigo-400'"
            >
              #{{ tag }}
            </button>
          </div>
          <div class="relative">
            <input 
              v-model="searchKeyword" 
              @keyup.enter="searchVideos(searchKeyword)"
              type="text" 
              placeholder="검색어 입력 (예: DP, BFS)" 
              class="w-full pl-4 pr-12 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:outline-none focus:border-indigo-500"
            />
            <button @click="searchVideos(searchKeyword)" class="absolute right-2 top-1/2 -translate-y-1/2 p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg">
              <Search :size="20"/>
            </button>
          </div>
        </div>
        
        <!-- 비디오 그리드 -->
        <div v-if="isLoadingVideos" class="text-center py-20">
          <div class="animate-spin text-indigo-600 mb-2"><Loader2 :size="40"/></div>
          <p class="text-slate-500">강의를 찾고 있습니다...</p>
        </div>
        <div v-else-if="videos.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="video in videos" :key="video.videoId" class="bg-white rounded-2xl overflow-hidden shadow-sm border border-slate-200 hover:shadow-md transition-shadow group cursor-pointer" @click="openVideo(video.videoId)">
            <div class="aspect-video bg-slate-200 relative overflow-hidden">
              <img :src="video.thumbnailUrl" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
              <div class="absolute inset-0 bg-black/20 group-hover:bg-black/40 transition-colors flex items-center justify-center text-white opacity-0 group-hover:opacity-100">
                <Play :size="40" fill="currentColor" />
              </div>
            </div>
            <div class="p-4">
              <h3 class="font-bold text-slate-800 line-clamp-2 mb-2 h-12">{{ video.title }}</h3>
              <div class="flex items-center justify-between text-xs text-slate-500">
                <span>{{ video.channelTitle }}</span>
                <span>{{ formatDate(video.publishedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-20 text-slate-500">
          검색 결과가 없습니다. 키워드를 클릭해보세요!
        </div>
      </div>


    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { 
    RefreshCw, Trophy, Swords, Play,
    Search, Loader2, Send, RotateCcw, LayoutGrid,
    Map as MapIcon, FileText, TrendingUp, Target, Activity, AlertTriangle, Zap
} from 'lucide-vue-next';
import AlgorithmRadarChart from '../components/charts/AlgorithmRadarChart.vue';
import LearningRoadmap from '../components/LearningRoadmap.vue';
import SkillTreeView from '../components/SkillTreeView.vue';
import { useAuth } from '../composables/useAuth';
import { aiApi } from '../api/ai';
import { youtubeApi } from '../api/youtube';
import { marked } from 'marked';

// State
const { user } = useAuth();
const currentTab = ref('roadmap');
const isSimulating = ref(false);
const isLoadingVideos = ref(false);

const tabs = [
    { id: 'roadmap', label: '⛳️ 로드맵' },
    { id: 'skilltree', label: '🌳 스킬 트리' },
    { id: 'videos', label: '📺 강의실' }
];

// 1. Roadmap Data
const learningPath = ref(null);
const dailyReview = ref(null);
const allTagStats = ref([]);

// User Tier (from auth or learning path)
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

const userTier = computed(() => user.value?.solvedacTier || 0);
const userTierName = computed(() => {
  const tier = userTier.value;
  return tier >= 0 && tier < TIER_NAMES.length ? TIER_NAMES[tier] : 'Unrated';
});

// 2. Videos Data
const searchKeyword = ref('');
const recommendedKeywords = ref(['Dynamic Programming', 'BFS', 'Dijkstra', 'Greedy']);
const videos = ref([]);

// Methods
onMounted(async () => {
    if (user.value) {
        loadLearningPath();
    }
});

const loadLearningPath = async () => {
    try {
        const res = await aiApi.getLearningPath(user.value.id);
        learningPath.value = res.data;
        
        if (res.data.weaknessTags?.length > 0) {
            recommendedKeywords.value = res.data.weaknessTags.map(t => t.tagName);
        }

        const weak = res.data.weaknessTags || [];
        const strong = res.data.strengthTags || [];
        const map = new Map();
        [...weak, ...strong].forEach(t => map.set(t.tagKey, t));
        allTagStats.value = Array.from(map.values());

        if (res.data.weaknessTags?.length > 0) {
            const worstTag = res.data.weaknessTags[0];
            const winRate = worstTag.total > 0 ? Math.round((worstTag.solved / worstTag.total) * 100) : 0;
            
            // Build tier range: userTier ~ userTier+4 (capped at 30)
            const tierStart = userTier.value || 1;
            const tierEnd = Math.min(tierStart + 4, 30);
            const tierRange = Array.from({ length: tierEnd - tierStart + 1 }, (_, i) => tierStart + i).join('%2C');
            
            // Build 백준 problemset URL with tier filtering
            const bojTagId = worstTag.bojTagId;
            const link = bojTagId 
                ? `https://www.acmicpc.net/problemset?sort=ac_desc&submit=pac%2Cfa%2Cus&tier=${tierRange}&algo=${bojTagId}&algo_if=and`
                : `https://solved.ac/problems/tags/${worstTag.tagKey}`;
            
            dailyReview.value = {
                title: `${worstTag.tagName} 집중 공략`,
                problemNumber: '', 
                link,
                reason: `정답률 ${winRate}%로 가장 낮습니다.`
            };
        } else if (res.data.classStats?.length > 0) {
            const processingClass = res.data.classStats.find(c => c.essentialSolved < c.essentials);
            if (processingClass) {
                dailyReview.value = {
                    title: `Class ${processingClass.classNumber} 정복`,
                    link: `https://solved.ac/class/${processingClass.classNumber}`,
                    reason: `${processingClass.essentials - processingClass.essentialSolved}문제 남음`
                };
            }
        }
    } catch (e) {
        console.error("Failed to load learning path:", e);
    }
};

const goToProblem = (problemNumber, link) => {
    if (link) window.open(link, '_blank');
    else if (problemNumber) window.open(`https://www.acmicpc.net/problem/${problemNumber}`, '_blank');
};

const searchVideos = async (keyword) => {
    currentTab.value = 'videos';
    searchKeyword.value = keyword;
    isLoadingVideos.value = true;
    try {
        const res = await youtubeApi.search(keyword + " 알고리즘 강의");
        videos.value = res.data;
    } catch (e) {
        console.error("Youtube search failed:", e);
    } finally {
        isLoadingVideos.value = false;
    }
};

const openVideo = (videoId) => {
    window.open(`https://www.youtube.com/watch?v=${videoId}`, '_blank');
};



const renderMarkdown = (text) => marked(text || '');

const formatDate = (isoString) => {
    if (!isoString) return '';
    return new Date(isoString).toLocaleDateString();
};
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
