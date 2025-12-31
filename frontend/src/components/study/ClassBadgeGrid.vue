<template>
  <div class="class-badge-grid bg-white/50 backdrop-blur border border-white/60 rounded-3xl p-6 shadow-xl shadow-brand-500/5">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-sm font-bold text-slate-500 uppercase tracking-wide">Class Badges</h3>
      <span class="text-xs font-medium text-slate-400">Earned: {{ filteredStats.length }}</span>
    </div>

    <!-- 작은 뱃지를 위한 그리드 조정 -->
    <div class="grid grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-3">
      <div 
        v-for="stat in filteredStats" 
        :key="stat.classNumber"
        class="group relative"
      >
        <!-- 뱃지 카드 (컴팩트) -->
        <div 
          class="aspect-[4/5] rounded-xl border flex flex-col items-center justify-center p-2 transition-all duration-300"
          :class="getBadgeStyle(stat.decoration)"
        >
          <!-- 아이콘/메달 (작게) -->
          <div class="w-8 h-8 mb-1.5 text-2xl flex items-center justify-center drop-shadow-md transform group-hover:scale-110 transition-transform duration-300">
            {{ getBadgeIcon(stat.decoration) }}
          </div>

          <!-- 라벨 (작게) -->
          <div class="text-center leading-tight">
            <div class="text-[10px] font-bold uppercase opacity-60">Class</div>
            <div class="text-lg font-extrabold tracking-tight">{{ stat.classNumber }}</div>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="filteredStats.length === 0" class="col-span-full py-8 text-center text-slate-400 text-sm">
         아직 획득한 클래스 뱃지가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  stats: {
    type: Array,
    default: () => []
  }
});

const filteredStats = computed(() => {
  return props.stats.filter(stat => stat.decoration && stat.decoration !== 'none');
});

const getBadgeStyle = (decoration) => {
  switch (decoration) {
    case 'gold':
      return 'bg-gradient-to-b from-amber-50 to-amber-100 border-amber-200 text-amber-800 shadow-sm hover:shadow-md hover:-translate-y-0.5';
    case 'silver':
      return 'bg-gradient-to-b from-slate-50 to-slate-200 border-slate-300 text-slate-700 shadow-sm hover:shadow-md hover:-translate-y-0.5';
    case 'completed': 
      return 'bg-gradient-to-b from-emerald-50 to-emerald-100 border-emerald-200 text-emerald-800 shadow-sm hover:shadow-md hover:-translate-y-0.5';
    default:
      return 'bg-slate-50 border-slate-100 text-slate-400';
  }
};

const getBadgeIcon = (decoration) => {
  switch (decoration) {
    case 'gold': return '🏆';
    case 'silver': return '🥈';
    case 'completed': return '🏅';
    default: return '🔒';
  }
};
</script>

<style scoped>
@keyframes shine {
  0% { transform: translateX(-100%) skewX(-15deg); }
  100% { transform: translateX(200%) skewX(-15deg); }
}

.animate-shine {
  animation: shine 2s infinite linear; 
  /* 보통 수동으로 트리거되거나 hover 로직으로 작동하지만, 
     여기서는 CSS hover 시 오버플로우 문제로 인해 간단히 처리합니다.
     간소화됨: 현재는 group-hover opacity를 대신 사용합니다. */
}
</style>
