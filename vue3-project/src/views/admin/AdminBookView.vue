<template>
  <div class="admin-book-view">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="$router.push('/admin/books/add')">
          <el-icon><Plus /></el-icon>
          新增图书
        </el-button>
        <el-button plain @click="$router.push('/admin/books/import')">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <ExportButton export-type="books" @export="handleExport" />
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索书名/作者/ISBN"
          clearable
          style="width: 240px"
          :prefix-icon="Search"
          @clear="fetchBooks"
          @keyup.enter="handleSearch"
        />
        <el-button :icon="Search" @click="handleSearch" />
      </div>
    </div>

    <div class="filter-bar">
      <el-select v-model="filterCategory" placeholder="全部分类" clearable style="width: 140px" @change="fetchBooks">
        <el-option label="计算机科学" value="cs" />
        <el-option label="文学小说" value="literature" />
        <el-option label="历史传记" value="history" />
        <el-option label="经济管理" value="economics" />
        <el-option label="哲学心理" value="philosophy" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 130px" @change="fetchBooks">
        <el-option label="在馆" value="1" />
        <el-option label="借出" value="0" />
        <el-option label="维护中" value="2" />
      </el-select>
      <el-select v-model="filterLanguage" placeholder="全部语言" clearable style="width: 120px" @change="fetchBooks">
        <el-option label="中文" value="zhCN" />
        <el-option label="英文" value="en" />
        <el-option label="日文" value="ja" />
      </el-select>
    </div>

    <div class="table-section" v-loading="loading">
      <el-table
        :data="bookList"
        style="width: 100%"
        stripe
        @selection-change="handleSelectionChange"
        ref="tableRef"
      >
        <el-table-column type="selection" width="48" align="center" />
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand-content">
              <el-descriptions :column="4" border size="small">
                <el-descriptions-item label="副标题">{{ row.subtitle || '-' }}</el-descriptions-item>
                <el-descriptions-item label="英文书名">{{ row.titleEn || '-' }}</el-descriptions-item>
                <el-descriptions-item label="英文作者">{{ row.authorEn || '-' }}</el-descriptions-item>
                <el-descriptions-item label="译者">{{ row.translator || '-' }}</el-descriptions-item>
                <el-descriptions-item label="版次">{{ row.edition || '-' }}</el-descriptions-item>
                <el-descriptions-item label="页数">{{ row.pages || '-' }}</el-descriptions-item>
                <el-descriptions-item label="价格">{{ row.price ? `¥${row.price}` : '-' }}</el-descriptions-item>
                <el-descriptions-item label="标签">{{ row.tags || '-' }}</el-descriptions-item>
                <el-descriptions-item label="封面URL">{{ row.coverUrl || '-' }}</el-descriptions-item>
                <el-descriptions-item label="馆藏位置">{{ row.location || '-' }}</el-descriptions-item>
                <el-descriptions-item label="书架号">{{ row.shelfNo || '-' }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatTime(row.updateTime) }}</el-descriptions-item>
                <el-descriptions-item label="简介" :span="4">
                  <div class="expand-summary">{{ row.summary || '暂无简介' }}</div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" min-width="120" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" width="120" show-overflow-tooltip />
        <el-table-column prop="publishYear" label="出版年份" width="90" align="center" />
        <el-table-column prop="category" label="分类" width="100" align="center" />
        <el-table-column prop="language" label="语言" width="80" align="center" />
        <el-table-column label="复本" width="90" align="center">
          <template #default="{ row }">
            <span>{{ row.availableCopies }}/{{ row.totalCopies }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'" size="small">
              {{ row.status === '1' ? '在馆' : '借出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入库时间" width="150" sortable>
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-popconfirm title="确定删除该图书吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" link>
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="batch-actions" v-if="selectedRows.length > 0">
        <span class="selected-info">已选 {{ selectedRows.length }} 项</span>
        <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="primary" size="small" @click="showBatchEditDialog = true">批量编辑</el-button>
        <el-button size="small" @click="tableRef?.clearSelection()">取消选择</el-button>
      </div>

      <EmptyState
        v-if="!loading && bookList.length === 0"
        title="暂无图书记录"
        description="点击「新增图书」开始添加"
      />

      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchBooks"
          @current-change="fetchBooks"
        />
      </div>
    </div>

    <el-dialog v-model="showEditDialog" title="编辑图书" width="900px" destroy-on-close>
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-tabs v-model="editTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="ISBN" prop="isbn">
                  <el-input v-model="editForm.isbn" placeholder="请输入ISBN" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="书名" prop="title">
                  <el-input v-model="editForm.title" placeholder="请输入书名" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="副标题">
                  <el-input v-model="editForm.subtitle" placeholder="请输入副标题" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="英文书名">
                  <el-input v-model="editForm.titleEn" placeholder="请输入英文书名" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="作者" prop="author">
                  <el-input v-model="editForm.author" placeholder="请输入作者" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="英文作者">
                  <el-input v-model="editForm.authorEn" placeholder="请输入英文作者" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="译者">
                  <el-input v-model="editForm.translator" placeholder="请输入译者" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="出版社">
                  <el-input v-model="editForm.publisher" placeholder="请输入出版社" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="出版年份">
                  <el-input v-model="editForm.publishYear" placeholder="如2023" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="版次">
                  <el-input v-model="editForm.edition" placeholder="如第1版" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="语言">
                  <el-select v-model="editForm.language" style="width: 100%">
                    <el-option label="中文" value="zhCN" />
                    <el-option label="英文" value="en" />
                    <el-option label="日文" value="ja" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="分类与价格" name="category">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="分类">
                  <el-input v-model="editForm.category" placeholder="请输入分类" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分类名称">
                  <el-input v-model="editForm.categoryName" placeholder="请输入分类名称" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="页数">
                  <el-input-number v-model="editForm.pages" :min="0" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="价格">
                  <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="标签">
                  <el-input v-model="editForm.tags" placeholder="多个用逗号分隔" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="馆藏信息" name="storage">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="总复本数">
                  <el-input-number v-model="editForm.totalCopies" :min="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="可借复本">
                  <el-input-number v-model="editForm.availableCopies" :min="0" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="状态">
                  <el-select v-model="editForm.status" style="width: 100%">
                    <el-option label="在馆" value="1" />
                    <el-option label="借出" value="0" />
                    <el-option label="维护中" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="馆藏位置">
                  <el-input v-model="editForm.location" placeholder="如：三楼借阅区" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="书架号">
                  <el-input v-model="editForm.shelfNo" placeholder="如：A101" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="封面图片">
              <div class="cover-upload-section">
                <div class="cover-preview" v-if="editForm.coverUrl">
                  <el-image :src="editForm.coverUrl" fit="cover" style="width: 120px; height: 160px; border-radius: 4px;" />
                  <el-button type="danger" size="small" link @click="editForm.coverUrl = ''">移除</el-button>
                </div>
                <el-upload
                  class="cover-uploader"
                  :show-file-list="false"
                  :before-upload="beforeCoverUpload"
                  :http-request="handleCoverUpload"
                  accept="image/*"
                >
                  <div class="upload-trigger" v-if="!editForm.coverUrl">
                    <el-icon><Plus /></el-icon>
                    <span>上传封面</span>
                  </div>
                </el-upload>
                <el-input v-model="editForm.coverUrl" placeholder="或输入封面图片URL" style="flex: 1; margin-left: 12px;" />
              </div>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="简介信息" name="summary">
            <el-form-item label="中文简介">
              <el-input v-model="editForm.summary" type="textarea" :rows="5" placeholder="请输入中文简介" maxlength="2000" show-word-limit />
            </el-form-item>
            <el-form-item label="英文简介">
              <el-input v-model="editForm.summaryEn" type="textarea" :rows="5" placeholder="请输入英文简介" maxlength="2000" show-word-limit />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量编辑对话框 -->
    <el-dialog v-model="showBatchEditDialog" title="批量编辑图书" width="700px" destroy-on-close>
      <el-alert type="info" :closable="false" style="margin-bottom: 20px">
        <template #title>
          将批量修改 <strong>{{ selectedRows.length }}</strong> 本图书的以下字段，留空的字段将保持不变
        </template>
      </el-alert>
      <el-form :model="batchEditForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="batchEditForm.category" placeholder="不修改" clearable style="width: 100%">
                <el-option label="计算机科学" value="计算机科学" />
                <el-option label="文学小说" value="文学小说" />
                <el-option label="历史传记" value="历史传记" />
                <el-option label="经济管理" value="经济管理" />
                <el-option label="哲学心理" value="哲学心理" />
                <el-option label="自然科学" value="自然科学" />
                <el-option label="艺术设计" value="艺术设计" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="语言">
              <el-select v-model="batchEditForm.language" placeholder="不修改" clearable style="width: 100%">
                <el-option label="中文" value="中文" />
                <el-option label="英文" value="英文" />
                <el-option label="日文" value="日文" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="batchEditForm.status" placeholder="不修改" clearable style="width: 100%">
                <el-option label="在馆" value="1" />
                <el-option label="借出" value="0" />
                <el-option label="维护中" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="馆藏位置">
              <el-input v-model="batchEditForm.location" placeholder="不修改" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片">
          <div style="display: flex; align-items: flex-start; gap: 12px;">
            <div class="cover-preview" v-if="batchEditForm.coverUrl">
              <el-image :src="batchEditForm.coverUrl" fit="cover" style="width: 100px; height: 140px; border-radius: 4px;" />
              <el-button type="danger" size="small" link @click="batchEditForm.coverUrl = ''">移除</el-button>
            </div>
            <el-upload
              class="cover-uploader"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :http-request="(options: any) => handleCoverUpload(options, true)"
              accept="image/*"
            >
              <div class="upload-trigger" v-if="!batchEditForm.coverUrl">
                <el-icon><Plus /></el-icon>
                <span>上传封面</span>
              </div>
            </el-upload>
            <el-input v-model="batchEditForm.coverUrl" placeholder="或输入封面图片URL" style="flex: 1;" clearable />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBatchEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBatchEdit" :loading="batchSaving">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Upload, Search, Edit, Delete } from '@element-plus/icons-vue'
import { bookApi } from '@/api/book'
import { exportApi } from '@/api/export'
import ExportButton from '@/components/ExportButton.vue'
import EmptyState from '@/components/EmptyState.vue'

interface BookItem {
  id: number
  isbn: string
  title: string
  subtitle?: string
  titleEn?: string
  author: string
  authorEn?: string
  translator?: string
  publisher?: string
  publishYear?: string
  edition?: string
  category?: string
  categoryName?: string
  language?: string
  pages?: number
  price?: number
  tags?: string
  coverUrl?: string
  summary?: string
  summaryEn?: string
  status: string
  totalCopies: number
  availableCopies: number
  location?: string
  shelfNo?: string
  createTime?: string
  updateTime?: string
}

const loading = ref(false)
const saving = ref(false)
const batchSaving = ref(false)
const showBatchEditDialog = ref(false)
const batchEditForm = reactive({
  category: '',
  language: '',
  status: '',
  location: '',
  coverUrl: ''
})
const searchKeyword = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const filterLanguage = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const bookList = ref<BookItem[]>([])
const selectedRows = ref<BookItem[]>([])
const tableRef = ref<any>()
const showEditDialog = ref(false)
const editFormRef = ref<FormInstance>()
const editTab = ref('basic')

const editForm = reactive({
  id: 0,
  isbn: '',
  title: '',
  subtitle: '',
  titleEn: '',
  author: '',
  authorEn: '',
  translator: '',
  publisher: '',
  publishYear: '',
  edition: '',
  category: '',
  categoryName: '',
  language: 'zhCN',
  pages: 0,
  price: 0,
  tags: '',
  coverUrl: '',
  summary: '',
  summaryEn: '',
  status: '1',
  totalCopies: 1,
  availableCopies: 1,
  location: '',
  shelfNo: ''
})

const editRules: FormRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }]
}

const formatTime = (time?: string): string => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

// 状态值映射：前端使用 '1'/'0'/'2'，后端使用 'available'/'borrowed'/'maintenance'
const statusMap: Record<string, string> = {
  '1': 'available',
  '0': 'borrowed',
  '2': 'maintenance'
}

// 反向映射：后端状态转前端显示
const reverseStatusMap: Record<string, string> = {
  'available': '1',
  'borrowed': '0',
  'maintenance': '2'
}

const fetchBooks = async () => {
  loading.value = true
  try {
    const params: any = { page: currentPage.value, size: pageSize.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filterCategory.value) params.category = filterCategory.value
    // 将前端状态值转换为后端格式
    if (filterStatus.value && statusMap[filterStatus.value]) {
      params.status = statusMap[filterStatus.value]
    }
    if (filterLanguage.value) params.language = filterLanguage.value
    const res: any = await bookApi.getBooks(params)
    // 处理返回数据，将后端状态转换为前端格式
    const list = res.records || res.content || []
    bookList.value = list.map((item: BookItem) => ({
      ...item,
      status: reverseStatusMap[item.status] || item.status
    }))
    total.value = res.total || res.totalElements || 0
  } catch (e: any) {
    ElMessage.error(e.message || '获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

const handleSelectionChange = (rows: BookItem[]) => {
  selectedRows.value = rows
}

const handleEdit = (row: BookItem) => {
  Object.assign(editForm, {
    id: row.id,
    isbn: row.isbn || '',
    title: row.title || '',
    subtitle: row.subtitle || '',
    titleEn: row.titleEn || '',
    author: row.author || '',
    authorEn: row.authorEn || '',
    translator: row.translator || '',
    publisher: row.publisher || '',
    publishYear: row.publishYear || '',
    edition: row.edition || '',
    category: row.category || '',
    categoryName: row.categoryName || '',
    language: row.language || 'zhCN',
    pages: row.pages || 0,
    price: row.price || 0,
    tags: row.tags || '',
    coverUrl: row.coverUrl || '',
    summary: row.summary || '',
    summaryEn: row.summaryEn || '',
    status: row.status || '1',
    totalCopies: row.totalCopies || 1,
    availableCopies: row.availableCopies || 1,
    location: row.location || '',
    shelfNo: row.shelfNo || ''
  })
  editTab.value = 'basic'
  showEditDialog.value = true
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await bookApi.updateBook(editForm.id, editForm)
      ElMessage.success('修改成功')
      showEditDialog.value = false
      fetchBooks()
    } catch (e: any) {
      ElMessage.error(e.message || '保存失败')
    } finally {
      saving.value = false
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await bookApi.deleteBook(id)
    ElMessage.success('删除成功')
    fetchBooks()
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 本图书吗？`, '批量删除', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await bookApi.batchDeleteBooks(selectedRows.value.map(b => b.id))
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchBooks()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '批量删除失败')
  }
}

const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleCoverUpload = async (options: any, isBatch: boolean = false) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res: any = await bookApi.uploadCover(formData)
    const url = res.url || res.data?.url || ''
    if (isBatch) {
      batchEditForm.coverUrl = url
    } else {
      editForm.coverUrl = url
    }
    ElMessage.success('封面上传成功')
  } catch (e: any) {
    ElMessage.error(e.message || '封面上传失败')
  }
}

const handleBatchEdit = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要编辑的图书')
    return
  }
  
  const hasChanges = batchEditForm.category || batchEditForm.language || 
                     batchEditForm.status || batchEditForm.location || batchEditForm.coverUrl
  
  if (!hasChanges) {
    ElMessage.warning('请至少修改一个字段')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量修改选中的 ${selectedRows.value.length} 本图书吗？`,
      '批量编辑确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    
    batchSaving.value = true
    const bookIds = selectedRows.value.map(b => b.id)
    const updateData: any = {}
    
    if (batchEditForm.category) updateData.category = batchEditForm.category
    if (batchEditForm.language) updateData.language = batchEditForm.language
    if (batchEditForm.status) updateData.status = batchEditForm.status
    if (batchEditForm.location) updateData.location = batchEditForm.location
    if (batchEditForm.coverUrl) updateData.coverUrl = batchEditForm.coverUrl
    
    await bookApi.batchUpdateBooks(bookIds, updateData)
    ElMessage.success(`成功修改 ${selectedRows.value.length} 本图书`)
    showBatchEditDialog.value = false
    
    batchEditForm.category = ''
    batchEditForm.language = ''
    batchEditForm.status = ''
    batchEditForm.location = ''
    batchEditForm.coverUrl = ''
    
    selectedRows.value = []
    fetchBooks()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '批量修改失败')
    }
  } finally {
    batchSaving.value = false
  }
}

const handleExport = async (format: 'excel' | 'csv') => {
  try {
    ElMessage.info('正在导出...')
    const blob: any = await exportApi.exportBooks({ format })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `图书列表_${new Date().toISOString().slice(0, 10)}.${format === 'csv' ? 'csv' : 'xlsx'}`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e: any) {
    ElMessage.error(e.message || '导出失败')
  }
}

onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.admin-book-view {
  max-width: 1400px;
  margin: 0 auto;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.table-section {
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
  min-height: 300px;
}

.expand-content {
  padding: 16px 20px;
  background: #fafafa;
}

.expand-summary {
  white-space: pre-wrap;
  line-height: 1.7;
  font-size: 13px;
  max-height: 120px;
  overflow-y: auto;
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding: 10px 14px;
  background: rgba(64, 158, 255, 0.04);
  border-radius: var(--border-radius-base);
  border: 1px solid rgba(64, 158, 255, 0.15);
}

.selected-info {
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 500;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.cover-upload-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.cover-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.cover-uploader {
  border: 1px dashed var(--color-border);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.cover-uploader:hover {
  border-color: var(--color-primary);
}

.upload-trigger {
  width: 120px;
  height: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.upload-trigger .el-icon {
  font-size: 28px;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left, .toolbar-right {
    width: 100%;
  }

  .toolbar-right > .el-input {
    width: 100% !important;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-bar .el-select {
    width: 100% !important;
  }
}
</style>
