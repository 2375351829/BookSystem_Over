<template>
  <div class="my-seat-reservations">
    <h2>我的座位预约</h2>
    
    <el-card class="reservations-card">
      <template #header>
        <div class="card-header">
          <span>预约记录</span>
          <el-button type="primary" size="small" @click="$router.push('/seats')">
            预约座位
          </el-button>
        </div>
      </template>
      
      <el-table :data="reservations" v-loading="loading" stripe>
        <el-table-column prop="seatNumber" label="座位号" width="120" />
        <el-table-column prop="roomName" label="阅览室" width="150" />
        <el-table-column prop="roomLocation" label="位置" width="120" />
        <el-table-column label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.reserveDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="80" />
        <el-table-column prop="endTime" label="结束时间" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              size="small" 
              type="success" 
              @click="handleCheckIn(row)"
            >
              签到
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              size="small" 
              type="warning" 
              @click="handleCheckOut(row)"
            >
              签退
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              size="small" 
              type="danger" 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchReservations"
          @current-change="fetchReservations"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { seatApi } from '@/api/seat'

const loading = ref(false)
const reservations = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function fetchReservations() {
  loading.value = true
  try {
    const res: any = await seatApi.getMyReservations(page.value, size.value)
    const outerData = res?.data ?? res
    const pageData = outerData?.records ? outerData : null
    reservations.value = pageData?.records ?? []
    total.value = pageData?.total ?? 0
    if (!Array.isArray(reservations.value)) {
      console.warn('预约数据格式异常', { raw: res, outer: outerData, page: pageData })
      reservations.value = []
    }
  } catch (e: any) {
    console.error('获取预约记录失败:', e)
    ElMessage.error(e?.response?.data?.message || e.message || '获取预约记录失败')
  } finally {
    loading.value = false
  }
}

async function handleCheckIn(row: any) {
  try {
    await seatApi.checkIn(row.id)
    ElMessage.success('签到成功')
    await fetchReservations()
  } catch (e: any) {
    ElMessage.error(e.message || '签到失败')
  }
}

async function handleCheckOut(row: any) {
  try {
    await seatApi.checkOut(row.id)
    ElMessage.success('签退成功')
    await fetchReservations()
  } catch (e: any) {
    ElMessage.error(e.message || '签退失败')
  }
}

async function handleCancel(row: any) {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await seatApi.cancelReservation(row.id)
    ElMessage.success('取消成功')
    await fetchReservations()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '取消失败')
    }
  }
}

function formatDate(date: any) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toISOString().split('T')[0]
}

function getStatusType(status: number) {
  const types: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger', 4: 'info' }
  return types[status] || 'info'
}

function getStatusText(status: number) {
  const texts: Record<number, string> = { 0: '已预约', 1: '已签到', 2: '已签退', 3: '已取消', 4: '已过期' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchReservations()
})
</script>

<style scoped>
.my-seat-reservations {
  padding: 20px;
}

.reservations-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
