import { defineStore } from 'pinia'
import { borrowApi } from '@/api/borrow'
import request from '@/utils/request'

interface BorrowRecord {
  id: number
  userId: number
  bookId: number
  bookBarcode: string
  borrowDate: string
  dueDate: string
  returnDate: string | null
  renewCount: number
  status: number
  operatorId: number
  remarks: string | null
  deleted: number
  createTime: string
  updateTime: string
}

interface Book {
  id: number
  isbn: string
  title: string
  titleEn: string
  author: string
  authorEn: string
  publisher: string
  publishYear: string
  category: string
  categoryName: string
  language: string
  pages: number
  price: number
  coverUrl: string
  summary: string
  summaryEn: string
  status: string
  totalCopies: number
  availableCopies: number
  location: string
  shelfNo: string
  deleted: number
  createTime: string
  updateTime: string
}

export interface BorrowRecordWithBook {
  id: number
  bookId: number
  title: string
  author: string
  borrowDate: string
  dueDate: string
  returnDate: string | null
  renewCount: number
  status: number
  coverUrl?: string
}

export const useBorrowStore = defineStore('borrow', {
  state: () => ({
    borrowRecords: [] as BorrowRecord[],
    books: new Map<number, Book>(),
    loading: false,
    error: null as string | null,
    total: 0,
    currentPage: 1,
    pageSize: 10
  }),

  getters: {
    borrowedBooks: (state): BorrowRecordWithBook[] => {
      return state.borrowRecords
        .filter(record => record.status === 0)
        .map(record => {
          const book = state.books.get(record.bookId)
          return {
            id: record.id,
            bookId: record.bookId,
            title: book?.title || '未知图书',
            author: book?.author || '未知作者',
            borrowDate: formatDate(record.borrowDate),
            dueDate: formatDate(record.dueDate),
            returnDate: record.returnDate ? formatDate(record.returnDate) : null,
            renewCount: record.renewCount,
            status: record.status,
            coverUrl: book?.coverUrl
          }
        })
    },

    returnedBooks: (state): BorrowRecordWithBook[] => {
      return state.borrowRecords
        .filter(record => record.status === 1)
        .map(record => {
          const book = state.books.get(record.bookId)
          return {
            id: record.id,
            bookId: record.bookId,
            title: book?.title || '未知图书',
            author: book?.author || '未知作者',
            borrowDate: formatDate(record.borrowDate),
            dueDate: formatDate(record.dueDate),
            returnDate: record.returnDate ? formatDate(record.returnDate) : null,
            renewCount: record.renewCount,
            status: record.status,
            coverUrl: book?.coverUrl
          }
        })
    }
  },

  actions: {
    async fetchBorrowRecords(status?: number) {
      this.loading = true
      this.error = null

      try {
        const params: Record<string, string | number> = {
          page: this.currentPage,
          size: this.pageSize
        }

        if (status !== undefined) {
          params.status = status
        }

        const res: any = await borrowApi.getBorrowRecords(params)

        if (res) {
          this.borrowRecords = res.records || []
          this.total = res.total || 0

          const bookIds = [...new Set(this.borrowRecords.map(record => record.bookId))]
          await this.fetchBooks(bookIds)
        }
      } catch (error: any) {
        this.error = error.response?.data?.message || error.message || '获取借阅记录失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchBooks(bookIds: number[]) {
      const fetchPromises = bookIds.map(bookId => {
        if (!this.books.has(bookId)) {
          return request.get(`/books/${bookId}`)
            .then((response: any) => {
              if (response) {
                this.books.set(bookId, response)
              }
            })
            .catch(() => {})
        }
        return Promise.resolve()
      })

      await Promise.all(fetchPromises)
    },

    async returnBook(recordId: number) {
      this.loading = true
      this.error = null

      try {
        const res: any = await borrowApi.returnBook(recordId)
        if (res && res.success !== false) {
          await this.fetchBorrowRecords()
          return true
        } else {
          throw new Error(res?.message || '归还失败')
        }
      } catch (error: any) {
        this.error = error.response?.data?.message || error.message || '归还失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async renewBook(recordId: number) {
      this.loading = true
      this.error = null

      try {
        const res: any = await borrowApi.renewBook(recordId)
        if (res && res.success !== false) {
          await this.fetchBorrowRecords()
          return res.newDueDate
        } else {
          throw new Error(res?.message || '续借失败')
        }
      } catch (error: any) {
        this.error = error.response?.data?.message || error.message || '续借失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    setPage(page: number) {
      this.currentPage = page
    },

    setPageSize(size: number) {
      this.pageSize = size
    },

    clearError() {
      this.error = null
    }
  }
})

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
