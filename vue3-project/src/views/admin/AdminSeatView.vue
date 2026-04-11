<template>
  <div class="admin-seat-view">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="阅览室管理" name="rooms">
        <div class="room-management">
          <div class="toolbar">
            <el-button type="primary" @click="openRoomDialog()">
              <el-icon><Plus /></el-icon>
              新建阅览室
            </el-button>
          </div>
          
          <el-table :data="allRooms" v-loading="roomsLoading" stripe>
            <el-table-column prop="name" label="阅览室名称" min-width="150" />
            <el-table-column prop="location" label="位置" min-width="120" />
            <el-table-column prop="capacity" label="座位容量" width="100" />
            <el-table-column prop="openTime" label="开放时间" width="100" />
            <el-table-column prop="closeTime" label="关闭时间" width="100" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '开放' : '关闭' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="openRoomDialog(row)">编辑</el-button>
                <el-button size="small" type="primary" @click="openSeatDialog(row)">配置座位</el-button>
                <el-button size="small" type="danger" @click="deleteRoom(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="座位布局" name="layout">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="room-list-card">
              <template #header>
                <div class="card-header">
                  <span>阅览室列表</span>
                  <el-button type="primary" size="small" @click="fetchRooms">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </template>
              <div v-loading="roomsLoading">
                <div
                  v-for="room in rooms"
                  :key="room.id"
                  class="room-item"
                  :class="{ active: String(room.id) === activeRoomId }"
                  @click="selectRoom(room)"
                >
                  <div class="room-name">{{ room.name }}</div>
                  <div class="room-info">
                    <span>容量: {{ room.capacity }}</span>
                    <el-tag :type="room.status === 1 ? 'success' : 'danger'" size="small">
                      {{ room.status === 1 ? '开放' : '关闭' }}
                    </el-tag>
                  </div>
                </div>
                <EmptyState v-if="rooms.length === 0" title="暂无阅览室" :icon-size="60" />
              </div>
            </el-card>
          </el-col>
          <el-col :span="18">
            <el-card class="seat-map-card">
              <template #header>
                <div class="card-header">
                  <span>{{ currentRoom?.name || '座位布局' }}</span>
                  <div class="header-actions">
                    <el-button type="primary" size="small" @click="fetchRoomLayout">
                      <el-icon><RefreshRight /></el-icon>
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>
              <div v-loading="layoutLoading" class="seat-map-container">
                <SeatMap
                  v-if="seatLayout && seats.length > 0"
                  :layout="seatLayout"
                  :seats="seats"
                  :editable="true"
                  @seat-click="handleSeatClick"
                />
                <EmptyState v-else title="请选择阅览室" :icon-size="60" />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="预约记录" name="reservations">
        <div class="reservation-management">
          <div class="toolbar">
            <el-select v-model="filterRoomId" placeholder="选择阅览室" clearable style="width: 200px; margin-right: 10px;">
              <el-option v-for="room in allRooms" :key="room.id" :label="room.name" :value="room.id" />
            </el-select>
            <el-date-picker v-model="filterDate" placeholder="选择日期" clearable style="margin-right: 10px;" />
            <el-button type="primary" @click="fetchReservations">查询</el-button>
          </div>
          
          <el-table :data="reservations" v-loading="reservationsLoading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="roomName" label="阅览室" width="120" />
            <el-table-column prop="seatNumber" label="座位号" width="100" />
            <el-table-column prop="reserveDate" label="预约日期" width="120">
              <template #default="{ row }">
                {{ formatDate(row.reserveDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="80" />
            <el-table-column prop="endTime" label="结束时间" width="80" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getReservationStatusType(row.status)" size="small">
                  {{ getReservationStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="checkInTime" label="签到时间" width="160">
              <template #default="{ row }">
                {{ row.checkInTime ? formatDateTime(row.checkInTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination">
            <el-pagination
              v-model:current-page="reservationPage"
              v-model:page-size="reservationSize"
              :total="reservationTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="fetchReservations"
              @current-change="fetchReservations"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑阅览室' : '新建阅览室'" width="500px">
      <el-form :model="roomForm" :rules="roomRules" ref="roomFormRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="roomForm.name" placeholder="请输入阅览室名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="roomForm.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="开放时间">
          <el-checkbox v-model="roomForm.allDayOpen" style="margin-bottom: 8px">全天候开放 (24小时)</el-checkbox>
          <div v-if="!roomForm.allDayOpen" class="time-range">
            <el-time-picker v-model="roomForm.openTime" placeholder="开始时间" format="HH:mm" value-format="HH:mm" />
            <span style="margin: 0 10px;">至</span>
            <el-time-picker v-model="roomForm.closeTime" placeholder="结束时间" format="HH:mm" value-format="HH:mm" />
          </div>
          <span v-else style="color: #909399; font-size: 12px;">00:00 - 23:59 全天候</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="roomForm.status" :active-value="1" :inactive-value="0" active-text="开放" inactive-text="关闭" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roomSaveLoading" @click="saveRoom">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="seatDialogVisible" title="批量生成座位" width="450px">
      <el-form :model="seatGenForm" :rules="seatGenRules" ref="seatGenFormRef" label-width="80px">
        <el-form-item label="阅览室">
          <span>{{ seatGenForm.roomName }}</span>
        </el-form-item>
        <el-form-item label="行数" prop="rows">
          <el-input-number v-model="seatGenForm.rows" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="列数" prop="cols">
          <el-input-number v-model="seatGenForm.cols" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="座位前缀">
          <el-input v-model="seatGenForm.prefix" placeholder="如: A-" />
        </el-form-item>
        <el-alert type="warning" :closable="false" style="margin-top: 10px;">
          将生成 {{ seatGenForm.rows * seatGenForm.cols }} 个座位
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="seatDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="seatGenLoading" @click="generateSeats">生成座位</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="座位详情" width="400px">
      <el-form :model="seatForm" label-width="80px">
        <el-form-item label="座位号">
          <span>{{ currentSeat?.seatNumber }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="seatForm.status" placeholder="请选择状态">
            <el-option label="可用" value="available" />
            <el-option label="使用中" value="in_use" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="detailDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="saveSeatStatus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, RefreshRight } from '@element-plus/icons-vue'
import { seatApi, type ReadingRoom, type Seat } from '@/api/seat'
import SeatMap from '@/components/SeatMap.vue'
import EmptyState from '@/components/EmptyState.vue'

const activeTab = ref('rooms')
const roomsLoading = ref(false)
const layoutLoading = ref(false)
const saveLoading = ref(false)
const roomSaveLoading = ref(false)
const seatGenLoading = ref(false)
const reservationsLoading = ref(false)
const activeRoomId = ref('')
const roomDialogVisible = ref(false)
const seatDialogVisible = ref(false)
const detailDialogVisible = ref(false)

const allRooms = ref<ReadingRoom[]>([])
const rooms = ref<ReadingRoom[]>([])
const seats = ref<any[]>([])
const currentSeat = ref<Seat | null>(null)
const reservations = ref<any[]>([])

const filterRoomId = ref<number | null>(null)
const filterDate = ref<Date | null>(null)
const reservationPage = ref(1)
const reservationSize = ref(10)
const reservationTotal = ref(0)

const roomFormRef = ref()
const seatGenFormRef = ref()

const roomForm = ref<any>({
  id: null,
  name: '',
  location: '',
  openTime: '08:00',
  closeTime: '22:00',
  allDayOpen: false,
  status: 1
})

const seatGenForm = ref({
  roomId: null,
  roomName: '',
  rows: 5,
  cols: 8,
  prefix: ''
})

const seatForm = ref({
  status: ''
})

const roomRules = {
  name: [{ required: true, message: '请输入阅览室名称', trigger: 'blur' }]
}

const seatGenRules = {
  rows: [{ required: true, message: '请输入行数', trigger: 'blur' }],
  cols: [{ required: true, message: '请输入列数', trigger: 'blur' }]
}

const currentRoom = computed(() => {
  if (!activeRoomId.value) return null
  return rooms.value.find((r: ReadingRoom) => String(r.id) === activeRoomId.value) || null
})

const seatLayout = computed(() => {
  if (seats.value.length === 0) return null
  const rows = Math.max(...seats.value.map((s: any) => s.row), 1)
  const cols = Math.max(...seats.value.map((s: any) => s.column || s.col), 1)
  return { rows: Math.max(rows, 8), cols: Math.max(cols, 10) }
})

async function fetchAllRooms() {
  roomsLoading.value = true
  try {
    const res: any = await seatApi.getRooms()
    const data = res?.data || res
    allRooms.value = Array.isArray(data) ? data : (data?.list || [])
    rooms.value = allRooms.value.filter((r: any) => r.status === 1)
  } catch {
    ElMessage.error('获取阅览室列表失败')
  } finally {
    roomsLoading.value = false
  }
}

async function fetchRooms() {
  await fetchAllRooms()
  if (rooms.value.length > 0 && !activeRoomId.value) {
    activeRoomId.value = String(rooms.value[0].id)
    await fetchRoomLayout()
  }
}

async function fetchRoomLayout() {
  if (!activeRoomId.value) return
  layoutLoading.value = true
  try {
    const res: any = await seatApi.getRoomLayout(Number(activeRoomId.value))
    const data = res?.data || res
    const seatsData = data?.seats || []
    seats.value = seatsData.map((s: any) => ({
      ...s,
      seatNumber: s.seatNumber || s.seat_no,
      status: typeof s.status === 'number' ? ['available', 'reserved', 'in_use', 'maintenance'][s.status] || 'available' : s.status
    }))
  } catch {
    ElMessage.error('获取座位布局失败')
  } finally {
    layoutLoading.value = false
  }
}

async function fetchReservations() {
  reservationsLoading.value = true
  try {
    const res: any = await seatApi.getAllReservations(filterRoomId.value, filterDate.value, reservationPage.value, reservationSize.value)
    const data = res?.data || {}
    reservations.value = data.records || []
    reservationTotal.value = data.total || 0
  } catch {
    ElMessage.error('获取预约记录失败')
  } finally {
    reservationsLoading.value = false
  }
}

function selectRoom(room: ReadingRoom) {
  activeRoomId.value = String(room.id)
  fetchRoomLayout()
}

function openRoomDialog(room?: any) {
  if (room) {
    const isAllDay = room.openTime === '00:00' && room.closeTime === '23:59'
    roomForm.value = {
      ...room,
      allDayOpen: isAllDay || room.allDayOpen || false
    }
  } else {
    roomForm.value = {
      id: null,
      name: '',
      location: '',
      openTime: '08:00',
      closeTime: '22:00',
      allDayOpen: false,
      status: 1
    }
  }
  roomDialogVisible.value = true
}

async function saveRoom() {
  await roomFormRef.value?.validate()
  const submitData = { ...roomForm.value }
  if (submitData.allDayOpen) {
    submitData.openTime = '00:00'
    submitData.closeTime = '23:59'
  }
  roomSaveLoading.value = true
  try {
    if (roomForm.value.id) {
      await seatApi.updateRoom(roomForm.value.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await seatApi.createRoom(submitData)
      ElMessage.success('创建成功')
    }
    roomDialogVisible.value = false
    await fetchAllRooms()
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    roomSaveLoading.value = false
  }
}

async function deleteRoom(room: any) {
  try {
    await ElMessageBox.confirm(`确定要删除阅览室"${room.name}"吗？删除后座位也将被删除。`, '确认删除', { type: 'warning' })
    await seatApi.deleteRoom(room.id)
    ElMessage.success('删除成功')
    await fetchAllRooms()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

function openSeatDialog(room: any) {
  seatGenForm.value = {
    roomId: room.id,
    roomName: room.name,
    rows: 5,
    cols: 8,
    prefix: ''
  }
  seatDialogVisible.value = true
}

async function generateSeats() {
  await seatGenFormRef.value?.validate()
  seatGenLoading.value = true
  try {
    const res: any = await seatApi.createSeats(seatGenForm.value.roomId!, seatGenForm.value)
    ElMessage.success(res.message || '座位生成成功')
    seatDialogVisible.value = false
    await fetchAllRooms()
  } catch (e: any) {
    ElMessage.error(e.message || '生成失败')
  } finally {
    seatGenLoading.value = false
  }
}

function handleSeatClick(seatId: number) {
  const seat = seats.value.find((s: any) => s.id === seatId)
  if (seat) {
    currentSeat.value = seat
    seatForm.value.status = typeof seat.status === 'string' ? seat.status : 'available'
    detailDialogVisible.value = true
  }
}

async function saveSeatStatus() {
  if (!currentSeat.value) return
  saveLoading.value = true
  try {
    await seatApi.updateSeatStatus(currentSeat.value.id, seatForm.value.status)
    ElMessage.success('保存成功')
    detailDialogVisible.value = false
    await fetchRoomLayout()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

function formatDate(date: any) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toISOString().split('T')[0]
}

function formatDateTime(date: any) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

function getReservationStatusType(status: number) {
  const types: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger', 4: 'info' }
  return types[status] || 'info'
}

function getReservationStatusText(status: number) {
  const texts: Record<number, string> = { 0: '已预约', 1: '已签到', 2: '已签退', 3: '已取消', 4: '已过期' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchAllRooms()
  fetchReservations()
})
</script>

<style scoped>
.admin-seat-view {
  padding: 20px;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.room-management,
.reservation-management {
  padding: 10px 0;
}

.toolbar {
  margin-bottom: 15px;
}

.room-list-card,
.seat-map-card {
  height: calc(100vh - 200px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-item {
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--color-border);
}

.room-item:hover {
  background: var(--color-bg-page);
}

.room-item.active {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}

.room-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.room-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.room-item.active .room-info {
  color: rgba(255, 255, 255, 0.8);
}

.seat-map-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
