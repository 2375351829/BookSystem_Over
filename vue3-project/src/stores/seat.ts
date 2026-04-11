import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { seatApi } from '@/api/seat'

export const useSeatStore = defineStore('seat', () => {
  const rooms = ref<any[]>([])
  const seats = ref<any[]>([])
  const currentRoom = ref<any>(null)
  const selectedDate = ref('')
  const loading = ref(false)
  const reservations = ref<any[]>([])
  const total = ref(0)

  function unwrapResponse(res: any) {
    if (!res) return null
    if (typeof res === 'object' && 'success' in res) {
      return res.data !== undefined ? res.data : res
    }
    return res
  }

  async function fetchRooms() {
    loading.value = true
    try {
      const res: any = await seatApi.getRooms()
      const data = unwrapResponse(res)
      rooms.value = Array.isArray(data) ? data : (data || [])
    } finally {
      loading.value = false
    }
  }

  async function fetchReadingRoom(id: number, date?: string) {
    loading.value = true
    try {
      const res: any = await seatApi.getRoomLayout(id, date)
      const data = unwrapResponse(res)
      currentRoom.value = data?.room || null
      selectedDate.value = data?.date || date || ''
      seats.value = (Array.isArray(data?.seats) ? data.seats : []).map((s: any) => ({
        ...s,
        seatNumber: s.seatNumber || s.seat_no,
        status: typeof s.status === 'number' ? s.status :
          ({ available: 0, reserved: 1, occupied: 1, maintenance: 2 }[String(s.status)] ?? 0)
      }))
      return data
    } finally {
      loading.value = false
    }
  }

  async function fetchRoomSeats(roomId: number, date?: string) {
    return fetchReadingRoom(roomId, date)
  }

  async function reserveSeat(data: { seatId: number; reservationDate: string; startTime: string; endTime: string }) {
    loading.value = true
    try {
      const res: any = await seatApi.reserveSeat(data)
      const result = unwrapResponse(res)
      if (result && result !== true && !result.success && result.message) {
        throw new Error(result.message)
      }
      if (result) {
        reservations.value.unshift(result)
        total.value++
      }
      return result
    } finally {
      loading.value = false
    }
  }

  async function cancelReservation(id: number) {
    loading.value = true
    try {
      await seatApi.cancelReservation(id)
      const index = reservations.value.findIndex((r: any) => r.id === id)
      if (index !== -1) {
        reservations.value.splice(index, 1)
        total.value--
      }
    } finally {
      loading.value = false
    }
  }

  async function checkIn(id: number) {
    loading.value = true
    try {
      await seatApi.checkIn(id)
      const item = reservations.value.find((r: any) => r.id === id)
      if (item) item.status = 2
    } finally {
      loading.value = false
    }
  }

  async function fetchMyReservations(page: number = 1, size: number = 10) {
    loading.value = true
    try {
      const res: any = await seatApi.getMyReservations(page, size)
      const data = unwrapResponse(res)
      if (data) {
        const newRecords = Array.isArray(data.records) ? data.records : (Array.isArray(data) ? data : [])
        if (newRecords.length > 0 || reservations.value.length === 0) {
          reservations.value = newRecords
        }
        total.value = data.total || reservations.value.length
      }
    } catch (e: any) {
      console.error('获取预约记录失败:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchMyActiveReservations() {
    loading.value = true
    try {
      const res: any = await seatApi.getMyActiveReservations()
      const data = unwrapResponse(res)
      reservations.value = Array.isArray(data) ? data : []
      total.value = reservations.value.length
    } finally {
      loading.value = false
    }
  }

  function getSeatStatusText(status: string | number) {
    const map: Record<string, string> = {
      available: '可用', reserved: '已预约', occupied: '使用中',
      maintenance: '维护中', '0': '可用', '1': '已预约/使用中', '2': '维护中'
    }
    return map[String(status)] || '未知'
  }

  function getSeatStatusType(status: string | number): '' | 'success' | 'warning' | 'danger' | 'info' {
    const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
      available: 'success', reserved: 'warning', occupied: 'danger',
      maintenance: 'info', '0': 'success', '1': 'warning', '2': 'info'
    }
    return map[String(status)] || 'info'
  }

  function reset() {
    rooms.value = []
    seats.value = []
    currentRoom.value = null
    selectedDate.value = ''
    reservations.value = []
    total.value = 0
  }

  return {
    rooms, seats, currentRoom, selectedDate, loading, reservations, total,
    fetchRooms, fetchReadingRoom, fetchRoomSeats,
    reserveSeat, cancelReservation, checkIn,
    fetchMyReservations, fetchMyActiveReservations,
    getSeatStatusText, getSeatStatusType, reset
  }
})
