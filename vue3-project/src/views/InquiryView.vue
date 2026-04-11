<template>
  <div class="inquiry-view">
    <div class="inquiry-layout">
      <aside class="inquiry-sidebar">
        <div class="sidebar-section">
          <h3 class="sidebar-title">FAQ分类</h3>
          <el-menu :default-active="activeCategory" @select="handleCategorySelect" class="faq-menu">
            <el-menu-item index="all">
              <el-icon><Grid /></el-icon>
              <span>全部问题</span>
            </el-menu-item>
            <el-menu-item index="borrow">
              <el-icon><Reading /></el-icon>
              <span>借阅相关</span>
            </el-menu-item>
            <el-menu-item index="return">
              <el-icon><RefreshRight /></el-icon>
              <span>归还续借</span>
            </el-menu-item>
            <el-menu-item index="overdue">
              <el-icon><WarningFilled /></el-icon>
              <span>逾期处理</span>
            </el-menu-item>
            <el-menu-item index="seat">
              <el-icon><OfficeBuilding /></el-icon>
              <span>座位预约</span>
            </el-menu-item>
            <el-menu-item index="account">
              <el-icon><UserFilled /></el-icon>
              <span>账户问题</span>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="sidebar-section faq-collapse-section">
          <h3 class="sidebar-title">常见问题</h3>
          <el-collapse v-model="activeFaq" accordion class="faq-collapse">
            <el-collapse-item v-for="faq in filteredFaqs" :key="faq.id" :name="faq.id">
              <template #title>
                <span class="faq-title">{{ faq.question }}</span>
              </template>
              <div class="faq-answer">{{ faq.answer }}</div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </aside>

      <main class="inquiry-main">
        <el-tabs v-model="activeTab" class="inquiry-tabs">
          <el-tab-pane name="history">
            <template #label>
              <span>我的咨询</span>
              <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
            </template>
            <div class="timeline-section">
              <el-timeline v-if="inquiries.length > 0">
                <el-timeline-item
                  v-for="item in inquiries"
                  :key="item.id"
                  :timestamp="item.createTime"
                  :type="getTimelineType(item.status)"
                  placement="top"
                  :hollow="item.status === 'closed'"
                >
                  <div class="timeline-card" @click="handleViewDetail(item)">
                    <div class="timeline-header">
                      <span class="timeline-title">{{ item.title }}</span>
                      <el-tag :type="getStatusType(item.status)" size="small">{{ getStatusLabel(item.status) }}</el-tag>
                    </div>
                    <p class="timeline-summary">{{ item.content?.substring(0, 80) }}...</p>
                    <div class="timeline-meta">
                      <el-tag size="small" type="info">{{ getCategoryLabel(item.category) }}</el-tag>
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
              <EmptyState v-else title="暂无咨询记录" description="提交您的第一个咨询吧" action-text="立即咨询" @action="activeTab = 'submit'" />
              <div class="pagination-wrapper" v-if="inquiryTotal > pageSize">
                <el-pagination
                  v-model:current-page="currentPage"
                  :page-size="pageSize"
                  :total="inquiryTotal"
                  layout="prev, pager, next"
                  @current-change="fetchInquiries"
                />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="提交咨询" name="submit">
            <div class="submit-section">
              <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px" class="submit-form">
                <el-form-item label="标题" prop="title">
                  <el-input v-model="form.title" placeholder="请输入咨询标题（5-100字）" maxlength="100" show-word-limit />
                </el-form-item>
                <el-form-item label="分类" prop="category">
                  <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                    <el-option label="借阅问题" value="borrow" />
                    <el-option label="归还续借" value="return" />
                    <el-option label="逾期处理" value="overdue" />
                    <el-option label="座位预约" value="seat" />
                    <el-option label="账户问题" value="account" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                  <el-input
                    v-model="form.content"
                    type="textarea"
                    :rows="6"
                    placeholder="请详细描述您的问题（10-500字）..."
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSubmit" :loading="submitting">
                    <el-icon><Promotion /></el-icon>
                    提交咨询
                  </el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </main>
    </div>

    <el-dialog v-model="showDetailDialog" title="咨询详情" width="600px" destroy-on-close>
      <div class="detail-content" v-if="currentInquiry">
        <el-descriptions :column="1" border class="detail-desc">
          <el-descriptions-item label="标题">{{ currentInquiry.title }}</el-descriptions-item>
          <el-descriptions-item label="分类">
            <el-tag size="small">{{ getCategoryLabel(currentInquiry.category) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentInquiry.status)" size="small">{{ getStatusLabel(currentInquiry.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentInquiry.createTime }}</el-descriptions-item>
          <el-descriptions-item label="咨询内容">
            <div class="detail-content-text">{{ currentInquiry.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentInquiry.replies && currentInquiry.replies.length > 0" class="replies-section">
          <h4>回复记录</h4>
          <div v-for="reply in currentInquiry.replies" :key="reply.id" class="reply-item" :class="{ 'is-admin': reply.isAdmin }">
            <div class="reply-header">
              <el-tag :type="reply.isAdmin ? 'warning' : ''" size="small">{{ reply.isAdmin ? '管理员' : '我' }}</el-tag>
              <span class="reply-time">{{ reply.createTime }}</span>
            </div>
            <p class="reply-content">{{ reply.content }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Grid, Reading, RefreshRight, WarningFilled,
  OfficeBuilding, UserFilled, Promotion
} from '@element-plus/icons-vue'
import { inquiryApi } from '@/api/inquiry'
import EmptyState from '@/components/EmptyState.vue'

interface FaqItem {
  id: string
  question: string
  answer: string
  category: string
}

interface InquiryItem {
  id: number
  title: string
  content: string
  category: string
  status: string
  createTime: string
  replies?: Array<{ id: number; content: string; isAdmin: boolean; createTime: string }>
}

const activeCategory = ref('all')
const activeFaq = ref('')
const activeTab = ref('history')
const loading = ref(false)
const submitting = ref(false)
const showDetailDialog = ref(false)
const formRef = ref<FormInstance>()
const currentPage = ref(1)
const pageSize = ref(10)
const inquiryTotal = ref(0)

const inquiries = ref<InquiryItem[]>([])
const currentInquiry = ref<InquiryItem | null>(null)

const form = ref({
  title: '',
  category: '',
  content: ''
})

const formRules: FormRules = {
  title: [
    { required: true, message: '请输入咨询标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在5到100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入咨询内容', trigger: 'blur' },
    { min: 10, max: 500, message: '内容长度在10到500个字符', trigger: 'blur' }
  ]
}

const faqList = ref<FaqItem[]>([
  { id: '1', question: '如何借阅图书？', answer: '登录系统后，在图书列表中找到想借阅的图书，点击"借阅"按钮即可。每人最多可同时借阅10本图书，借阅期限为30天。', category: 'borrow' },
  { id: '2', question: '如何续借图书？', answer: '在"我的借阅"页面找到需要续借的图书，点击"续借"按钮。每本图书可续借2次，每次延长30天。逾期图书无法续借。', category: 'return' },
  { id: '3', question: '图书逾期如何处理？', answer: '逾期将按每天0.1元/本收取滞纳金。请尽快归还或在线缴纳罚款。如遇特殊情况，可提交申诉说明。', category: 'overdue' },
  { id: '4', question: '如何预约座位？', answer: '进入"座位预约"页面，选择日期和时段，在座位图上点击可用座位即可完成预约。每次最长预约4小时。', category: 'seat' },
  { id: '5', question: '忘记密码怎么办？', answer: '点击登录页面的"忘记密码"，通过注册邮箱或手机号验证后可重置密码。也可联系管理员协助处理。', category: 'account' },
  { id: '6', question: '如何修改个人信息？', answer: '进入"个人中心"页面，可以修改头像、昵称、联系方式等信息。修改用户名需联系管理员。', category: 'account' },
  { id: '7', question: '可以推荐图书吗？', answer: '可以！在"图书推荐"页面提交推荐申请，管理员审核后会考虑采购。也可直接在咨询中提出建议。', category: 'borrow' },
  { id: '8', question: '如何查看借阅历史？', answer: '在"我的借阅"页面可以看到当前借阅和历史的全部借阅记录，包括已归还的图书详情。', category: 'return' }
])

const filteredFaqs = computed(() => {
  if (activeCategory.value === 'all') return faqList.value
  return faqList.value.filter(f => f.category === activeCategory.value)
})

const pendingCount = computed(() => inquiries.value.filter(i => i.status === 'pending').length)

const getCategoryLabel = (category: string) => {
  const map: Record<string, string> = {
    borrow: '借阅问题', return: '归还续借', overdue: '逾期处理',
    seat: '座位预约', account: '账户问题', other: '其他'
  }
  return map[category] || '其他'
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', replied: 'success', closed: 'info' }
  return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = { pending: '待处理', replied: '已回复', closed: '已关闭' }
  return map[status] || '未知'
}

const getTimelineType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', replied: 'success', closed: 'info' }
  return (map[status] as any) || 'primary'
}

const handleCategorySelect = (index: string) => {
  activeCategory.value = index
  activeFaq.value = ''
}

const fetchInquiries = async () => {
  loading.value = true
  try {
    const res: any = await inquiryApi.getMyInquiries({
      page: currentPage.value,
      size: pageSize.value
    })
    inquiries.value = (res.records || []).map((item: any) => {
      const replies: any[] = []
      if (item.replyContent) {
        replies.push({
          id: 1,
          content: item.replyContent,
          isAdmin: true,
          createTime: formatDate(item.replyDate)
        })
      }
      return {
        id: item.id,
        title: item.title,
        content: item.content,
        category: item.category,
        status: item.status === 0 ? 'pending' : item.status === 1 ? 'replied' : 'closed',
        createTime: formatDate(item.createTime),
        replies
      }
    })
    inquiryTotal.value = res.total || 0
  } catch (e: any) {
    ElMessage.error(e.message || '获取咨询列表失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string | Date) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await inquiryApi.submitInquiry(form.value)
      ElMessage.success('咨询提交成功')
      form.value = { title: '', category: '', content: '' }
      activeTab.value = 'history'
      fetchInquiries()
    } catch (e: any) {
      ElMessage.error(e.message || '提交失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleReset = () => {
  form.value = { title: '', category: '', content: '' }
  formRef.value?.resetFields()
}

const handleViewDetail = (item: InquiryItem) => {
  currentInquiry.value = item
  showDetailDialog.value = true
}

onMounted(() => {
  fetchInquiries()
})
</script>

<style scoped>
.inquiry-view {
  max-width: 1400px;
  margin: 0 auto;
}

.inquiry-layout {
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 120px);
}

.inquiry-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-section {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.sidebar-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border-light);
}

.faq-menu {
  border-right: none;
}

.faq-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  border-radius: var(--border-radius-base);
  margin-bottom: 4px;
}

.faq-collapse-section {
  max-height: calc(100vh - 400px);
  overflow-y: auto;
}

.faq-title {
  font-size: 13px;
  font-weight: 500;
}

.faq-answer {
  font-size: 13px;
  line-height: 1.8;
  color: var(--color-text-secondary);
  padding: 8px 0;
}

.inquiry-main {
  flex: 1;
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
  min-width: 0;
}

.inquiry-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.tab-badge {
  margin-left: 8px;
}

.timeline-section {
  min-height: 300px;
}

.timeline-card {
  background: var(--color-bg-page);
  border-radius: var(--border-radius-base);
  padding: 14px 18px;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid transparent;
}

.timeline-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
  transform: translateX(4px);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.timeline-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-text-primary);
}

.timeline-summary {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 8px;
  line-height: 1.5;
}

.timeline-meta {
  display: flex;
  gap: 8px;
}

.submit-section {
  max-width: 640px;
  padding: 20px 0;
}

.submit-form {
  padding: 20px;
  background: var(--color-bg-page);
  border-radius: var(--border-radius-lg);
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.detail-content {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-desc {
  margin-bottom: 20px;
}

.detail-content-text {
  white-space: pre-wrap;
  line-height: 1.7;
  font-size: 13px;
}

.replies-section h4 {
  font-size: 15px;
  color: var(--color-text-primary);
  margin: 0 0 12px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border-light);
}

.reply-item {
  padding: 12px 16px;
  border-radius: var(--border-radius-base);
  background: var(--color-bg-page);
  margin-bottom: 10px;
}

.reply-item.is-admin {
  background: #fdf6ec;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.reply-time {
  font-size: 12px;
  color: var(--color-text-placeholder);
}

.reply-content {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

@media (max-width: 900px) {
  .inquiry-layout {
    flex-direction: column;
  }

  .inquiry-sidebar {
    width: 100%;
  }

  .faq-collapse-section {
    max-height: none;
  }
}
</style>
