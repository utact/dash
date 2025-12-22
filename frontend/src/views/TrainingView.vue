<template>
  <div class="min-h-screen bg-slate-50 relative pb-20">
    <!-- 배경 효과 -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden">
      <div class="absolute top-0 left-1/4 w-96 h-96 bg-indigo-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-rose-200/20 rounded-full blur-3xl"></div>
    </div>

    <!-- 헤더 영역 -->
    <div class="relative bg-white/70 backdrop-blur-xl border-b border-slate-200 z-10">
      <div class="container mx-auto px-6 h-16 flex items-center justify-between">
        <h1 class="text-xl font-bold text-slate-900 flex items-center gap-2">
          <span class="text-2xl">🏋️</span>
          <span>DASH 트레이닝 센터</span>
        </h1>
        
        <!-- 탭 네비게이션 -->
        <div class="flex gap-1 bg-slate-100 p-1 rounded-xl">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="currentTab = tab.id"
            class="px-4 py-2 rounded-lg text-sm font-bold transition-all duration-200"
            :class="currentTab === tab.id ? 'bg-white text-indigo-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>
    </div>

    <div class="container mx-auto px-6 py-8 relative z-10">
      
      <!-- 1. 로드맵 (스킬 트리) -->
      <div v-if="currentTab === 'roadmap'" class="animate-fade-in-up">
        <!-- Hero Section: Daily Mission -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <!-- 오늘의 복습 -->
          <div class="bg-white/80 backdrop-blur-md rounded-3xl p-6 border border-slate-200 shadow-sm relative overflow-hidden group hover:border-indigo-200 transition-colors">
            <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
              <RefreshCw :size="80" class="text-indigo-600" />
            </div>
            <div class="relative z-10">
              <div class="flex items-center gap-2 mb-2">
                <span class="bg-indigo-100 text-indigo-600 px-3 py-1 rounded-full text-xs font-bold">Today's Review</span>
                <span class="text-slate-400 text-xs">AI 추천 복습</span>
              </div>
              <h3 class="text-xl font-bold text-slate-800 mb-1">{{ dailyReview?.title || '복습할 문제가 없습니다' }}</h3>
              <p class="text-slate-500 text-sm mb-6">{{ dailyReview?.reason || '꾸준한 학습으로 실력을 키워보세요!' }}</p>
              <button 
                v-if="dailyReview"
                @click="goToProblem(dailyReview.problemNumber, dailyReview.link)"
                class="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold shadow-lg shadow-indigo-200 transition-all flex items-center justify-center gap-2"
              >
                <Play :size="18" />
                다시 풀기
              </button>
            </div>
          </div>

          <!-- 오늘의 도전 -->
           <div class="bg-white/80 backdrop-blur-md rounded-3xl p-6 border border-slate-200 shadow-sm relative overflow-hidden group hover:border-rose-200 transition-colors">
            <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
              <Trophy :size="80" class="text-rose-500" />
            </div>
            <div class="relative z-10">
              <div class="flex items-center gap-2 mb-2">
                <span class="bg-rose-100 text-rose-600 px-3 py-1 rounded-full text-xs font-bold">Daily Challenge</span>
                <span class="flex items-center text-amber-500 text-xs font-bold">
                  <Star :size="12" fill="currentColor" /> Level Up
                </span>
              </div>
              <h3 class="text-xl font-bold text-slate-800 mb-1">{{ learningPath?.goalLevel || '목표 설정 중...' }}</h3>
              <p class="text-slate-500 text-sm mb-6">다음 단계로 나아가기 위한 도전입니다.</p>
              <button class="w-full py-3 bg-white border-2 border-slate-200 hover:border-rose-400 text-slate-700 hover:text-rose-500 rounded-xl font-bold transition-all flex items-center justify-center gap-2">
                <Swords :size="18" />
                도전하기
              </button>
            </div>
          </div>
        </div>
        

        
        <!-- AI Overall Analysis Text -->
        <div class="bg-indigo-50 rounded-2xl p-6 mb-8 border border-indigo-100 shadow-sm">
            <h3 class="font-bold text-indigo-800 mb-3 flex items-center gap-2">
                <span>📊</span> AI 종합 분석 리포트
            </h3>
            <p class="text-slate-700 leading-relaxed mb-4">{{ learningPath?.aiAnalysis?.overallAssessment || '분석 데이터를 불러오는 중입니다...' }}</p>
            
            <div v-if="learningPath?.aiAnalysis?.personalizedAdvice" class="bg-white/60 rounded-xl p-4 border border-indigo-100">
                <div class="text-sm text-indigo-700 font-bold mb-1 flex items-center gap-1">
                    <span>💡</span> 맞춤 조언
                </div>
                <p class="text-slate-600 text-sm">{{ learningPath.aiAnalysis.personalizedAdvice }}</p>
            </div>
        </div>

        <!-- Strength & Weakness Highlight Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
            <div class="bg-emerald-50 border border-emerald-200 rounded-2xl p-5 hover:shadow-md transition-shadow">
                <div class="flex items-center gap-2 text-emerald-600 text-sm font-bold mb-2">
                    <span class="text-lg">💪</span> 핵심 강점
                </div>
                <div class="text-slate-700 font-medium leading-relaxed">{{ learningPath?.aiAnalysis?.keyStrength || '분석 중...' }}</div>
            </div>
            <div class="bg-amber-50 border border-amber-200 rounded-2xl p-5 hover:shadow-md transition-shadow">
                <div class="flex items-center gap-2 text-amber-600 text-sm font-bold mb-2">
                    <span class="text-lg">🎯</span> 집중 영역
                </div>
                <div class="text-slate-700 font-medium leading-relaxed">{{ learningPath?.aiAnalysis?.primaryWeakness || '분석 중...' }}</div>
            </div>
        </div>

        <!-- Graph Analysis & Skill Tree Row -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
            <!-- Left: Radar Chart -->
            <div class="bg-white rounded-3xl p-6 shadow-sm border border-slate-200 flex flex-col items-center justify-center">
                 <h3 class="text-lg font-bold text-slate-800 mb-6 flex items-center gap-2">
                    <LayoutGrid :size="20" class="text-indigo-500" /> 
                    역량 레이더
                 </h3>
                 <div class="w-full max-w-[280px] aspect-square">
                     <AlgorithmRadarChart :stats="allTagStats" :max-tags="6" />
                 </div>
                 <p class="text-xs text-slate-400 mt-4 text-center">최근 해결한 문제 태그 기반 분석</p>
            </div>

            <!-- Right: Learning Phases (Roadmap Details) -->
            <div class="lg:col-span-2 bg-white rounded-3xl p-8 shadow-sm border border-slate-200 relative overflow-hidden">
                 <h3 class="text-lg font-bold text-slate-800 mb-6">📚 단계별 학습 로드맵</h3>
                 <div class="space-y-6">
                    <div v-for="(phase, idx) in learningPath?.aiAnalysis?.phases || []" :key="idx" class="flex gap-4 group">
                        <!-- Phase Number -->
                        <div class="flex flex-col items-center">
                             <div class="w-8 h-8 rounded-lg bg-slate-100 text-slate-500 font-bold flex items-center justify-center group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                                 {{ phase.phase || idx + 1 }}
                             </div>
                             <div v-if="idx < (learningPath?.aiAnalysis?.phases?.length || 0) - 1" class="w-0.5 flex-1 bg-slate-100 my-2 group-hover:bg-indigo-100 transition-colors"></div>
                        </div>
                        <!-- Phase Content -->
                        <div class="flex-1 pb-2">
                             <div class="flex items-center gap-3 mb-1">
                                 <h4 class="font-bold text-slate-800">{{ phase.title }}</h4>
                                 <span class="text-xs px-2 py-0.5 bg-slate-100 text-slate-500 rounded">{{ phase.duration }}</span>
                             </div>
                             <p class="text-sm text-slate-500 mb-3">{{ phase.focus }}</p>
                             
                             <!-- Phase Goals -->
                             <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                                 <div class="text-xs text-slate-600 bg-slate-50 p-2 rounded border border-slate-100">
                                     <span class="font-bold text-indigo-500 block mb-1">Goals</span>
                                     <ul class="list-disc list-inside">
                                         <li v-for="g in phase.goals" :key="g">{{ g }}</li>
                                     </ul>
                                 </div>
                                 <div class="text-xs text-slate-600 bg-slate-50 p-2 rounded border border-slate-100">
                                     <span class="font-bold text-amber-500 block mb-1">Milestones</span>
                                      <ul class="list-disc list-inside">
                                         <li v-for="m in phase.milestones" :key="m">{{ m }}</li>
                                     </ul>
                                 </div>
                             </div>
                        </div>
                    </div>
                 </div>
            </div>
        </div>
        
        <!-- Skill Tree Section (새로운 컴포넌트) -->
        <SkillTreeView />

        <!-- Motivational Message Banner -->
        <div v-if="learningPath?.aiAnalysis?.motivationalMessage" class="mt-8 bg-gradient-to-r from-indigo-500 via-purple-500 to-rose-500 rounded-2xl p-6 shadow-lg overflow-hidden relative">
            <div class="absolute inset-0 bg-white/5 backdrop-blur-3xl"></div>
            <div class="relative z-10 text-center">
                <span class="text-4xl mb-3 inline-block">🚀</span>
                <p class="text-white text-lg font-medium leading-relaxed max-w-3xl mx-auto">"{{ learningPath.aiAnalysis.motivationalMessage }}"</p>
            </div>
        </div>
      </div>

      <!-- 2. 강의실 (Youtube) -->
      <div v-if="currentTab === 'videos'" class="animate-fade-in-up">
         <!-- Search & Filter -->
         <div class="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 mb-6">
             <h2 class="text-lg font-bold text-slate-800 mb-4">📺 맞춤형 알고리즘 강의</h2>
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
                     placeholder="검색어를 입력하세요 (예: DP, Greedy, BFS)" 
                     class="w-full pl-4 pr-12 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:outline-none focus:border-indigo-500"
                 />
                 <button @click="searchVideos(searchKeyword)" class="absolute right-2 top-1/2 -translate-y-1/2 p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg">
                     <Search :size="20"/>
                 </button>
             </div>
         </div>
         
         <!-- Video Grid -->
         <div v-if="isLoadingVideos" class="text-center py-20">
             <div class="animate-spin text-indigo-600 mb-2"><Loader2 :size="40"/></div>
             <p class="text-slate-500">유튜브에서 강의를 찾고 있습니다...</p>
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

      <!-- 3. AI 튜터 (Simulator) -->
      <div v-if="currentTab === 'tutor'" class="animate-fade-in h-[calc(100vh-180px)] min-h-[600px] flex flex-col md:flex-row gap-4">
          <!-- Left: Code Studio -->
          <div class="flex-1 bg-[#1e1e1e] rounded-2xl overflow-hidden flex flex-col shadow-2xl relative min-h-[400px]">
              <!-- Toolbar -->
              <div class="h-10 bg-[#252526] border-b border-[#333] flex items-center px-4 justify-between shrink-0">
                  <div class="flex gap-2">
                       <span class="w-3 h-3 rounded-full bg-red-500"></span>
                       <span class="w-3 h-3 rounded-full bg-amber-500"></span>
                       <span class="w-3 h-3 rounded-full bg-green-500"></span>
                  </div>
                  <div class="text-xs text-slate-400 font-mono">MyCode.java</div>
                  <button @click="runSimulation" :disabled="isSimulating" class="text-xs bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white px-3 py-1.5 rounded flex items-center gap-1 font-bold transition-colors">
                      <Play v-if="!isSimulating" :size="12" fill="currentColor" /> 
                      <Loader2 v-else :size="12" class="animate-spin" />
                      {{ isSimulating ? 'Analyzing...' : 'Run Simulation' }}
                  </button>
              </div>
              
              <!-- Simple Editor (Textarea instead of Monaco for MVP) -->
              <div class="flex-1 relative font-mono text-sm bg-[#1e1e1e] group">
                  <textarea 
                    v-model="editorCode"
                    class="w-full h-full bg-transparent text-slate-300 p-4 resize-none focus:outline-none font-mono leading-relaxed"
                    spellcheck="false"
                  ></textarea>
                  <!-- Placeholder hint -->
                  <div v-if="!editorCode" class="absolute inset-0 p-4 pointer-events-none text-slate-600">
                      // 여기에 코드를 작성하고 Run 버튼을 눌러보세요.
                      // AI가 실행 결과를 시뮬레이션해줍니다.
                  </div>
              </div>

              <!-- Terminal Area -->
              <div class="h-1/3 bg-[#1e1e1e] border-t border-[#333] flex flex-col shrink-0">
                  <div class="px-4 py-1 text-xs text-slate-400 border-b border-[#333] bg-[#252526] flex justify-between items-center">
                      <span>TERMINAL (AI PREDICTION)</span>
                      <button @click="terminalOutput = ''" class="hover:text-white">Clear</button>
                  </div>
                  <div class="p-4 font-mono text-sm text-slate-300 flex-1 overflow-auto whitespace-pre-wrap">
                      <div v-if="terminalOutput" v-html="renderMarkdown(terminalOutput)"></div>
                      <div v-else class="text-slate-600">$ Ready to run...</div>
                  </div>
              </div>
          </div>

          <!-- Right: Chat Coach -->
          <div class="w-full md:w-[400px] flex flex-col bg-white rounded-2xl shadow-xl border border-slate-200 overflow-hidden h-[600px] md:h-auto">
               <!-- Header -->
               <div class="h-14 bg-indigo-600 flex items-center px-4 justify-between shrink-0">
                   <div class="flex items-center gap-3">
                       <div class="w-8 h-8 rounded-full bg-white flex items-center justify-center text-xl">
                           {{ tutorEmotion }}
                       </div>
                       <div>
                           <div class="text-white font-bold text-sm">AI 소크라테스</div>
                           <div class="text-indigo-200 text-xs flex items-center gap-1">
                               <span class="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse"></span> Online
                           </div>
                       </div>
                   </div>
                   <button @click="resetChat" class="text-indigo-200 hover:text-white" title="대화 초기화">
                       <RotateCcw :size="18"/>
                   </button>
               </div>

               <!-- Chat Body -->
               <div class="flex-1 bg-slate-50 p-4 overflow-y-auto space-y-4" ref="chatContainer">
                   <div v-for="(msg, idx) in chatMessages" :key="idx" class="flex gap-3" :class="msg.role === 'user' ? 'flex-row-reverse' : ''">
                       <div v-if="msg.role === 'ai'" class="w-8 h-8 rounded-full bg-indigo-100 flex-shrink-0 flex items-center justify-center text-indigo-600 text-xs font-bold">AI</div>
                       
                       <div 
                         class="p-3 rounded-2xl shadow-sm text-sm max-w-[85%] border"
                         :class="msg.role === 'user' ? 'bg-indigo-600 text-white rounded-tr-none border-indigo-600' : 'bg-white text-slate-700 rounded-tl-none border-slate-100'"
                       >
                           <div v-if="msg.isLoading" class="flex gap-1 justify-center py-1">
                               <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce"></span>
                               <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce delay-100"></span>
                               <span class="w-1.5 h-1.5 bg-current rounded-full animate-bounce delay-200"></span>
                           </div>
                           <div v-else class="prose prose-sm max-w-none" :class="msg.role === 'user' ? 'prose-invert' : 'prose-slate'" v-html="renderMarkdown(msg.content)"></div>
                       </div>
                   </div>
               </div>

               <!-- Quick Replies -->
               <div class="px-4 pt-2 pb-2 bg-white flex gap-2 overflow-x-auto no-scrollbar border-t border-slate-50">
                   <button v-for="reply in quickReplies" :key="reply" @click="sendQuickReply(reply)" 
                       class="px-3 py-1.5 rounded-full bg-slate-100 hover:bg-indigo-50 text-xs text-slate-600 hover:text-indigo-600 transition-colors whitespace-nowrap border border-slate-200">
                       {{ reply }}
                   </button>
               </div>

               <!-- Input Area -->
               <div class="p-4 bg-white border-t border-slate-100">
                   <div class="relative">
                       <input 
                         v-model="userMessage" 
                         @keyup.enter="sendMessage"
                         type="text" 
                         placeholder="질문을 입력하세요..." 
                         class="w-full pl-4 pr-10 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 outline-none text-sm transition-all"
                         :disabled="isChatting"
                       />
                       <button @click="sendMessage" :disabled="isChatting || !userMessage.trim()" class="absolute right-2 top-1/2 -translate-y-1/2 p-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 disabled:bg-slate-300 text-white transition-colors">
                           <Send :size="16" />
                       </button>
                   </div>
               </div>
          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue';
import { 
    RefreshCw, Trophy, Star, Swords, Play, AlertTriangle, CheckCircle2, Flag,
    PlayCircle, Search, Loader2, Send, RotateCcw, LayoutGrid
} from 'lucide-vue-next';
import AlgorithmRadarChart from '../components/charts/AlgorithmRadarChart.vue';
import SkillTreeView from '../components/SkillTreeView.vue';
import { useAuth } from '../composables/useAuth';
import { aiApi } from '../api/ai';
import { youtubeApi } from '../api/youtube';
import { marked } from 'marked';
import { useRouter } from 'vue-router';

// State
const router = useRouter();
const { user } = useAuth();
const currentTab = ref('roadmap');
const isSimulating = ref(false);
const isChatting = ref(false);
const isLoadingVideos = ref(false);

const tabs = [
    { id: 'roadmap', label: '⛳️ 로드맵' },
    { id: 'videos', label: '📺 강의실' },
    { id: 'tutor', label: '🤖 AI 튜터' }
];

// 1. Roadmap Data
const learningPath = ref(null);
const dailyReview = ref(null);
const allTagStats = ref([]);

// 2. Videos Data
const searchKeyword = ref('');
const recommendedKeywords = ref(['Dynamic Programming', 'BFS', 'Dijkstra', 'Greedy']);
const videos = ref([]);

// 3. Tutor Data
const editorCode = ref('public class Solution {\n    public static void main(String[] args) {\n        System.out.println("Hello DASH!");\n    }\n}');
const terminalOutput = ref('');
const chatMessages = ref([
    { role: 'ai', content: '안녕하세요! **AI 소크라테스 튜터**입니다. 👋\n\n알고리즘 공부를 하다가 막히는 부분이 있다면 저에게 물어봐주세요. 정답을 바로 알려드리기보다, 스스로 깨우칠 수 있도록 돕겠습니다!' }
]);
const userMessage = ref('');
const tutorEmotion = ref('🤖'); // 🤖, 🤔, 💡, 🎓
const quickReplies = ref(['💡 힌트 주세요', '🐛 버그가 있어요', '📚 이 개념 설명해줘', '⏰ 시간 복잡도 분석']);
const chatContainer = ref(null);
let sessionId = null; // AI Session ID

// Methods

// --- Setup ---
onMounted(async () => {
    if (user.value) {
        loadLearningPath();
    }
});

// --- Roadmap Logic ---
const loadLearningPath = async () => {
    try {
        const res = await aiApi.getLearningPath(user.value.id);
        learningPath.value = res.data;
        
        // 약점 태그를 추천 키워드로 설정
        if (res.data.weaknessTags?.length > 0) {
            recommendedKeywords.value = res.data.weaknessTags.map(t => t.tagName);
        }

        // Radar Chart Data (Merge Weakness + Strength)
        const weak = res.data.weaknessTags || [];
        const strong = res.data.strengthTags || [];
        const map = new Map();
        [...weak, ...strong].forEach(t => map.set(t.tagKey, t));
        allTagStats.value = Array.from(map.values());

        // Daily Review Logic (Real Data)
        // 약점이 있으면 약점 문제 추천, 없으면 다음 클래스 목표 추천
        if (res.data.weaknessTags?.length > 0) {
            const worstTag = res.data.weaknessTags[0];
            const winRate = worstTag.total > 0 ? Math.round((worstTag.solved / worstTag.total) * 100) : 0;
            
            dailyReview.value = {
                title: `${worstTag.tagName} 집중 공략`,
                problemNumber: '', 
                link: `https://www.acmicpc.net/search#q=%23${worstTag.tagKey}&c=Problems`,
                reason: `${worstTag.tagName} 태그의 정답률이 ${winRate}%로 가장 낮습니다. 기초부터 다시 다져보세요!`
            };
        } else if (res.data.classStats?.length > 0) {
             // 도전 중인 클래스 찾기
             const processingClass = res.data.classStats.find(c => c.essentialSolved < c.essentials);
             if (processingClass) {
                 dailyReview.value = {
                     title: `Class ${processingClass.classNumber} 에센셜 정복`,
                     link: `https://solved.ac/class/${processingClass.classNumber}`,
                     reason: `남은 ${processingClass.essentials - processingClass.essentialSolved}문제를 해결하고 다음 레벨로 승급하세요!`
                 };
             }
        }
    } catch (e) {
        console.error("Failed to load learning path:", e);
    }
};

const goToProblem = (problemNumber, link) => {
    if (link) {
        window.open(link, '_blank');
    } else if (problemNumber) {
        window.open(`https://www.acmicpc.net/problem/${problemNumber}`, '_blank');
    }
};

// --- Video Logic ---
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

// --- Tutor & Simulator Logic ---
const scrollToBottom = () => {
    nextTick(() => {
        if (chatContainer.value) {
            chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
        }
    });
};

const resetChat = () => {
    chatMessages.value = [{ role: 'ai', content: '대화가 초기화되었습니다. 무엇을 도와드릴까요?' }];
    sessionId = null;
};

const sendMessage = async () => {
    if (!userMessage.value.trim() || isChatting.value) return;

    const msg = userMessage.value;
    userMessage.value = '';
    
    // User Msg
    chatMessages.value.push({ role: 'user', content: msg });
    scrollToBottom();

    // AI Loading Msg
    isChatting.value = true;
    chatMessages.value.push({ role: 'ai', content: '', isLoading: true });
    tutorEmotion.value = '🤔';
    scrollToBottom();

    try {
        const payload = {
            userId: user.value?.id || 1,
            sessionId: sessionId,
            message: msg,
            code: editorCode.value // Context code
        };

        const res = await aiApi.chat(payload);
        
        // Remove Loading
        chatMessages.value.pop();
        
        // Add AI Response
        sessionId = res.data.sessionId; // Update Session ID
        chatMessages.value.push({ role: 'ai', content: res.data.reply });
        tutorEmotion.value = '🎓'; // Happy/Teaching state

        if (res.data.followUpQuestions?.length > 0) {
            quickReplies.value = res.data.followUpQuestions;
        }
    } catch (e) {
        chatMessages.value.pop();
        chatMessages.value.push({ role: 'ai', content: '죄송합니다. 오류가 발생했습니다. 잠시 후 다시 시도해주세요.' });
        tutorEmotion.value = '🤖';
    } finally {
        isChatting.value = false;
        scrollToBottom();
    }
};

const sendQuickReply = (text) => {
    userMessage.value = text;
    sendMessage();
};

const runSimulation = async () => {
    if (isSimulating.value) return;
    
    isSimulating.value = true;
    terminalOutput.value = '$ Compiling & analyzing code...\n';
    
    try {
        const payload = {
            code: editorCode.value,
            language: 'java' // MVP: Java Fixed
        };

        const res = await aiApi.simulate(payload);
        const data = res.data;

        let output = '';
        if (data.stdout) output += `[Output]\n${data.stdout}\n\n`;
        if (data.stderr) output += `[Error]\n${data.stderr}\n\n`;
        
        output += `[Analysis]\nTime Complexity: ${data.timeComplexity}\nSpace Complexity: ${data.spaceComplexity}\n\n${data.analysis}`;

        terminalOutput.value = output;
        
    } catch (e) {
        terminalOutput.value = '$ Error: Failed to run simulation.';
        console.error(e);
    } finally {
        isSimulating.value = false;
    }
};

const renderMarkdown = (text) => {
    return marked(text || '');
};

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
