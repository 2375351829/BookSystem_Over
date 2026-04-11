<template>
  <div class="my-borrow-page">
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="8">
        <StatCard :title="t('borrow.borrowing')" :value="stats.borrowing" icon="Reading" color="primary" />
      </el-col>
      <el-col :xs="8">
        <StatCard :title="t('borrow.overdue')" :value="stats.overdue" icon="Warning" color="danger" />
      </el-col>
      <el-col :xs="8">
        <StatCard :title="t('borrow.returned')" :value="stats.returned" icon="CircleCheck" color="success" />
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane :label="t('borrow.all')" name="" />
            <el-tab-pane :label="t('borrow.borrowing')" name="borrowing" />
            <el-tab-pane :label="t('borrow.overdue')" name="overdue" />
            <el-tab-pane :label="t('borrow.returned')" name="returned" />
          </el-tabs>
          <div class="header-actions">
            <el-checkbox v-model="selectAll" :indeterminate="isIndeterminate" @change="handleSelectAll">{{ t('borrow.selectAll') }}</el-checkbox>
            <el-button type="warning" plain :disabled="!selectedIds.length" @click="handleBatchReturn">
              {{ t('borrow.batchReturn') }}
            </el-button>
            <ExportButton export-type="borrows" :params="{ status: activeTab }" @export="handleExport" />
          </div>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" @selection-change="handleSelectionChange" stripe>
        <el-table-column type="selection" width="48" />
        <el-table-column :label="t('borrow.cover')" width="80">
          <template #default="{ row }">
            <el-image :src="row.bookCover || defaultCover" :alt="row.bookTitle" class="cover-thumb" fit="cover" style="width:60px;height:80px;border-radius:4px;" />
          </template>
        </el-table-column>
        <el-table-column :label="t('borrow.bookInfo')" min-width="220">
          <template #default="{ row }">
            <div class="book-info-cell">
              <span class="book-name">{{ row.bookTitle }}</span>
              <span class="book-isbn">ISBN: {{ row.bookIsbn }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('borrow.borrowDate')" prop="borrowDate" width="120" sortable />
        <el-table-column :label="t('borrow.dueDate')" prop="dueDate" width="120" sortable>
          <template #default="{ row }">
            <span :class="getDueClass(row)">{{ row.dueDate }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('borrow.status')" width="100">
          <template #default="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="t('borrow.renewCount')" width="90" align="center">
          <template #default="{ row }">
            {{ row.renewCount }} / {{ row.maxRenewCount }}
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain :disabled="row.renewCount >= (row.maxRenewCount || 3) || row.status === 1 || row.status === 2"
              @click="handleRenew(row)">
              {{ t('borrow.renew') }}
            </el-button>
            <el-button size="small" type="danger" plain :disabled="row.status === 1" @click="handleReturn(row)">
              {{ t('borrow.return') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="total > 0" class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
      <EmptyState v-if="!loading && !list.length" :description="t('borrow.noRecords')" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import ExportButton from '@/components/ExportButton.vue'
import EmptyState from '@/components/EmptyState.vue'
import { borrowApi } from '@/api/borrow'
import { exportApi } from '@/api/export'
import type { Borrow } from '@/api/types'

const { t } = useI18n()

const loading = ref(false)
const activeTab = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const list = ref<Borrow[]>([])
const selectedIds = ref<number[]>([])
const selectAll = ref(false)

const stats = reactive({ borrowing: 0, overdue: 0, returned: 0 })

const isIndeterminate = computed(() => selectedIds.value.length > 0 && selectedIds.value.length < list.value.length)

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iODAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZWVlIi8+PC9zdmc+'

function getDueClass(row: Borrow) {
  if (row.status === 2) return 'text-danger'
  if (row.status !== 0) return ''
  const due = new Date(row.dueDate)
  const now = new Date()
  const diff = Math.ceil((due.getTime() - now.getTime()) / 86400000)
  if (diff <= 3 && diff >= 0) return 'text-warning'
  if (diff < 0) return 'text-danger'
  return ''
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await borrowApi.getMyBorrows({
      status: activeTab.value || undefined,
      page: page.value,
      pageSize
    })
    list.value = (res.records || []).map((item: any) => ({
      id: item.id,
      bookId: item.bookId,
      bookTitle: item.bookTitle || '未知图书',
      bookIsbn: item.bookIsbn || '-',
      bookCover: item.bookCover || '',
      borrowDate: item.borrowDate,
      dueDate: item.dueDate,
      returnDate: item.returnDate,
      status: item.status,
      renewCount: item.renewCount || 0,
      maxRenewCount: 2
    }))
    total.value = res.total || 0
    const borrowingList = list.value.filter((item: any) => item.status === 0)
    stats.borrowing = borrowingList.length
    stats.overdue = borrowingList.filter((item: any) => new Date(item.dueDate) < new Date()).length
    stats.returned = list.value.filter((item: any) => item.status === 1).length
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  page.value = 1
  selectedIds.value = []
  selectAll.value = false
  fetchData()
}

function handleSelectionChange(rows: Borrow[]) {
  selectedIds.value = rows.map(r => r.id)
  selectAll.value = rows.length === list.value.length && list.value.length > 0
}

function handleSelectAll(val: boolean) {
  selectedIds.value = val ? list.value.map(r => r.id) : []
}

async function handleReturn(row: Borrow) {
  try {
    await ElMessageBox.confirm(t('borrow.confirmReturn'), t('common.confirm'), { type: 'warning' })
    await borrowApi.returnBook(row.id)
    ElMessage.success(t('borrow.returnSuccess'))
    fetchData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e?.message || '')
  }
}

async function handleRenew(row: Borrow) {
  try {
    await borrowApi.renewBook(row.id)
    ElMessage.success(t('borrow.renewSuccess'))
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || t('borrow.renewFailed'))
  }
}

async function handleBatchReturn() {
  try {
    await ElMessageBox.confirm(t('borrow.confirmBatchReturn'), t('common.confirm'), { type: 'warning' })
    await borrowApi.batchReturn(selectedIds.value)
    ElMessage.success(t('borrow.batchReturnSuccess'))
    fetchData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e?.message || '')
  }
}

function handleExport(format: 'excel' | 'csv') {
  exportApi.exportBorrows({ format, status: activeTab.value }).then((blob: any) => {
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `借阅记录_${new Date().toISOString().slice(0, 10)}.${format === 'csv' ? 'csv' : 'xlsx'}`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  }).catch((e: any) => {
    ElMessage.error(e.message || '导出失败')
  })
}

onMounted(() => fetchData())
</script>

<style scoped>
.my-borrow-page { display: flex; flex-direction: column; gap: 20px; }
.stat-row { margin-bottom: 0; }
.table-card { border-radius: var(--border-radius-base); }
.card-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; }
.card-header :deep(.el-tabs__header) { margin-bottom: 0; }
.header-actions { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.book-info-cell { display: flex; flex-direction: column; gap: 2px; }
.book-name { font-weight: 600; color: var(--color-text-primary); font-size: 14px; }
.book-isbn { font-size: 12px; color: var(--color-text-secondary); }
.text-danger { color: var(--color-danger); font-weight: 500; }
.text-warning { color: var(--color-warning); font-weight: 500; }
.pagination-wrap { display: flex; justify-content: center; padding: 16px 0 0; }
</style>
