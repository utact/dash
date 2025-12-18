<template>
  <div class="min-h-screen bg-slate-50 text-slate-800">
    <!-- Navbar / Header Area -->



    <main class="container mx-auto px-6 py-8">
      <!-- Welcome Section -->
      <div class="mb-10">
        <h1 class="text-3xl font-extrabold text-slate-900 mb-2">
          안녕하세요, <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">탐험가님!</span> 👋
        </h1>
        <p class="text-slate-500">오늘도 알고리즘의 바다를 항해할 준비가 되셨나요?</p>
      </div>

      <!-- Content Grid -->
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="h-64 rounded-3xl bg-white shadow-sm border border-slate-100 animate-pulse"></div>
      </div>

      <div v-else-if="records.length === 0" class="flex flex-col items-center justify-center py-20 bg-white rounded-3xl border border-dashed border-slate-300">
        <div class="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center mb-6">
          <Code2 :size="40" class="text-indigo-400" />
        </div>
        <h3 class="text-xl font-bold text-slate-800 mb-2">기록된 모험이 없습니다</h3>
        <p class="text-slate-500 mb-6">첫 번째 알고리즘 문제를 해결하고 커밋해보세요!</p>
        <button class="px-6 py-3 bg-slate-900 text-white rounded-xl font-bold hover:bg-slate-800 transition-colors">
          가이드 보기
        </button>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <!-- Record Card -->
        <div 
          v-for="record in records" 
          :key="record.id" 
          class="group relative bg-white rounded-3xl p-6 shadow-sm border border-slate-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 flex flex-col"
        >
          <!-- Language Badge -->
          <div class="absolute top-6 right-6">
            <span class="px-3 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200 uppercase tracking-wider">
              {{ record.language }}
            </span>
          </div>

          <!-- Problem Info -->
          <div class="mb-6 mt-2">
            <div class="flex items-center gap-2 mb-2">
               <span class="text-xs font-bold text-indigo-500 bg-indigo-50 px-2 py-0.5 rounded">#{{ record.problemNumber }}</span>
            </div>
            <h3 class="text-lg font-bold text-slate-800 leading-snug line-clamp-2 group-hover:text-indigo-600 transition-colors">
              {{ record.title }}
            </h3>
            <div class="text-xs text-slate-400 mt-2 flex items-center gap-1">
              {{ formatDate(record.committedAt) }}
            </div>
          </div>

          <!-- Stats Row -->
          <div class="grid grid-cols-2 gap-3 mb-6">
            <div class="bg-slate-50 rounded-2xl p-3 flex flex-col items-center justify-center border border-slate-100">
              <Zap :size="16" class="text-amber-400 mb-1" />
              <span class="text-sm font-bold text-slate-700">{{ record.runtimeMs }}ms</span>
            </div>
            <div class="bg-slate-50 rounded-2xl p-3 flex flex-col items-center justify-center border border-slate-100">
              <Database :size="16" class="text-blue-400 mb-1" />
              <span class="text-sm font-bold text-slate-700">{{ record.memoryKb }}KB</span>
            </div>
          </div>

          <!-- Actions -->
          <div class="mt-auto grid grid-cols-2 gap-3">
             <button 
                @click="requestReview(record)" 
                class="flex items-center justify-center gap-2 py-2.5 rounded-xl bg-indigo-50 text-indigo-600 font-bold text-sm hover:bg-indigo-100 transition-colors"
                :disabled="processing === record.id"
            >
                <Bot :size="16" />
                <span v-if="processing === record.id">...</span>
                <span v-else>리뷰</span>
            </button>
            <button 
                @click="requestHint(record)" 
                class="flex items-center justify-center gap-2 py-2.5 rounded-xl bg-amber-50 text-amber-600 font-bold text-sm hover:bg-amber-100 transition-colors"
            >
                <Lightbulb :size="16" />
                힌트
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Trendy Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="closeModal"></div>
      <div class="bg-white rounded-[32px] w-full max-w-2xl max-h-[85vh] overflow-hidden relative shadow-2xl flex flex-col animate-pop-in">
        
        <!-- Modal Header -->
        <div class="px-8 py-6 border-b border-slate-100 flex justify-between items-center bg-white/80 backdrop-blur z-10 sticky top-0">
           <h3 class="text-2xl font-black text-slate-800 flex items-center gap-3">
             <div class="p-2 rounded-xl" :class="modalType === 'review' ? 'bg-indigo-100 text-indigo-600' : 'bg-amber-100 text-amber-600'">
                <Bot v-if="modalType === 'review'" :size="24" />
                <Lightbulb v-else :size="24" />
             </div>
             {{ modalTitle }}
           </h3>
           <button @click="closeModal" class="p-2 rounded-full hover:bg-slate-100 text-slate-400 transition-colors">
             <X :size="24" />
           </button>
        </div>

        <!-- Modal Content -->
        <div class="p-8 overflow-y-auto custom-scrollbar">
            <!-- Loading -->
            <div v-if="modalLoading" class="py-20 flex flex-col items-center justify-center text-center">
                 <div class="w-16 h-16 border-4 border-indigo-100 border-t-indigo-500 rounded-full animate-spin mb-6"></div>
                 <h4 class="text-lg font-bold text-slate-800">AI가 분석중입니다</h4>
                 <p class="text-slate-500">잠시만 기다려주세요...</p>
            </div>

            <!-- Review Data -->
            <div v-else-if="modalType === 'review' && modalData" class="space-y-8">
                 <div class="bg-indigo-50/50 p-6 rounded-3xl border border-indigo-100">
                    <h4 class="text-sm font-bold text-indigo-900 uppercase tracking-wide mb-3">Analysis Summary</h4>
                    <p class="text-slate-700 leading-relaxed text-lg">{{ modalData.summary }}</p>
                 </div>

                 <div class="grid grid-cols-2 gap-4">
                     <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm">
                         <span class="text-xs font-bold text-slate-400 uppercase">Time Complexity</span>
                         <div class="text-2xl font-black text-slate-800 mt-1">{{ modalData.complexity?.time || '-' }}</div>
                     </div>
                     <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm">
                         <span class="text-xs font-bold text-slate-400 uppercase">Space Complexity</span>
                         <div class="text-2xl font-black text-slate-800 mt-1">{{ modalData.complexity?.space || '-' }}</div>
                     </div>
                 </div>

                 <div>
                     <h4 class="flex items-center gap-2 text-lg font-bold text-slate-800 mb-4">
                         <TrendingUp class="text-green-500" /> 개선 포인트
                     </h4>
                     <ul class="space-y-3">
                         <li v-for="(item, idx) in (modalData.pitfalls?.improvements || [])" :key="idx" class="flex items-start gap-3 bg-slate-50 p-4 rounded-xl">
                             <div class="w-1.5 h-1.5 rounded-full bg-slate-400 mt-2 shrink-0"></div>
                             <span class="text-slate-600">{{ item }}</span>
                         </li>
                     </ul>
                 </div>
            </div>

            <!-- Hint Data -->
            <div v-else-if="modalType === 'hint' && modalData" class="space-y-8">
                <div class="text-center py-6">
                    <div class="inline-block p-4 rounded-full bg-amber-100 text-amber-500 mb-4">
                        <Lightbulb :size="48" />
                    </div>
                    <h4 class="text-2xl font-bold text-slate-800 mb-4">Level 1 Hint</h4>
                    <p class="text-xl text-slate-600 font-medium leading-relaxed">
                        "{{ modalData.hint }}"
                    </p>
                </div>
            </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { dashboardApi } from '../api/dashboard';
import http from '../api/http';
import { 
  Bot, 
  Lightbulb, 
  Zap, 
  Database, 
  ExternalLink,
  Code2,
  X,
  TrendingUp,
  LayoutGrid,
  Youtube,
  Map
} from 'lucide-vue-next';

const records = ref([]);
const loading = ref(true);
const processing = ref(null);
const showModal = ref(false);
const modalType = ref('');
const modalTitle = ref('');
const modalLoading = ref(false);
const modalData = ref(null);

onMounted(async () => {
  try {
    const res = await dashboardApi.getRecords();
    records.value = res.data;
  } catch(e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});

const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
};

const requestReview = async (record) => {
    openModal('review', 'AI Code Review');
    modalLoading.value = true;
    try {
        const res = await http.post('/ai/review', {
            algorithmRecordId: record.id,
            code: record.code,
            language: record.language,
            problemNumber: String(record.problemNumber)
        });
        modalData.value = res.data;
    } catch (e) {
        modalData.value = { summary: 'Error analyzing code.' };
    } finally {
        modalLoading.value = false;
    }
};

const requestHint = async (record) => {
    openModal('hint', 'Smart Hint');
    modalLoading.value = true;
    try {
        const res = await http.post('/ai/hint', {
            userId: record.userId, 
            problemNumber: String(record.problemNumber),
            problemTitle: record.title,
            level: 1
        });
        modalData.value = res.data;
    } catch(e) {
        modalData.value = { hint: 'Error fetching hint.' };
    } finally {
        modalLoading.value = false;
    }
};

const openModal = (type, title) => {
    modalType.value = type;
    modalTitle.value = title;
    showModal.value = true;
    modalData.value = null;
};

const closeModal = () => {
    showModal.value = false;
};
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');

* {
  font-family: 'Pretendard', sans-serif;
}

.btn-primary-soft {
  @apply flex items-center gap-2 px-4 py-2.5 rounded-xl bg-indigo-50 text-indigo-600 font-bold hover:bg-indigo-100 transition-colors;
}

.btn-secondary-soft {
  @apply flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-100 text-slate-600 font-bold hover:bg-slate-200 transition-colors;
}

.animate-pop-in {
  animation: popIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: scale(0.95);
}

@keyframes popIn {
  to { opacity: 1; transform: scale(1); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
</style>
