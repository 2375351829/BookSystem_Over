import request from '@/utils/request'

export type ExportFormat = 'excel' | 'csv' | string

export interface ExportTask {
  id: number
  type: string
  format: ExportFormat
  status: 'pending' | 'processing' | 'completed' | 'failed'
  fileUrl?: string
  createdAt: string
  completedAt?: string
}

export interface BookExportParams {
  type?: string
  title?: string
  author?: string
  isbn?: string
  category?: string
  status?: string
  startDate?: string
  endDate?: string
  format?: ExportFormat
}

export interface BorrowStatisticsExportParams {
  startDate?: string
  endDate?: string
  groupBy?: 'day' | 'week' | 'month'
}

export interface UserExportParams {
  username?: string
  type?: string
  status?: string
  startDate?: string
  endDate?: string
}

export const exportApi = {
  exportBooks(params: BookExportParams) {
    return request.get('/export/books', { params, responseType: 'blob' })
  },
  exportBorrows(params: any) {
    return request.get('/export/borrow-records', { params, responseType: 'blob' })
  },
  exportUsers(params: UserExportParams) {
    return request.get('/export/users', { params, responseType: 'blob' })
  },
  exportBorrowStatistics(params: BorrowStatisticsExportParams) {
    return request.get('/export/borrow-statistics', { params, responseType: 'blob' })
  },
  getExportTasks() {
    return request.get('/export/tasks')
  },
  downloadExportFile(fileId: number) {
    return request.get(`/export/download/${fileId}`, { responseType: 'blob' })
  }
}

export const {
  exportBooks,
  exportBorrows,
  exportUsers,
  exportBorrowStatistics,
  getExportTasks,
  downloadExportFile
} = exportApi
