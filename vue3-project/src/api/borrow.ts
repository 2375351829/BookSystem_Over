import request from '@/utils/request'

export const borrowApi = {
  borrowBook(bookId: number, bookBarcode?: string) {
    return request.post('/borrow/borrow', null, {
      params: { bookId, bookBarcode: bookBarcode || 'N/A' }
    })
  },
  returnBook(recordId: number) {
    return request.post('/borrow/return', null, {
      params: { recordId }
    })
  },
  renewBook(recordId: number) {
    return request.post('/borrow/renew', null, {
      params: { recordId }
    })
  },
  getMyBorrows(params?: any) {
    return request.get('/borrow/my', { params })
  },
  getBorrowCalendar(params?: any) {
    return request.get('/borrow/calendar', { params })
  },
  getBorrowRecords(params?: any) {
    return request.get('/borrow/records', { params })
  },
  getAdminBorrowRecords(params?: any) {
    return request.get('/borrow/admin/records', { params })
  },
  exportBorrows(params?: any) {
    return request.get('/borrow/export', { params, responseType: 'blob' })
  },
  batchReturn(ids: number[]) {
    return request.post('/borrow/batch-return', { ids })
  },
  adminBorrow(data: any) {
    return request.post('/borrow/admin/borrow', data)
  }
}

export const {
  borrowBook,
  returnBook,
  renewBook,
  getMyBorrows,
  getBorrowCalendar,
  getBorrowRecords,
  getAdminBorrowRecords,
  exportBorrows,
  batchReturn,
  adminBorrow
} = borrowApi
