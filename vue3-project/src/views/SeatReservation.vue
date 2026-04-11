<template>
  <div class="seat-reservation-container fade-in">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon">
            <el-icon><Location /></el-icon>
          </div>
          <div>
            <h1>座位预约</h1>
            <p>选择阅览室和座位，开始您的学习之旅</p>
          </div>
        </div>
      </div>
    </div>

    <div class="reservation-content">
      <div class="left-panel">
        <div class="selection-card">
          <h3 class="card-title">
            <el-icon><Setting /></el-icon>
            预约设置
          </h3>
          <el-form :model="reservationForm" label-width="80px" class="reservation-form">
            <el-form-item label="阅览室">
              <el-select 
                v-model="selectedRoomId" 
                placeholder="请选择阅览室"
                style="width: 100%"
                @change="handleRoomChange"
              >
                <el-option
                  v-for="room in seatStore.rooms"
                  :key="room.id"
                  :label="room.name"
                  :value="room.id"
                >
                  <div class="room-option">
                    <span>{{ room.name }}</span>
                    <span class="room-location">{{ room.location }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker
                v-model="selectedDate"
                type="date"
                placeholder="选择日期"
                :disabled-date="disabledDate"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="handleDateChange"
              />
            </el-form-item>
            <el-form-item label="时间段">
              <div class="time-range">
                <el-time-select
                  v-model="reservationForm.startTime"
                  :max-time="reservationForm.endTime"
                  placeholder="开始时间"
                  start="08:00"
                  step="00:30"
                  end="22:00"
                  style="width: 48%"
                />
                <span class="time-separator">至</span>
                <el-time-select
                  v-model="reservationForm.endTime"
                  :min-time="reservationForm.startTime"
                  placeholder="结束时间"
                  start="08:00"
                  step="00:30"
                  end="22:00"
                  style="width: 48%"
                />
              </div>
            </el-form-item>
          </el-form>
        </div>

        <div class="seat-map-card">
          <div class="card-header">
            <h3 class="card-title">
              <el-icon><Grid /></el-icon>
              座位布局
            </h3>
            <div class="legend">
              <div class="legend-item">
                <span class="seat-dot available"></span>
                <span>可用</span>
              </div>
              <div class="legend-item">
                <span class="seat-dot reserved"></span>
                <span>已预约</span>
              </div>
              <div class="legend-item">
                <span class="seat-dot occupied"></span>
                <span>使用中</span>
              </div>
              <div class="legend-item">
                <span class="seat-dot selected"></span>
                <span>已选中</span>
              </div>
              <div class="legend-item">
                <span class="seat-dot maintenance"></span>
                <span>维护中</span>
              </div>
            </div>
          </div>
          
          <div v-loading="seatStore.loading" class="seat-map-wrapper">
            <div v-if="seatStore.seats.length > 0" class="seat-map">
              <div class="seat-row" v-for="row in seatRows" :key="row">
                <div
                  v-for="seat in getSeatsByRow(row)"
                  :key="seat.id"
                  class="seat-item"
                  :class="getSeatClass(seat)"
                  @click="handleSeatClick(seat)"
                >
                  <span class="seat-number">{{ seat.seatNumber }}</span>
                  <el-icon v-if="seat.hasSocket" class="seat-icon"><Connection /></el-icon>
                  <el-icon v-if="seat.nearWindow" class="seat-icon window"><Sunny /></el-icon>
                </div>
              </div>
              <div class="entrance">入口</div>
            </div>
            <div v-else class="empty-seat-map">
              <el-icon class="empty-icon"><OfficeBuilding /></el-icon>
              <p>请先选择阅览室和日期</p>
            </div>
          </div>

          <div class="selected-seat-info" v-if="selectedSeat">
            <div class="info-item">
              <span class="label">已选座位：</span>
              <span class="value">{{ selectedSeat.seatNumber }}</span>
            </div>
            <div class="info-item">
              <span class="label">座位特性：</span>
              <span class="value">
                <el-tag v-if="selectedSeat.hasSocket" size="small" type="warning">有插座</el-tag>
                <el-tag v-if="selectedSeat.nearWindow" size="small" type="info">靠窗</el-tag>
              </span>
            </div>
            <el-button 
              type="primary" 
              class="reserve-btn"
              :loading="reserving"
              :disabled="!canReserve"
              @click="handleReserve"
            >
              <el-icon><Check /></el-icon>
              确认预约
            </el-button>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="my-reservations-card">
          <h3 class="card-title">
            <el-icon><List /></el-icon>
            我的预约
          </h3>
          <div v-loading="loadingReservations" class="reservations-list">
            <div 
              v-for="reservation in myReservations" 
              :key="reservation.id" 
              class="reservation-item"
            >
              <div class="reservation-header">
                <span class="room-name">{{ reservation.roomName }}</span>
                <el-tag :type="getReservationStatusType(reservation.status)" size="small">
                  {{ getReservationStatusText(reservation.status) }}
                </el-tag>
              </div>
              <div class="reservation-body">
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span>座位：{{ reservation.seatNumber }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Calendar /></el-icon>
                  <span>日期：{{ reservation.reserveDate }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Clock /></el-icon>
                  <span>时间：{{ reservation.startTime }} - {{ reservation.endTime }}</span>
                </div>
              </div>
              <div class="reservation-actions">
                <el-button
                  v-if="canCheckIn(reservation)"
                  type="success"
                  size="small"
                  :loading="actionId === reservation.id"
                  @click="handleCheckIn(reservation.id)"
                >
                  签到
                </el-button>
                <el-button
                  v-if="canCheckOut(reservation)"
                  type="warning"
                  size="small"
                  :loading="actionId === reservation.id"
                  @click="handleCheckOut(reservation.id)"
                >
                  签退
                </el-button>
                <el-button
                  v-if="canCancel(reservation)"
                  type="danger"
                  size="small"
                  :loading="actionId === reservation.id"
                  @click="handleCancel(reservation.id)"
                >
                  取消
                </el-button>
              </div>
            </div>
            <div v-if="myReservations.length === 0" class="empty-reservations">
              <el-icon><Document /></el-icon>
              <p>暂无预约记录</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, Setting, Grid, Check, List, Calendar, Clock,
  Connection, Sunny, OfficeBuilding, Document
} from '@element-plus/icons-vue'
import { useSeatStore } from '@/stores/seat'
import type { Seat, SeatReservation as SeatReservationType } from '@/api/seat'

const seatStore = useSeatStore()

const selectedRoomId = ref<number | null>(null)
const selectedDate = ref<string>('')
const selectedSeat = ref<Seat | null>(null)
const reserving = ref(false)
const loadingReservations = ref(false)
const actionId = ref<number | null>(null)
const myReservations = ref<SeatReservationType[]>([])

const reservationForm = ref({
  startTime: '08:00',
  endTime: '12:00'
})

const seatRows = computed(() => {
  const rows = new Set(seatStore.seats.map(s => s.row))
  return Array.from(rows).sort((a, b) => a - b)
})

const canReserve = computed(() => {
  return selectedSeat.value && 
         selectedSeat.value.status === 0 &&
         reservationForm.value.startTime &&
         reservationForm.value.endTime
})

const disabledDate = (date: Date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

const getSeatsByRow = (row: number) => {
  return seatStore.seats
    .filter((s: any) => s.row === row)
    .sort((a: any, b: any) => a.col - b.col)
}

const getSeatStatus = (seat: any) => {
  const statusMap: Record<string, string> = {
    'available': 'available',
    'reserved': 'reserved',
    'occupied': 'occupied',
    'maintenance': 'maintenance',
    '0': 'available',
    '1': 'reserved',
    '2': 'maintenance'
  }
  return statusMap[String(seat.status)] || 'available'
}

const getSeatClass = (seat: any) => {
  const baseClass = `seat--${getSeatStatus(seat)}`
  return selectedSeat.value?.id === seat.id ? `${baseClass} selected` : baseClass
}

const getReservationStatusType = (status: string | number) => {
  const statusMap: Record<string, string> = {
    '0': 'warning',
    '1': 'success',
    '2': 'info',
    '3': 'danger',
    '4': 'danger',
    'reserved': 'warning',
    'checked_in': 'success',
    'checked_out': 'info',
    'cancelled': 'danger',
    'expired': 'danger'
  }
  return statusMap[String(status)] || 'info'
}

const getReservationStatusText = (status: string | number) => {
  const statusMap: Record<string, string> = {
    '0': '已预约',
    '1': '已签到',
    '2': '已签退',
    '3': '已取消',
    '4': '已过期',
    'reserved': '已预约',
    'checked_in': '已签到',
    'checked_out': '已签退',
    'cancelled': '已取消',
    'expired': '已过期'
  }
  return statusMap[String(status)] || '未知'
}

const canCheckIn = (reservation: SeatReservationType) => {
  const statusStr = String(reservation.status)
  return statusStr === '0' || statusStr === 'reserved'
}

const canCheckOut = (reservation: SeatReservationType) => {
  const statusStr = String(reservation.status)
  return statusStr === '1' || statusStr === 'checked_in'
}

const canCancel = (reservation: SeatReservationType) => {
  const statusStr = String(reservation.status)
  return statusStr === '0' || statusStr === 'reserved'
}

const handleRoomChange = async () => {
  selectedSeat.value = null
  if (selectedRoomId.value && selectedDate.value) {
    await fetchSeats()
  }
}

const handleDateChange = async () => {
  selectedSeat.value = null
  if (selectedRoomId.value && selectedDate.value) {
    await fetchSeats()
  }
}

const fetchSeats = async () => {
  if (!selectedRoomId.value || !selectedDate.value) return
  try {
    await seatStore.fetchRoomSeats(selectedRoomId.value, selectedDate.value)
  } catch (error) {
    ElMessage.error('获取座位信息失败')
  }
}

const handleSeatClick = (seat: any) => {
  const status = typeof seat.status === 'number' ? seat.status :
    ({ available: 0, reserved: 1, occupied: 1, maintenance: 2 }[String(seat.status)] ?? 0)
  if (status === 0) {
    selectedSeat.value = seat
  } else if (status === 1) {
    ElMessage.warning('该座位已被预约或使用中')
  } else if (status === 2) {
    ElMessage.warning('该座位正在维护中')
  }
}

const handleReserve = async () => {
  if (!selectedSeat.value || !selectedDate.value) return

  try {
    await ElMessageBox.confirm(
      `确认预约 ${selectedSeat.value.seatNumber} 座位？\n日期：${selectedDate.value}\n时间：${reservationForm.value.startTime} - ${reservationForm.value.endTime}`,
      '确认预约',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    reserving.value = true
    const result = await seatStore.reserveSeat({
      seatId: selectedSeat.value.id,
      reservationDate: selectedDate.value,
      startTime: reservationForm.value.startTime,
      endTime: reservationForm.value.endTime
    })
    ElMessage.success('预约成功')
    selectedSeat.value = null
    myReservations.value = [...seatStore.reservations]
    fetchSeats()
    fetchMyReservations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '预约失败')
    }
  } finally {
    reserving.value = false
  }
}

const handleCheckIn = async (id: number) => {
  try {
    actionId.value = id
    await seatStore.checkIn(id)
    ElMessage.success('签到成功')
    await fetchMyReservations()
  } catch (error: any) {
    ElMessage.error(error.message || '签到失败')
  } finally {
    actionId.value = null
  }
}

const handleCheckOut = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认签退？', '签退确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    actionId.value = id
    await seatStore.checkOut(id)
    ElMessage.success('签退成功')
    await fetchMyReservations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '签退失败')
    }
  } finally {
    actionId.value = null
  }
}

const handleCancel = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认取消该预约？', '取消预约', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    actionId.value = id
    await seatStore.cancelReservation(id)
    ElMessage.success('取消成功')
    await fetchMyReservations()
    if (selectedRoomId.value && selectedDate.value) {
      await fetchSeats()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消失败')
    }
  } finally {
    actionId.value = null
  }
}

const fetchMyReservations = async () => {
  loadingReservations.value = true
  try {
    await seatStore.fetchMyReservations(1, 10)
    myReservations.value = seatStore.reservations
  } catch (error) {
    console.error('获取预约记录失败', error)
  } finally {
    loadingReservations.value = false
  }
}

const initDefaultDate = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  selectedDate.value = `${year}-${month}-${day}`
}

onMounted(async () => {
  initDefaultDate()
  await seatStore.fetchRooms()
  await fetchMyReservations()
})

watch(selectedRoomId, () => {
  if (selectedRoomId.value && selectedDate.value) {
    fetchSeats()
  }
})
</script>

<style scoped>
.seat-reservation-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: var(--radius-xl);
  padding: 32px 40px;
  margin-bottom: 32px;
  box-shadow: var(--shadow-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.title-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
}

.title-section h1 {
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  margin: 0 0 4px 0;
}

.title-section p {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.reservation-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.selection-card,
.seat-map-card,
.my-reservations-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header .card-title {
  margin: 0;
}

.room-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.room-location {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.reservation-form {
  margin-top: 16px;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.time-separator {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.legend {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.seat-dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.seat-dot.available {
  background: #48bb78;
}

.seat-dot.reserved {
  background: #f56565;
}

.seat-dot.occupied {
  background: #ed8936;
}

.seat-dot.selected {
  background: var(--primary);
}

.seat-dot.maintenance {
  background: #a0aec0;
}

.seat-map-wrapper {
  min-height: 300px;
}

.seat-map {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: #f7fafc;
  border-radius: var(--radius-md);
  position: relative;
}

.seat-row {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.seat-item {
  width: 60px;
  height: 50px;
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border: 2px solid transparent;
}

.seat-item.seat--available {
  background: #c6f6d5;
  color: #22543d;
}

.seat-item.seat--available:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(72, 187, 120, 0.3);
}

.seat-item.seat--reserved {
  background: #fed7d7;
  color: #822727;
  cursor: not-allowed;
}

.seat-item.seat--occupied {
  background: #feebc8;
  color: #744210;
  cursor: not-allowed;
}

.seat-item.seat--selected {
  background: var(--primary);
  color: white;
  border-color: var(--primary-dark);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(49, 130, 206, 0.4);
}

.seat-item.seat--maintenance {
  background: #e2e8f0;
  color: #4a5568;
  cursor: not-allowed;
}

.seat-number {
  font-size: 0.85rem;
  font-weight: 600;
}

.seat-icon {
  font-size: 0.7rem;
  position: absolute;
  top: 2px;
  right: 2px;
  color: #ed8936;
}

.seat-icon.window {
  left: 2px;
  right: auto;
  color: #4299e1;
}

.entrance {
  position: absolute;
  bottom: 5px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.8rem;
  color: var(--text-secondary);
  background: white;
  padding: 2px 12px;
  border-radius: 4px;
  border: 1px dashed var(--border);
}

.empty-seat-map {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: var(--text-muted);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 12px;
}

.selected-seat-info {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.info-item .label {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.info-item .value {
  font-weight: 500;
  color: var(--text-primary);
}

.reserve-btn {
  width: 100%;
  margin-top: 8px;
  height: 44px;
  font-size: 1rem;
}

.reservations-list {
  max-height: 500px;
  overflow-y: auto;
}

.reservation-item {
  background: #f7fafc;
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid var(--border);
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.room-name {
  font-weight: 600;
  color: var(--text-primary);
}

.reservation-body {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.info-row .el-icon {
  color: var(--primary);
}

.reservation-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-reservations {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-muted);
}

.empty-reservations .el-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
}

@media (max-width: 1200px) {
  .reservation-content {
    grid-template-columns: 1fr;
  }
  
  .right-panel {
    order: -1;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px;
  }
  
  .seat-map {
    padding: 12px;
  }
  
  .seat-item {
    width: 50px;
    height: 42px;
  }
  
  .legend {
    gap: 8px;
  }
  
  .legend-item {
    font-size: 0.75rem;
  }
}
</style>
