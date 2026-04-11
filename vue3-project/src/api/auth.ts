import request from '@/utils/request'

export const authApi = {
  login(data: { username: string; password: string; captcha: string }) {
    return request.post('/auth/login', data)
  },
  logout() {
    return request.post('/auth/logout')
  },
  getUserInfo() {
    return request.get('/auth/user/info')
  },
  getCaptcha() {
    return request.get('/auth/captcha')
  },
  sendEmailCode(email: string) {
    return request.post('/auth/email/code', { email })
  },
  resetPassword(data: { email: string; code: string; newPassword: string }) {
    return request.post('/auth/password/reset', data)
  },
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return request.post('/auth/password/change', data)
  }
}
