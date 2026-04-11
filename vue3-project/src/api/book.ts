import request from '@/utils/request'

export const bookApi = {
  getBooks(params?: any) {
    return request.get('/books', { params })
  },
  getBookDetail(id: number) {
    return request.get(`/books/${id}`)
  },
  getHotBooks() {
    return request.get('/books/hot')
  },
  getNewBooks() {
    return request.get('/books/new')
  },
  getRecommendedBooks() {
    return request.get('/recommendations/popular')
  },
  getSimilarBooks(id: number) {
    return request.get(`/recommendations/similar/${id}`)
  },
  createBook(data: any) {
    return request.post('/books', data)
  },
  updateBook(id: number, data: any) {
    return request.put(`/books/${id}`, data)
  },
  deleteBook(id: number) {
    return request.delete(`/books/${id}`)
  },
  batchDeleteBooks(ids: number[]) {
    return request.post('/books/batch-delete', { ids })
  },
  batchUpdateBooks(ids: number[], data: any) {
    return request.put('/books/batch-update', { ids, data })
  },
  importBooks(data: any) {
    return request.post('/books/import', data, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  exportBooks(params?: any) {
    return request.post('/books/export', params, { responseType: 'blob' })
  },
  getBookStats() {
    return request.get('/books/stats')
  },
  uploadCover(data: FormData) {
    return request.post('/upload/cover', data, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const {
  getBooks,
  getBookDetail,
  getHotBooks,
  getNewBooks,
  getRecommendedBooks,
  getSimilarBooks,
  createBook,
  updateBook,
  deleteBook,
  batchDeleteBooks,
  importBooks,
  exportBooks,
  getBookStats
} = bookApi
