<template>
  <div
    class="book-card"
    :class="[`book-card--${mode}`]"
    @click="$emit('click', book)"
  >
    <div class="book-cover-wrapper">
      <img
        :src="book.coverUrl || defaultCover"
        :alt="book.title"
        class="book-cover"
      />
      <StatusTag :status="book.status" class="status-overlay" />
    </div>
    <div class="book-info">
      <h3 class="book-title" :title="book.title">{{ book.title }}</h3>
      <p class="book-author">{{ book.author }}</p>
      <el-rate
        v-if="book.rating"
        :model-value="book.rating"
        disabled
        size="small"
        show-score
        :score-template="`${book.rating} (${book.ratingCount})`"
        class="book-rating"
      />
      <div class="book-actions">
        <el-button
          type="primary"
          size="small"
          :disabled="book.status !== 'available'"
          @click.stop="$emit('borrow', book)"
        >
          借阅
        </el-button>
        <el-button
          size="small"
          plain
          @click.stop="$emit('favorite', book)"
        >
          收藏
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Book } from '@/api/types'
import StatusTag from './StatusTag.vue'

interface Props {
  book: Book
  mode?: 'grid' | 'list'
}

withDefaults(defineProps<Props>(), {
  mode: 'grid'
})

defineEmits<{
  borrow: [book: Book]
  favorite: [book: Book]
  click: [book: Book]
}>()

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjI4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjI4MCIgZmlsbD0iI2VlZSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5IiBmb250LXNpemU9IjE0Ij7nu4nkuo48L3RleHQ+PC9zdmc+'
</script>

<style scoped>
.book-card {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-base);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.book-card:hover {
  box-shadow: var(--shadow-base);
  transform: translateY(-2px);
}

.book-card--grid {
  display: flex;
  flex-direction: column;
}

.book-card--grid .book-cover-wrapper {
  position: relative;
  width: 100%;
  padding-top: 140%;
  overflow: hidden;
}

.book-card--grid .book-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-card--grid .book-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.book-card--grid .book-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  min-height: 42px;
}

.book-card--grid .book-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-card--grid .book-rating {
  margin-bottom: 12px;
}

.book-card--grid .book-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.book-card--list {
  display: flex;
  flex-direction: row;
  gap: 20px;
  padding: 16px;
}

.book-card--list .book-cover-wrapper {
  position: relative;
  flex-shrink: 0;
  width: 120px;
  height: 160px;
  overflow: hidden;
  border-radius: var(--border-radius-sm);
}

.book-card--list .book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-card--list .book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 8px 0;
}

.book-card--list .book-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 8px;
}

.book-card--list .book-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 8px;
}

.book-card--list .book-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.status-overlay {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
}
</style>
