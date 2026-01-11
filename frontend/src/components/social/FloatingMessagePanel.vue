<template>
    <!-- 로그인한 사용자에게만 표시 -->
    <div v-if="isAuthenticated" class="fixed z-50 bottom-20 right-4 md:bottom-6 md:right-6">
        <!-- 플로팅 버튼 -->
        <Transition name="scale">
            <button
                v-if="!isOpen"
                @click="toggle"
                class="w-12 h-12 md:w-14 md:h-14 bg-brand-600 hover:bg-brand-500 text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200 flex items-center justify-center group hover:scale-105"
            >
                <MessageCircle :size="20" class="md:hidden group-hover:scale-110 transition-transform" />
                <MessageCircle :size="24" class="hidden md:block group-hover:scale-110 transition-transform" />
                <!-- 안읽음 뱃지 -->
                <div 
                    v-if="totalUnread > 0" 
                    class="absolute -top-1 -right-1 min-w-[20px] h-[20px] md:min-w-[22px] md:h-[22px] bg-rose-500 text-white text-[10px] md:text-xs font-bold rounded-full flex items-center justify-center px-1 shadow-md"
                >
                    {{ totalUnread > 99 ? '99+' : totalUnread }}
                </div>
            </button>
        </Transition>

        <!-- 확장 패널 -->
        <Transition name="slide-up">
            <div 
                v-if="isOpen"
                class="fixed bottom-0 right-0 md:absolute md:bottom-0 md:right-0 w-full md:w-[380px] h-[85vh] md:h-auto bg-white md:rounded-2xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col"
                :style="{ maxHeight: viewMode === 'chat' || viewMode === 'groupChat' ? '85vh' : 'auto' }"
            >
                <!-- 헤더 - 대화 목록 -->
                <div v-if="viewMode === 'list'" class="flex items-center justify-between p-4 border-b border-slate-100 bg-brand-600 text-white shrink-0">
                    <div class="flex items-center gap-2">
                        <MessageCircle :size="20" />
                        <span class="font-bold">메시지</span>
                        <span v-if="totalUnread > 0" class="px-2 py-0.5 bg-white/20 rounded-full text-xs">{{ totalUnread }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                        <button @click="showCreateModal = true" class="p-1.5 hover:bg-white/20 rounded-lg transition-colors" title="새 대화">
                            <Plus :size="16" />
                        </button>
                        <button @click="openFullView" class="p-1.5 hover:bg-white/20 rounded-lg transition-colors" title="전체보기">
                            <Maximize2 :size="16" />
                        </button>
                        <button @click="toggle" class="p-1.5 hover:bg-white/20 rounded-lg transition-colors" title="닫기">
                            <X :size="16" />
                        </button>
                    </div>
                </div>

                <!-- 헤더 - 1:1 채팅 뷰 -->
                <div v-else-if="viewMode === 'chat'" class="flex items-center justify-between p-3 border-b border-slate-100 bg-slate-50 shrink-0">
                    <div class="flex items-center gap-3">
                        <button @click="goBack" class="p-1.5 hover:bg-slate-200 rounded-lg transition-colors text-slate-600">
                            <ChevronLeft :size="20" />
                        </button>
                        <img 
                            :src="getAvatar(activeChat?.partnerAvatar, isPartnerDeleted)" 
                            class="w-9 h-9 rounded-full border border-slate-200 bg-white object-cover"
                            :class="{ 'grayscale opacity-60': isPartnerDeleted }"
                        />
                        <span class="font-bold text-slate-800 text-sm" :class="{ 'text-slate-500': isPartnerDeleted }">
                            {{ isPartnerDeleted ? '탈퇴한 회원' : activeChat?.partnerName }}
                        </span>
                    </div>
                    <div class="flex items-center gap-1">
                        <button @click="toggle" class="p-1.5 hover:bg-slate-200 rounded-lg transition-colors text-slate-500" title="닫기">
                            <X :size="16" />
                        </button>
                    </div>
                </div>

                <!-- 헤더 - 그룹 채팅 뷰 -->
                <div v-else-if="viewMode === 'groupChat'" class="flex items-center justify-between p-3 border-b border-slate-100 bg-indigo-50 shrink-0">
                    <div class="flex items-center gap-3">
                        <button @click="goBack" class="p-1.5 hover:bg-indigo-100 rounded-lg transition-colors text-indigo-600">
                            <ChevronLeft :size="20" />
                        </button>
                        <div class="w-9 h-9 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white text-xs font-bold">
                            {{ activeGroupRoom?.members?.length || 0 }}
                        </div>
                        <div>
                            <h3 class="font-bold text-slate-800 text-sm">{{ activeGroupRoom?.name }}</h3>
                            <p class="text-[10px] text-slate-400">{{ activeGroupRoom?.members?.length || 0 }}명</p>
                        </div>
                    </div>
                    <div class="flex items-center gap-1">
                        <button @click="showGroupMembers = !showGroupMembers" class="p-1.5 hover:bg-indigo-100 rounded-lg transition-colors text-indigo-500" title="멤버">
                            <Users :size="16" />
                        </button>
                        <button @click="toggle" class="p-1.5 hover:bg-indigo-100 rounded-lg transition-colors text-slate-500" title="닫기">
                            <X :size="16" />
                        </button>
                    </div>
                </div>

                <!-- 대화 목록 뷰 (1:1 + 그룹 통합) -->
                <div v-if="viewMode === 'list'" class="max-h-[450px] overflow-y-auto">
                    <div v-if="loading" class="flex justify-center py-10">
                        <Loader2 class="animate-spin text-brand-500" />
                    </div>
                    <div v-else-if="allConversations.length === 0" class="text-center py-10 text-slate-400">
                        <MessageCircle :size="32" class="mx-auto mb-2 opacity-30" />
                        <p class="text-sm">아직 대화가 없어요</p>
                        <button @click="showCreateModal = true" class="mt-3 text-brand-500 text-sm font-bold hover:underline">
                            + 새 대화 시작하기
                        </button>
                    </div>
                    <div v-else>
                        <div 
                            v-for="conv in allConversations" 
                            :key="conv.key" 
                            @click="conv.isGroup ? openGroupChat(conv) : openChat(conv)"
                            class="flex items-center gap-3 p-4 hover:bg-slate-50 cursor-pointer transition-colors border-b border-slate-50 last:border-b-0"
                        >
                            <div class="relative">
                                <!-- 그룹 채팅 아이콘 -->
                                <div v-if="conv.isGroup" class="w-11 h-11 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white font-bold text-sm">
                                    {{ conv.memberCount }}
                                </div>
                                <!-- 1:1 채팅 아바타 -->
                                <img v-else
                                    :src="getAvatar(conv.partnerAvatar)" 
                                    class="w-11 h-11 rounded-full border border-slate-200 bg-white object-cover"
                                />
                                <div 
                                    v-if="conv.unreadCount > 0" 
                                    class="absolute -top-1 -right-1 min-w-[16px] h-[16px] bg-rose-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1"
                                >
                                    {{ conv.unreadCount > 9 ? '9+' : conv.unreadCount }}
                                </div>
                            </div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center gap-2">
                                    <span class="font-semibold text-slate-800 text-sm truncate">{{ conv.name }}</span>
                                    <span v-if="conv.isGroup && conv.type === 'STUDY'" class="text-[9px] px-1.5 py-0.5 bg-indigo-100 text-indigo-600 rounded font-bold shrink-0">스터디</span>
                                    <span v-else-if="conv.isGroup" class="text-[9px] px-1.5 py-0.5 bg-purple-100 text-purple-600 rounded font-bold shrink-0">그룹</span>
                                </div>
                                <p class="text-xs text-slate-500 truncate mt-0.5">{{ conv.lastMessagePreview || '메시지 없음' }}</p>
                            </div>
                            <span class="text-[10px] text-slate-400 shrink-0">{{ formatTime(conv.lastMessageTime) }}</span>
                        </div>
                    </div>
                </div>

                <!-- 1:1 채팅 뷰 -->
                <template v-if="viewMode === 'chat'">
                    <!-- 메시지 목록 -->
                    <div class="flex-1 overflow-y-auto p-3 space-y-3 bg-slate-50" ref="messagesContainer">
                        <div v-if="messagesLoading" class="flex justify-center py-8">
                            <Loader2 class="animate-spin text-brand-500" />
                        </div>
                        <template v-else>
                            <template v-for="(msg, index) in messages" :key="msg.id">
                                <div v-if="showDateSeparator(index)" class="w-full flex justify-center my-4">
                                    <span class="text-[10px] font-bold text-slate-500 bg-slate-200/80 px-3 py-1 rounded-full">
                                        {{ formatDate(msg.createdAt) }}
                                    </span>
                                </div>
                                <div class="flex flex-col" :class="msg.isMine ? 'items-end' : 'items-start'">
                                    <div 
                                        class="max-w-[75%] px-3 py-2 rounded-2xl text-sm shadow-sm leading-relaxed whitespace-pre-wrap"
                                        :class="msg.isMine ? 'bg-brand-500 text-white rounded-tr-sm' : 'bg-white text-slate-700 border border-slate-200 rounded-tl-sm'"
                                    >
                                        {{ msg.content }}
                                    </div>
                                    <span class="text-[9px] text-slate-400 mt-0.5 px-1">{{ formatMsgTime(msg.createdAt) }}</span>
                                </div>
                            </template>
                        </template>
                    </div>

                    <!-- 입력 영역 -->
                    <div class="p-3 bg-white border-t border-slate-100 shrink-0">
                        <div v-if="isPartnerDeleted" class="p-3 bg-rose-50 border border-rose-200 rounded-xl flex items-center justify-center gap-2">
                            <AlertCircle :size="16" class="text-rose-500 shrink-0"/>
                            <p class="text-xs text-rose-600 font-bold">탈퇴한 회원에게는 메시지를 보낼 수 없습니다.</p>
                        </div>
                        <form v-else @submit.prevent="sendMessage" class="flex items-center gap-2">
                            <input 
                                v-model="newMessage" 
                                type="text" 
                                placeholder="메시지 입력..." 
                                class="flex-1 px-3 py-2.5 bg-slate-100 border-0 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/30 transition-all text-sm"
                                :disabled="sending"
                            />
                            <button 
                                type="submit" 
                                class="p-2.5 bg-brand-500 text-white rounded-xl hover:bg-brand-600 disabled:opacity-50 transition-all"
                                :disabled="!newMessage.trim() || sending"
                            >
                                <Send :size="18" />
                            </button>
                        </form>
                    </div>
                </template>

                <!-- 그룹 채팅 뷰 -->
                <template v-if="viewMode === 'groupChat'">
                    <!-- 멤버 목록 (토글) -->
                    <Transition name="slide-down">
                        <div v-if="showGroupMembers" class="p-3 bg-indigo-50 border-b border-indigo-100">
                            <div class="flex flex-wrap gap-2">
                                <div v-for="member in activeGroupRoom?.members" :key="member.userId" class="flex items-center gap-1.5 bg-white px-2 py-1 rounded-full border border-slate-200">
                                    <img :src="getAvatar(member.avatarUrl)" class="w-5 h-5 rounded-full" />
                                    <span class="text-xs font-medium text-slate-600">{{ member.username }}</span>
                                </div>
                            </div>
                        </div>
                    </Transition>

                    <!-- 메시지 목록 -->
                    <div class="flex-1 overflow-y-auto p-3 space-y-3 bg-slate-50" ref="groupMessagesContainer">
                        <div v-if="groupMessagesLoading" class="flex justify-center py-8">
                            <Loader2 class="animate-spin text-indigo-500" />
                        </div>
                        <template v-else>
                            <template v-for="msg in groupMessages" :key="msg.id">
                                <div v-if="msg.isMe" class="flex justify-end">
                                    <div class="flex flex-col items-end max-w-[75%]">
                                        <div class="bg-indigo-500 text-white px-3 py-2 rounded-2xl rounded-br-sm text-sm whitespace-pre-wrap break-words">
                                            {{ msg.content }}
                                        </div>
                                        <span class="text-[9px] text-slate-400 mt-0.5 px-1">{{ formatMsgTime(msg.createdAt) }}</span>
                                    </div>
                                </div>
                                <div v-else class="flex gap-2">
                                    <img :src="getAvatar(msg.senderAvatarUrl)" class="w-8 h-8 rounded-full shrink-0" />
                                    <div class="flex flex-col max-w-[75%]">
                                        <span class="text-[10px] text-slate-500 font-medium mb-0.5">{{ msg.senderUsername }}</span>
                                        <div class="bg-white border border-slate-200 px-3 py-2 rounded-2xl rounded-tl-sm text-sm text-slate-700 whitespace-pre-wrap break-words">
                                            {{ msg.content }}
                                        </div>
                                        <span class="text-[9px] text-slate-400 mt-0.5 px-1">{{ formatMsgTime(msg.createdAt) }}</span>
                                    </div>
                                </div>
                            </template>
                        </template>
                    </div>

                    <!-- 입력 영역 -->
                    <div class="p-3 bg-white border-t border-slate-100 shrink-0">
                        <form @submit.prevent="sendGroupMessage" class="flex items-center gap-2">
                            <input 
                                v-model="newGroupMessage" 
                                type="text" 
                                placeholder="메시지 입력..." 
                                class="flex-1 px-3 py-2.5 bg-slate-100 border-0 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500/30 transition-all text-sm"
                                :disabled="groupSending"
                            />
                            <button 
                                type="submit" 
                                class="p-2.5 bg-indigo-500 text-white rounded-xl hover:bg-indigo-600 disabled:opacity-50 transition-all"
                                :disabled="!newGroupMessage.trim() || groupSending"
                            >
                                <Send :size="18" />
                            </button>
                        </form>
                    </div>
                </template>
            </div>
        </Transition>

        <!-- 새 대화 생성 모달 -->
        <Transition name="fade">
            <div v-if="showCreateModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4" @click.self="showCreateModal = false">
                <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm"></div>
                <div class="relative bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden">
                    <div class="p-4 border-b border-slate-100 flex items-center justify-between">
                        <h3 class="font-bold text-slate-800">새 대화 시작</h3>
                        <button @click="showCreateModal = false" class="p-1.5 hover:bg-slate-100 rounded-lg">
                            <X :size="18" class="text-slate-400" />
                        </button>
                    </div>
                    
                    <!-- 대화 타입 선택 -->
                    <div v-if="createStep === 'type'" class="p-4 space-y-3">
                        <button 
                            @click="startDMCreate"
                            class="w-full p-4 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors text-left flex items-center gap-4"
                        >
                            <div class="w-12 h-12 rounded-full bg-brand-100 flex items-center justify-center">
                                <MessageCircle :size="24" class="text-brand-600" />
                            </div>
                            <div>
                                <h4 class="font-bold text-slate-800">1:1 대화</h4>
                                <p class="text-xs text-slate-500">친구와 개인 대화</p>
                            </div>
                        </button>
                        <button 
                            @click="startGroupCreate"
                            class="w-full p-4 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors text-left flex items-center gap-4"
                        >
                            <div class="w-12 h-12 rounded-full bg-indigo-100 flex items-center justify-center">
                                <Users :size="24" class="text-indigo-600" />
                            </div>
                            <div>
                                <h4 class="font-bold text-slate-800">그룹 대화</h4>
                                <p class="text-xs text-slate-500">여러 친구와 함께</p>
                            </div>
                        </button>
                    </div>

                    <!-- 1:1 친구 선택 -->
                    <div v-else-if="createStep === 'dm'" class="p-4">
                        <div class="mb-3">
                            <input 
                                v-model="friendSearch"
                                type="text" 
                                placeholder="친구 검색..."
                                class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/20"
                            />
                        </div>
                        <div class="max-h-64 overflow-y-auto border border-slate-200 rounded-lg">
                            <div 
                                v-for="friend in filteredFriends" 
                                :key="friend.friend?.id" 
                                @click="startDMWithFriend(friend.friend)"
                                class="flex items-center gap-3 p-3 hover:bg-slate-50 cursor-pointer border-b border-slate-100 last:border-b-0"
                            >
                                <img :src="getAvatar(friend.friend?.avatarUrl)" class="w-10 h-10 rounded-full" />
                                <span class="text-sm font-medium text-slate-700">{{ friend.friend?.username }}</span>
                            </div>
                            <div v-if="filteredFriends.length === 0" class="p-4 text-center text-sm text-slate-400">
                                {{ friendSearch ? '검색 결과가 없습니다' : '친구가 없습니다' }}
                            </div>
                        </div>
                    </div>

                    <!-- 그룹 채팅 생성 -->
                    <div v-else-if="createStep === 'group'" class="p-4 space-y-4">
                        <div>
                            <label class="text-xs font-bold text-slate-600 mb-1.5 block">채팅방 이름</label>
                            <input 
                                v-model="groupForm.name" 
                                type="text" 
                                placeholder="예: 알고리즘 잡담방"
                                class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
                            />
                        </div>
                        <div>
                            <label class="text-xs font-bold text-slate-600 mb-1.5 block">초대할 친구 ({{ groupForm.memberIds.length }}명 선택)</label>
                            <div class="max-h-48 overflow-y-auto border border-slate-200 rounded-lg">
                                <div 
                                    v-for="friend in friends" 
                                    :key="friend.friend?.id" 
                                    @click="toggleGroupMember(friend.friend?.id)"
                                    class="flex items-center gap-3 p-3 hover:bg-slate-50 cursor-pointer border-b border-slate-100 last:border-b-0"
                                >
                                    <input 
                                        type="checkbox" 
                                        :checked="groupForm.memberIds.includes(friend.friend?.id)"
                                        class="w-4 h-4 text-indigo-500 rounded border-slate-300"
                                        @click.stop
                                    />
                                    <img :src="getAvatar(friend.friend?.avatarUrl)" class="w-8 h-8 rounded-full" />
                                    <span class="text-sm font-medium text-slate-700">{{ friend.friend?.username }}</span>
                                </div>
                                <div v-if="friends.length === 0" class="p-4 text-center text-sm text-slate-400">
                                    친구가 없습니다
                                </div>
                            </div>
                        </div>
                        <div class="flex justify-end gap-2 pt-2">
                            <button @click="createStep = 'type'" class="px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 rounded-lg">
                                뒤로
                            </button>
                            <button 
                                @click="createGroupRoom"
                                :disabled="!groupForm.name.trim() || groupForm.memberIds.length === 0 || creating"
                                class="px-4 py-2 text-sm font-bold text-white bg-indigo-500 hover:bg-indigo-600 disabled:opacity-50 rounded-lg"
                            >
                                {{ creating ? '생성 중...' : '만들기' }}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { MessageCircle, X, Maximize2, Loader2, ChevronLeft, Send, AlertCircle, Plus, Users } from 'lucide-vue-next';
import { socialApi } from '@/api/social';
import { chatApi } from '@/api/chat';
import { userApi } from '@/api/user';
import { useAuth } from '@/composables/useAuth';
import { useFloatingChat } from '@/composables/useFloatingChat';

const router = useRouter();
const route = useRoute();
const { user } = useAuth();
const { isOpen, activeChat, toggle } = useFloatingChat();

const isAuthenticated = computed(() => !!user.value);

const loading = ref(false);
const conversations = ref([]);
const groupRooms = ref([]);

// Chat view state
const viewMode = ref('list'); // 'list', 'chat', 'groupChat'
const messages = ref([]);
const messagesLoading = ref(false);
const newMessage = ref('');
const sending = ref(false);
const messagesContainer = ref(null);
const isPartnerDeleted = ref(false);

// Group chat state
const activeGroupRoom = ref(null);
const groupMessages = ref([]);
const groupMessagesLoading = ref(false);
const newGroupMessage = ref('');
const groupSending = ref(false);
const showGroupMembers = ref(false);
const groupMessagesContainer = ref(null);

// Create modal state
const showCreateModal = ref(false);
const createStep = ref('type'); // 'type', 'dm', 'group'
const friends = ref([]);
const friendSearch = ref('');
const creating = ref(false);
const groupForm = ref({ name: '', memberIds: [] });

let chatPollInterval = null;

// 1:1 + 그룹 통합 목록
const allConversations = computed(() => {
    const dmList = conversations.value.map(c => ({
        key: `dm-${c.partnerId}`,
        isGroup: false,
        name: c.partnerName,
        partnerId: c.partnerId,
        partnerName: c.partnerName,
        partnerAvatar: c.partnerAvatar,
        lastMessagePreview: c.lastMessagePreview,
        lastMessageTime: c.lastMessageTime,
        unreadCount: c.unreadCount || 0
    }));

    const groupList = groupRooms.value.map(r => ({
        key: `group-${r.id}`,
        isGroup: true,
        id: r.id,
        name: r.name,
        type: r.type,
        memberCount: r.memberCount,
        lastMessagePreview: r.lastMessage,
        lastMessageTime: r.lastMessageAt,
        unreadCount: r.unreadCount || 0
    }));

    // 시간순 정렬
    return [...dmList, ...groupList].sort((a, b) => {
        const timeA = a.lastMessageTime ? new Date(a.lastMessageTime) : new Date(0);
        const timeB = b.lastMessageTime ? new Date(b.lastMessageTime) : new Date(0);
        return timeB - timeA;
    });
});

const totalUnread = computed(() => {
    return allConversations.value.reduce((sum, c) => sum + (c.unreadCount || 0), 0);
});

const filteredFriends = computed(() => {
    if (!friendSearch.value) return friends.value;
    const q = friendSearch.value.toLowerCase();
    return friends.value.filter(f => f.friend?.username?.toLowerCase().includes(q));
});

// Watch isOpen
watch(isOpen, (newVal) => {
    if (newVal) {
        if (!activeChat.value && !activeGroupRoom.value) {
            viewMode.value = 'list';
            loadAllConversations();
        }
    } else {
        stopChatPolling();
    }
});

// Watch activeChat for 1:1
watch(activeChat, async (newVal) => {
    if (newVal) {
        viewMode.value = 'chat';
        messages.value = [];
        isPartnerDeleted.value = false;
        
        try {
            const res = await userApi.getById(newVal.partnerId);
            if (res.data.isDeleted) isPartnerDeleted.value = true;
        } catch (e) {
            console.error("Failed to check partner status", e);
        }

        fetchMessages();
        startChatPolling();
    }
});

const loadAllConversations = async () => {
    if (!isAuthenticated.value) return;
    loading.value = true;
    try {
        const [dmRes, groupRes] = await Promise.all([
            socialApi.getConversations(),
            chatApi.getRooms()
        ]);
        conversations.value = dmRes.data;
        groupRooms.value = groupRes.data;
    } catch (e) {
        console.error(e);
    } finally {
        loading.value = false;
    }
};

const openChat = (conv) => {
    activeChat.value = {
        partnerId: conv.partnerId,
        partnerName: conv.partnerName,
        partnerAvatar: conv.partnerAvatar,
        partnerDecoration: ''
    };
};

const openGroupChat = async (room) => {
    viewMode.value = 'groupChat';
    activeGroupRoom.value = room;
    showGroupMembers.value = false;
    groupMessagesLoading.value = true;

    try {
        const [detailRes, msgRes] = await Promise.all([
            chatApi.getRoom(room.id),
            chatApi.getMessages(room.id)
        ]);
        activeGroupRoom.value = { ...room, ...detailRes.data };
        groupMessages.value = msgRes.data.reverse();
        await chatApi.markAsRead(room.id);
        nextTick(() => scrollToBottomGroup());
        startGroupPolling();
    } catch (e) {
        console.error(e);
    } finally {
        groupMessagesLoading.value = false;
    }
};

const goBack = () => {
    viewMode.value = 'list';
    activeChat.value = null;
    activeGroupRoom.value = null;
    isPartnerDeleted.value = false;
    showGroupMembers.value = false;
    stopChatPolling();
    loadAllConversations();
};

const fetchMessages = async () => {
    if (!activeChat.value) return;
    if (messages.value.length === 0) messagesLoading.value = true;
    try {
        const res = await socialApi.getConversation(activeChat.value.partnerId);
        messages.value = res.data;
        if (messagesLoading.value) scrollToBottom();
    } catch (e) {
        console.error(e);
    } finally {
        messagesLoading.value = false;
    }
};

const sendMessage = async () => {
    if (!newMessage.value.trim() || sending.value || !activeChat.value || isPartnerDeleted.value) return;
    
    const content = newMessage.value;
    newMessage.value = '';
    sending.value = true;
    
    try {
        await socialApi.sendMessage(activeChat.value.partnerId, content);
        await fetchMessages();
        scrollToBottom();
    } catch (e) {
        newMessage.value = content;
        alert(e.response?.data?.message || '전송 실패');
    } finally {
        sending.value = false;
    }
};

const sendGroupMessage = async () => {
    if (!newGroupMessage.value.trim() || groupSending.value || !activeGroupRoom.value) return;

    const content = newGroupMessage.value;
    newGroupMessage.value = '';
    groupSending.value = true;

    try {
        const res = await chatApi.sendMessage(activeGroupRoom.value.id, content);
        groupMessages.value.push(res.data);
        nextTick(() => scrollToBottomGroup());
    } catch (e) {
        newGroupMessage.value = content;
        alert(e.response?.data?.message || '전송 실패');
    } finally {
        groupSending.value = false;
    }
};

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
    });
};

const scrollToBottomGroup = () => {
    nextTick(() => {
        if (groupMessagesContainer.value) {
            groupMessagesContainer.value.scrollTop = groupMessagesContainer.value.scrollHeight;
        }
    });
};

const startChatPolling = () => {
    stopChatPolling();
    chatPollInterval = setInterval(() => {
        if (viewMode.value === 'chat' && activeChat.value) {
            fetchMessages();
        } else if (viewMode.value === 'groupChat' && activeGroupRoom.value) {
            fetchGroupMessages();
        }
    }, 3000);
};

const startGroupPolling = () => {
    startChatPolling(); // 같은 인터벌 사용
};

const fetchGroupMessages = async () => {
    if (!activeGroupRoom.value) return;
    try {
        const res = await chatApi.getMessages(activeGroupRoom.value.id);
        if (res.data.length !== groupMessages.value.length) {
            groupMessages.value = res.data.reverse();
            nextTick(() => scrollToBottomGroup());
        }
    } catch (e) {
        console.error(e);
    }
};

const stopChatPolling = () => {
    if (chatPollInterval) {
        clearInterval(chatPollInterval);
        chatPollInterval = null;
    }
};

// Create modal functions
const openCreateModal = async () => {
    showCreateModal.value = true;
    createStep.value = 'type';
    groupForm.value = { name: '', memberIds: [] };
    friendSearch.value = '';
    try {
        const res = await socialApi.getFriends();
        friends.value = res.data;
    } catch (e) {
        console.error(e);
    }
};

const startDMCreate = () => {
    createStep.value = 'dm';
};

const startGroupCreate = () => {
    createStep.value = 'group';
};

const startDMWithFriend = (friend) => {
    showCreateModal.value = false;
    activeChat.value = {
        partnerId: friend.id,
        partnerName: friend.username,
        partnerAvatar: friend.avatarUrl,
        partnerDecoration: ''
    };
};

const toggleGroupMember = (friendId) => {
    const idx = groupForm.value.memberIds.indexOf(friendId);
    if (idx > -1) {
        groupForm.value.memberIds.splice(idx, 1);
    } else {
        groupForm.value.memberIds.push(friendId);
    }
};

const createGroupRoom = async () => {
    if (!groupForm.value.name.trim() || creating.value) return;
    creating.value = true;
    try {
        const res = await chatApi.createRoom(groupForm.value.name, groupForm.value.memberIds);
        showCreateModal.value = false;
        await loadAllConversations();
        // 바로 채팅방 열기
        openGroupChat({ id: res.data.id, name: res.data.name });
    } catch (e) {
        alert(e.response?.data?.message || '채팅방 생성에 실패했습니다.');
    } finally {
        creating.value = false;
    }
};

const openFullView = () => {
    router.push('/social?tab=messages');
    isOpen.value = false;
    stopChatPolling();
};

const getAvatar = (url, isDeleted = false) => {
    if (isDeleted) return 'https://avatars.githubusercontent.com/u/0';
    if (url && !url.includes('dicebear')) return url;
    return '/images/profiles/default-profile.png';
};

const formatTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const oneDay = 24 * 60 * 60 * 1000;
    
    if (diff < oneDay) {
        return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    } else if (diff < 7 * oneDay) {
        return `${Math.floor(diff / oneDay)}일 전`;
    } else {
        return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
    }
};

const formatMsgTime = (iso) => {
    const d = new Date(iso);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const formatDate = (iso) => {
    const d = new Date(iso);
    return d.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' });
};

const showDateSeparator = (index) => {
    if (index === 0) return true;
    const currentMsgDate = new Date(messages.value[index].createdAt).toLocaleDateString();
    const prevMsgDate = new Date(messages.value[index - 1].createdAt).toLocaleDateString();
    return currentMsgDate !== prevMsgDate;
};

// Setup
let refreshInterval = null;
onMounted(() => {
    if (isAuthenticated.value) {
        loadAllConversations();
        refreshInterval = setInterval(() => {
            if (viewMode.value === 'list') loadAllConversations();
        }, 60000);
    }
});

onUnmounted(() => {
    if (refreshInterval) clearInterval(refreshInterval);
    stopChatPolling();
});

watch(() => route.path, () => {
    isOpen.value = false;
    stopChatPolling();
});

watch(isAuthenticated, (val) => {
    if (val) loadAllConversations();
    else {
        conversations.value = [];
        groupRooms.value = [];
    }
});
</script>

<style scoped>
.scale-enter-active,
.scale-leave-active {
    transition: all 0.2s ease;
}
.scale-enter-from,
.scale-leave-to {
    opacity: 0;
    transform: scale(0.8);
}

.slide-up-enter-active,
.slide-up-leave-active {
    transition: all 0.25s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
}

.slide-down-enter-active,
.slide-down-leave-active {
    transition: all 0.2s ease;
}
.slide-down-enter-from,
.slide-down-leave-to {
    max-height: 0;
    opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>
