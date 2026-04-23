<template>
  <div class="admin-book-add">
    <el-card>
      <template #header>
        <el-page-header @back="$router.push('/admin/books')" title="返回">
          <template #content>图书录入</template>
        </el-page-header>
      </template>
      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="书名" required>
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" required>
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">确认录入</el-button>
          <el-button @click="$router.push('/admin/books')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const form = ref({
  title: '',
  author: '',
  isbn: '',
  category: '',
  publisher: '',
  quantity: 1
})

async function submitForm() {
  if (!form.value.title || !form.value.author) {
    ElMessage.warning('请填写必要信息')
    return
  }
  try {
    await request.post('/books', form.value)
    ElMessage.success('录入成功')
    router.push('/admin/books')
  } catch (e) {
    ElMessage.error('录入失败')
  }
}
</script>
