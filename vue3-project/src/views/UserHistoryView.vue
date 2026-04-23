<template>
  <div class="user-history-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>阅读历史</span>
          <el-button type="danger" text size="small" @click="clearAll">清空全部历史</el-button>
        </div>
      </template>
      <div v-if="history.length === 0" class="empty-text">暂无阅读历史</div>
      <el-table v-else :data="history" stripe>
        <el-table-column prop="bookTitle" label="书名" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="viewTime" label="浏览时间" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="$router.push(`/books/${row.bookId}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import request from '@/utils/request'

const history = ref<any[]>([])

onMounted(async () => {
  try {
    const res: any = await request.get('/user/history', { params: { page: 0, size: 20 } })
    const data = res.data || res
    history.value = data.content || data || []
  } catch (e) {
    console.error('Failed to load history:', e)
  }
})

async function clearAll() {
  try {
    await ElMessageBox.confirm('确定清空所有浏览历史吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete('/user/history')
    history.value = []
    ElMessage.success('已清空阅读历史')
  } catch {
    // cancelled
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px;
}
</style>
