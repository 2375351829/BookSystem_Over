import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(
    localStorage.getItem('token') || sessionStorage.getItem('token') || ''
  )
  const user = ref<any>(null)
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => {
    if (!user.value) return false
    const type = (user.value.userType || '').toUpperCase()
    const role = (user.value.role || '').toUpperCase()
    return type === 'ADMIN' || role === 'ADMIN' || role === 'ROLE_ADMIN'
  })

  async function initAuth() {
    const savedToken = localStorage.getItem('token') || sessionStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
      if (!user.value) {
        try {
          await getUserInfo()
        } catch (e) {
          console.error('Failed to init auth:', e)
        }
      }
    }
  }

  async function login(loginData: { username: string; password: string; captcha: string; remember?: boolean }) {
    const res: any = await authApi.login(loginData)
    const accessToken = res.accessToken || res.token
    token.value = accessToken
    user.value = res.user
    if (loginData.remember) {
      localStorage.setItem('token', accessToken)
      localStorage.setItem('user', JSON.stringify(res.user))
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('user')
    } else {
      sessionStorage.setItem('token', accessToken)
      sessionStorage.setItem('user', JSON.stringify(res.user))
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  async function getUserInfo() {
    const res: any = await authApi.getUserInfo()
    user.value = res.data || res
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
    authApi.logout().catch(() => {})
  }

  function loadUserFromStorage() {
    if (!user.value) {
      const savedUser = localStorage.getItem('user') || sessionStorage.getItem('user')
      if (savedUser) {
        try {
          user.value = JSON.parse(savedUser)
        } catch (e) {
          console.error('Failed to parse saved user:', e)
        }
      }
    }
  }

  loadUserFromStorage()

  return { token, user, isLoggedIn, isAdmin, initAuth, login, getUserInfo, logout }
})
