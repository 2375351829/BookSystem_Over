import request from '@/utils/request'

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

export interface CreateInquiryRequest {
  title: string
  content: string
  category: string
  priority?: string
}

export interface ReplyInquiryRequest {
  replyContent: string
  status?: string
}

export const inquiryApi = {
  submitInquiry(data: CreateInquiryRequest) {
    return request.post('/inquiries', data)
  },
  getMyInquiries(params?: any) {
    return request.get('/inquiries', { params })
  },
  replyInquiry(id: number, data: ReplyInquiryRequest) {
    return request.post(`/inquiries/${id}/reply`, data)
  },
  getAllInquiries(params?: any) {
    return request.get('/inquiries/all', { params })
  },
  closeInquiry(id: number) {
    return request.put(`/inquiries/${id}/close`)
  },
  getUserInquiries(params?: any) {
    return request.get('/inquiries', { params })
  }
}

export const {
  submitInquiry,
  getMyInquiries,
  replyInquiry,
  getAllInquiries,
  closeInquiry,
  getUserInquiries
} = inquiryApi
