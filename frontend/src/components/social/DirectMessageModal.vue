<template>
    <div class="fixed inset-0 z-[9999] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="$emit('close')"></div>
        
        <div class="relative bg-white rounded-3xl w-full max-w-lg shadow-2xl flex flex-col h-[600px] overflow-hidden animate-in fade-in zoom-in-95 duration-200">
            <!-- Header -->
            <div class="px-6 py-4 bg-slate-50 border-b border-slate-100 flex items-center justify-between shrink-0">
                <div class="flex items-center gap-3">
                    <img 
                        :src="isPartnerDeleted ? 'https://avatars.githubusercontent.com/u/0' : (partnerInfo.avatar || '/images/profiles/default-profile.png')" 
                        class="w-10 h-10 rounded-full border border-brand-100 object-cover bg-white"
                        :class="{ 'grayscale opacity-60': isPartnerDeleted }"
                        :alt="partnerInfo.name"
                    />
                    <div>
                        <div class="font-bold text-base" :class="isPartnerDeleted ? 'text-slate-400' : 'text-slate-800'">
                            {{ isPartnerDeleted ? '탈퇴한 회원' : partnerInfo.name }}
                        </div>
                        <p v-if="!isPartnerDeleted" class="text-xs text-brand-500 font-bold">Online</p>
                    </div>
                </div>
                <button @click="$emit('close')" class="p-2 text-slate-400 hover:text-slate-600 bg-white rounded-xl shadow-sm hover:shadow-md transition-all">
                    <X :size="20"/>
                </button>
            </div>
            
            <!-- Message List -->
            <div class="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50" ref="messagesContainer">
                <div v-if="loading" class="flex justify-center py-10"><Loader2 class="animate-spin text-brand-500"/></div>
                <template v-else>
                    <template v-for="(msg, index) in messages" :key="msg.id">
                        <!-- Date Separator -->
                        <div v-if="showDateSeparator(index, messages)" class="w-full flex justify-center my-6">
                            <span class="text-xs font-bold text-slate-600 bg-slate-200/80 px-4 py-1.5 rounded-full border border-slate-300/50 shadow-sm">
                                {{ formatDate(msg.createdAt) }}
                            </span>
                        </div>

                        <!-- Message Item -->
                        <div 
                            class="flex flex-col"
                            :class="msg.isMine ? 'items-end' : 'items-start'"
                        >
                             <div 
                                class="max-w-[70%] px-4 py-2.5 rounded-2xl text-sm font-medium shadow-sm leading-relaxed whitespace-pre-wrap"
                                :class="msg.isMine ? 'bg-brand-500 text-white rounded-tr-sm' : 'bg-white text-slate-700 border border-slate-200 rounded-tl-sm'"
                             >
                                {{ msg.content }}
                             </div>
                             <span class="text-[10px] text-slate-400 mt-1 px-1">{{ formatTime(msg.createdAt) }}</span>
                        </div>
                    </template>
                </template>
            </div>
            
            <!-- Input Area -->
            <div class="p-4 bg-white border-t border-slate-100 shrink-0">
                <!-- 탈퇴 회원 채팅방 -->
                <div v-if="isPartnerDeleted" class="p-4 bg-rose-50 border border-rose-200 rounded-xl flex items-center justify-center gap-2">
                    <AlertCircle :size="18" class="text-rose-500 shrink-0"/>
                    <p class="text-sm text-rose-600 font-bold">탈퇴한 회원에게는 메시지를 보낼 수 없습니다.</p>
                </div>
                <!-- 일반 입력창 -->
                <form v-else @submit.prevent="sendMessage" class="flex items-center gap-2">
                    <input 
                        v-model="newMessage" 
                        type="text" 
                        placeholder="메시지를 입력하세요..." 
                        class="flex-1 px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 transition-all text-sm font-medium"
                        :disabled="sending"
                    />
                    <button 
                        type="submit" 
                        class="p-3 bg-brand-500 text-white rounded-xl shadow-lg shadow-brand-500/30 hover:bg-brand-600 transition-all flex items-center justify-center"
                        :disabled="!newMessage.trim() || sending"
                    >
                        <Send :size="20" :class="sending ? 'opacity-0' : ''"/>
                        <Loader2 v-if="sending" class="animate-spin absolute" :size="20"/>
                    </button>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue';
import { socialApi } from '@/api/social';
import { userApi } from '@/api/user';
import { X, Send, Loader2, AlertCircle } from 'lucide-vue-next';
import NicknameRenderer from '@/components/common/NicknameRenderer.vue';

const props = defineProps({
    partnerId: Number,
    partnerName: String,
    partnerAvatar: String,
    partnerDecoration: String,
    partnerIsDeleted: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['close']);

const messages = ref([]);
const loading = ref(true);
const newMessage = ref('');
const sending = ref(false);
const messagesContainer = ref(null);

// 실시간 탈퇴 여부
const isPartnerDeleted = ref(props.partnerIsDeleted);
const partnerInfo = ref({
    name: props.partnerName,
    avatar: props.partnerAvatar,
    decoration: props.partnerDecoration
});

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
    });
};

// 상대방 정보 조회 (탈퇴 여부 확인)
const fetchPartnerInfo = async () => {
    try {
        const res = await userApi.getById(props.partnerId);
        const user = res.data;
        isPartnerDeleted.value = user.isDeleted || false;
        partnerInfo.value = {
            name: user.username || props.partnerName,
            avatar: user.avatarUrl || props.partnerAvatar,
            decoration: user.equippedDecorationClass || props.partnerDecoration
        };
    } catch (e) {
        console.error('Failed to fetch partner info:', e);
        isPartnerDeleted.value = props.partnerIsDeleted;
    }
};

const fetchMessages = async () => {
    const isInitialLoad = loading.value;
    try {
        const res = await socialApi.getConversation(props.partnerId);
        messages.value = res.data;
    } catch(e) {
        console.error(e);
    } finally {
        loading.value = false;
        // 최초 로드 시 맨 아래로 스크롤
        if (isInitialLoad) {
            scrollToBottom();
        }
    }
};

const sendMessage = async () => {
    if (!newMessage.value.trim() || sending.value || isPartnerDeleted.value) return;
    
    const content = newMessage.value;
    newMessage.value = ''; // 낙관적 업데이트 (Optimistic clear)
    sending.value = true;
    
    try {
        await socialApi.sendMessage(props.partnerId, content);
        await fetchMessages(); // 새 메시지 확인 (혹은 시뮬레이션된 응답 등)
        scrollToBottom();
    } catch(e) {
        console.error(e);
        newMessage.value = content; // 실패 시 복구
        // 탈퇴 회원 에러 메시지 확인
        const errorMsg = e.response?.data?.message || '전송 실패';
        alert(errorMsg);
    } finally {
        sending.value = false;
    }
};

const formatTime = (iso) => {
    const d = new Date(iso);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const formatDate = (iso) => {
    const d = new Date(iso);
    return d.toLocaleDateString('ko-KR', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric', 
        weekday: 'long' 
    });
};

const showDateSeparator = (index, msgs) => {
    if (index === 0) return true;
    
    const currentMsgDate = new Date(msgs[index].createdAt).toLocaleDateString();
    const prevMsgDate = new Date(msgs[index - 1].createdAt).toLocaleDateString();
    
    return currentMsgDate !== prevMsgDate;
};
let pollInterval;

onMounted(async () => {
    // 상대방 정보 먼저 조회 (탈퇴 여부 확인)
    await fetchPartnerInfo();
    fetchMessages();
    pollInterval = setInterval(fetchMessages, 3000); // 3초마다 새 메시지 폴링
});

onBeforeUnmount(() => {
    clearInterval(pollInterval);
});

</script>
