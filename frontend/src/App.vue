<template>
  <el-container class="layout">
    <el-aside width="220px">
      <div class="logo">
        <el-icon><ChatDotRound /></el-icon>
        <span>获客平台</span>
      </div>
      <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>仪表盘</span></el-menu-item>
        <el-menu-item index="/campaigns"><el-icon><Promotion /></el-icon><span>获客活动</span></el-menu-item>
        <el-menu-item index="/contacts"><el-icon><User /></el-icon><span>联系人</span></el-menu-item>
        <el-menu-item index="/conversations"><el-icon><ChatLineRound /></el-icon><span>会话管理</span></el-menu-item>
        <el-menu-item index="/scoring"><el-icon><TrendCharts /></el-icon><span>意向打分</span></el-menu-item>
        <el-menu-item index="/settings"><el-icon><Setting /></el-icon><span>系统设置</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header><h2>{{ currentTitle }}</h2></el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '企业微信获客平台')
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
.layout { height: 100vh; }
.el-aside { background: #304156; color: #fff; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; gap: 10px; font-size: 18px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1); }
.el-header { background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; }
.el-main { background: #f0f2f5; }
</style>
