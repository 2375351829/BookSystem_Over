<template>
  <div class="user-fines-view">
    <el-card>
      <template #header>
        <span>罚款管理</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="未缴罚款" name="unpaid" />
        <el-tab-pane label="已缴罚款" name="paid" />
      </el-tabs>

      <div v-if="fines.length === 0" class="empty-text">暂无罚款记录</div>
      <el-table v-else :data="fines" stripe>
        <el-table-column prop="bookTitle" label="书名" />
        <el-table-column prop="amount" label="罚款金额(元)" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="createTime" label="产生时间" />
        <el-table-column v-if="activeTab === 'unpaid'" label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="payFine(row.id)">缴纳</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('unpaid')
const fines = ref<any[]>([])

async function loadFines() {
  try {
    const params: any = { page: 0, size: 20, paid: activeTab.value === 'paid' }
    const res: any = await request.get('/fines', { params })
    const data = res.data || res
    fines.value = data.content || data || []
  } catch (e) {
    console.error('Failed to load fines:', e)
  }
}

async function payFine(id: number) {
  try {
    await request.put(`/fines/${id}/pay`)
    ElMessage.success('缴纳成功')
    loadFines()
  } catch (e) {
    ElMessage.error('缴纳失败')
  }
}

watch(activeTab, loadFines)
onMounted(loadFines)
</script>

<style scoped>
.empty-text {
  text-align: center;
  color: #909399;
  padding: 40px;
}
</style>
