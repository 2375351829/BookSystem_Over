<template>
  <div class="admin-log-view">
    <div class="filter-bar">
      <el-autocomplete
        v-model="searchParams.operator"
        :fetch-suggestions="searchOperators"
        placeholder="搜索操作人"
        clearable
        style="width: 180px"
        value-key="value"
        @clear="fetchLogs"
      />
      <el-select v-model="searchParams.operationType" placeholder="操作类型" clearable style="width: 150px" @change="fetchLogs">
        <el-option label="全部" value="" />
        <el-option
          v-for="item in operationOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="searchParams.module" placeholder="模块筛选" clearable style="width: 140px" @change="fetchLogs">
        <el-option label="全部" value="" />
        <el-option
          v-for="item in moduleOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 280px"
        :shortcuts="dateShortcuts"
        @change="handleDateChange"
      />
    </div>

    <div class="toolbar">
      <ExportButton export-type="log" :params="searchParams" @export="handleExport" />
      <el-button type="danger" plain @click="handleClearLogs">
        <el-icon><Delete /></el-icon>
        清空日志
      </el-button>
      <el-button type="primary" :icon="Search" @click="fetchLogs">搜索</el-button>
      <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="logList" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="username" label="操作人" min-width="110" />
      <el-table-column label="操作类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="operationTagMap[row.operation]?.type || 'info'" size="small" effect="light">
            {{ operationTagMap[row.operation]?.label || row.operation }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模块" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" effect="plain">{{ moduleLabelMap[row.module] || row.module }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作描述" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tooltip :content="row.description" placement="top" :disabled="(row.description || '').length <= 30">
            <span>{{ (row.description || '-').slice(0, 30) }}{{ (row.description || '').length > 30 ? '...' : '' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="ipAddress" label="IP地址" width="140" />
      <el-table-column label="结果" width="80" align="center">
        <template #default="{ row }">
          <el-tag
            :type="row.status === 1 ? 'success' : 'danger'"
            size="small"
            effect="dark"
          >{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="操作时间" width="170" sortable />
      <el-table-column label="操作" width="90" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <EmptyState v-if="!loading && logList.length === 0" title="暂无日志记录" description="系统操作日志将在此处显示" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchLogs"
        @current-change="fetchLogs"
      />
    </div>

    <el-dialog v-model="detailDialogVisible" title="日志详情" width="720px" destroy-on-close>
      <el-descriptions :column="2" border size="medium" v-if="currentLog">
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="operationTagMap[currentLog.operation]?.type || 'info'" size="small">
            {{ operationTagMap[currentLog.operation]?.label || currentLog.operation }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属模块">
          <el-tag size="small" effect="plain">{{ moduleLabelMap[currentLog.module] || currentLog.module }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="执行结果">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="small" effect="dark">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ currentLog.duration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求方法" v-if="currentLog.method">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="用户代理" v-if="currentLog.userAgent">
          <el-tooltip :content="currentLog.userAgent" placement="top">
            <span class="ua-text">{{ currentLog.userAgent.slice(0, 40) }}{{ currentLog.userAgent.length > 40 ? '...' : '' }}</span>
          </el-tooltip>
        </el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">
          <div class="desc-text">{{ currentLog.description || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2" v-if="currentLog.requestData">
          <div class="json-container">
            <pre class="json-block">{{ formatJson(currentLog.requestData) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="响应数据" :span="2" v-if="currentLog.responseData">
          <div class="json-container">
            <pre class="json-block">{{ formatJson(currentLog.responseData) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.errorMessage">
          <div class="error-message">
            <el-icon class="error-icon"><WarningFilled /></el-icon>
            <pre class="error-text">{{ currentLog.errorMessage }}</pre>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshLeft, Delete, WarningFilled } from '@element-plus/icons-vue'
import { logApi } from '@/api/log'
import EmptyState from '@/components/EmptyState.vue'
import ExportButton from '@/components/ExportButton.vue'
import type { OperationLog } from '@/api/types'

const loading = ref(false)
const detailDialogVisible = ref(false)
const logList = ref<OperationLog[]>([])
const total = ref(0)
const currentLog = ref<OperationLog | null>(null)

const searchParams = reactive({
  operator: '',
  operationType: '',
  module: ''
})

const dateRange = ref<string[]>([])

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const dateShortcuts = [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      const dateStr = today.toISOString().split('T')[0]
      return [dateStr, dateStr]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 6 * 24 * 3600 * 1000)
      return [start.toISOString().split('T')[0], end.toISOString().split('T')[0]]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 29 * 24 * 3600 * 1000)
      return [start.toISOString().split('T')[0], end.toISOString().split('T')[0]]
    }
  }
]

const operationTagMap: Record<string, { type: string; label: string }> = {
  create: { type: 'success', label: '新增' },
  delete: { type: 'danger', label: '删除' },
  update: { type: 'warning', label: '修改' },
  read: { type: '', label: '查询' },
  login: { type: 'primary', label: '登录' },
  logout: { type: 'info', label: '登出' },
  system: { type: 'warning', label: '设置' }
}

const moduleLabelMap: Record<string, string> = {
  user: '用户管理',
  book: '图书管理',
  borrow: '借阅管理',
  category: '分类管理',
  config: '系统配置',
  seat: '座位管理',
  inquiry: '咨询管理',
  acquisition: '图书采编',
  statistics: '统计报表'
}

const operationOptions = ref<{ value: string; label: string }[]>([
  { value: 'create', label: '新增' },
  { value: 'delete', label: '删除' },
  { value: 'update', label: '修改' },
  { value: 'read', label: '查询' },
  { value: 'login', label: '登录' },
  { value: 'logout', label: '登出' },
  { value: 'system', label: '系统设置' }
])

const moduleOptions = ref<{ value: string; label: string }[]>([
  { value: 'user', label: '用户管理' },
  { value: 'book', label: '图书管理' },
  { value: 'borrow', label: '借阅管理' },
  { value: 'category', label: '分类管理' },
  { value: 'config', label: '系统配置' },
  { value: 'seat', label: '座位管理' },
  { value: 'inquiry', label: '咨询管理' },
  { value: 'acquisition', label: '图书采编' },
  { value: 'statistics', label: '统计报表' }
])

async function fetchLogs() {
  loading.value = true
  try {
    const params: any = {
      ...searchParams,
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res: any = await logApi.getLogs(params)
    logList.value = res.records || []
    total.value = res.total || 0
    updateFilterOptions(logList.value)
  } catch {
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

function updateFilterOptions(logs: OperationLog[]) {
  const operationSet = new Set<string>()
  const moduleSet = new Set<string>()
  
  logs.forEach(log => {
    if (log.operation) operationSet.add(log.operation)
    if (log.module) moduleSet.add(log.module)
  })

  operationSet.forEach(op => {
    if (!operationOptions.value.find(opt => opt.value === op)) {
      operationOptions.value.push({
        value: op,
        label: operationTagMap[op]?.label || op
      })
    }
  })

  moduleSet.forEach(mod => {
    if (!moduleOptions.value.find(opt => opt.value === mod)) {
      moduleOptions.value.push({
        value: mod,
        label: moduleLabelMap[mod] || mod
      })
    }
  })
}

function resetSearch() {
  searchParams.operator = ''
  searchParams.operationType = ''
  searchParams.module = ''
  dateRange.value = []
  pagination.page = 1
  fetchLogs()
}

function handleDateChange() {
  pagination.page = 1
  fetchLogs()
}

interface OperatorOption { value: string }

async function searchOperators(queryString: string, cb: (results: OperatorOption[]) => void) {
  if (!queryString) { cb([]); return }
  try {
    const res: any = await logApi.getLogs({ keyword: queryString, pageSize: 8 })
    const users = new Set((res.data.list || []).map((l: OperationLog) => l.username).filter(Boolean))
    cb(Array.from(users).map((u: unknown) => ({ value: String(u) })))
  } catch {
    cb([])
  }
}

function handleDetail(row: OperationLog) {
  currentLog.value = row
  detailDialogVisible.value = true
}

function handleClearLogs() {
  ElMessageBox.confirm(
    '确定要清空所有操作日志吗？此操作不可撤销！',
    '危险操作确认',
    {
      confirmButtonText: '确定清空',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(() => {
    ElMessage.info('清空日志功能需要后端支持')
  }).catch(() => {})
}

function handleExport(format: 'excel' | 'csv') {
  ElMessage.success(`正在导出 ${format.toUpperCase()} 格式日志...`)
}

function formatJson(data: any): string {
  try {
    const parsed = typeof data === 'string' ? JSON.parse(data) : data
    return JSON.stringify(parsed, null, 2)
  } catch {
    return String(data)
  }
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.admin-log-view {
  padding: var(--content-padding);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.desc-text {
  white-space: pre-wrap;
  line-height: 1.7;
  word-break: break-all;
  max-height: 150px;
  overflow-y: auto;
}

.ua-text {
  color: var(--color-text-secondary);
  font-size: var(--font-size-xs);
}

.json-container {
  width: 100%;
  overflow-x: auto;
}

.json-block {
  background: var(--color-bg-page);
  border-radius: var(--border-radius-base);
  padding: 12px 16px;
  font-size: var(--font-size-xs);
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  color: var(--color-text-regular);
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}

.error-message {
  background: var(--el-color-danger-light-9);
  border: 1px solid var(--el-color-danger-light-5);
  border-radius: var(--border-radius-base);
  padding: 12px 16px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.error-icon {
  color: var(--el-color-danger);
  font-size: 18px;
  flex-shrink: 0;
  margin-top: 2px;
}

.error-text {
  color: var(--el-color-danger);
  font-size: var(--font-size-xs);
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  line-height: 1.6;
}
</style>
