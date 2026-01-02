<template>
  <div class="w-full h-full bg-slate-50 flex items-center justify-center overflow-hidden relative">
      <!-- 배경 장식 -->
      <div class="absolute top-0 right-0 w-1/2 h-full bg-slate-100/50 skew-x-12 origin-top-right z-0"></div>
      <div class="absolute inset-0 bg-[radial-gradient(#e5e7eb_1px,transparent_1px)] [background-size:20px_20px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)] opacity-30 z-0"></div>

      <div class="relative z-10 max-w-6xl mx-auto px-6 w-full flex flex-col md:flex-row items-center gap-12">
          
          <!-- 텍스트 영역 -->
          <div class="flex-1 space-y-8 animate-in slide-in-from-left duration-700">
              <div class="space-y-4">
                  <span class="inline-block px-3 py-1 bg-rose-100 text-rose-600 rounded-lg text-xs font-bold uppercase tracking-wider">Pain Point</span>
                  <h2 class="text-4xl md:text-5xl font-black text-slate-800 leading-tight">
                    <span class="text-slate-400 line-through decoration-rose-500 decoration-4">귀찮은 기록</span>은 이제 그만.<br/>
                    알고리즘 공부에만<br/>
                    <span class="text-brand-500">집중하세요.</span>
                  </h2>
                  <p class="text-lg text-slate-600 leading-relaxed">
                      귀찮은 주석, PR, 문서 정리, 코드 리뷰...<br/>
                      이 모든 번거로움, <strong>DashHub</strong>가 대신해 드립니다.
                  </p>
              </div>

              <!-- 비교 리스트 -->
              <div class="space-y-4">
                <div class="flex items-center gap-4 p-4 bg-white rounded-2xl border border-slate-100 shadow-sm opacity-60 grayscale hover:grayscale-0 hover:opacity-100 transition-all">
                    <div class="w-12 h-12 bg-slate-100 rounded-xl flex items-center justify-center shrink-0">
                        <FileX class="w-6 h-6 text-slate-500" />
                    </div>
                    <div>
                        <h4 class="font-bold text-slate-700">수동 커밋 & 리드미 작성</h4>
                        <p class="text-sm text-slate-500">문제 풀 때마다 반복되는 단순 노동</p>
                    </div>
                </div>

                <div class="flex items-center gap-4 p-5 bg-white rounded-2xl border-2 border-brand-100 shadow-xl shadow-brand-500/10 scale-105 relative z-10">
                    <div class="absolute -left-2 top-1/2 -translate-y-1/2 w-1.5 h-12 bg-brand-500 rounded-full"></div>
                    <div class="w-12 h-12 bg-brand-50 rounded-xl flex items-center justify-center shrink-0">
                        <Zap class="w-7 h-7 text-brand-500 fill-brand-500" />
                    </div>
                    <div>
                        <h4 class="font-bold text-slate-800">Zero-Manual 파이프라인</h4>
                        <p class="text-sm text-slate-600">제출 버튼 클릭 한 번으로 <span class="text-brand-600 font-bold">자동 업로드 & 분석 완료</span></p>
                    </div>
               </div>
            </div>
          </div>

          <!-- 그래픽 영역 -->
          <div class="flex-1 w-full max-w-lg relative animate-in slide-in-from-right duration-700 delay-200">
               <!-- 파이프라인 시각화 -->
               <div class="relative bg-white rounded-[2rem] shadow-2xl p-8 border border-slate-100 overflow-hidden">
                   <!-- 배경 그리드 -->
                   <div class="absolute inset-0 bg-slate-50/50 bg-[linear-gradient(to_right,#f1f5f9_1px,transparent_1px),linear-gradient(to_bottom,#f1f5f9_1px,transparent_1px)] bg-[size:24px_24px]"></div>

                   <div class="relative z-10 flex flex-col items-center gap-6">
                       <!-- Step 1: Solving -->
                       <div class="w-full flex items-center gap-4 p-4 bg-white rounded-2xl shadow-sm border border-slate-200">
                           <div class="w-10 h-10 bg-[#0078C0] rounded-lg flex items-center justify-center shrink-0 text-white font-bold text-xs ring-4 ring-[#0078C0]/10">
                              BOJ
                           </div>
                           <div class="flex-1">
                               <div class="h-2 w-20 bg-slate-200 rounded mb-2"></div>
                               <div class="h-2 w-32 bg-slate-100 rounded"></div>
                           </div>
                           <span class="text-xs font-bold text-slate-400">User</span>
                       </div>

                       <ArrowDown class="w-6 h-6 text-brand-300 animate-bounce" />

                       <!-- Step 2: Automation Core -->
                       <div class="w-full p-1 bg-gradient-to-br from-brand-400 to-brand-600 rounded-2xl shadow-lg">
                           <div class="bg-white rounded-xl p-5 flex items-center justify-between">
                               <div class="flex items-center gap-3">
                                   <div class="w-10 h-10 bg-brand-50 rounded-lg flex items-center justify-center">
                                       <img src="/images/icons/icon-512.png" class="w-6 h-6" alt="Icon"/>
                                   </div>
                                   <div>
                                       <h5 class="font-bold text-slate-800 text-sm">DashHub Engine</h5>
                                       <span class="text-[10px] text-brand-500 font-bold bg-brand-50 px-1.5 py-0.5 rounded">Running...</span>
                                   </div>
                               </div>
                               <div class="flex gap-1">
                                   <span class="block w-1.5 h-1.5 bg-brand-500 rounded-full animate-ping"></span>
                               </div>
                           </div>
                       </div>

                       <ArrowDown class="w-6 h-6 text-brand-300 animate-bounce delay-100" />

                        <!-- Step 3: Result -->
                        <div class="w-full flex gap-3">
                            <div class="flex-1 p-3 bg-slate-900 rounded-xl flex items-center gap-3 shadow-md">
                                <Github class="w-5 h-5 text-white" />
                                <span class="text-xs font-bold text-slate-300">Commit Pushed</span>
                            </div>
                             <div class="flex-1 p-3 bg-white border border-slate-200 rounded-xl flex items-center gap-3 shadow-sm">
                                <BarChart3 class="w-5 h-5 text-brand-500" />
                                <span class="text-xs font-bold text-slate-600">Stats Updated</span>
                            </div>
                        </div>
                   </div>
               </div>
               
               <!-- 장식 -->
               <div class="absolute -top-6 -right-6 w-24 h-24 bg-brand-400/20 rounded-full blur-2xl animate-pulse"></div>
               <div class="absolute -bottom-8 -left-8 w-32 h-32 bg-rose-400/20 rounded-full blur-3xl animate-pulse delay-700"></div>
          </div>
      </div>
  </div>
</template>

<script setup>
import { FileX, Zap, ArrowDown, Github, BarChart3 } from 'lucide-vue-next';
</script>
