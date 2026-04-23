import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)

  async function fetchUnreadCount() {
    try {
      const res: any = await request.get('/notifications/unread-count')
      unreadCount.value = res.data || res || 0
    } catch (e) {
      console.error('Failed to fetch unread count:', e)
    }
  }

  function decrementUnread() {
    if (unreadCount.value > 0) {
      unreadCount.value--
    }
  }

  function resetUnread() {
    unreadCount.value = 0
  }

  return { unreadCount, fetchUnreadCount, decrementUnread, resetUnread }
})
