<template>
  <div class="admin-borrow-view">
    <div class="stat-cards">
      <StatCard title="今日借阅" :value="stats.todayBorrow" icon="Reading" color="primary" />
      <StatCard title="今日归还" :value="stats.todayReturn" icon="Finished" color="success" />
      <StatCard title="逾期总数" :value="stats.overdueCount" icon="Warning" color="danger" />
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="showBorrowDialog = true">
        <el-icon><Plus /></el-icon>
        登记借阅
      </el-button>
      <ExportButton export-type="borrow" :params="searchParams" @export="handleExport" />
    </div>

    <div class="filter-bar">
      <el-input
        v-model="searchParams.keyword"
        placeholder="搜索用户名/书名/ISBN"
        clearable
        style="width: 260px"
        :prefix-icon="Search"
        @clear="fetchBorrows"
        @keyup.enter="fetchBorrows"
      />
      <el-select v-model="searchParams.status" placeholder="状态筛选" clearable style="width: 130px" @change="fetchBorrows">
        <el-option label="借阅中" :value="0" />
        <el-option label="已归还" :value="1" />
        <el-option label="已逾期" :value="2" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
        @change="handleDateChange"
      />
      <el-button type="primary" :icon="Search" @click="fetchBorrows">搜索</el-button>
      <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="borrowList" stripe border style="width: 100%">
      <el-table-column type="index" label="ID" width="70" align="center" :index="(idx) => (pagination.page - 1) * pagination.pageSize + idx + 1" />
      <el-table-column label="用户名" min-width="100">
        <template #default="{ row }">{{ row.username || '-' }}</template>
      </el-table-column>
      <el-table-column label="书名 / ISBN" min-width="220">
        <template #default="{ row }">
          <div class="book-info">
            <span class="book-title">{{ row.bookTitle }}</span>
            <span class="book-isbn">ISBN: {{ row.bookIsbn }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="borrowDate" label="借阅日期" width="120" sortable />
      <el-table-column prop="dueDate" label="应还日期" width="120" sortable />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <span :class="{ 'overdue-highlight': row.status === 2 }">
            <StatusTag :status="row.status" />
          </span>
        </template>
      </el-table-column>
      <el-table-column label="续借次数" width="90" align="center">
        <template #default="{ row }">
          {{ row.renewCount }} / {{ row.maxRenewCount }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right" align="center">
        <template #default="{ row }">
          <el-button
            v-if="row.status !== 1"
            type="success"
            link
            size="small"
            @click="handleReturn(row)"
          >归还</el-button>
          <el-button
            v-if="row.status === 0 && row.renewCount < row.maxRenewCount"
            type="warning"
            link
            size="small"
            @click="handleRenew(row)"
          >续借</el-button>
          <el-tag v-else-if="row.status === 0" size="small" type="info">已达上限</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <EmptyState v-if="!loading && borrowList.length === 0" title="暂无借阅记录" description="调整搜索条件查看借阅数据" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchBorrows"
        @current-change="fetchBorrows"
      />
    </div>

    <el-dialog v-model="returnDialogVisible" title="确认归还" width="480px" destroy-on-close>
      <div class="return-info" v-if="currentRow">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="书名">{{ currentRow.bookTitle }}</el-descriptions-item>
          <el-descriptions-item label="ISBN">{{ currentRow.bookIsbn }}</el-descriptions-item>
          <el-descriptions-item label="借阅人">{{ currentRow.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="借阅日期">{{ currentRow.borrowDate }}</el-descriptions-item>
          <el-descriptions-item label="应还日期">{{ currentRow.dueDate }}</el-descriptions-item>
        </el-descriptions>
        <div class="return-date-picker">
          <label>实际归还日期：</label>
          <el-date-picker v-model="actualReturnDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 200px" />
        </div>
      </div>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="returnLoading" @click="confirmReturn">确认归还</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showBorrowDialog" title="登记借阅" width="520px" destroy-on-close>
      <el-form ref="borrowFormRef" :model="borrowForm" :rules="borrowFormRules" label-width="100px">
        <el-form-item label="用户" prop="userId">
          <el-autocomplete
            v-model="borrowForm.userLabel"
            :fetch-suggestions="searchUsers"
            placeholder="输入用户名或姓名搜索"
            value-key="label"
            style="width: 100%"
            @select="handleUserSelect"
          />
        </el-form-item>
        <el-form-item label="图书ISBN" prop="isbn">
          <el-input v-model="borrowForm.isbn" placeholder="输入ISBN或扫描条码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBorrowDialog = false">取消</el-button>
        <el-button type="primary" :loading="borrowLoading" @click="confirmBorrow">确认登记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, RefreshLeft, Plus } from '@element-plus/icons-vue'
import { borrowApi } from '@/api/borrow'
import { userApi } from '@/api/user'
import { exportApi } from '@/api/export'
import StatCard from '@/components/StatCard.vue'
import StatusTag from '@/components/StatusTag.vue'
import EmptyState from '@/components/EmptyState.vue'
import ExportButton from '@/components/ExportButton.vue'
import type { Borrow, User } from '@/api/types'

const loading = ref(false)
const returnLoading = ref(false)
const borrowLoading = ref(false)
const returnDialogVisible = ref(false)
const showBorrowDialog = ref(false)
const borrowFormRef = ref<FormInstance>()
const borrowList = ref<Borrow[]>([])
const total = ref(0)
const currentRow = ref<Borrow | null>(null)
const actualReturnDate = ref('')

const stats = reactive({
  todayBorrow: 0,
  todayReturn: 0,
  overdueCount: 0
})

const searchParams = reactive({
  keyword: '',
  status: ''
})

const dateRange = ref<string[]>([])

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const borrowForm = reactive({
  userId: null as number | null,
  userLabel: '',
  isbn: ''
})

const borrowFormRules: FormRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  isbn: [{ required: true, message: '请输入图书ISBN', trigger: 'blur' }]
}

async function fetchStats() {
  try {
    const res: any = await borrowApi.getAdminBorrowRecords({ page: 1, pageSize: 1 })
    const data = res || {}
    stats.todayBorrow = data.todayBorrow || 0
    stats.todayReturn = data.todayReturn || 0
    stats.overdueCount = data.overdueCount || 0
  } catch {
  }
}

async function fetchBorrows() {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (searchParams.keyword) params.keyword = searchParams.keyword
    if (searchParams.status) params.status = searchParams.status
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res: any = await borrowApi.getAdminBorrowRecords(params)
    const pageData = res || {}
    borrowList.value = pageData.records || pageData.list || []
    total.value = pageData.total || 0
  } catch {
    ElMessage.error('获取借阅列表失败')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchParams.keyword = ''
  searchParams.status = ''
  dateRange.value = []
  pagination.page = 1
  fetchBorrows()
}

function handleDateChange() {
  pagination.page = 1
  fetchBorrows()
}

function handleReturn(row: Borrow) {
  currentRow.value = row
  actualReturnDate.value = new Date().toISOString().slice(0, 10)
  returnDialogVisible.value = true
}

async function confirmReturn() {
  if (!currentRow.value) return
  returnLoading.value = true
  try {
    await borrowApi.returnBook(currentRow.value.id)
    ElMessage.success('归还成功')
    returnDialogVisible.value = false
    fetchBorrows()
    fetchStats()
  } catch {
    ElMessage.error('归还失败')
  } finally {
    returnLoading.value = false
  }
}

function handleRenew(row: Borrow) {
  if (row.renewCount >= (row.maxRenewCount || 3)) {
    ElMessage.warning('已达到最大续借次数')
    return
  }
  ElMessageBox.confirm(`确定要为《${row.bookTitle}》续借吗？`, '续借确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await borrowApi.renewBook(row.id)
      ElMessage.success('续借成功')
      fetchBorrows()
    } catch {
      ElMessage.error('续借失败')
    }
  }).catch(() => {})
}

interface UserOption { value: number; label: string }

async function searchUsers(queryString: string, cb: (results: UserOption[]) => void) {
  if (!queryString) { cb([]); return }
  try {
    const res: any = await userApi.getUsers({ keyword: queryString, pageSize: 10 })
    const list = (res.records || res.list || res.data?.list || []).map((u: User) => ({
      value: u.id,
      label: `${u.username} (${u.realName || u.username})`
    }))
    cb(list)
  } catch {
    cb([])
  }
}

function handleUserSelect(item: UserOption) {
  borrowForm.userId = item.value
}

async function confirmBorrow() {
  const valid = await borrowFormRef.value?.validate().catch(() => false)
  if (!valid) return

  borrowLoading.value = true
  try {
    await borrowApi.adminBorrow({ userId: borrowForm.userId, isbn: borrowForm.isbn })
    ElMessage.success('登记借阅成功')
    showBorrowDialog.value = false
    borrowForm.userId = null
    borrowForm.userLabel = ''
    borrowForm.isbn = ''
    fetchBorrows()
    fetchStats()
  } catch {
    ElMessage.error('登记借阅失败')
  } finally {
    borrowLoading.value = false
  }
}

function handleExport(format: 'excel' | 'csv') {
  exportApi.exportBorrows({ format, ...searchParams }).then((blob: any) => {
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

onMounted(() => {
  fetchBorrows()
  fetchStats()
})
</script>

<style scoped>
.admin-borrow-view {
  padding: var(--content-padding);
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.book-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.book-title {
  font-weight: 500;
  color: var(--color-text-primary);
}

.book-isbn {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.overdue-highlight {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.return-info {
  margin-bottom: 16px;
}

.return-date-picker {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
