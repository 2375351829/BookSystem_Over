<template>
  <div class="my-reservation-page">
    <el-card class="filter-card">
      <div class="filter-bar">
        <span class="filter-label">{{ t('reservation.statusFilter') }}:</span>
        <el-select v-model="statusFilter" :placeholder="t('reservation.allStatus')" clearable style="width:160px" @change="fetchData">
          <el-option :label="t('reservation.allStatus')" value="" />
          <el-option :label="t('reservation.pendingPickup')" value="pending" />
          <el-option :label="t('reservation.cancelled')" value="cancelled" />
          <el-option :label="t('reservation.completed')" value="completed" />
        </el-select>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column :label="t('reservation.bookName')" prop="bookTitle" min-width="200">
          <template #default="{ row }">
            <router-link :to="`/books/${row.bookId}`" class="book-link">{{ row.bookTitle }}</router-link>
          </template>
        </el-table-column>
        <el-table-column :label="t('reservation.reserveTime')" prop="reserveDate" width="170" sortable />
        <el-table-column :label="t('reservation.validUntil')" prop="expireDate" width="170" sortable />
        <el-table-column :label="t('reservation.status')" width="120">
          <template #default="{ row }">
            <StatusTag :status="mapStatus(row.status)" />
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" size="small" type="danger" plain @click="handleCancel(row)">
              {{ t('reservation.cancel') }}
            </el-button>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="total > 0" class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
      <EmptyState v-if="!loading && !list.length" :description="t('reservation.noRecords')" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import StatusTag from '@/components/StatusTag.vue'
import EmptyState from '@/components/EmptyState.vue'
import { reservationApi } from '@/api/reservation'
import type { Reservation } from '@/api/types'

const { t } = useI18n()

const loading = ref(false)
const statusFilter = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const list = ref<Reservation[]>([])

function mapStatus(s: string): string {
  const m: Record<string, string> = { pending: 'reserved', cancelled: 'returned', completed: 'available' }
  return m[s] || s
}

async function fetchData() {
  loading.value = true
  try {
    const res: any = await reservationApi.getMyReservations({
      status: statusFilter.value || undefined,
      page: page.value,
      pageSize
    })
    list.value = (res.records || []).map((item: any) => ({
      id: item.id,
      bookId: item.bookId,
      bookTitle: item.bookTitle || '未知图书',
      reserveDate: item.reserveDate,
      expireDate: item.expireDate,
      status: item.status === 0 ? 'pending' : item.status === 1 ? 'completed' : 'cancelled'
    }))
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function handleCancel(row: Reservation) {
  try {
    await ElMessageBox.confirm(t('reservation.confirmCancel'), t('common.confirm'), { type: 'warning' })
    await reservationApi.cancelReservation(row.id)
    ElMessage.success(t('reservation.cancelSuccess'))
    fetchData()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e?.message || '')
  }
}

onMounted(() => fetchData())
</script>

<style scoped>
.my-reservation-page { display: flex; flex-direction: column; gap: 20px; }
.filter-card { border-radius: var(--border-radius-base); }
.filter-bar { display: flex; align-items: center; gap: 12px; }
.filter-label { font-size: 14px; color: var(--text-regular); font-weight: 500; }
.table-card { border-radius: var(--border-radius-base); }
.book-link { color: var(--color-primary); text-decoration: none; font-weight: 500; }
.book-link:hover { text-decoration: underline; }
.no-action { color: var(--color-text-placeholder); }
.pagination-wrap { display: flex; justify-content: center; padding: 16px 0 0; }
</style>
