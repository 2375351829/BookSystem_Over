<template>
  <div class="admin-user-view">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
        <el-button type="success" @click="handleImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
      <ExportButton export-type="user" :params="searchParams" @export="handleExport" />
    </div>

    <div class="filter-bar">
      <el-select v-model="searchParams.searchField" placeholder="筛选方式" style="width: 120px" @change="fetchUsers">
        <el-option label="全部字段" value="" />
        <el-option label="用户名" value="username" />
        <el-option label="身份标识" value="identityId" />
        <el-option label="真实姓名" value="realName" />
        <el-option label="类型" value="userType" />
        <el-option label="手机号" value="phone" />
        <el-option label="邮箱" value="email" />
        <el-option label="校区" value="campus" />
        <el-option label="院系" value="college" />
        <el-option label="年级" value="grade" />
        <el-option label="班级" value="className" />
      </el-select>
      <el-input
        v-model="searchParams.keyword"
        :placeholder="searchPlaceholder"
        clearable
        style="width: 240px"
        :prefix-icon="Search"
        @clear="fetchUsers"
        @keyup.enter="fetchUsers"
      />
      <el-select v-model="searchParams.type" placeholder="用户类型" clearable style="width: 140px" @change="fetchUsers">
        <el-option label="普通读者" value="READER" />
        <el-option label="VIP读者" value="VIP" />
        <el-option label="教师" value="TEACHER" />
        <el-option label="学生" value="STUDENT" />
        <el-option label="管理员" value="ADMIN" />
      </el-select>
      <el-select v-model="searchParams.status" placeholder="状态筛选" clearable style="width: 120px" @change="fetchUsers">
        <el-option label="正常" :value="1" />
        <el-option label="停用" :value="0" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="fetchUsers">搜索</el-button>
      <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
    </div>

    <div class="batch-actions" v-if="selectedRows.length > 0">
      <span class="selected-info">已选 {{ selectedRows.length }} 项</span>
      <el-button type="success" size="small" @click="handleBatchEnable">批量启用</el-button>
      <el-button type="warning" size="small" @click="handleBatchDisable">批量禁用</el-button>
      <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
      <el-button size="small" @click="tableRef?.clearSelection()">取消选择</el-button>
    </div>

    <el-table 
      ref="tableRef"
      v-loading="loading" 
      :data="userList" 
      stripe 
      border 
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column type="index" label="序号" width="70" align="center" :index="indexMethod" />
      <el-table-column label="头像" width="80" align="center">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar" :style="{ background: getAvatarColor(row.username) }">
            {{ (row.realName || row.username).charAt(0) }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="110" />
      <el-table-column label="身份标识" width="130" align="center">
        <template #default="{ row }">
          <span>{{ row.studentId || row.facultyId || row.userId || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="realName" label="真实姓名" min-width="100" />
      <el-table-column label="类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="typeTagMap[row.userType]?.type || 'info'" size="small" effect="light">
            {{ typeTagMap[row.userType]?.label || row.userType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="campus" label="校区" width="100" />
      <el-table-column prop="college" label="院系" width="120" show-overflow-tooltip />
      <el-table-column prop="grade" label="年级" width="80" />
      <el-table-column prop="className" label="班级" width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" sortable>
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link size="small" @click="handleResetPassword(row)">重置密码</el-button>
          <el-button
            :type="row.status === 1 ? 'danger' : 'success'"
            link
            size="small"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <EmptyState v-if="!loading && userList.length === 0" title="暂无用户数据" description="点击上方按钮新增用户或调整搜索条件" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchUsers"
        @current-change="fetchUsers"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="560px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item :label="identityLabel" prop="identityId">
          <el-input v-model="form.identityId" :placeholder="'请输入' + identityLabel" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码(留空自动生成)" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-radio-group v-model="form.userType" @change="handleUserTypeChange">
            <el-radio value="READER">普通读者</el-radio>
            <el-radio value="VIP">VIP读者</el-radio>
            <el-radio value="TEACHER">教师</el-radio>
            <el-radio value="STUDENT">学生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="校区" prop="campus">
          <el-input v-model="form.campus" placeholder="请输入校区" />
        </el-form-item>
        <el-form-item label="院系" prop="college">
          <el-input v-model="form.college" placeholder="请输入院系" />
        </el-form-item>
        <el-form-item label="年级" prop="grade" v-if="form.userType === 'STUDENT'">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="班级" prop="className" v-if="form.userType === 'STUDENT'">
          <el-input v-model="form.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="辅导员" prop="counselor" v-if="form.userType === 'STUDENT'">
          <el-input v-model="form.counselor" placeholder="请输入辅导员姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="importDialogVisible"
      title="批量导入用户"
      width="700px"
      destroy-on-close
    >
      <el-steps :active="importStep" finish-status="success" align-center style="margin-bottom: 24px;">
        <el-step title="上传文件" />
        <el-step title="数据预览" />
        <el-step title="导入结果" />
      </el-steps>

      <div v-show="importStep === 0" class="import-step-content">
        <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
          <template #title>导入说明</template>
          <ul style="margin: 8px 0 0; padding-left: 18px; line-height: 1.8;">
            <li>支持格式：.xlsx、.xls、.csv</li>
            <li>必填字段：身份标识(学号/教职工号/用户编号)、真实姓名、用户类型</li>
            <li>可选字段：手机号、邮箱、校区、院系、年级(学生)、班级(学生)、辅导员(学生)</li>
            <li>身份标识不能重复，重复记录将被跳过</li>
            <li>密码将自动生成</li>
          </ul>
        </el-alert>
        <el-button type="primary" plain @click="handleDownloadTemplate" style="margin-bottom: 16px;">
          <el-icon><Download /></el-icon>
          下载导入模板
        </el-button>
        <el-upload
          ref="uploadRef"
          drag
          action=""
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls,.csv"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="importFileList"
        >
          <el-icon class="el-icon--upload" style="font-size: 48px; color: #909399;"><UploadFilled /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
      </div>

      <div v-show="importStep === 1" class="import-step-content">
        <el-alert type="success" :closable="false" style="margin-bottom: 16px;">
          已解析 {{ importData.length }} 条用户数据
        </el-alert>
        <el-table :data="importData" border stripe max-height="300">
          <el-table-column prop="identityId" label="身份标识" width="120" />
          <el-table-column prop="realName" label="真实姓名" width="100" />
          <el-table-column prop="userType" label="类型" width="90" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="150" />
          <el-table-column prop="campus" label="校区" width="90" />
          <el-table-column prop="college" label="院系" width="100" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row._valid ? 'success' : 'danger'" size="small">
                {{ row._valid ? '有效' : '无效' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-show="importStep === 2" class="import-step-content">
        <div class="import-result">
          <el-icon :size="64" :color="importResult.fail > 0 ? '#E6A23C' : '#67C23A'">
            <component :is="importResult.fail > 0 ? 'WarningFilled' : 'SuccessFilled'" />
          </el-icon>
          <h3>{{ importResult.fail > 0 ? '导入完成（部分失败）' : '导入成功' }}</h3>
          <p>成功：{{ importResult.success }} 条，失败：{{ importResult.fail }} 条</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="importDialogVisible = false" v-if="importStep === 0">取消</el-button>
        <el-button @click="importStep = 0" v-if="importStep > 0 && importStep < 2">上一步</el-button>
        <el-button type="primary" @click="handleParseFile" :loading="importing" v-if="importStep === 0" :disabled="!importFile">
          解析文件
        </el-button>
        <el-button type="primary" @click="handleExecuteImport" :loading="importing" v-if="importStep === 1">
          开始导入
        </el-button>
        <el-button type="primary" @click="closeImportDialog" v-if="importStep === 2">
          完成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { Search, RefreshLeft, Plus, Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import EmptyState from '@/components/EmptyState.vue'
import ExportButton from '@/components/ExportButton.vue'
import request from '@/utils/request'

interface UserItem {
  id: number
  username: string
  studentId?: string
  facultyId?: string
  userId?: string
  realName: string
  phone: string
  email: string
  userType: string
  status: number
  campus?: string
  college?: string
  grade?: string
  className?: string
  counselor?: string
  institution?: string
  createTime: string
  avatar?: string
}

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const tableRef = ref()
const userList = ref<UserItem[]>([])
const total = ref(0)
const selectedRows = ref<UserItem[]>([])

const searchParams = reactive({
  searchField: '',
  keyword: '',
  type: '',
  status: '' as number | ''
})

const searchPlaceholderMap: Record<string, string> = {
  '': '输入关键词搜索全部字段',
  username: '请输入用户名',
  identityId: '请输入学号/教职工号/用户编号',
  realName: '请输入真实姓名',
  userType: 'STUDENT/TEACHER/READER/VIP',
  phone: '请输入手机号',
  email: '请输入邮箱',
  campus: '请输入校区',
  college: '请输入院系',
  grade: '请输入年级（如2024级）',
  className: '请输入班级名称'
}

const searchPlaceholder = computed(() => {
  return searchPlaceholderMap[searchParams.searchField] || searchPlaceholderMap['']
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const form = reactive({
  username: '',
  identityId: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  userType: 'READER',
  campus: '',
  college: '',
  grade: '',
  className: '',
  counselor: ''
})

const identityLabel = computed(() => {
  switch (form.userType) {
    case 'STUDENT': return '学号'
    case 'TEACHER': return '教职工号'
    default: return '用户编号'
  }
})

const formRules: FormRules = {
  identityId: [{ required: true, message: '请输入身份标识', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  campus: [{ required: true, message: '请输入校区', trigger: 'blur' }],
  college: [{ required: true, message: '请输入院系', trigger: 'blur' }]
}

const typeTagMap: Record<string, { type: string; label: string }> = {
  READER: { type: '', label: '普通读者' },
  VIP: { type: 'warning', label: 'VIP' },
  TEACHER: { type: 'success', label: '教师' },
  STUDENT: { type: 'info', label: '学生' },
  ADMIN: { type: 'danger', label: '管理员' }
}

const importDialogVisible = ref(false)
const importStep = ref(0)
const importFile = ref<File | null>(null)
const importFileList = ref<UploadFile[]>([])
const importData = ref<any[]>([])
const importing = ref(false)
const importResult = reactive({ success: 0, fail: 0 })

function indexMethod(index: number): number {
  return (pagination.page - 1) * pagination.pageSize + index + 1
}

function getAvatarColor(name: string): string {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function fetchUsers() {
  loading.value = true
  try {
    const res: any = await userApi.getUsers({
      ...searchParams,
      page: pagination.page,
      size: pagination.pageSize
    })
    userList.value = res.records || res.data?.list || []
    total.value = res.total || res.data?.total || 0
  } catch {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function handleSelectionChange(rows: UserItem[]) {
  selectedRows.value = rows
}

function resetSearch() {
  searchParams.searchField = ''
  searchParams.keyword = ''
  searchParams.type = ''
  searchParams.status = ''
  pagination.page = 1
  fetchUsers()
}

function handleAdd() {
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleUserTypeChange() {
  form.identityId = ''
}

function handleEdit(row: UserItem) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    username: row.username,
    identityId: row.studentId || row.facultyId || row.userId || '',
    realName: row.realName,
    password: '',
    phone: row.phone,
    email: row.email,
    userType: row.userType,
    campus: row.campus || '',
    college: row.college || '',
    grade: row.grade || '',
    className: row.className || '',
    counselor: row.counselor || ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const submitData: any = { ...form }

    if (submitData.identityId) {
      switch (form.userType) {
        case 'STUDENT':
          submitData.studentId = form.identityId
          break
        case 'TEACHER':
          submitData.facultyId = form.identityId
          break
        default:
          submitData.userId = form.identityId
          break
      }
    }
    delete submitData.identityId
    delete submitData.username

    if (isEdit.value && editId.value) {
      await userApi.updateUser(editId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      await userApi.createUser(submitData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchUsers()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, {
    username: '',
    identityId: '',
    realName: '',
    password: '',
    phone: '',
    email: '',
    userType: 'READER',
    campus: '',
    college: '',
    grade: '',
    className: '',
    counselor: ''
  })
}

function handleResetPassword(row: UserItem) {
  ElMessageBox.confirm(`确定要重置用户 "${row.realName || row.username}" 的密码吗？`, '重置密码确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await userApi.resetUserPassword(row.id)
      ElMessage.success('密码已重置为默认密码')
    } catch {
      ElMessage.error('重置密码失败')
    }
  }).catch(() => {})
}

async function handleToggleStatus(row: UserItem) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '禁用' : '启用'

  ElMessageBox.confirm(`确定要${actionText}用户 "${row.realName || row.username}" 吗？`, `${actionText}确认`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.put(`/users/${row.id}/status`, { status: newStatus })
      ElMessage.success(`${actionText}成功`)
      fetchUsers()
    } catch {
      ElMessage.error(`${actionText}失败`)
    }
  }).catch(() => {})
}

async function handleBatchEnable() {
  const ids = selectedRows.value.map(r => r.id)
  ElMessageBox.confirm(`确定要启用选中的 ${ids.length} 个用户吗？`, '批量启用确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.put('/users/batch/status', { ids, status: 1 })
      ElMessage.success('批量启用成功')
      tableRef.value?.clearSelection()
      fetchUsers()
    } catch {
      ElMessage.error('批量启用失败')
    }
  }).catch(() => {})
}

async function handleBatchDisable() {
  const ids = selectedRows.value.map(r => r.id)
  ElMessageBox.confirm(`确定要禁用选中的 ${ids.length} 个用户吗？`, '批量禁用确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.put('/users/batch/status', { ids, status: 0 })
      ElMessage.success('批量禁用成功')
      tableRef.value?.clearSelection()
      fetchUsers()
    } catch {
      ElMessage.error('批量禁用失败')
    }
  }).catch(() => {})
}

async function handleBatchDelete() {
  const ids = selectedRows.value.map(r => r.id)
  ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个用户吗？此操作不可恢复！`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete('/users/batch', { data: { ids } })
      ElMessage.success('批量删除成功')
      tableRef.value?.clearSelection()
      fetchUsers()
    } catch {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

function handleExport(format: 'excel' | 'csv') {
  ElMessage.success(`正在导出 ${format.toUpperCase()} 格式...`)
}

function handleImport() {
  importStep.value = 0
  importFile.value = null
  importFileList.value = []
  importData.value = []
  importDialogVisible.value = true
}

function handleFileChange(file: UploadFile) {
  importFile.value = file.raw || null
  importFileList.value = [file]
}

function handleFileRemove() {
  importFile.value = null
  importFileList.value = []
}

async function handleDownloadTemplate() {
  try {
    const response = await fetch('/api/users/import/template', {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.ok) {
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'user-import-template.xlsx'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      ElMessage.success('模板下载成功')
    } else {
      ElMessage.error('模板下载失败')
    }
  } catch {
    ElMessage.error('模板下载失败')
  }
}

async function handleParseFile() {
  if (!importFile.value) return
  importing.value = true
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    const res: any = await request.post('/users/import/parse', formData)
    importData.value = (res.data || res).map((row: any) => ({
      ...row,
      _valid: !!(row.identityId && row.realName && row.userType)
    }))
    importStep.value = 1
  } catch (e: any) {
    ElMessage.error(e.message || '文件解析失败')
  } finally {
    importing.value = false
  }
}

async function handleExecuteImport() {
  importing.value = true
  try {
    const validData = importData.value.filter(r => r._valid)
    const res: any = await request.post('/users/import/execute', { users: validData })
    importResult.success = res.success || 0
    importResult.fail = res.fail || 0
    importStep.value = 2
    fetchUsers()
  } catch (e: any) {
    ElMessage.error(e.message || '导入失败')
  } finally {
    importing.value = false
  }
}

function closeImportDialog() {
  importDialogVisible.value = false
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-user-view {
  padding: var(--content-padding);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 12px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 4px;
}

.selected-info {
  font-size: 14px;
  color: #606266;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.import-step-content {
  min-height: 200px;
}

.import-result {
  text-align: center;
  padding: 40px 0;
}

.import-result h3 {
  margin: 16px 0 8px;
  font-size: 18px;
}

.import-result p {
  color: #909399;
  margin: 0;
}
</style>
