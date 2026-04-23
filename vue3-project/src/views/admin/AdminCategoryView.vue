<template>
  <div class="admin-category">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>分类管理</span>
          <el-button type="primary" size="small" @click="showAddDialog = true">添加分类</el-button>
        </div>
      </template>
      <el-table :data="categories" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="code" label="分类编码" />
        <el-table-column prop="parentName" label="父级分类" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editCategory(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteCategory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="添加分类" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitCategory">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const categories = ref<any[]>([])
const showAddDialog = ref(false)
const form = ref({ name: '', code: '', sort: 0, parentId: null })

onMounted(async () => {
  try {
    const res: any = await request.get('/categories')
    categories.value = res.data || res || []
  } catch (e) {
    console.error('Failed to load categories:', e)
  }
})

function editCategory(row: any) {
  form.value = { ...row }
  showAddDialog.value = true
}

async function deleteCategory(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
    await request.delete(`/categories/${id}`)
    ElMessage.success('删除成功')
    const res: any = await request.get('/categories')
    categories.value = res.data || res || []
  } catch { /* cancelled */ }
}

async function submitCategory() {
  try {
    if (form.value.id) {
      await request.put(`/categories/${form.value.id}`, form.value)
    } else {
      await request.post('/categories', form.value)
    }
    ElMessage.success('操作成功')
    showAddDialog.value = false
    form.value = { name: '', code: '', sort: 0, parentId: null }
    const res: any = await request.get('/categories')
    categories.value = res.data || res || []
  } catch (e) {
    ElMessage.error('操作失败')
  }
}
</script>
