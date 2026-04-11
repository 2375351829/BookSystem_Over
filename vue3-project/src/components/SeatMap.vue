<template>
  <div class="seat-map">
    <div
      class="seat-grid"
      :style="{ gridTemplateColumns: `repeat(${layout.cols}, 1fr)` }"
    >
      <div
        v-for="seat in seatMatrix"
        :key="seat.id"
        class="seat-item"
        :class="[`seat--${seat.status}`, { 'seat--editable': editable && seat.id > 0 }]"
        @click="handleSeatClick(seat)"
        :title="`${seat.seatNo} - ${statusLabel[seat.status]}`"
      >
        <span class="seat-no">{{ seat.seatNo }}</span>
      </div>
    </div>
    <div v-if="editable" class="seat-legend">
      <span class="legend-item" v-for="(label, key) in statusLabel" :key="key">
        <span class="legend-dot" :class="`seat--${key}`"></span>
        {{ label }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  layout: {
    rows: number
    cols: number
  }
  seats: any[]
  editable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  editable: false
})

const emit = defineEmits<{
  seatClick: [seatId: number]
}>()

interface SeatWithPosition {
  id: number
  seatNo: string
  row: number
  col: number
  status: string
}

const statusLabel: Record<string, string> = {
  available: '可用',
  reserved: '已预约',
  in_use: '使用中',
  maintenance: '维护中'
}

const seatMatrix = computed<SeatWithPosition[]>(() => {
  const matrix: SeatWithPosition[] = []
  
  const seatMap = new Map<string, any>()
  props.seats.forEach((s: any) => {
    const row = s.row || 0
    const col = s.column || s.col || 0
    const key = `${row}-${col}`
    seatMap.set(key, s)
  })

  for (let r = 1; r <= props.layout.rows; r++) {
    for (let c = 1; c <= props.layout.cols; c++) {
      const key = `${r}-${c}`
      const existingSeat = seatMap.get(key)
      if (existingSeat) {
        matrix.push({
          id: existingSeat.id,
          seatNo: existingSeat.seatNumber || existingSeat.seatNo || key,
          row: r,
          col: c,
          status: typeof existingSeat.status === 'number' 
            ? ['available', 'reserved', 'in_use', 'maintenance'][existingSeat.status] || 'available'
            : existingSeat.status
        })
      } else {
        matrix.push({
          id: -r * 100 - c,
          seatNo: key,
          row: r,
          col: c,
          status: 'maintenance'
        })
      }
    }
  }

  return matrix
})

function handleSeatClick(seat: SeatWithPosition) {
  if (seat.id <= 0) return
  emit('seatClick', seat.id)
}
</script>

<style scoped>
.seat-map {
  padding: 16px;
}

.seat-grid {
  display: grid;
  gap: 8px;
  max-width: 800px;
  margin: 0 auto;
}

.seat-item {
  aspect-ratio: 1;
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
  font-weight: 500;
  font-size: 12px;
}

.seat--available {
  background: rgba(103, 194, 58, 0.15);
  color: #67C23A;
  border-color: #67C23A;
}

.seat--reserved {
  background: rgba(230, 162, 60, 0.15);
  color: #E6A23C;
  border-color: #E6A23C;
  cursor: not-allowed;
}

.seat--in_use {
  background: rgba(64, 158, 255, 0.15);
  color: #409EFF;
  border-color: #409EFF;
  cursor: not-allowed;
}

.seat--maintenance {
  background: rgba(144, 147, 153, 0.15);
  color: #909399;
  border-color: #909399;
  cursor: not-allowed;
}

.seat--editable:hover {
  transform: scale(1.08);
  box-shadow: var(--shadow-base);
}

.seat--available.seat--editable:hover {
  background: rgba(103, 194, 58, 0.3);
}

.seat-no {
  pointer-events: none;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-regular);
}

.legend-dot {
  width: 14px;
  height: 14px;
  border-radius: 4px;
  flex-shrink: 0;
}
</style>
