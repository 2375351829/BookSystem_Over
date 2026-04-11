import request from '@/utils/request'

export const userApi = {
  getProfile() {
    return request.get('/users/me')
  },
  updateProfile(data: any) {
    return request.put('/users/me', data)
  },
  uploadAvatar(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/avatar', formData)
  },
  updatePreferences(data: any) {
    return request.put('/users/preferences', data)
  },
  updateNotificationSettings(data: any) {
    return request.put('/users/notification-settings', data)
  },
  getUsers(params?: any) {
    return request.get('/users', { params })
  },
  createUser(data: any) {
    return request.post('/users', data)
  },
  updateUser(id: number, data: any) {
    return request.put(`/users/${id}`, data)
  },
  toggleUserStatus(id: number, status: string) {
    return request.put(`/users/${id}/status`, { status })
  },
  resetUserPassword(id: number) {
    return request.put(`/users/${id}/reset-password`)
  }
}
