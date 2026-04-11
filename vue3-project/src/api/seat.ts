import request from '@/utils/request'

export interface ReadingRoom {
  id: number
  name: string
  location: string
  capacity: number
  openTime: string
  closeTime: string
  status: number
}

export interface Seat {
  id: number
  roomId: number
  seatNumber: string
  row: number
  column: number
  status: number | string
  hasSocket?: boolean
  nearWindow?: boolean
}

export interface SeatReservation {
  id: number
  userId: number
  seatId: number
  seatNumber?: string
  roomName?: string
  roomLocation?: string
  reserveDate: string
  startTime: string
  endTime: string
  status: number | string
  checkInTime?: string
  checkOutTime?: string
  createTime?: string
}

export interface RoomFormData {
  id?: number
  name: string
  capacity: number
  location?: string
  openTime?: string
  closeTime?: string
  description?: string
  status?: number | string
}

export interface SeatFormData {
  id?: number
  roomId: number
  seatNumber?: string
  rows?: number
  cols?: number
  row?: number
  column?: number
  status?: number | string
  hasSocket?: boolean
  nearWindow?: boolean
  prefix?: string
}

export const seatApi = {
  getRooms() {
    return request.get('/seats/rooms')
  },
  getRoomLayout(roomId: number, date?: string) {
    const params = date ? { date } : {}
    return request.get(`/seats/room/${roomId}`, { params })
  },
  reserveSeat(data: { seatId: number; reservationDate: string; startTime: string; endTime: string }) {
    return request.post('/seats/reserve', null, {
      params: {
        seatId: data.seatId,
        reserveDate: data.reservationDate,
        startTime: data.startTime,
        endTime: data.endTime
      }
    })
  },
  cancelReservation(reservationId: number) {
    return request.delete(`/seats/reserve/${reservationId}`)
  },
  checkIn(reservationId: number) {
    return request.post(`/seats/check-in/${reservationId}`)
  },
  checkOut(reservationId: number) {
    return request.post(`/seats/check-out/${reservationId}`)
  },
  getMyReservations(page: number = 1, size: number = 10) {
    return request.get('/seats/my-reservations', { params: { page, size } })
  },
  getMyActiveReservations() {
    return request.get('/seats/my-active-reservations')
  },
  getReservationDetail(id: number) {
    return request.get(`/seats/reservation/${id}`)
  },
  updateSeatStatus(seatId: number, data: { status?: string | number; action?: string } | string) {
    const body = typeof data === 'string' ? { status: data } : data
    return request.put(`/seats/seat/${seatId}/status`, body)
  },
  getRoomUsageStats(roomId: number) {
    return request.get(`/seats/rooms/${roomId}/stats`)
  },
  createRoom(data: any) {
    return request.post('/seats/rooms', data)
  },
  updateRoom(id: number, data: any) {
    return request.put(`/seats/rooms/${id}`, data)
  },
  deleteRoom(id: number) {
    return request.delete(`/seats/rooms/${id}`)
  },
  createSeats(roomId: number, data: any) {
    return request.post(`/seats/rooms/${roomId}/seats`, data)
  },
  deleteSeat(id: number) {
    return request.delete(`/seats/seats/${id}`)
  },
  getAllReservations(roomId?: number | null, date?: Date | null, page: number = 1, size: number = 10) {
    const params: any = { page, size }
    if (roomId) params.roomId = roomId
    if (date) params.date = date.toISOString().split('T')[0]
    return request.get('/seats/reservations', { params })
  }
}
