<template>
  <div class="seat-admin-container fade-in">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div>
            <h1>座位管理</h1>
            <p>管理阅览室、座位和预约记录</p>
          </div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="admin-tabs">
      <el-tab-pane label="阅览室管理" name="rooms">
        <div class="tab-content">
          <div class="toolbar">
            <el-button type="primary" @click="showRoomDialog()">
              <el-icon><Plus /></el-icon>
              添加阅览室
            </el-button>
          </div>
          
          <div v-loading="seatStore.loading" class="room-grid">
            <div 
              v-for="room in rooms" 
              :key="room.id" 
              class="room-card"
            >
              <div class="room-header">
                <h4>{{ room.name }}</h4>
                <el-tag :type="room.status === 1 ? 'success' : 'danger'" size="small">
                  {{ room.status === 1 ? '开放' : '关闭' }}
                </el-tag>
              </div>
              <div class="room-body">
                <div class="room-info">
                  <el-icon><Location /></el-icon>
                  <span>{{ room.location }}</span>
                </div>
                <div class="room-info">
                  <el-icon><Clock /></el-icon>
                  <span>{{ room.openTime }} - {{ room.closeTime }}</span>
                </div>
                <div class="room-info">
                  <el-icon><User /></el-icon>
                  <span>容量：{{ room.capacity }}人</span>
                </div>
              </div>
              <div class="room-actions">
                <el-button type="primary" size="small" @click="showRoomDialog(room)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="info" size="small" @click="showSeatManagement(room)">
                  <el-icon><Grid /></el-icon>
                  座位管理
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteRoom(room.id)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-if="!seatStore.loading && seatStore.rooms.length === 0" class="empty-state">
            <el-icon class="empty-icon"><OfficeBuilding /></el-icon>
            <p>暂无阅览室，请点击上方按钮添加</p>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="座位管理" name="seats">
        <div class="tab-content">
          <div class="toolbar">
            <el-select v-model="selectedRoomId" placeholder="选择阅览室" style="width: 200px" @change="handleRoomSelect">
              <el-option
                v-for="room in seatStore.rooms"
                :key="room.id"
                :label="room.name"
                :value="room.id"
              />
            </el-select>
            <el-button type="primary" :disabled="!selectedRoomId" @click="showBatchSeatDialog">
              <el-icon><Plus /></el-icon>
              批量添加座位
            </el-button>
            <el-button type="primary" :disabled="!selectedRoomId" @click="showSeatDialog()">
              <el-icon><Plus /></el-icon>
              添加单个座位
            </el-button>
          </div>

          <div v-loading="seatStore.loading" class="seat-table-wrapper">
            <el-table 
              :data="seatStore.seats" 
              style="width: 100%"
              :header-cell-style="{ background: '#f7fafc', color: '#1a202c' }"
            >
              <el-table-column prop="seatNumber" label="座位号" width="120" align="center" />
              <el-table-column prop="row" label="行" width="80" align="center" />
              <el-table-column prop="column" label="列" width="80" align="center" />
              <el-table-column prop="status" label="状态" width="120" align="center">
                <template #default="{ row }">
                  <el-tag :type="getSeatStatusType(row.status)" size="small">
                    {{ getSeatStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="特性" width="150" align="center">
                <template #default="{ row }">
                  <el-tag v-if="row.hasSocket" type="warning" size="small" style="margin-right: 4px">插座</el-tag>
                  <el-tag v-if="row.nearWindow" type="info" size="small">靠窗</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" align="center">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" align="center">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="showSeatDialog(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDeleteSeat(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <div v-if="!selectedRoomId" class="empty-state">
            <el-icon class="empty-icon"><Grid /></el-icon>
            <p>请先选择阅览室</p>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="预约记录" name="reservations">
        <div class="tab-content">
          <div class="toolbar">
            <el-select v-model="reservationQuery.roomId" placeholder="阅览室" clearable style="width: 150px">
              <el-option
                v-for="room in seatStore.rooms"
                :key="room.id"
                :label="room.name"
                :value="room.id"
              />
            </el-select>
            <el-select v-model="reservationQuery.status" placeholder="状态" clearable style="width: 120px">
              <el-option label="已预约" :value="0" />
              <el-option label="已签到" :value="1" />
              <el-option label="已签退" :value="2" />
              <el-option label="已取消" :value="3" />
              <el-option label="已过期" :value="4" />
            </el-select>
            <el-date-picker
              v-model="reservationQuery.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 260px"
            />
            <el-button type="primary" @click="fetchReservations">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
          </div>

          <div v-loading="seatStore.loading" class="table-wrapper">
            <el-table 
              :data="seatStore.reservations" 
              style="width: 100%"
              :header-cell-style="{ background: '#f7fafc', color: '#1a202c' }"
            >
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="username" label="用户" width="120" align="center" />
              <el-table-column prop="roomName" label="阅览室" width="150" />
              <el-table-column prop="seatNumber" label="座位号" width="100" align="center" />
              <el-table-column prop="reservationDate" label="预约日期" width="120" align="center" />
              <el-table-column label="时间段" width="140" align="center">
                <template #default="{ row }">
                  {{ row.startTime }} - {{ row.endTime }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getReservationStatusType(row.status)" size="small">
                    {{ getReservationStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="checkInTime" label="签到时间" width="160" align="center">
                <template #default="{ row }">
                  {{ row.checkInTime ? formatDateTime(row.checkInTime) : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="checkOutTime" label="签退时间" width="160" align="center">
                <template #default="{ row }">
                  {{ row.checkOutTime ? formatDateTime(row.checkOutTime) : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="160" align="center">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <div class="pagination-wrapper" v-if="seatStore.total > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="seatStore.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchReservations"
              @current-change="fetchReservations"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑阅览室' : '添加阅览室'" width="500px">
      <el-form :model="roomForm" :rules="roomRules" ref="roomFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="roomForm.name" placeholder="请输入阅览室名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="roomForm.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="roomForm.capacity" :min="1" :max="500" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开放时间">
          <div class="time-range">
            <el-time-select
              v-model="roomForm.openTime"
              :max-time="roomForm.closeTime"
              placeholder="开始"
              start="06:00"
              step="00:30"
              end="24:00"
              style="width: 45%"
            />
            <span class="time-separator">至</span>
            <el-time-select
              v-model="roomForm.closeTime"
              :min-time="roomForm.openTime"
              placeholder="结束"
              start="06:00"
              step="00:30"
              end="24:00"
              style="width: 45%"
            />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roomForm.status">
            <el-radio :value="1">开放</el-radio>
            <el-radio :value="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roomForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleRoomSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="seatDialogVisible" :title="seatForm.id ? '编辑座位' : '添加座位'" width="450px">
      <el-form :model="seatForm" :rules="seatRules" ref="seatFormRef" label-width="80px">
        <el-form-item label="座位号" prop="seatNumber">
          <el-input v-model="seatForm.seatNumber" placeholder="请输入座位号" />
        </el-form-item>
        <el-form-item label="行" prop="row">
          <el-input-number v-model="seatForm.row" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="列" prop="column">
          <el-input-number v-model="seatForm.column" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="seatForm.status" style="width: 100%">
            <el-option label="可用" :value="0" />
            <el-option label="维护中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="特性">
          <el-checkbox v-model="seatForm.hasSocket">有插座</el-checkbox>
          <el-checkbox v-model="seatForm.nearWindow">靠窗</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="seatDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSeatSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchSeatDialogVisible" title="批量添加座位" width="450px">
      <el-form :model="batchSeatForm" :rules="batchSeatRules" ref="batchSeatFormRef" label-width="80px">
        <el-form-item label="行数" prop="rows">
          <el-input-number v-model="batchSeatForm.rows" :min="1" :max="20" style="width: 100%" />
        </el-form-item>
        <el-form-item label="列数" prop="cols">
          <el-input-number v-model="batchSeatForm.cols" :min="1" :max="20" style="width: 100%" />
        </el-form-item>
        <el-form-item label="座位前缀" prop="prefix">
          <el-input v-model="batchSeatForm.prefix" placeholder="如：A-" />
        </el-form-item>
        <el-form-item label="预览">
          <div class="batch-preview">
            将生成 {{ batchSeatForm.rows * batchSeatForm.cols }} 个座位
            <span v-if="batchSeatForm.prefix">
              （{{ batchSeatForm.prefix }}1-1 至 {{ batchSeatForm.prefix }}{{ batchSeatForm.rows }}-{{ batchSeatForm.cols }}）
            </span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchSeatDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBatchSeatSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { 
  OfficeBuilding, Plus, Location, Clock, User, Edit, Delete, 
  Grid, Search
} from '@element-plus/icons-vue'
import { seatApi, type ReadingRoom, type Seat, type RoomFormData, type SeatFormData } from '@/api/seat'
import { useSeatStore } from '@/stores/seat'

const seatStore = useSeatStore()

const activeTab = ref('rooms')
const selectedRoomId = ref<number | null>(null)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const roomDialogVisible = ref(false)
const seatDialogVisible = ref(false)
const batchSeatDialogVisible = ref(false)

const roomFormRef = ref<FormInstance>()
const seatFormRef = ref<FormInstance>()
const batchSeatFormRef = ref<FormInstance>()

const rooms = ref<ReadingRoom[]>([])
const seats = ref<Seat[]>([])
const reservations = ref<any[]>([])

const roomForm = ref<RoomFormData>({
  name: '',
  description: '',
  location: '',
  capacity: 50,
  openTime: '08:00',
  closeTime: '22:00',
  status: 1
})

const seatForm = ref<SeatFormData>({
  roomId: 0,
  seatNumber: '',
  row: 1,
  column: 1,
  status: 0,
  hasSocket: false,
  nearWindow: false
})

const batchSeatForm = ref({
  roomId: 0,
  rows: 5,
  cols: 8,
  prefix: ''
})

const reservationQuery = reactive({
  roomId: null as number | null,
  status: null as number | null,
  dateRange: [] as string[]
})

const roomRules: FormRules = {
  name: [{ required: true, message: '请输入阅览室名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'change' }]
}

const seatRules: FormRules = {
  seatNumber: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
  row: [{ required: true, message: '请输入行号', trigger: 'change' }],
  column: [{ required: true, message: '请输入列号', trigger: 'change' }]
}

const batchSeatRules: FormRules = {
  rows: [{ required: true, message: '请输入行数', trigger: 'change' }],
  cols: [{ required: true, message: '请输入列数', trigger: 'change' }]
}

const getSeatStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '可用',
    1: '已预约',
    2: '使用中',
    3: '维护中'
  }
  return statusMap[status] || '未知'
}

const getSeatStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'success',
    1: 'danger',
    2: 'warning',
    3: 'info'
  }
  return statusMap[status] || 'info'
}

const getReservationStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '已预约',
    1: '已签到',
    2: '已签退',
    3: '已取消',
    4: '已过期'
  }
  return statusMap[status] || '未知'
}

const getReservationStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'danger',
    4: 'danger'
  }
  return statusMap[status] || 'info'
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

const showRoomDialog = (room?: any) => {
  if (room) {
    roomForm.value = {
      id: room.id,
      name: room.name,
      location: room.location,
      capacity: room.capacity,
      openTime: room.openTime,
      closeTime: room.closeTime,
      status: room.status
    }
  } else {
    roomForm.value = {
      name: '',
      location: '',
      capacity: 50,
      openTime: '08:00',
      closeTime: '22:00',
      status: 1
    }
  }
  roomDialogVisible.value = true
}

const showSeatDialog = (seat?: Seat) => {
  if (!selectedRoomId.value) {
    ElMessage.warning('请先选择阅览室')
    return
  }
  if (seat) {
    seatForm.value = {
      id: seat.id,
      roomId: seat.roomId,
      seatNumber: seat.seatNumber,
      row: seat.row,
      column: seat.column,
      status: typeof seat.status === 'number' ? seat.status : 0,
      hasSocket: seat.hasSocket || false,
      nearWindow: seat.nearWindow || false
    }
  } else {
    seatForm.value = {
      roomId: selectedRoomId.value!,
      seatNumber: '',
      row: 1,
      column: 1,
      status: 0,
      hasSocket: false,
      nearWindow: false
    }
  }
  seatDialogVisible.value = true
}

const showBatchSeatDialog = () => {
  if (!selectedRoomId.value) {
    ElMessage.warning('请先选择阅览室')
    return
  }
  batchSeatForm.value = {
    roomId: selectedRoomId.value,
    rows: 5,
    cols: 8,
    prefix: ''
  }
  batchSeatDialogVisible.value = true
}

const showSeatManagement = (room: ReadingRoom) => {
  selectedRoomId.value = room.id
  activeTab.value = 'seats'
  fetchSeats()
}

const handleRoomSelect = () => {
  if (selectedRoomId.value) {
    fetchSeats()
  } else {
    seatStore.clearSeats()
  }
}

const fetchRooms = async () => {
  try {
    const res: any = await seatApi.getRooms()
    rooms.value = res.data || []
  } catch (error) {
    ElMessage.error('获取阅览室列表失败')
  }
}

const fetchSeats = async () => {
  if (!selectedRoomId.value) return
  try {
    const res: any = await seatApi.getRoomLayout(selectedRoomId.value)
    seats.value = res.data?.seats || []
  } catch (error) {
    ElMessage.error('获取座位信息失败')
  }
}

const fetchReservations = async () => {
  try {
    const res: any = await seatApi.getRoomUsageStats(selectedRoomId.value || 0)
    reservations.value = res.data?.list || res.data || []
  } catch (error) {
    ElMessage.error('获取预约记录失败')
  }
}

const handleRoomSubmit = async () => {
  if (!roomFormRef.value) return
  await roomFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (roomForm.value.id) {
        await seatApi.updateRoom(roomForm.value.id, roomForm.value)
        ElMessage.success('更新成功')
      } else {
        await seatApi.createRoom(roomForm.value)
        ElMessage.success('添加成功')
      }
      roomDialogVisible.value = false
      await fetchRooms()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleSeatSubmit = async () => {
  if (!seatFormRef.value) return
  await seatFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (seatForm.value.id) {
        await seatApi.updateSeatStatus(seatForm.value.id, seatForm.value)
        ElMessage.success('更新成功')
      } else {
        await seatApi.createSeats(selectedRoomId.value!, seatForm.value)
        ElMessage.success('添加成功')
      }
      seatDialogVisible.value = false
      await fetchSeats()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleBatchSeatSubmit = async () => {
  if (!batchSeatFormRef.value) return
  await batchSeatFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await seatApi.createSeats(selectedRoomId.value!, { ...batchSeatForm.value, roomId: selectedRoomId.value! })
      ElMessage.success('批量添加成功')
      batchSeatDialogVisible.value = false
      await fetchSeats()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleDeleteRoom = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该阅览室吗？删除后该阅览室下的所有座位也将被删除。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await seatApi.deleteRoom(id)
    ElMessage.success('删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleDeleteSeat = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该座位吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await seatApi.deleteSeat(id)
    ElMessage.success('删除成功')
    await fetchSeats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(async () => {
  await seatStore.fetchRooms()
})
</script>

<style scoped>
.seat-admin-container {
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

.admin-tabs {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
}

.tab-content {
  min-height: 400px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.room-card {
  background: #f7fafc;
  border-radius: var(--radius-lg);
  padding: 20px;
  border: 1px solid var(--border);
  transition: all 0.3s ease;
}

.room-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.room-header h4 {
  margin: 0;
  font-size: 1.1rem;
  color: var(--text-primary);
}

.room-body {
  margin-bottom: 16px;
}

.room-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.room-info .el-icon {
  color: var(--primary);
}

.room-desc {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin: 12px 0 0;
  line-height: 1.5;
}

.room-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.seat-table-wrapper,
.table-wrapper {
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border);
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.empty-state {
  text-align: center;
  padding: 60px 40px;
  color: var(--text-muted);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  color: var(--text-muted);
}

.empty-state p {
  margin: 0;
  font-size: 1rem;
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

.batch-preview {
  padding: 12px;
  background: #f7fafc;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px;
  }
  
  .room-grid {
    grid-template-columns: 1fr;
  }
  
  .toolbar {
    flex-direction: column;
  }
  
  .toolbar .el-select,
  .toolbar .el-button {
    width: 100%;
  }
}
</style>
