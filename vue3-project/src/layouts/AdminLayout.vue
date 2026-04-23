<template>
  <div class="admin-layout">
    <header class="admin-header">
      <div class="header-left">
        <span class="logo-icon">📚</span>
        <span class="logo-text">城市图书馆 · 管理后台</span>
      </div>
      <div class="header-right">
        <span class="welcome-text">{{ authStore.user?.username || '管理员' }}</span>
        <el-button size="small" @click="$router.push('/')">返回前台</el-button>
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </header>

    <div class="admin-body">
      <aside class="admin-sidebar">
        <el-menu
          :default-active="$route.path"
          router
          class="admin-sidebar-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>管理仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/admin/books">
            <el-icon><Reading /></el-icon>
            <template #title>图书管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/borrows">
            <el-icon><Notebook /></el-icon>
            <template #title>借阅管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/seats">
            <el-icon><Grid /></el-icon>
            <template #title>座位管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/inquiries">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>咨询管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <el-icon><TrendCharts /></el-icon>
            <template #title>统计报表</template>
          </el-menu-item>
          <el-menu-item index="/admin/config">
            <el-icon><Setting /></el-icon>
            <template #title>系统配置</template>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-menu>
      </aside>

      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  DataAnalysis, Reading, User, Notebook, Grid,
  ChatDotRound, TrendCharts, Setting, Document
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // cancelled
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.admin-header {
  height: 56px;
  background: #1d1e1f;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-text {
  font-size: 14px;
  color: #ccc;
}

.admin-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.admin-sidebar {
  width: 200px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  flex-shrink: 0;
}

.admin-sidebar-menu {
  border-right: none;
  height: 100%;
}

.admin-main {
  flex: 1;
  overflow-y: auto;
  background: #f5f7fa;
  padding: 20px;
}
</style>
