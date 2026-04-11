import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

export function setupRouterGuard(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    const appTitle = '城市图书馆管理系统'
    document.title = to.meta.title ? `${to.meta.title} - ${appTitle}` : appTitle

    const authStore = useAuthStore()

    // 加载用户信息（如果已登录但用户信息为空）
    if (authStore.isLoggedIn && !authStore.user) {
      try {
        await authStore.initAuth()
      } catch (e) {
        console.error('Failed to init auth:', e)
      }
    }

    if (authStore.isLoggedIn) {
      if (to.path === '/login') {
        next({ path: (to.query.redirect as string) || '/' })
        return
      }
      if (to.meta.requiresAdmin) {
        if (!authStore.isAdmin) {
          next({ path: '/' })
          return
        }
      }
      next()
      return
    }

    if (to.meta.requiresAuth !== false) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    next()
  })

  router.afterEach(() => {})
}
