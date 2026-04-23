<template>
  <div class="admin-book-import">
    <el-card>
      <template #header>
        <el-page-header @back="$router.push('/admin/books')" title="返回">
          <template #content>图书批量导入</template>
        </el-page-header>
      </template>
      <el-upload
        class="upload-area"
        drag
        action="/api/books/import"
        :headers="uploadHeaders"
        accept=".xlsx,.xls,.csv"
        :on-success="handleSuccess"
        :on-error="handleError"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx / .xls / .csv 格式</div>
        </template>
      </el-upload>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

function handleSuccess(response: any) {
  ElMessage.success(`导入成功，共导入 ${response.data?.count || 0} 条记录`)
}

function handleError() {
  ElMessage.error('导入失败，请检查文件格式')
}
</script>

<style scoped>
.upload-area {
  width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
  width: 100%;
}
</style>
