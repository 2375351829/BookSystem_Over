<template>
  <div class="notification-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
          <el-button type="primary" text size="small" @click="markAllRead">全部标记已读</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="未读" name="unread" />
        <el-tab-pane label="已读" name="read" />
        <el-tab-pane label="全部" name="all" />
      </el-tabs>

      <div v-if="notifications.length === 0" class="empty-text">暂无通知</div>
      <div v-else>
        <div v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: !item.read }">
          <div class="notification-title">
            <el-tag v-if="!item.read" type="danger" size="small">未读</el-tag>
            <span>{{ item.title }}</span>
            <span class="notification-time">{{ item.createTime }}</span>
          </div>
          <div class="notification-content">{{ item.content }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('unread')
const notifications = ref<any[]>([])

async function loadNotifications() {
  try {
    const params: any = { page: 0, size: 20 }
    if (activeTab.value === 'unread') params.read = false
    if (activeTab.value === 'read') params.read = true
    const res: any = await request.get('/notifications', { params })
    const data = res.data || res
    notifications.value = data.content || data || []
  } catch (e) {
    console.error('Failed to load notifications:', e)
  }
}

async function markAllRead() {
  try {
    await request.put('/notifications/read-all')
    ElMessage.success('已全部标记为已读')
    loadNotifications()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

watch(activeTab, loadNotifications)
onMounted(loadNotifications)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-item.unread {
  background: #ecf5ff;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-weight: 600;
}

.notification-time {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.notification-content {
  color: #606266;
  line-height: 1.6;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px;
}
</style>
