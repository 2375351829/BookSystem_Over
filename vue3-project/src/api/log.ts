import request from '@/utils/request'

export const logApi = {
  getLogs(params?: any) {
    return request.get('/logs', { params })
  },
  getLogDetail(id: number) {
    return request.get(`/logs/${id}`)
  },
  exportLogs(params?: any) {
    return request.get('/logs/export', { 
      params,
      responseType: 'blob'
    })
  },
  getLogStatus() {
    return request.get('/logs/stream/status')
  },
  pauseStream(sessionId: string) {
    return request.post(`/logs/stream/pause/${sessionId}`)
  },
  resumeStream(sessionId: string) {
    return request.post(`/logs/stream/resume/${sessionId}`)
  },
  closeStream(sessionId: string) {
    return request.delete(`/logs/stream/${sessionId}`)
  },
  clearLogs() {
    return request.delete('/logs')
  }
}
