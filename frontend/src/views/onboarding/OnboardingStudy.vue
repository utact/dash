<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-950 text-slate-100 p-6">
    <div class="max-w-4xl w-full space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div class="text-center space-y-2">
        <h1 class="text-3xl font-bold text-white">스터디 매칭</h1>
        <p class="text-slate-400">함께 성장할 동료를 찾거나 새로운 여정을 시작하세요.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Join Existing Study -->
        <div class="space-y-4">
          <h2 class="text-xl font-semibold text-slate-200">추천 스터디</h2>
          <div class="space-y-3 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
            <div 
              v-for="study in studies" 
              :key="study.id"
              class="group p-5 rounded-xl bg-white/5 border border-white/10 hover:border-indigo-500/50 hover:bg-indigo-500/10 transition-all cursor-pointer flex justify-between items-center"
              @click="joinStudy(study.id)"
            >
              <div>
                <h3 class="font-bold text-slate-200 group-hover:text-indigo-300 transition-colors">{{ study.name }}</h3>
                <p class="text-sm text-slate-500 mt-1">멤버 {{ study.memberCount || 0 }}명 · 정복도 {{ study.progress || 0 }}%</p>
              </div>
              <div class="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-slate-400 group-hover:bg-indigo-500 group-hover:text-white transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            
            <div v-if="studies.length === 0" class="p-8 text-center text-slate-500 bg-white/5 rounded-xl border border-dashed border-slate-700">
              참여 가능한 스터디가 없습니다.<br>새로운 스터디를 만들어보세요!
            </div>
          </div>
        </div>

        <!-- Create New Study -->
        <div class="md:border-l md:border-white/10 md:pl-6 space-y-6">
          <h2 class="text-xl font-semibold text-slate-200">새 스터디 만들기</h2>
          <form @submit.prevent="createStudy" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-slate-400 mb-2">스터디 이름</label>
              <input 
                v-model="newStudyName"
                type="text" 
                placeholder="예: 알고리즘 정복원정대"
                class="w-full bg-slate-900/50 border border-slate-700 rounded-xl px-4 py-3 text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 font-mono"
                required
              />
            </div>
            
            <button 
              type="submit"
              class="w-full bg-slate-100 hover:bg-white text-slate-900 font-bold py-3.5 rounded-xl transition-all shadow-lg flex items-center justify-center gap-2"
              :disabled="creating"
            >
              <span>{{ creating ? '생성 중...' : '스터디 생성하기' }}</span>
            </button>
          </form>

          <div class="bg-indigo-500/10 rounded-xl p-4 text-sm text-indigo-200">
            <h4 class="font-bold mb-1">💡 스터디장이 되면?</h4>
            <ul class="list-disc list-inside space-y-1 opacity-80">
              <li>스터디 방향성을 설정할 수 있어요.</li>
              <li>멤버 관리 권한을 가집니다.</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { studyApi } from '../../api/study';

const router = useRouter();
const studies = ref([]);
const newStudyName = ref('');
const creating = ref(false);

const loadStudies = async () => {
  try {
    const res = await studyApi.getStudies();
    studies.value = res.data;
  } catch (error) {
    console.error('Failed to load studies', error);
  }
};

const joinStudy = async (id) => {
  try {
    await studyApi.joinStudy(id);
    router.push('/onboarding/repo');
  } catch (error) {
    alert('스터디 가입에 실패했습니다.');
  }
};

const createStudy = async () => {
  if (!newStudyName.value) return;
  
  creating.value = true;
  try {
    await studyApi.createStudy(newStudyName.value);
    router.push('/onboarding/repo');
  } catch (error) {
    alert('스터디 생성에 실패했습니다.');
  } finally {
    creating.value = false;
  }
};

onMounted(() => {
  loadStudies();
});
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>
