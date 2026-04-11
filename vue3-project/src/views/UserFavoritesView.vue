<template>
  <div class="favorites-view">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <el-icon><StarFilled /></el-icon>
          我的收藏
        </h1>
        <p class="page-desc">管理您收藏的图书</p>
      </div>
      <div class="header-actions">
        <el-checkbox
          v-model="selectAll"
          :indeterminate="isIndeterminate"
          @change="handleSelectAll"
          :disabled="favorites.length === 0"
        >
          全选
        </el-checkbox>
        <el-button type="danger" plain :disabled="selectedIds.length === 0" @click="handleBatchCancel">
          <el-icon><Delete /></el-icon>
          批量取消收藏 ({{ selectedIds.length }})
        </el-button>
      </div>
    </div>

    <div class="favorites-content" v-loading="loading">
      <template v-if="favorites.length > 0">
        <el-row :gutter="20" class="favorites-grid">
          <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="book in favorites" :key="book.id">
            <div class="favorite-card-wrapper">
              <div class="card-select" @click.stop>
                <el-checkbox
                  :model-value="selectedIds.includes(book.id)"
                  @change="(val: boolean) => toggleSelect(book.id, val)"
                />
              </div>
              <BookCard :book="book" mode="grid" @click="handleViewDetail(book)" />
            </div>
          </el-col>
        </el-row>

        <div class="pagination-wrapper" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 48]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="fetchFavorites"
            @current-change="fetchFavorites"
          />
        </div>
      </template>

      <EmptyState
        v-else
        title="暂无收藏"
        description="去发现感兴趣的图书吧"
        action-text="去逛逛"
        @action="$router.push('/books')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { StarFilled, Delete } from '@element-plus/icons-vue'
import { favoriteApi } from '@/api/favorite'
import BookCard from '@/components/BookCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { Book } from '@/api/types'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const favorites = ref<Book[]>([])
const selectedIds = ref<number[]>([])

const selectAll = ref(false)

const isIndeterminate = computed(() =>
  selectedIds.value.length > 0 && selectedIds.value.length < favorites.value.length
)

const toggleSelect = (id: number, val: boolean) => {
  if (val) {
    if (!selectedIds.value.includes(id)) selectedIds.value.push(id)
  } else {
    selectedIds.value = selectedIds.value.filter(i => i !== id)
  }
}

const handleSelectAll = (val: boolean) => {
  selectedIds.value = val ? favorites.value.map(b => b.id) : []
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res: any = await favoriteApi.getFavorites({
      page: currentPage.value,
      size: pageSize.value
    })
    favorites.value = (res.records || []).map((item: any) => ({
      id: item.bookId,
      title: item.title,
      author: item.author,
      coverUrl: item.coverUrl,
      isbn: item.isbn
    }))
    total.value = res.total || 0
  } catch (e: any) {
    ElMessage.error(e.message || '获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleBatchCancel = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要取消选中的 ${selectedIds.value.length} 本图书的收藏吗？`,
      '批量取消收藏',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await favoriteApi.batchRemoveFavorites(selectedIds.value)
    ElMessage.success('已取消收藏')
    selectedIds.value = []
    selectAll.value = false
    fetchFavorites()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '操作失败')
  }
}

const handleViewDetail = (book: Book) => {
  // 跳转图书详情页
  window.location.href = `/books/${book.id}`
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-view {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.favorites-grid {
  margin-bottom: 20px;
}

.favorite-card-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.card-select {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 2px;
  transition: opacity 0.2s;
}

.favorite-card-wrapper:hover .card-select {
  opacity: 1;
}

.card-select:not(:hover) {
  opacity: 0;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .card-select {
    opacity: 1;
  }
}
</style>
