<template>
  <div class="dashboard-view">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><DataAnalysis /></el-icon>
        管理员仪表盘
      </h1>
      <p class="page-desc">图书馆运营数据总览</p>
    </div>

    <div class="stats-row">
      <StatCard title="总藏书量" :value="dashboardData.totalBooks || 0" icon="Document" color="primary" />
      <StatCard title="今日借阅" :value="dashboardData.todayBorrows || 0" icon="Notebook" color="success" />
      <StatCard title="今日归还" :value="dashboardData.todayReturns || 0" icon="Switch" color="warning" />
      <StatCard title="逾期未还" :value="dashboardData.overdueCount || 0" icon="WarningFilled" color="danger" />
      <StatCard title="活跃用户" :value="dashboardData.activeUsers || 0" icon="UserFilled" color="info" />
    </div>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">借阅趋势</span>
          </template>
          <div ref="borrowTrendRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">分类分布</span>
          </template>
          <div ref="categoryPieRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">热门图书 Top10</span>
          </template>
          <div ref="hotBooksRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">待处理事项</span>
          </template>
          <div class="timeline-wrapper">
            <el-timeline v-if="pendingItems.length > 0">
              <el-timeline-item
                v-for="item in pendingItems"
                :key="item.id"
                :timestamp="item.time"
                :type="item.type || undefined"
                placement="top"
              >
                <div class="pending-item">
                  <span class="pending-text">{{ item.text }}</span>
                  <el-tag :type="item.tagType || 'info'" size="small">{{ item.tag }}</el-tag>
                </div>
              </el-timeline-item>
            </el-timeline>
            <EmptyState v-else title="暂无待办事项" :icon-size="48" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="quick-actions-card">
      <template #header>
        <span class="card-title">快捷操作</span>
      </template>
      <div class="quick-actions-grid">
        <div class="action-btn" @click="$router.push('/admin/books/add')">
          <div class="action-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF)">
            <el-icon :size="28"><Plus /></el-icon>
          </div>
          <span>新增图书</span>
        </div>
        <div class="action-btn" @click="$router.push('/admin/books/import')">
          <div class="action-icon" style="background: linear-gradient(135deg, #67C23A, #85CE61)">
            <el-icon :size="28"><Upload /></el-icon>
          </div>
          <span>批量导入</span>
        </div>
        <div class="action-btn" @click="$router.push('/admin/users')">
          <div class="action-icon" style="background: linear-gradient(135deg, #E6A23C, #F0C78A)">
            <el-icon :size="28"><User /></el-icon>
          </div>
          <span>用户管理</span>
        </div>
        <div class="action-btn" @click="$router.push('/statistics')">
          <div class="action-icon" style="background: linear-gradient(135deg, #909399, #B1B3B8)">
            <el-icon :size="28"><TrendCharts /></el-icon>
          </div>
          <span>查看报表</span>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="activity-table-card">
      <template #header>
        <span class="card-title">用户活跃度 Top10</span>
      </template>
      <el-table :data="hotUsersList" stripe size="small" style="width: 100%" v-loading="usersLoading">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center" sortable />
        <el-table-column prop="role" label="用户类型" width="100" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import {
  DataAnalysis, Plus, Upload, User, TrendCharts
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statsApi } from '@/api/stats'
import StatCard from '@/components/StatCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const borrowTrendRef = ref<HTMLElement>()
const categoryPieRef = ref<HTMLElement>()
const hotBooksRef = ref<HTMLElement>()

let borrowChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null
let hotBooksChart: echarts.ECharts | null = null

const dashboardData = reactive({
  totalBooks: 0,
  todayBorrows: 0,
  todayReturns: 0,
  overdueCount: 0,
  activeUsers: 0,
  availableBooks: 0
})

const pendingItems = ref<any[]>([])
const hotUsersList = ref<any[]>([])
const usersLoading = ref(false)
const categoryStats = ref<Record<string, number>>({})
const hotBooksData = ref<any[]>([])
const borrowTrendData = ref<{ dates: string[], borrows: number[], returns: number[] }>({
  dates: [], borrows: [], returns: []
})

const fetchDashboardData = async () => {
  try {
    const res: any = await statsApi.getDashboardData()
    Object.assign(dashboardData, res)

    await fetchPendingItems()
  } catch (e: any) {
    console.error('获取仪表盘数据失败:', e)
  }
}

const fetchPendingItems = async () => {
  const items: any[] = []
  let id = 1

  if ((dashboardData.overdueCount || 0) > 0) {
    items.push({
      id: id++,
      text: `${dashboardData.overdueCount} 本图书逾期未还`,
      time: '今日',
      type: 'danger',
      tagType: 'danger',
      tag: '逾期提醒'
    })
  }

  if ((dashboardData.todayBorrows || 0) > 0) {
    items.push({
      id: id++,
      text: `今日借阅 ${dashboardData.todayBorrows} 本`,
      time: '今日',
      type: 'primary',
      tagType: 'primary',
      tag: '借阅'
    })
  }

  if ((dashboardData.todayReturns || 0) > 0) {
    items.push({
      id: id++,
      text: `今日归还 ${dashboardData.todayReturns} 本`,
      time: '今日',
      type: 'success',
      tagType: 'success',
      tag: '归还'
    })
  }

  if ((dashboardData.totalBooks || 0) > 0 && (dashboardData.availableBooks || dashboardData.totalBooks) < (dashboardData.totalBooks || 0)) {
    const borrowedCount = (dashboardData.totalBooks || 0) - ((dashboardData.availableBooks || 0))
    items.push({
      id: id++,
      text: `当前在借图书 ${borrowedCount} 本`,
      time: '当前',
      type: 'warning',
      tagType: 'warning',
      tag: '在借'
    })
  }

  pendingItems.value = items
}

const fetchBorrowTrends = async () => {
  try {
    const res: any = await statsApi.getBorrowTrends()
    if (res.borrowTrend && Array.isArray(res.borrowTrend)) {
      const dates: string[] = []
      const borrows: number[] = []
      const returns: number[] = []

      res.borrowTrend.forEach((item: any) => {
        const date = new Date(item.date)
        dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
        borrows.push(item.count || 0)
      })

      if (res.returnTrend && Array.isArray(res.returnTrend)) {
        res.returnTrend.forEach((item: any) => {
          returns.push(item.count || 0)
        })
      }

      borrowTrendData.value = { dates, borrows, returns }
    }
    initBorrowTrendChart()
  } catch (e) {
    initBorrowTrendChart()
  }
}

const fetchCategoryStats = async () => {
  try {
    const res: any = await statsApi.getCategoryStats()
    if (res.categoryStatistics) {
      categoryStats.value = res.categoryStatistics
    } else if (res.categories) {
      categoryStats.value = res.categories
    }
    initCategoryPieChart()
  } catch (e) {
    initCategoryPieChart()
  }
}

const fetchHotBooks = async () => {
  try {
    const res: any = await statsApi.getPopularBooks({ limit: 10 })
    hotBooksData.value = Array.isArray(res) ? res : (res.data || [])
    initHotBooksChart()
  } catch (e) {
    initHotBooksChart()
  }
}

const fetchHotUsers = async () => {
  usersLoading.value = true
  try {
    const res: any = await statsApi.getHotUsers({ limit: 10 })
    hotUsersList.value = Array.isArray(res) ? res.slice(0, 10) : []
  } catch (e) {
    hotUsersList.value = []
  } finally {
    usersLoading.value = false
  }
}

const initBorrowTrendChart = () => {
  if (!borrowTrendRef.value) return
  borrowChart = echarts.init(borrowTrendRef.value)

  const dates = borrowTrendData.value.dates.length > 0
    ? borrowTrendData.value.dates
    : Array.from({ length: 30 }, (_, i) => {
        const d = new Date()
        d.setDate(d.getDate() - (29 - i))
        return `${d.getMonth() + 1}/${d.getDate()}`
      })

  const borrows = borrowTrendData.value.borrows.length > 0
    ? borrowTrendData.value.borrows
    : Array.from({ length: 30 }, () => Math.floor(Math.random() * 80) + 20)

  const returns = borrowTrendData.value.returns.length > 0
    ? borrowTrendData.value.returns
    : Array.from({ length: 30 }, () => Math.floor(Math.random() * 70) + 15)

  borrowChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['借阅量', '归还量'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '14%', top: '8%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: dates, axisLabel: { fontSize: 11 } },
    yAxis: [
      { type: 'value', name: '借阅量', position: 'left', splitLine: { lineStyle: { type: 'dashed' } } },
      { type: 'value', name: '归还量', position: 'right', splitLine: { show: false } }
    ],
    series: [
      { name: '借阅量', type: 'line', smooth: true, data: borrows, itemStyle: { color: '#409EFF' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.3)' }, { offset: 1, color: 'rgba(64,158,255,0.02)' }]) } },
      { name: '归还量', type: 'line', smooth: true, yAxisIndex: 1, data: returns, itemStyle: { color: '#67C23A' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(103,194,58,0.25)' }, { offset: 1, color: 'rgba(103,194,58,0.02)' }]) } }
    ]
  })
}

const initCategoryPieChart = () => {
  if (!categoryPieRef.value) return
  categoryChart = echarts.init(categoryPieRef.value)

  const stats = categoryStats.value
  const data = Object.keys(stats || {}).length > 0
    ? Object.entries(stats).map(([name, value]) => ({ name, value }))
    : [
        { value: 1048, name: '计算机科学' },
        { value: 735, name: '文学小说' },
        { value: 580, name: '历史传记' },
        { value: 484, name: '经济管理' },
        { value: 300, name: '哲学心理' },
        { value: 200, name: '其他' }
      ]

  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['38%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      labelLine: { show: false },
      data,
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#b39ddb']
    }]
  })
}

const initHotBooksChart = () => {
  if (!hotBooksRef.value) return
  hotBooksChart = echarts.init(hotBooksRef.value)

  const books = hotBooksData.value.length > 0
    ? hotBooksData.value.map((b: any) => b.title || b.bookTitle || '未知书名')
    : [
        'JavaScript高级程序设计', 'Vue.js实战', 'Python编程从入门到实践',
        '深入理解计算机系统', '算法导论', '设计模式',
        '代码整洁之道', '重构：改善既有代码', '人类简史', '三体'
      ]

  const values = hotBooksData.value.length > 0
    ? hotBooksData.value.map((b: any) => b.borrowCount || 0)
    : [186, 165, 148, 132, 118, 105, 95, 88, 78, 70]

  hotBooksChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '4%', top: '4%', containLabel: true },
    xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
    yAxis: {
      type: 'category',
      data: books.reverse(),
      axisLabel: { fontSize: 11, width: 140, overflow: 'truncate' }
    },
    series: [{
      type: 'bar',
      data: values.reverse(),
      barWidth: '55%',
      itemStyle: {
        borderRadius: [0, 4, 4, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#66B1FF' }
        ])
      }
    }]
  })
}

const handleResize = () => {
  borrowChart?.resize()
  categoryChart?.resize()
  hotBooksChart?.resize()
}

onMounted(async () => {
  await fetchDashboardData()
  await fetchBorrowTrends()
  await fetchCategoryStats()
  await fetchHotBooks()
  await fetchHotUsers()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  borrowChart?.dispose()
  categoryChart?.dispose()
  hotBooksChart?.dispose()
})
</script>

<style scoped>
.dashboard-view {
  max-width: 1300px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
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

.stats-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: var(--border-radius-lg);
  margin-bottom: 0;
}

.chart-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.chart-container {
  height: 320px;
  width: 100%;
}

.timeline-wrapper {
  max-height: 300px;
  overflow-y: auto;
  padding-right: 4px;
}

.pending-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.pending-text {
  font-size: 13px;
  color: var(--color-text-primary);
}

.quick-actions-card {
  border-radius: var(--border-radius-lg);
  margin-bottom: 20px;
}

.quick-actions-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  border-radius: var(--border-radius-base);
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--color-bg-page);
}

.action-btn:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.action-btn span {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.activity-table-card {
  border-radius: var(--border-radius-lg);
}

.activity-table-card :deep(.el-card__header) {
  padding: 14px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

@media (max-width: 1100px) {
  .stats-row {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }

  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr 1fr;
  }

  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-container {
    height: 260px;
  }
}
</style>
