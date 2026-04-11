<template>
  <div class="book-list-page">
    <el-card class="search-card">
      <el-input
        v-model="searchParams.keyword"
        :placeholder="t('books.searchPlaceholder')"
        size="large"
        clearable
        prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append>
          <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('books.search') }}</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="filter-card">
      <div class="filter-bar">
        <el-select v-model="searchParams.category" :placeholder="t('books.category')" clearable style="width:140px" @change="handleSearch">
          <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
        <el-select v-model="searchParams.status" :placeholder="t('books.status')" clearable style="width:130px" @change="handleSearch">
          <el-option label="全部" value="" />
          <el-option label="在馆可借" value="available" />
          <el-option label="已借出" value="borrowed" />
          <el-option label="已预约" value="reserved" />
        </el-select>
        <el-select v-model="searchParams.language" :placeholder="t('books.language')" clearable style="width:120px" @change="handleSearch">
          <el-option label="全部语言" value="" />
          <el-option label="中文" value="zh" />
          <el-option label="英文" value="en" />
          <el-option label="日文" value="ja" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-select v-model="searchParams.sortBy" :placeholder="t('books.sortBy')" style="width:140px" @change="handleSearch">
          <el-option label="默认排序" value="" />
          <el-option label="最新上架" value="newest" />
          <el-option label="评分最高" value="rating" />
          <el-option label="借阅最多" value="popular" />
        </el-select>
        <div class="view-toggle">
          <el-button :type="viewMode === 'grid' ? 'primary' : ''" :icon="Grid" circle @click="viewMode = 'grid'" />
          <el-button :type="viewMode === 'list' ? 'primary' : ''" :icon="List" circle @click="viewMode = 'list'" />
        </div>
        <el-button plain @click="showAdvanced = !showAdvanced">{{ t('books.advancedSearch') }}</el-button>
      </div>

      <el-collapse-transition>
        <div v-show="showAdvanced" class="advanced-panel">
          <el-row :gutter="16">
            <el-col :span="6">
              <el-input v-model="searchParams.isbn" placeholder="ISBN" clearable />
            </el-col>
            <el-col :span="6">
              <el-input v-model="searchParams.publisher" :placeholder="t('books.publisher')" clearable />
            </el-col>
            <el-col :span="6">
              <el-date-picker v-model="searchParams.yearRange" type="yearrange" :start-placeholder="t('books.startYear')" :end-placeholder="t('books.endYear')" style="width:100%" value-format="YYYY" />
            </el-col>
            <el-col :span="6">
              <div class="price-range">
                <el-input-number v-model="searchParams.priceMin" :min="0" :placeholder="t('books.minPrice')" controls-position="right" style="width:45%" />
                <span>~</span>
                <el-input-number v-model="searchParams.priceMax" :min="0" :placeholder="t('books.maxPrice')" controls-position="right" style="width:45%" />
              </div>
            </el-col>
          </el-row>
          <el-button type="primary" style="margin-top:12px" @click="handleSearch">{{ t('books.search') }}</el-button>
        </div>
      </el-collapse-transition>
    </el-card>

    <div v-if="viewMode === 'grid'" class="book-grid">
      <el-row :gutter="20">
        <el-col v-for="book in bookStore.list" :key="book.id" :xs="12" :sm="8" :md="6">
          <BookCard :book="book" mode="grid" @click="router.push(`/books/${book.id}`)" @borrow="handleBorrow" @favorite="handleFavorite" />
        </el-col>
      </el-row>
    </div>
    <div v-else class="book-list-mode">
      <div v-for="book in bookStore.list" :key="book.id" class="list-item-wrapper">
        <BookCard :book="book" mode="list" @click="router.push(`/books/${book.id}`)" @borrow="handleBorrow" @favorite="handleFavorite" />
      </div>
    </div>

    <EmptyState v-if="!bookStore.loading && !bookStore.list.length" :description="t('books.noBooks')" action-text="清除筛选" @action="resetFilters" />

    <div v-if="bookStore.total > 0" class="pagination-wrap">
      <el-pagination
        v-model:current-page="searchParams.page"
        :page-size="searchParams.pageSize"
        :total="bookStore.total"
        layout="total, prev, pager, next, jumper"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search as SearchIcon, Grid as GridIcon, List as ListIcon } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useBookStore } from '@/stores/bookStore'
import BookCard from '@/components/BookCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import { borrowApi } from '@/api/borrow'
import { favoriteApi } from '@/api/favorite'
import type { Book } from '@/api/types'

const { t } = useI18n()
const router = useRouter()
const bookStore = useBookStore()

const viewMode = ref<'grid' | 'list'>('grid')
const showAdvanced = ref(false)

const Search = SearchIcon
const Grid = GridIcon
const List = ListIcon

const categories = [
  { label: '全部分类', value: '' },
  { label: 'A-F', value: 'A-F' },
  { label: 'G-H', value: 'G-H' },
  { label: 'I-J', value: 'I-J' },
  { label: 'K-L', value: 'K-L' },
  { label: 'M-P', value: 'M-P' },
  { label: 'Q-R', value: 'Q-R' },
  { label: 'S-Z', value: 'S-Z' },
  { label: 'T-Z', value: 'T-Z' }
]

const searchParams = reactive({
  keyword: '',
  category: '',
  status: '',
  language: '',
  sortBy: '',
  isbn: '',
  publisher: '',
  yearRange: null as [string, string] | null,
  priceMin: undefined as number | undefined,
  priceMax: undefined as number | undefined,
  page: 1,
  pageSize: 12
})

function buildQuery() {
  const q: Record<string, any> = { ...searchParams }
  if (q.yearRange) {
    q.startYear = q.yearRange[0]
    q.endYear = q.yearRange[1]
    delete q.yearRange
  }
  Object.keys(q).forEach(k => { if (q[k] === '' || q[k] === null || q[k] === undefined) delete q[k] })
  return q
}

async function fetchData() {
  await bookStore.fetchBooks(buildQuery())
}

function handleSearch() {
  searchParams.page = 1
  fetchData()
}

function resetFilters() {
  Object.assign(searchParams, {
    keyword: '', category: '', status: '', language: '', sortBy: '',
    isbn: '', publisher: '', yearRange: null, priceMin: undefined, priceMax: undefined,
    page: 1
  })
  fetchData()
}

async function handleBorrow(book: Book) {
  try {
    await borrowApi.borrowBook(book.id)
    ElMessage.success(t('home.borrowSuccess'))
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || t('home.borrowFailed'))
  }
}

async function handleFavorite(book: Book) {
  try {
    await favoriteApi.addFavorite(book.id)
    ElMessage.success(t('home.favoriteSuccess'))
  } catch (e: any) {
    ElMessage.error(e?.message || t('home.favoriteFailed'))
  }
}

watch(() => [searchParams.category, searchParams.status, searchParams.language, searchParams.sortBy], () => {
  searchParams.page = 1
  fetchData()
})

onMounted(() => fetchData())
</script>

<style scoped>
.book-list-page { display: flex; flex-direction: column; gap: 20px; padding: 20px; }
.search-card { border-radius: var(--border-radius-base); }
.search-card :deep(.el-input-group__append) { background: var(--color-primary); color: #fff; border-color: var(--color-primary); }
.filter-card { border-radius: var(--border-radius-base); }
.filter-bar { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; }
.view-toggle { display: flex; gap: 4px; margin-left: auto; }
.advanced-panel { margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--color-border-light); }
.price-range { display: flex; align-items: center; gap: 8px; width: 100%; }
.book-grid { min-height: 200px; }
.book-grid :deep(.el-col) { margin-bottom: 20px; }
.book-list-mode { display: flex; flex-direction: column; gap: 16px; min-height: 200px; }
.list-item-wrapper { padding: 4px 0; }
.pagination-wrap { display: flex; justify-content: center; padding: 20px 0; }
</style>
