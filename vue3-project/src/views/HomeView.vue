<template>
  <div class="home-view">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-section">
          <h1>欢迎来到城市图书馆</h1>
          <p>基于 Spring Boot + Vue 3 的图书馆管理系统</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409eff">📚</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBooks }}</div>
            <div class="stat-label">总藏书量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67c23a">📖</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.borrowing }}</div>
            <div class="stat-label">借阅中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6a23c">⏰</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.overdue }}</div>
            <div class="stat-label">逾期未还</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f56c6c">👤</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.activeUsers }}</div>
            <div class="stat-label">活跃用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>新书上架</span>
              <el-button text type="primary" @click="$router.push('/books')">查看更多</el-button>
            </div>
          </template>
          <div v-if="newBooks.length === 0" class="empty-text">暂无新书</div>
          <el-table v-else :data="newBooks" stripe>
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" />
            <el-table-column prop="category" label="分类" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" text size="small" @click="$router.push(`/books/${row.id}`)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/books')">🔍 搜索图书</el-button>
            <el-button type="success" @click="$router.push('/borrows')">📖 我的借阅</el-button>
            <el-button type="warning" @click="$router.push('/seats')">💺 座位预约</el-button>
            <el-button type="info" @click="$router.push('/profile')">👤 个人中心</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({
  totalBooks: 0,
  borrowing: 0,
  overdue: 0,
  activeUsers: 0
})

const newBooks = ref<any[]>([])

onMounted(async () => {
  try {
    const res: any = await request.get('/books', { params: { page: 0, size: 5, sort: 'id,desc' } })
    const data = res.data || res
    newBooks.value = data.content || data || []
  } catch (e) {
    console.error('Failed to load books:', e)
  }

  try {
    const res: any = await request.get('/dashboard/stats')
    if (res.data) {
      stats.value = res.data
    }
  } catch (e) {
    // Dashboard stats endpoint may not exist, use defaults
  }
})
</script>

<style scoped>
.home-view {
  padding: 0;
}

.welcome-section {
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: #fff;
  padding: 40px;
  border-radius: 8px;
}

.welcome-section h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.welcome-section p {
  font-size: 16px;
  opacity: 0.9;
}

.stat-card {
  display: flex;
  align-items: center;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
  justify-content: flex-start;
}
</style>
