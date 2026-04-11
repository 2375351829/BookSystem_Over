import request from '@/utils/request'
import type { PageResponse } from './index'

export interface PurchaseRequest {
  id: number
  userId: number
  username: string
  bookName: string
  author: string
  isbn: string
  publisher: string
  reason: string
  status: number
  reviewComment: string
  reviewerName: string
  createTime: string
  updateTime: string
}

export interface ImportBatch {
  id: number
  batchNo: string
  operatorId: number
  operatorName: string
  totalBooks: number
  totalQuantity: number
  status: number
  createTime: string
}

export interface ImportBatchItem {
  id: number
  batchId: number
  bookId: number
  bookName: string
  author: string
  isbn: string
  publisher: string
  quantity: number
  price: number
}

export interface CreatePurchaseRequest {
  bookName: string
  author: string
  isbn?: string
  publisher?: string
  reason: string
}

export interface ReviewPurchaseRequest {
  approved: boolean
  reviewComment: string
}

export interface CreateImportBatchRequest {
  items: {
    bookName: string
    author: string
    isbn?: string
    publisher?: string
    quantity: number
    price: number
  }[]
}

export interface PurchaseQueryParams {
  page?: number
  size?: number
  status?: number
}

export const submitPurchaseRequest = (data: CreatePurchaseRequest) => {
  return request.post<PurchaseRequest>('/purchase/request', data)
}

export const reviewPurchaseRequest = (id: number, data: ReviewPurchaseRequest) => {
  return request.put<PurchaseRequest>(`/purchase/request/${id}/review`, data)
}

export const getPurchaseRequests = async (params?: PurchaseQueryParams) => {
  const res = await request.get<PageResponse<PurchaseRequest>>('/purchase/requests', { params })
  return (res as any).data?.data || (res as any).data || res
}

export const getMyPurchaseRequests = async (params?: PurchaseQueryParams) => {
  const res = await request.get<PageResponse<PurchaseRequest>>('/purchase/my-requests', { params })
  return (res as any).data?.data || (res as any).data || res
}

export const getPurchaseRequestById = (id: number) => {
  return request.get<PurchaseRequest>(`/purchase/request/${id}`)
}

export const completePurchaseRequest = (id: number) => {
  return request.put<PurchaseRequest>(`/purchase/request/${id}/complete`)
}

export const createImportBatch = (data: CreateImportBatchRequest) => {
  return request.post<ImportBatch>('/purchase/import-batch', data)
}

export const processBatchImport = (id: number) => {
  return request.post<ImportBatch>(`/purchase/import-batch/${id}/process`)
}

export const getImportBatches = async (params?: PurchaseQueryParams) => {
  const res = await request.get<PageResponse<ImportBatch>>('/purchase/import-batches', { params })
  return (res as any).data?.data || (res as any).data || res
}

export const getImportBatchById = (id: number) => {
  return request.get<ImportBatch & { items: ImportBatchItem[] }>(`/purchase/import-batch/${id}`)
}
