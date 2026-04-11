<template>
  <div class="exports-view">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Download /></el-icon>
        数据导出
      </h1>
      <p class="page-desc">导出您的个人数据记录</p>
    </div>

    <div class="export-cards">
      <el-card shadow="hover" class="export-option-card" v-for="option in exportOptions" :key="option.key">
        <template #header>
          <div class="card-header-row">
            <div class="card-header-left">
              <span class="card-icon" :style="{ background: option.color }">
                <el-icon :size="22"><component :is="option.icon" /></el-icon>
              </span>
              <span class="card-label">{{ option.label }}</span>
            </div>
          </div>
        </template>
        <p class="card-desc">{{ option.description }}</p>
        <div class="card-form">
          <div class="form-item">
            <label>时间范围</label>
            <el-date-picker
              v-model="exportForms[option.key].dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              size="default"
            />
          </div>
          <div class="form-item">
            <label>导出格式</label>
            <el-select v-model="exportForms[option.key].format" style="width: 100%">
              <el-option label="Excel (.xlsx)" value="xlsx" />
              <el-option label="CSV (.csv)" value="csv" />
              <el-option label="PDF (.pdf)" value="pdf" />
            </el-select>
          </div>
          <el-button
            type="primary"
            class="export-btn"
            @click="handleExport(option.key)"
            :loading="exportingKey === option.key"
            style="width: 100%"
          >
            <el-icon><Download /></el-icon>
            导出{{ option.label }}
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="history-section">
      <h2 class="section-title">导出历史</h2>
      <div class="history-content" v-loading="historyLoading">
        <el-table :data="exportHistory" stripe style="width: 100%" v-if="exportHistory.length > 0">
          <el-table-column prop="type" label="导出类型" width="130">
            <template #default="{ row }">
              <el-tag size="small">{{ getTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dateRange" label="时间范围" min-width="200" />
          <el-table-column prop="format" label="格式" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.format?.toUpperCase() }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="导出时间" width="170" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'COMPLETED' ? 'success' : row.status === 'FAILED' ? 'danger' : 'warning'" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'COMPLETED'"
                type="primary"
                size="small"
                link
                @click="handleDownload(row)"
              >
                下载
              </el-button>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
        </el-table>

        <EmptyState
          v-else-if="!historyLoading"
          title="暂无导出记录"
          description="完成的数据导出将显示在这里"
        />

        <div class="pagination-wrapper" v-if="historyTotal > historyPageSize">
          <el-pagination
            v-model:current-page="historyPage"
            :page-size="historyPageSize"
            :total="historyTotal"
            layout="prev, pager, next"
            @current-change="fetchExportHistory"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Document, Tickets, Star, Clock } from '@element-plus/icons-vue'
import { exportApi } from '@/api/export'
import EmptyState from '@/components/EmptyState.vue'

interface ExportHistoryItem {
  id: string
  type: string
  dateRange: string
  format: string
  createTime: string
  status: string
  fileId?: string
}

const exportingKey = ref('')
const historyLoading = ref(false)
const historyPage = ref(1)
const historyPageSize = ref(10)
const historyTotal = ref(0)

const exportHistory = ref<ExportHistoryItem[]>([])

const exportOptions = [
  {
    key: 'borrow',
    label: '借阅记录',
    description: '导出您的图书借阅和归还历史记录，包含借阅时间、应还时间等信息。',
    icon: Document,
    color: 'linear-gradient(135deg, #409EFF, #66B1FF)'
  },
  {
    key: 'reservation',
    label: '预约记录',
    description: '导出您的座位预约和图书预约记录，包含预约状态、预约时间等。',
    icon: Tickets,
    color: 'linear-gradient(135deg, #67C23A, #85CE61)'
  },
  {
    key: 'favorite',
    label: '收藏列表',
    description: '导出您当前收藏的图书清单，包含书名、作者、分类等信息。',
    icon: Star,
    color: 'linear-gradient(135deg, #E6A23C, #F0C78A)'
  },
  {
    key: 'history',
    label: '阅读历史',
    description: '导出您的图书浏览历史记录，包含浏览时间、书名、作者等。',
    icon: Clock,
    color: 'linear-gradient(135deg, #909399, #B1B3B8)'
  }
]

const exportForms = reactive<Record<string, { dateRange: string[]; format: string }>>({
  borrow: { dateRange: [], format: 'xlsx' },
  reservation: { dateRange: [], format: 'xlsx' },
  favorite: { dateRange: [], format: 'xlsx' },
  history: { dateRange: [], format: 'xlsx' }
})

const getTypeLabel = (type: string) => {
  const map: Record<string, string> = { borrow: '借阅记录', reservation: '预约记录', favorite: '收藏列表', history: '阅读历史' }
  return map[type] || type
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = { COMPLETED: '已完成', FAILED: '失败', PENDING: '处理中' }
  return map[status] || status
}

const handleExport = async (key: string) => {
  const form = exportForms[key]
  exportingKey.value = key
  try {
    let blob: any
    const params = {
      format: form.format
    }
    switch (key) {
      case 'borrows':
      case 'history':
        blob = await exportApi.exportBorrows(params)
        break
      case 'favorites':
        blob = await exportApi.exportBooks(params)
        break
      case 'reservations':
      default:
        blob = await exportApi.exportBorrows(params)
    }
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${getTypeLabel(key)}_${new Date().toISOString().slice(0, 10)}.${form.format === 'csv' ? 'csv' : 'xlsx'}`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e: any) {
    ElMessage.error(e.message || '导出失败')
  } finally {
    exportingKey.value = ''
  }
}

const handleDownload = async (item: ExportHistoryItem) => {
  try {
    const fileId = Number(item.fileId) || Number(item.id) || 0
    const blob: any = await exportApi.downloadExportFile(fileId)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = `${getTypeLabel(item.type)}_${item.createTime}.${item.format}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e: any) {
    ElMessage.error(e.message || '下载失败')
  }
}

const fetchExportHistory = async () => {
  historyLoading.value = true
  try {
    const res: any = await exportApi.getExportTasks()
    exportHistory.value = res || []
    historyTotal.value = res?.length || 0
  } catch (_) {
  } finally {
    historyLoading.value = false
  }
}

onMounted(() => {
  fetchExportHistory()
})
</script>

<style scoped>
.exports-view {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.export-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 36px;
}

.export-option-card {
  border-radius: var(--border-radius-lg);
}

.export-option-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: none;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.card-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0 0 16px;
}

.card-form .form-item {
  margin-bottom: 12px;
}

.card-form label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-regular);
  margin-bottom: 6px;
}

.export-btn {
  margin-top: 4px;
}

.history-section {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.section-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 20px;
}

.history-content {
  min-height: 200px;
}

.text-muted {
  color: var(--color-text-placeholder);
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .export-cards {
    grid-template-columns: 1fr;
  }

  .history-section {
    padding: 16px;
  }
}
</style>
