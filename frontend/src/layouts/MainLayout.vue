<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px">
      <div class="logo">
        <el-icon><ChatDotRound /></el-icon>
        <span>获客平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-menu-item index="/campaign">
          <el-icon><Promotion /></el-icon>
          <span>获客活动</span>
        </el-menu-item>
        
        <el-menu-item index="/contact">
          <el-icon><User /></el-icon>
          <span>联系人管理</span>
        </el-menu-item>
        
        <el-menu-item index="/conversation">
          <el-icon><ChatLineRound /></el-icon>
          <span>会话管理</span>
        </el-menu-item>
        
        <el-menu-item index="/scoring">
          <el-icon><TrendCharts /></el-icon>
          <span>意向打分</span>
        </el-menu-item>
        
        <el-menu-item index="/template">
          <el-icon><Document /></el-icon>
          <span>话术模板</span>
        </el-menu-item>
        
        <el-menu-item index="/data">
          <el-icon><PieChart /></el-icon>
          <span>数据报表</span>
        </el-menu-item>
        
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header>
        <div class="header-left">
          <el-icon class="breadcrumb-icon"><Fold /></el-icon>
          <span class="breadcrumb">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" icon="User" />
              <span class="username">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title as string || '仪表盘')

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    .el-icon {
      font-size: 24px;
      color: #409EFF;
    }
  }
  
  .el-menu {
    border-right: none;
  }
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .breadcrumb-icon {
      font-size: 20px;
      cursor: pointer;
    }
    
    .breadcrumb {
      font-size: 16px;
      font-weight: 500;
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      
      .username {
        font-size: 14px;
      }
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
