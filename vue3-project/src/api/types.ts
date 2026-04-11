export interface Book {
  id: number
  isbn: string
  title: string
  subtitle?: string
  titleEn?: string
  author: string
  authorEn?: string
  translator?: string
  publisher?: string
  publishYear?: string
  edition?: string
  category: string
  categoryName?: string
  language: string
  pages?: number
  price?: number
  tags?: string
  coverUrl?: string
  summary?: string
  summaryEn?: string
  status: string
  totalCopies: number
  availableCopies: number
  location?: string
  shelfNo?: string
  deleted: number
  createTime?: string
  updateTime?: string
  rating?: number
  ratingCount?: number
}

export interface Borrow {
  id: number
  userId: number
  bookId: number
  bookBarcode?: string
  borrowDate: string
  dueDate: string
  returnDate?: string
  renewCount: number
  maxRenewCount?: number
  status: number
  operatorId?: number
  remarks?: string
  deleted: number
  createTime?: string
  updateTime?: string
  bookTitle?: string
  bookIsbn?: string
  bookCover?: string
  username?: string
}

export interface User {
  id: number
  username: string
  password?: string
  userType: string
  realName?: string
  phone?: string
  email?: string
  idCard?: string
  institution?: string
  role: string
  language: string
  status: number
  deleted: number
  createTime?: string
  updateTime?: string
  avatar?: string
}

export interface Notification {
  id: number
  type: 'borrow' | 'reservation' | 'system' | 'fine'
  title: string
  content: string
  isRead: boolean
  createdAt: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pages: number
  current: number
  size: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface Reservation {
  id: number
  bookId: number
  bookTitle: string
  status: string
  reserveDate: string
  expireDate: string
}

export interface Inquiry {
  id: number
  title: string
  content: string
  category: string
  status: string
  replyContent?: string
  replyDate?: string
  createdAt: string
  username?: string
  createTime?: string
  replies?: InquiryReply[]
  priority?: string
}

export interface InquiryReply {
  id: number
  content: string
  createdAt: string
  username?: string
}

export interface FineRecord {
  id: number
  bookId: number
  bookTitle: string
  overdueDays: number
  amount: number
  status: string
  createdAt: string
}

export interface Category {
  id: number
  code: string
  name: string
  parentId?: number
  children?: Category[]
  bookCount?: number
  sortOrder?: number
  description?: string
}

export interface OperationLog {
  id: number
  userId?: number
  username: string
  operation: string
  module: string
  method?: string
  description: string
  ipAddress: string
  userAgent?: string
  status: number
  createdAt: string
  duration?: number
  requestData?: string
  responseData?: string
  errorMessage?: string
}

export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  configType: string
  description?: string
}

export interface ReadingRoom {
  id: number
  name: string
  capacity: number
  status: string
  location?: string
  openTime?: string
  closeTime?: string
  description?: string
}

export interface Seat {
  id: number
  roomId: number
  seatNo: string
  seatNumber?: string
  row: number
  col: number
  column?: number
  status: 'available' | 'reserved' | 'in_use' | 'maintenance' | number | string
  lastUser?: string
  lastUseTime?: string
  hasSocket?: boolean
  nearWindow?: boolean
}

export interface BookImportBatch {
  id: number
  batchNo: string
  operatorId: number
  totalCount: number
  successCount: number
  failCount: number
  status: number
  deleted: number
  createTime: string
}
