<template>
  <el-tag :type="statusConfig.type" :effect="statusConfig.effect" size="small">
    {{ statusConfig.label }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: string | number
  availableCopies?: number
}

const props = defineProps<Props>()

interface StatusConfig {
  type: '' | 'success' | 'warning' | 'danger' | 'info'
  effect: 'dark' | 'light' | 'plain'
  label: string
}

const statusMap: Record<string, StatusConfig> = {
  available: { type: 'success', effect: 'light', label: '在馆' },
  borrowed: { type: 'warning', effect: 'light', label: '已借出' },
  reserved: { type: 'info', effect: 'light', label: '已预约' },
  returned: { type: '', effect: 'plain', label: '已归还' },
  overdue: { type: 'danger', effect: 'light', label: '已逾期' },
  active: { type: 'success', effect: 'light', label: '正常' },
  disabled: { type: 'info', effect: 'light', label: '停用' },
  pending: { type: 'warning', effect: 'light', label: '待处理' },
  approved: { type: 'success', effect: 'light', label: '已通过' },
  rejected: { type: 'danger', effect: 'light', label: '已拒绝' }
}

const borrowStatusMap: Record<number, StatusConfig> = {
  0: { type: 'warning', effect: 'light', label: '借阅中' },
  1: { type: '', effect: 'plain', label: '已归还' },
  2: { type: 'danger', effect: 'light', label: '已逾期' }
}

const statusConfig = computed<StatusConfig>(() => {
  if (props.availableCopies !== undefined) {
    if (props.availableCopies > 0) {
      return { type: 'success', effect: 'light', label: '可借' }
    } else {
      return { type: 'danger', effect: 'light', label: '已借完' }
    }
  }
  const key = String(props.status)
  if (borrowStatusMap[props.status as number]) {
    return borrowStatusMap[props.status as number]
  }
  return statusMap[key] || { type: 'info', effect: 'light', label: key }
})
</script>

<style scoped>
</style>
