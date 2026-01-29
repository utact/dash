<template>
  <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm animate-in slide-in-from-right duration-300">
    <!-- 1. 미션 현황판 -->
    <div class="mb-8">
       <div class="flex items-center justify-between mb-4">
         <h4 class="font-bold text-slate-700 text-sm flex items-center gap-2">
            📊 미션 현황판
         </h4>
       </div>
       
       <div class="overflow-x-auto relative">
          <table class="w-full text-xs text-left">
             <thead class="text-xs text-slate-500 uppercase">
                <tr>
                   <th class="px-2 py-2 font-bold w-12 min-w-[3rem] sticky left-0 bg-white z-10 text-center">스터디원</th>
                   <th v-for="pid in mission.problemIds" :key="pid" class="px-2 py-2 text-center min-w-[40px]">
                      <a :href="`https://www.acmicpc.net/problem/${pid}`" target="_blank" class="hover:text-emerald-600 hover:underline font-bold">
                        {{ pid }}
                      </a>
                   </th>
                   <th class="px-2 py-2 text-center w-12 font-bold text-[10px] sticky right-0 bg-white z-10">진행률</th>
                </tr>
             </thead>
              <tbody class="divide-y divide-slate-50">
                 <tr v-for="member in sortedMemberProgressList" :key="member.userId" 
                     class="hover:bg-slate-50/50"
                     :class="{ 'bg-emerald-50/30': isCurrentUser(member.userId) }">
                   
                   <!-- 스터디원 이름 (아바타만 표시) -->
                   <td class="px-2 py-2 font-medium sticky left-0 z-10 group relative"
                       :class="isCurrentUser(member.userId) ? 'bg-emerald-50/30' : 'bg-white'">
                      <div class="relative flex justify-center">
                        <NicknameRenderer 
                            :username="member.username"
                            :avatar-url="member.avatarUrl"
                            avatar-class="w-8 h-8 font-bold border border-slate-100"
                            text-class="hidden"
                            :icon-size="16"
                        />
                        
                        <div v-if="member.completedCount === member.totalProblems" class="absolute -top-1 -right-1 text-rose-500 drop-shadow-sm">
                            <span class="text-[10px]">👑</span>
                        </div>

                        <!-- 커스텀 툴팁 (우측) -->
                        <div class="absolute left-full top-1/2 -translate-y-1/2 ml-3 px-2 py-1 bg-slate-800 text-white text-[10px] font-medium rounded shadow-xl z-50 whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
                            <NicknameRenderer 
                                :username="member.username"
                                :show-avatar="false"
                                text-class="text-white"
                            />
                            <div class="absolute top-1/2 -translate-y-1/2 -left-1 w-2 h-2 bg-slate-800 rotate-45"></div>
                        </div>
                      </div>
                   </td>
                   
                   <!-- 문제 셀 -->
                    <td v-for="pid in mission.problemIds" :key="pid" 
                        class="px-1 py-2 text-center group relative h-10"> <!-- 정렬을 위한 고정 높이 -->
                       
                       <div class="flex items-center justify-center w-full h-full">
                           <!-- 해결됨 (클릭 시 대시보드로 이동하여 코드 보기) -->
                           <div v-if="isSolved(member, pid)" class="flex justify-center">
                              <button 
                                  @click="goToDashboard(pid, member.userId)"
                                  class="w-5 h-5 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center hover:bg-emerald-200 hover:scale-110 transition-all"
                                  title="코드 보러 가기"
                              >
                                 <CheckIcon class="w-3 h-3" stroke-width="3" />
                              </button>
                           </div>
                           
                           <!-- 미해결 -->
                           <div v-else class="flex justify-center items-center">
                               <!-- SOS 상태 -->
                               <div v-if="isSos(member, pid)" class="cursor-help animate-pulse" title="SOS 요청 중!">
                                  <FlagIcon class="w-4 h-4 text-rose-500 fill-rose-100" />
                               </div>
                               
                               <!-- 빈 점 -->
                               <span v-else class="w-1.5 h-1.5 rounded-full bg-slate-200"></span>
                           </div>
                       </div>
                       
                       <!-- SOS 버튼 오버레이 -->
                       <button v-if="isCurrentUser(member.userId) && !isSolved(member, pid) && mission.status !== 'COMPLETED'" 
                               @click.stop="toggleSos(pid)"
                               class="absolute inset-0 m-auto w-6 h-6 rounded-full items-center justify-center shadow-sm z-20 hidden group-hover:flex transition-all"
                               :class="isSos(member, pid) ? 'bg-slate-700 text-white' : 'bg-white text-rose-500 border border-rose-100'"
                               :title="isSos(member, pid) ? 'SOS 취소' : 'SOS 요청'">
                          <XIcon v-if="isSos(member, pid)" class="w-3 h-3" />
                          <FlagIcon v-else class="w-3 h-3" />
                       </button>
                    </td>
                   
                   <!-- 진행률 -->
                   <td class="px-2 py-2 text-center font-bold text-[10px] sticky right-0 z-10"
                        :class="isCurrentUser(member.userId) ? 'text-emerald-600 bg-emerald-50/30' : 'text-slate-400 bg-white'">
                       {{ Math.round((member.completedCount / Math.max(mission.totalProblems, 1)) * 100) }}%
                    </td>
                </tr>
             </tbody>
          </table>
       </div>
    </div>

    <!-- 1.5 미션 분석 (난이도 분포 & 태그) -->
    <div v-if="mission.problems && mission.problems.length > 0" class="mb-8 p-4 bg-gradient-to-br from-slate-50 to-white rounded-xl border border-slate-100">
       <h4 class="font-bold text-slate-700 text-sm mb-3 flex items-center gap-2">
          📈 미션 분석
       </h4>
       
       <!-- 난이도 분포 바 -->
       <div class="mb-4">
          <div class="flex items-center justify-between mb-1.5">
             <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wide">난이도 분포</span>
          </div>
          <div class="flex h-3 rounded-full overflow-hidden shadow-inner bg-slate-200">
             <div v-for="tier in tierDistribution" :key="tier.name"
                  :style="{ width: tier.percentage + '%' }"
                  :class="tier.colorClass"
                  :title="`${tier.name}: ${tier.count}문제`"
                  class="transition-all duration-500"
             />
          </div>
          <div class="flex flex-wrap gap-1.5 mt-2">
             <span v-for="tier in tierDistribution" :key="'legend-'+tier.name"
                   class="text-[10px] font-bold px-1.5 py-0.5 rounded-full flex items-center gap-1"
                   :class="tier.legendClass">
                <span class="w-1.5 h-1.5 rounded-full" :class="tier.colorClass"></span>
                {{ tier.name }} {{ tier.count }}
             </span>
          </div>
       </div>

       <!-- 재미있는 멘트 -->
       <p class="text-sm font-bold text-center py-2 px-3 rounded-lg mb-4"
          :class="difficultyMessageStyle">
          {{ difficultyMessage }}
       </p>
       
       <!-- 핵심 알고리즘 태그 -->
       <div v-if="topTags.length > 0">
          <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wide block mb-2">핵심 키워드</span>
          <div class="flex flex-wrap gap-1.5">
             <span v-for="tag in topTags" :key="tag.name"
                   class="px-2 py-0.5 bg-indigo-50 text-indigo-600 rounded-full text-xs font-bold border border-indigo-100">
                {{ tag.name }}
             </span>
          </div>
       </div>
    </div>

    <!-- 2. 문제 목록 및 관리 -->
    <div v-if="isLeader">
       <h4 class="font-bold text-slate-700 text-sm mb-4 flex items-center gap-2">
          🛠️ 문제 관리
       </h4>
       
       <div class="space-y-2">
          <div v-for="pid in mission.problemIds" :key="pid" 
               class="flex items-center justify-between p-3 bg-slate-50 rounded-xl border border-slate-100 hover:border-slate-200 transition-colors group">
             <div class="flex items-center gap-3">
                <span class="text-sm font-bold text-slate-700">#{{ pid }}</span>
                <a :href="`https://www.acmicpc.net/problem/${pid}`" target="_blank" class="text-[10px] text-slate-400 hover:text-brand-500 hover:underline flex items-center gap-1">
                   백준
                   <ExternalLinkIcon class="w-3 h-3" />
                </a>
             </div>
             
             <button v-if="isLeader && mission.status !== 'COMPLETED'" @click="confirmDeleteProblem(pid)" 
                     class="opacity-0 group-hover:opacity-100 px-2 py-1 text-[10px] font-bold text-rose-500 bg-white hover:bg-rose-50 rounded border border-rose-100 transition-all">
                삭제
             </button>
          </div>
          
          <div v-if="isAddingProblem" class="flex items-center gap-2 p-1 bg-white rounded-xl border border-brand-200 shadow-sm animate-in fade-in zoom-in-95 duration-200">
              <span class="pl-2 text-xs font-bold text-slate-500">#</span>
              <input 
                  ref="inputRef"
                  v-model="newProblemId"
                  @keyup.enter="submitAddProblem"
                  type="text" 
                  placeholder="문제 번호" 
                  class="flex-1 w-full text-sm font-bold text-slate-700 outline-none placeholder:text-slate-300 placeholder:font-normal bg-transparent py-1.5"
              />
              <button @click="submitAddProblem" class="p-1.5 bg-brand-500 text-white rounded-lg hover:bg-brand-600 transition-colors">
                  <CheckIcon class="w-4 h-4" stroke-width="3" />
              </button>
              <button @click="cancelAdding" class="p-1.5 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors">
                  <XIcon class="w-4 h-4" />
              </button>
          </div>

          <button v-else-if="isLeader && mission.status !== 'COMPLETED'" @click="startAdding" 
                  class="w-full py-2.5 border border-dashed border-slate-300 rounded-xl text-slate-400 text-xs font-bold hover:border-brand-300 hover:text-brand-500 hover:bg-brand-50/50 transition-all flex items-center justify-center gap-1">
             <PlusIcon class="w-4 h-4" />
             문제 추가
          </button>
       </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { 
    X as XIcon, 
    Check as CheckIcon, 
    Flag as FlagIcon, 
    ExternalLink as ExternalLinkIcon,
    Plus as PlusIcon
} from 'lucide-vue-next';
import NicknameRenderer from '@/components/common/NicknameRenderer.vue';
import axios from 'axios';

const props = defineProps({
  mission: {
    type: Object,
    required: true
  },
  currentUserId: Number,
  studyId: Number,
  isLeader: Boolean
});

const emit = defineEmits(['close', 'refresh', 'open-add-modal']);

const router = useRouter();

const isAddingProblem = ref(false);
const newProblemId = ref('');
const inputRef = ref(null);

const sortedMemberProgressList = computed(() => {
   if (!props.mission.memberProgressList) return [];
   return [...props.mission.memberProgressList].sort((a, b) => {
      if (a.userId === props.currentUserId) return -1;
      if (b.userId === props.currentUserId) return 1;
      return 0;
   });
});

// 난이도 분포 계산
const tierDistribution = computed(() => {
  const tiers = { bronze: 0, silver: 0, gold: 0, platinum: 0, diamond: 0 };
  props.mission.problems?.forEach(p => {
    if (p.level <= 5) tiers.bronze++;
    else if (p.level <= 10) tiers.silver++;
    else if (p.level <= 15) tiers.gold++;
    else if (p.level <= 20) tiers.platinum++;
    else tiers.diamond++;
  });
  const total = Object.values(tiers).reduce((a, b) => a + b, 0) || 1;
  return [
    { name: '브론즈', count: tiers.bronze, percentage: (tiers.bronze / total) * 100, colorClass: 'bg-amber-700', legendClass: 'bg-amber-100 text-amber-700' },
    { name: '실버', count: tiers.silver, percentage: (tiers.silver / total) * 100, colorClass: 'bg-slate-400', legendClass: 'bg-slate-100 text-slate-600' },
    { name: '골드', count: tiers.gold, percentage: (tiers.gold / total) * 100, colorClass: 'bg-yellow-400', legendClass: 'bg-yellow-100 text-yellow-700' },
    { name: '플래티넘', count: tiers.platinum, percentage: (tiers.platinum / total) * 100, colorClass: 'bg-emerald-400', legendClass: 'bg-emerald-100 text-emerald-700' },
    { name: '다이아', count: tiers.diamond, percentage: (tiers.diamond / total) * 100, colorClass: 'bg-sky-400', legendClass: 'bg-sky-100 text-sky-700' },
  ].filter(t => t.count > 0);
});

// 평균 난이도 기반 재미있는 멘트
const difficultyMessage = computed(() => {
  const problems = props.mission.problems || [];
  if (problems.length === 0) return '';
  const avg = problems.reduce((sum, p) => sum + (p.level || 0), 0) / problems.length;
  if (avg <= 5) return '🌱 가볍게 몸 풀어볼까요?';
  if (avg <= 10) return '💪 적당한 챌린지!';
  if (avg <= 15) return '🔥 불지옥 주간입니다!';
  if (avg <= 20) return '💀 생존을 기원합니다...';
  return '🚀 전설을 향해!';
});

const difficultyMessageStyle = computed(() => {
  const problems = props.mission.problems || [];
  if (problems.length === 0) return 'bg-slate-100 text-slate-500';
  const avg = problems.reduce((sum, p) => sum + (p.level || 0), 0) / problems.length;
  if (avg <= 5) return 'bg-emerald-50 text-emerald-600';
  if (avg <= 10) return 'bg-blue-50 text-blue-600';
  if (avg <= 15) return 'bg-orange-50 text-orange-600';
  return 'bg-rose-50 text-rose-600';
});

// 핵심 알고리즘 태그 (상위 5개)
const topTags = computed(() => {
  const tagCount = {};
  props.mission.problems?.forEach(p => {
    p.tags?.forEach(t => { tagCount[t] = (tagCount[t] || 0) + 1; });
  });
  return Object.entries(tagCount)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5)
    .map(([name, count]) => ({ name, count }));
});

const isCurrentUser = (userId) => props.currentUserId === userId;

const isSolved = (member, problemId) => {
   return member.solvedProblemIds && member.solvedProblemIds.includes(problemId);
};

const isSos = (member, problemId) => {
   return member.sosProblemIds && member.sosProblemIds.includes(problemId);
};

const toggleSos = async (problemId) => {
   try {
      await axios.post(`/api/studies/${props.studyId}/missions/${props.mission.id}/problems/${problemId}/sos`);
      emit('refresh'); // Refresh parent data
   } catch (e) {
      alert('SOS 요청 처리에 실패했습니다.');
   }
};

const confirmDeleteProblem = async (problemId) => {
   if (!confirm(`정말 문제 #${problemId}를 이 미션에서 삭제하시겠습니까?`)) return;
   
   try {
      await axios.delete(`/api/studies/${props.studyId}/missions/${props.mission.id}/problems/${problemId}`);
      emit('refresh');
   } catch (e) {
      alert('문제 삭제에 실패했습니다.');
   }
};

const startAdding = async () => {
    isAddingProblem.value = true;
    newProblemId.value = '';
    await nextTick();
    if (inputRef.value) inputRef.value.focus();
};

const cancelAdding = () => {
    isAddingProblem.value = false;
    newProblemId.value = '';
};

const submitAddProblem = async () => {
    if (!newProblemId.value) return;
    
    // 간단한 유효성 검사 (숫자 확인)
    const pid = parseInt(newProblemId.value);
    if (isNaN(pid)) {
        alert('유효한 문제 번호를 입력해주세요.');
        return;
    }

    try {
        await axios.put(`/api/studies/${props.studyId}/missions/${props.mission.id}/problems`, {
            problemIds: [pid]
        });
        emit('refresh');
        isAddingProblem.value = false;
        newProblemId.value = '';
    } catch (e) {
        console.error('문제 추가 실패', e);
        alert('문제 추가 중 오류가 발생했습니다.');
    }
};

const goToDashboard = (problemId, userId) => {
    // 관리자가 아닌 일반 유저의 대시보드 경로는 /dashboard 입니다.
    router.push({
        path: '/dashboard',
        query: {
            problemNumber: problemId,
            userId: userId,
            // drawer: 'true' // 추후 상세 뷰 바로 열기 지원 시 활성화
        }
    });
};
</script>
