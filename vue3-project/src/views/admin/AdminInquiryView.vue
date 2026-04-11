<template>
  <div class="admin-inquiry-view">
    <div class="filter-bar">
      <el-select v-model="searchParams.status" placeholder="状态筛选" clearable style="width: 140px" @change="fetchInquiries">
        <el-option label="全部" value="" />
        <el-option label="待回复" value="pending" />
        <el-option label="已回复" value="replied" />
        <el-option label="已关闭" value="closed" />
      </el-select>
      <el-select v-model="searchParams.category" placeholder="分类筛选" clearable style="width: 140px" @change="fetchInquiries">
        <el-option label="借阅咨询" value="borrow" />
        <el-option label="罚款问题" value="fine" />
        <el-option label="预约问题" value="reservation" />
        <el-option label="系统故障" value="system" />
        <el-option label="其他" value="other" />
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
      <el-button type="primary" :icon="Search" @click="fetchInquiries">搜索</el-button>
      <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="inquiryList" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="提交人" min-width="120">
        <template #default="{ row }">{{ row.username || '匿名用户' }}</template>
      </el-table-column>
      <el-table-column label="分类" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" effect="light">{{ categoryMap[row.category] || row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <StatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="170" sortable />
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">查看详情</el-button>
          <el-button v-if="row.status !== 'closed'" type="success" link size="small" @click="handleReply(row)">回复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <EmptyState v-if="!loading && inquiryList.length === 0" title="暂无咨询记录" description="所有咨询将在此处显示" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchInquiries"
        @current-change="fetchInquiries"
      />
    </div>

    <el-dialog v-model="replyDialogVisible" title="回复咨询" width="600px" destroy-on-close>
      <div class="inquiry-original" v-if="currentRow">
        <div class="original-header">
          <h4>{{ currentRow.title }}</h4>
          <span class="original-meta">
            {{ currentRow.username || '匿名' }} · {{ currentRow.createdAt }}
            <el-tag size="small" style="margin-left: 8px">{{ categoryMap[currentRow.category] || currentRow.category }}</el-tag>
          </span>
        </div>
        <div class="original-content">
          {{ currentRow.content }}
        </div>
        <el-divider content-position="left">回复内容</el-divider>
      </div>
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="5"
        placeholder="请输入回复内容..."
        maxlength="1000"
        show-word-limit
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="咨询详情" width="640px" destroy-on-close>
      <div class="detail-content" v-if="currentRow">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="ID">{{ currentRow.id }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ currentRow.title }}</el-descriptions-item>
          <el-descriptions-item label="分类">
            <el-tag size="small">{{ categoryMap[currentRow.category] || currentRow.category }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <StatusTag :status="currentRow.status" />
          </el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentRow.username || '匿名用户' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentRow.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="咨询内容" :span="1">
            <div class="content-text">{{ currentRow.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentRow.replyContent" class="reply-section">
          <el-divider content-position="left">管理员回复</el-divider>
          <div class="reply-meta">{{ currentRow.replyDate }}</div>
          <div class="reply-text">{{ currentRow.replyContent }}</div>
        </div>
        <el-empty v-else description="暂无回复" :image-size="80" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, RefreshLeft } from '@element-plus/icons-vue'
import { inquiryApi } from '@/api/inquiry'
import StatusTag from '@/components/StatusTag.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { Inquiry } from '@/api/types'

const loading = ref(false)
const replyLoading = ref(false)
const replyDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const inquiryList = ref<Inquiry[]>([])
const total = ref(0)
const currentRow = ref<Inquiry | null>(null)
const replyContent = ref('')

const searchParams = reactive({
  status: '',
  category: ''
})

const dateRange = ref<string[]>([])

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const apiStatusMap: Record<string, number> = {
  'pending': 0,
  'replied': 1,
  'closed': 2
}

const reverseApiStatusMap: Record<number, string> = {
  0: 'pending',
  1: 'replied',
  2: 'closed'
}

const categoryMap: Record<string, string> = {
  borrow: '借阅咨询',
  fine: '罚款问题',
  reservation: '预约问题',
  system: '系统故障',
  other: '其他'
}

async function fetchInquiries() {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (searchParams.status && apiStatusMap[searchParams.status] !== undefined) {
      params.status = apiStatusMap[searchParams.status]
    }
    if (searchParams.category) {
      params.category = searchParams.category
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res: any = await inquiryApi.getAllInquiries(params)
    const pageData = res || {}
    const list = pageData.records || pageData.list || []
    inquiryList.value = list.map((item: any) => ({
      ...item,
      status: reverseApiStatusMap[item.status] ?? item.status,
      createdAt: item.createTime || item.createdAt,
      username: item.username || '用户' + item.userId
    }))
    total.value = pageData.total || 0
  } catch {
    ElMessage.error('获取咨询列表失败')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchParams.status = ''
  searchParams.category = ''
  dateRange.value = []
  pagination.page = 1
  fetchInquiries()
}

function handleDateChange() {
  pagination.page = 1
  fetchInquiries()
}

function handleDetail(row: Inquiry) {
  currentRow.value = row
  detailDialogVisible.value = true
}

function handleReply(row: Inquiry) {
  currentRow.value = row
  replyContent.value = ''
  replyDialogVisible.value = true
}

async function submitReply() {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  if (!currentRow.value) return

  replyLoading.value = true
  try {
    await inquiryApi.replyInquiry(currentRow.value.id, { replyContent: replyContent.value })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchInquiries()
  } catch {
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

onMounted(() => {
  fetchInquiries()
})
</script>

<style scoped>
.admin-inquiry-view {
  padding: var(--content-padding);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.inquiry-original {
  margin-bottom: 16px;
}

.original-header h4 {
  margin: 0 0 8px;
  color: var(--color-text-primary);
}

.original-meta {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.original-content {
  background: var(--color-bg-page);
  border-radius: var(--border-radius-base);
  padding: 12px 16px;
  font-size: var(--font-size-base);
  line-height: 1.7;
  color: var(--color-text-regular);
  white-space: pre-wrap;
  word-break: break-all;
}

.detail-content .content-text {
  white-space: pre-wrap;
  line-height: 1.7;
  max-height: 200px;
  overflow-y: auto;
}

.reply-section {
  margin-top: 8px;
}

.reply-meta {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.reply-text {
  background: #f0f9eb;
  border-radius: var(--border-radius-base);
  padding: 12px 16px;
  white-space: pre-wrap;
  line-height: 1.7;
  color: var(--color-text-regular);
}
</style>
