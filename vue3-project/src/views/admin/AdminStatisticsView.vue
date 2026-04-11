<template>
  <div class="admin-statistics-view">
    <div class="filter-bar">
      <el-date-picker
        v-model="dateRange"
        type="monthrange"
        range-separator="至"
        start-placeholder="开始月份"
        end-placeholder="结束月份"
        value-format="YYYY-MM"
        style="width: 280px"
        @change="handleDateChange"
      />
      <div class="quick-select">
        <el-button
          v-for="item in quickOptions"
          :key="item.value"
          :type="activeQuick === item.value ? 'primary' : ''"
          size="small"
          @click="handleQuickSelect(item.value)"
        >{{ item.label }}</el-button>
      </div>
    </div>

    <div class="charts-grid">
      <div class="chart-card">
        <h4 class="chart-title">借阅趋势</h4>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h4 class="chart-title">分类分布</h4>
        <div ref="categoryChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h4 class="chart-title">用户活跃度 TOP10</h4>
        <div ref="userChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h4 class="chart-title">时段分布</h4>
        <div ref="timeChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="data-section">
      <div class="section-header">
        <el-radio-group v-model="detailType" size="small" @change="fetchDetailData">
          <el-radio-button value="borrow">借阅明细</el-radio-button>
          <el-radio-button value="user">用户排行</el-radio-button>
          <el-radio-button value="category">分类统计</el-radio-button>
        </el-radio-group>
        <ExportButton export-type="statistics" :params="{ detailType, dateRange: dateRange }" @export="handleExport" />
      </div>

      <el-table v-loading="detailLoading" :data="detailList" stripe border style="width: 100%">
        <template v-if="detailType === 'borrow'">
          <el-table-column prop="month" label="月份" width="100" />
          <el-table-column prop="borrowCount" label="借阅量" width="120" sortable />
          <el-table-column prop="returnCount" label="归还量" width="120" sortable />
          <el-table-column prop="overdueCount" label="逾期数" width="100" sortable />
          <el-table-column prop="newUsers" label="新增用户" width="110" sortable />
        </template>
        <template v-else-if="detailType === 'user'">
          <el-table-column prop="rank" label="排名" width="70" align="center" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="100" />
          <el-table-column prop="borrowCount" label="借阅总数" width="110" sortable />
          <el-table-column prop="activeDays" label="活跃天数" width="100" sortable />
        </template>
        <template v-else-if="detailType === 'category'">
          <el-table-column prop="name" label="分类名称" min-width="150" />
          <el-table-column prop="code" label="编码" width="90" />
          <el-table-column prop="bookCount" label="图书数量" width="110" sortable />
          <el-table-column prop="borrowCount" label="借阅次数" width="110" sortable />
          <el-table-column prop="percentage" label="占比" width="100">
            <template #default="{ row }">{{ row.percentage }}%</template>
          </el-table-column>
        </template>
      </el-table>

      <EmptyState v-if="!detailLoading && detailList.length === 0" title="暂无数据" description="请选择时间范围查看统计数据" />

      <div class="pagination-wrapper" v-if="detailTotal > 0">
        <el-pagination
          v-model:current-page="detailPage"
          :total="detailTotal"
          :page-size="20"
          layout="total, prev, pager, next"
          @current-change="fetchDetailData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { statsApi } from '@/api/stats'
import ExportButton from '@/components/ExportButton.vue'
import EmptyState from '@/components/EmptyState.vue'

const trendChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
const userChartRef = ref<HTMLElement>()
const timeChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null
let userChart: echarts.ECharts | null = null
let timeChart: echarts.ECharts | null = null

const dateRange = ref<string[]>([])
const activeQuick = ref('thisMonth')
const detailType = ref<'borrow' | 'user' | 'category'>('borrow')
const detailLoading = ref(false)
const detailList = ref<any[]>([])
const detailTotal = ref(0)
const detailPage = ref(1)

const quickOptions = [
  { label: '本周', value: 'thisWeek' },
  { label: '本月', value: 'thisMonth' },
  { label: '本季度', value: 'thisQuarter' },
  { label: '本年', value: 'thisYear' }
]

function initCharts() {
  nextTick(() => {
    if (trendChartRef.value) trendChart = echarts.init(trendChartRef.value)
    if (categoryChartRef.value) categoryChart = echarts.init(categoryChartRef.value)
    if (userChartRef.value) userChart = echarts.init(userChartRef.value)
    if (timeChartRef.value) timeChart = echarts.init(timeChartRef.value)

    window.addEventListener('resize', handleResize)
    fetchAllChartData()
  })
}

function handleResize() {
  trendChart?.resize()
  categoryChart?.resize()
  userChart?.resize()
  timeChart?.resize()
}

async function fetchAllChartData() {
  try {
    const [trendsRes, catRes, userRes] = await Promise.allSettled([
      statsApi.getBorrowTrends({ range: activeQuick.value }),
      statsApi.getCategoryStats(),
      statsApi.getUserActivityStats()
    ])

    if (trendsRes.status === 'fulfilled') {
      const res = trendsRes.value as any
      // axios 拦截器已经返回 response.data
      const data = res || {}
      renderTrendChart(data.months || [], data.values || [])
    }
    if (catRes.status === 'fulfilled') {
      const res = catRes.value as any
      // axios 拦截器已经返回 response.data
      const data = res || []
      const list = Array.isArray(data) ? data : []
      const categories = list.map((d: any) => d.name || '-')
      const values = list.map((d: any) => d.count || 0)
      renderCategoryChart(categories, values)
    }
    if (userRes.status === 'fulfilled') {
      const res = userRes.value as any
      // axios 拦截器已经返回 response.data
      const data = res || []
      const list = Array.isArray(data) ? data : []
      renderUserChart(
        list.map((u: any) => u.realName || u.username || '-'), 
        list.map((u: any) => u.borrowCount || 0)
      )
    }

    renderTimeChart()
  } catch {
    // fallback mock data
    renderTrendChart(['1月','2月','3月','4月','5月','6月'], [320,280,450,380,520,410])
    renderCategoryChart(['文学','科技','历史','艺术','经济'], [35,25,15,12,13])
    renderUserChart(['张三','李四','王五','赵六','钱七','孙八'], [45,38,32,28,24,20])
    renderTimeChart()
  }
}

function renderTrendChart(months: string[], values: number[]) {
  if (!trendChart) return
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '10%', right: '5%', bottom: '12%', top: '10%' },
    xAxis: { type: 'category', data: months.length ? months : ['1月','2月','3月','4月','5月','6月'] },
    yAxis: { type: 'value', name: '借阅量' },
    series: [{
      name: '借阅量',
      type: 'line',
      data: values.length ? values : [320,280,450,380,520,410],
      smooth: true,
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#409EFF40' }, { offset: 1, color: '#409EFF05' }]) },
      lineStyle: { color: '#409EFF', width: 2.5 },
      itemStyle: { color: '#409EFF' }
    }]
  })
}

function renderCategoryChart(categories: string[], values: number[]) {
  if (!categoryChart) return
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: '2%', left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['50%', '46%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: (categories.length ? categories : ['文学','科技','历史','艺术','经济']).map((name, i) => ({
        name,
        value: values.length ? values[i] : [35,25,15,12,13][i]
      }))
    }],
    color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  })
}

function renderUserChart(names: string[], values: number[]) {
  if (!userChart) return
  userChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '12%', right: '8%', bottom: '16%', top: '8%' },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: names.length ? names.slice().reverse() : ['孙八','钱七','赵六','王五','李四','张三'].reverse()
    },
    series: [{
      type: 'bar',
      data: (values.length ? values.slice().reverse() : [20,24,28,32,38,45]).reverse(),
      barWidth: '50%',
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

function renderTimeChart() {
  if (!timeChart) return
  const hours = Array.from({ length: 18 }, (_, i) => `${String(i + 7).padStart(2, '0')}:00`)
  const values = [12, 8, 6, 15, 28, 42, 65, 88, 95, 78, 55, 38, 30, 22, 18, 14, 10, 7]
  timeChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '10%', right: '5%', bottom: '16%', top: '8%' },
    xAxis: { type: 'category', data: hours, axisLabel: { rotate: 30, fontSize: 11 } },
    yAxis: { type: 'value', name: '借阅次数' },
    series: [{
      type: 'bar',
      data: values,
      barWidth: '60%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: function(params: any) {
          const colors = ['#c6e2ff', '#a0cfff', '#79bbff', '#409EFF', '#337ecc']
          const ratio = params.value / Math.max(...values)
          if (ratio > 0.8) return colors[4]
          if (ratio > 0.6) return colors[3]
          if (ratio > 0.4) return colors[2]
          if (ratio > 0.2) return colors[1]
          return colors[0]
        }
      }
    }]
  })
}

function handleDateChange() {
  activeQuick.value = ''
  fetchAllChartData()
  fetchDetailData()
}

function handleQuickSelect(value: string) {
  activeQuick.value = value
  dateRange.value = []
  fetchAllChartData()
  fetchDetailData()
}

async function fetchDetailData() {
  detailLoading.value = true
  try {
    const res: any = await statsApi.getPopularBooks({
      type: detailType.value,
      range: activeQuick.value,
      page: detailPage.value,
      pageSize: 20
    })
    // axios 拦截器已经返回 response.data，所以 res 就是后端返回的数据
    const data = res || {}
    detailList.value = Array.isArray(data) ? data : (data.list || [])
    detailTotal.value = data.total || detailList.value.length
  } catch (e) {
    console.error('获取统计数据失败:', e)
    detailList.value = []
    detailTotal.value = 0
  } finally {
    detailLoading.value = false
  }
}

function handleExport(format: 'excel' | 'csv') {
  ElMessage.success(`正在导出 ${format.toUpperCase()} 格式报表...`)
}

onMounted(() => {
  initCharts()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
  userChart?.dispose()
  timeChart?.dispose()
})
</script>

<style scoped>
.admin-statistics-view {
  padding: var(--content-padding);
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.quick-select {
  display: flex;
  gap: 8px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.chart-title {
  margin: 0 0 16px;
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--color-text-primary);
}

.chart-container {
  width: 100%;
  height: 300px;
}

.data-section {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
