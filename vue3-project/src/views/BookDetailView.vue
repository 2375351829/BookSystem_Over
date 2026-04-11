<template>
  <div class="book-detail-page" v-loading="loading">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">{{ t('nav.home') }}</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/books' }">{{ t('nav.bookList') }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ book?.title || '...' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <template v-if="book">
      <el-card class="main-info-card">
        <el-row :gutter="40" align="top">
          <el-col :span="8">
            <div class="cover-wrapper">
              <el-image :src="book.coverUrl || defaultCover" :alt="book.title" class="book-cover" fit="cover">
                <template #error>
                  <div class="cover-placeholder"><el-icon :size="48"><PictureFilled /></el-icon></div>
                </template>
              </el-image>
            </div>
          </el-col>
          <el-col :span="16">
            <el-descriptions :column="2" bordered>
              <el-descriptions-item :span="2" label-class-name="desc-label">
                <template #label>{{ t('book.detail.pageTitle') }}</template>
                <h2 class="book-title">{{ book.title }}</h2>
              </el-descriptions-item>
              <el-descriptions-item v-if="book.subtitle" :span="2" label-class-name="desc-label">
                <template #label>{{ t('book.detail.subtitle') }}</template>
                {{ book.subtitle }}
              </el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.author')">{{ book.author }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.translator')">{{ book.translator || '-' }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.publisher')">{{ book.publisher }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.publishYear')">{{ book.publishYear }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.isbn')">{{ book.isbn }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.category')">{{ book.category || '-' }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.language')">{{ book.language }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.pages')">{{ book.pages || '-' }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.price')">{{ book.price ? `¥${book.price}` : '-' }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.location')">{{ book.location }}</el-descriptions-item>
              <el-descriptions-item :label="t('book.detail.shelfNo')">{{ book.shelfNo }}</el-descriptions-item>
              <el-descriptions-item v-if="book.summary" :span="2" label-class-name="desc-label">
                <template #label>{{ t('book.detail.description') }}</template>
                <p class="book-desc">{{ book.summary }}</p>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
      </el-card>

      <el-row :gutter="12" class="action-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="mini-card">
            <StatusTag :status="book.status" :available-copies="book.availableCopies" />
            <p class="mini-label">{{ t('book.detail.collectionStatus') }}</p>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mini-card">
            <span class="mini-value">{{ book.availableCopies }}<small>/{{ book.totalCopies }}</small></span>
            <p class="mini-label">{{ t('book.detail.availableCopies') }}</p>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mini-card">
            <span class="mini-value">{{ book.location }}-{{ book.shelfNo }}</span>
            <p class="mini-label">{{ t('book.detail.positionInfo') }}</p>
          </el-card>
        </el-col>
        <el-col :span="6">
          <div class="action-btns">
            <el-button type="primary" size="large" :disabled="!isBookAvailable" @click="handleBorrow">
              <el-icon><Reading /></el-icon> {{ t('book.detail.borrowBtn') }}
            </el-button>
            <el-button type="success" size="large" @click="handleReserve">
              <el-icon><Clock /></el-icon> {{ t('book.detail.reserveBtn') }}
            </el-button>
            <el-button type="warning" size="large" @click="handleFavorite">
              <el-icon><Star /></el-icon> {{ t('book.detail.favoriteBtn') }}
            </el-button>
          </div>
        </el-col>
      </el-row>

      <el-card class="tags-card" v-if="book.tags">
        <div class="tag-list">
          <el-tag v-for="tag in (book.tags || '').split(',')" :key="tag" type="info" effect="plain" class="tag-item">{{ tag }}</el-tag>
        </div>
      </el-card>

      <el-card class="reviews-card">
        <template #header>
          <div class="section-header">
            <span class="section-title">{{ t('book.detail.reviews') }} ({{ reviews.length }})</span>
            <el-button type="primary" plain size="small" @click="showReviewDialog = true">{{ t('book.detail.submitReview') }}</el-button>
          </div>
        </template>
        <div v-if="reviews.length" class="review-list">
          <div v-for="r in reviews" :key="r.id" class="review-item">
            <el-avatar :size="40" :src="r.avatar">{{ r.username?.charAt(0) }}</el-avatar>
            <div class="review-content">
              <div class="review-header">
                <span class="review-user">{{ r.username }}</span>
                <el-rate :model-value="r.rating" disabled size="small" />
                <span class="review-time">{{ r.createdAt }}</span>
              </div>
              <p class="review-text">{{ r.content }}</p>
            </div>
          </div>
        </div>
        <EmptyState v-else :description="t('book.detail.noReview')" />
      </el-card>

      <el-card class="similar-card" v-if="similarBooks.length">
        <template #header>
          <span class="section-title">{{ t('book.detail.similarBooks') }}</span>
        </template>
        <div class="similar-scroll">
          <BookCard v-for="b in similarBooks" :key="b.id" :book="b" mode="grid" style="width:200px;flex-shrink:0"
            @click="router.push(`/books/${b.id}`)" @borrow="handleBorrow" @favorite="handleFavorite" />
        </div>
      </el-card>
    </template>

    <EmptyState v-if="!loading && !book" title="图书不存在" description="该图书可能已被删除或不存在" action-text="返回图书列表" @action="router.push('/books')" />

    <el-dialog v-model="showReviewDialog" :title="t('book.detail.submitReview')" width="500px">
      <el-form :model="reviewForm" label-position="top">
        <el-form-item :label="t('book.detail.reviewRating')">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        <el-form-item :label="t('book.detail.reviewContent')">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" :placeholder="t('book.detail.reviewPlaceholder')" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">{{ t('common.action.cancel') }}</el-button>
        <el-button type="primary" :loading="submittingReview" @click="submitReview">{{ t('common.action.submit') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, Clock, Star, PictureFilled } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useBookStore } from '@/stores/bookStore'
import StatusTag from '@/components/StatusTag.vue'
import BookCard from '@/components/BookCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import { borrowApi } from '@/api/borrow'
import { reservationApi } from '@/api/reservation'
import { favoriteApi } from '@/api/favorite'
import { reviewApi } from '@/api/review'
import { bookApi } from '@/api/book'
import type { Book } from '@/api/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()

const loading = ref(false)
const showReviewDialog = ref(false)
const submittingReview = ref(false)

const book = ref<Book | null>(null)
const reviews = ref<any[]>([])
const similarBooks = ref<Book[]>([])

const reviewForm = reactive({ rating: 5, content: '' })

const isBookAvailable = computed(() => {
  if (!book.value) return false
  if (book.value.availableCopies !== undefined) {
    return book.value.availableCopies > 0
  }
  const status = book.value.status
  return status === 'available' || status === '可借' || status === '1'
})

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjgwIiBoZWlnaHQ9IjM4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjgwIiBoZWlnaHQ9IjM4MCIgZmlsbD0iI2VlZSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5IiBmb250LXNpemU9IjE4Ij7nu4nkuo48L3RleHQ+PC9zdmc+'

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    await bookStore.fetchBookDetail(id)
    book.value = bookStore.currentBook
    const [revRes, simRes] = await Promise.all([
      reviewApi.getBookReviews(id).catch(() => ({ data: [] })),
      bookApi.getSimilarBooks(id).catch(() => ({ data: [] }))
    ])
    reviews.value = revRes.data || []
    similarBooks.value = (simRes.data || []).slice(0, 5)
  } finally {
    loading.value = false
  }
}

async function handleBorrow() {
  if (!book.value) return
  try {
    await borrowApi.borrowBook(book.value.id)
    ElMessage.success(t('home.borrowSuccess'))
    loadDetail()
  } catch (e: any) { ElMessage.error(e?.message || t('home.borrowFailed')) }
}

async function handleReserve() {
  if (!book.value) return
  try {
    await reservationApi.reserveBook(book.value.id)
    ElMessage.success(t('book.detail.reserveSuccess'))
  } catch (e: any) { ElMessage.error(e?.message || t('book.detail.reserveFailed')) }
}

async function handleFavorite() {
  if (!book.value) return
  try {
    await favoriteApi.addFavorite(book.value.id)
    ElMessage.success(t('home.favoriteSuccess'))
  } catch (e: any) { ElMessage.error(e?.message || t('home.favoriteFailed')) }
}

async function submitReview() {
  if (!book.value) return
  if (!reviewForm.content.trim()) { ElMessage.warning(t('book.detail.inputReviewContent')); return }
  submittingReview.value = true
  try {
    await reviewApi.submitReview(book.value.id, reviewForm)
    ElMessage.success(t('book.detail.reviewSubmitted'))
    showReviewDialog.value = false
    reviewForm.content = ''
    reviewForm.rating = 5
    const revRes = await reviewApi.getBookReviews(book.value.id)
    reviews.value = revRes.data || []
  } catch (e: any) { ElMessage.error(e?.message || '') }
  finally { submittingReview.value = false }
}

onMounted(() => loadDetail())
</script>

<style scoped>
.book-detail-page { display: flex; flex-direction: column; gap: 20px; }
.breadcrumb { margin-bottom: 0; }

.main-info-card { border-radius: var(--border-radius-base); }
.cover-wrapper { text-align: center; }
.book-cover { width: 280px; height: 380px; border-radius: var(--border-radius-base); box-shadow: var(--shadow-lg); }
.cover-placeholder { width: 280px; height: 380px; background: #f0f0f0; border-radius: var(--border-radius-base); display: flex; align-items: center; justify-content: center; color: #ccc; }
.book-title { font-size: 22px; font-weight: 700; margin: 0; color: var(--color-text-primary); }
.desc-label { font-weight: 600; width: 100px; }
.book-desc { line-height: 1.8; color: var(--color-text-regular); margin: 0; white-space: pre-wrap; }

.action-cards { margin-top: 0; }
.mini-card { text-align: center; padding: 16px; min-height: 80px; display: flex; flex-direction: column; justify-content: center; }
.mini-value { font-size: 24px; font-weight: 700; color: var(--color-primary); }
.mini-value small { font-size: 14px; color: var(--color-text-secondary); font-weight: 400; }
.mini-label { font-size: 12px; color: var(--color-text-secondary); margin: 8px 0 0; }
.action-btns { display: flex; flex-direction: column; gap: 8px; align-items: stretch; justify-content: center; min-height: 80px; }
.action-btns .el-button { width: 100%; }

.tags-card { border-radius: var(--border-radius-base); }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-item { cursor: default; }

.reviews-card, .similar-card { border-radius: var(--border-radius-base); }
.section-header { display: flex; justify-content: space-between; align-items: center; }
.section-title { font-size: 16px; font-weight: 600; }

.review-list { display: flex; flex-direction: column; gap: 20px; }
.review-item { display: flex; gap: 14px; padding-bottom: 20px; border-bottom: 1px solid var(--color-border-light); }
.review-item:last-child { border-bottom: none; padding-bottom: 0; }
.review-content { flex: 1; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.review-user { font-weight: 600; font-size: 14px; color: var(--color-text-primary); }
.review-time { font-size: 12px; color: var(--color-text-placeholder); margin-left: auto; }
.review-text { margin: 0; font-size: 14px; color: var(--color-text-regular); line-height: 1.7; }

.similar-scroll { display: flex; gap: 16px; overflow-x: auto; padding-bottom: 8px; }
.similar-scroll::-webkit-scrollbar { height: 6px; }
.similar-scroll::-webkit-scrollbar-thumb { background: var(--color-border); border-radius: 3px; }
</style>
