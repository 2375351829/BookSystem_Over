<template>
  <div class="book-review-view">
    <el-page-header @back="$router.back()" title="返回">
      <template #content>
        <span>图书评价</span>
      </template>
    </el-page-header>

    <el-card style="margin-top: 16px">
      <div v-if="reviews.length === 0" class="empty-text">暂无评价</div>
      <div v-else>
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="review-user">{{ review.username || '匿名用户' }}</span>
            <el-rate v-model="review.rating" disabled />
            <span class="review-time">{{ review.createTime }}</span>
          </div>
          <div class="review-content">{{ review.content }}</div>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header>发表评价</template>
      <el-form :model="form">
        <el-form-item label="评分">
          <el-rate v-model="form.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入评价内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitReview">提交评价</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const bookId = route.params.id

const reviews = ref<any[]>([])
const form = ref({ rating: 5, content: '' })

onMounted(async () => {
  try {
    const res: any = await request.get(`/books/${bookId}/reviews`)
    reviews.value = res.data || res || []
  } catch (e) {
    console.error('Failed to load reviews:', e)
  }
})

async function submitReview() {
  if (!form.value.content) {
    ElMessage.warning('请输入评价内容')
    return
  }
  try {
    await request.post(`/books/${bookId}/reviews`, form.value)
    ElMessage.success('评价提交成功')
    form.value = { rating: 5, content: '' }
    // Reload reviews
    const res: any = await request.get(`/books/${bookId}/reviews`)
    reviews.value = res.data || res || []
  } catch (e) {
    ElMessage.error('评价提交失败')
  }
}
</script>

<style scoped>
.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-user {
  font-weight: 600;
  color: #303133;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  color: #606266;
  line-height: 1.6;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px;
}
</style>
