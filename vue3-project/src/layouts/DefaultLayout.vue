<template>
  <div class="default-layout">
    <header class="header">
      <div class="header-left">
        <div class="logo">
          <span class="logo-icon">📚</span>
          <span class="logo-text">城市图书馆</span>
        </div>
      </div>
      <div class="header-right">
        <span class="welcome-text">{{ t('home.welcome') }}，{{ authStore.user?.username || '用户' }}</span>
        <el-dropdown trigger="click" @command="handleLanguageChange">
          <span class="language-trigger">
            <el-icon><Platform /></el-icon>
            {{ currentLocale === 'zh-CN' ? '中文' : 'EN' }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="zh-CN">中文</el-dropdown-item>
              <el-dropdown-item command="en-US">English</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-badge :value="notificationStore.unreadCount" :hidden="notificationStore.unreadCount === 0" :max="99">
          <el-button :icon="Bell" circle size="small" @click="$router.push('/notifications')" />
        </el-badge>
        <el-button type="danger" plain size="small" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </header>

    <div class="body-container">
      <aside class="sidebar" :class="{ collapsed: isCollapsed }">
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapsed"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <template #title>图书列表</template>
          </el-menu-item>
          <el-menu-item index="/borrows">
            <el-icon><Notebook /></el-icon>
            <template #title>我的借阅</template>
          </el-menu-item>
          <el-menu-item index="/reservations">
            <el-icon><Clock /></el-icon>
            <template #title>我的预约</template>
          </el-menu-item>
          <el-menu-item index="/seats">
            <el-icon><Grid /></el-icon>
            <template #title>座位预约</template>
          </el-menu-item>
          <el-menu-item index="/my-seats">
            <el-icon><Location /></el-icon>
            <template #title>我的座位</template>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
          <el-menu-item index="/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>消息中心</template>
          </el-menu-item>
          <el-menu-item index="/inquiries">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>咨询中心</template>
          </el-menu-item>
          <el-menu-item v-if="authStore.isAdmin" index="/admin/dashboard">
            <el-icon><Setting /></el-icon>
            <template #title>管理后台</template>
          </el-menu-item>
        </el-menu>
      </aside>

      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  HomeFilled, Reading, Notebook, Clock, User,
  Bell, ChatDotRound, ArrowDown, Platform, Setting, Grid, Location
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/authStore'
import { useNotificationStore } from '@/stores/notificationStore'
import { useI18n } from 'vue-i18n'
import { loadLocaleMessages } from '@/i18n'

const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const { t, locale } = useI18n()

const isCollapsed = ref(false)
const currentLocale = computed(() => locale.value)

function handleLanguageChange(lang: string) {
  loadLocaleMessages(lang)
}

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

onMounted(() => {
  if (authStore.isLoggedIn) {
    notificationStore.fetchUnreadCount()
  }
})
</script>

<style scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  height: var(--header-height);
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: var(--shadow-sm);
  z-index: 100;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-text {
  font-size: 14px;
  color: var(--color-text-regular);
}

.language-trigger {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--color-text-regular);
}

.body-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: var(--sidebar-width);
  background: var(--color-bg-card);
  border-right: 1px solid var(--color-border-light);
  transition: width 0.3s ease;
  overflow-y: auto;
  overflow-x: hidden;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: var(--sidebar-collapsed-width);
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background: var(--color-bg-page);
  padding: var(--content-padding);
}
</style>
