import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { setupRouterGuard } from './guard'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: { title: '首页', requiresAuth: true },
    children: [
      { path: '', name: 'HomePage', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
      { path: 'books', name: 'BookList', component: () => import('@/views/BookListView.vue'), meta: { title: '图书列表' } },
      { path: 'books/:id', name: 'BookDetail', component: () => import('@/views/BookDetailView.vue'), meta: { title: '图书详情' } },
      { path: 'books/:id/reviews', name: 'BookReviews', component: () => import('@/views/BookReviewView.vue'), meta: { title: '图书评价' } },
      { path: 'borrows', name: 'MyBorrows', component: () => import('@/views/MyBorrowView.vue'), meta: { title: '我的借阅' } },
      { path: 'reservations', name: 'MyReservations', component: () => import('@/views/MyReservationView.vue'), meta: { title: '我的预约' } },
      { path: 'seats', name: 'SeatReservation', component: () => import('@/views/SeatReservation.vue'), meta: { title: '座位预约' } },
      { path: 'my-seats', name: 'MySeatReservations', component: () => import('@/views/MySeatReservationView.vue'), meta: { title: '我的座位' } },
      { path: 'inquiries', name: 'Inquiry', component: () => import('@/views/InquiryView.vue'), meta: { title: '咨询中心' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/UserProfileView.vue'), meta: { title: '个人中心' } },
      { path: 'notifications', name: 'Notifications', component: () => import('@/views/NotificationView.vue'), meta: { title: '消息中心' } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/UserFavoritesView.vue'), meta: { title: '我的收藏' } },
      { path: 'history', name: 'History', component: () => import('@/views/UserHistoryView.vue'), meta: { title: '阅读历史' } },
      { path: 'fines', name: 'Fines', component: () => import('@/views/UserFinesView.vue'), meta: { title: '罚款管理' } },
      { path: 'exports', name: 'Exports', component: () => import('@/views/UserExportsView.vue'), meta: { title: '数据导出' } }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/AdminDashboardView.vue'), meta: { title: '管理仪表盘' } },
      { path: 'books', name: 'AdminBooks', component: () => import('@/views/admin/AdminBookView.vue'), meta: { title: '图书管理' } },
      { path: 'books/add', name: 'AdminBookAdd', component: () => import('@/views/admin/AdminBookAddView.vue'), meta: { title: '图书录入' } },
      { path: 'books/import', name: 'AdminBookImport', component: () => import('@/views/admin/AdminBookImportView.vue'), meta: { title: '图书批量导入' } },
      { path: 'acquisition', name: 'AdminAcquisition', component: () => import('@/views/admin/AdminAcquisitionView.vue'), meta: { title: '图书采编' } },
      { path: 'categories', name: 'AdminCategories', component: () => import('@/views/admin/AdminCategoryView.vue'), meta: { title: '分类管理' } },
      { path: 'seats', name: 'AdminSeats', component: () => import('@/views/admin/AdminSeatView.vue'), meta: { title: '座位管理' } },
      { path: 'statistics', name: 'AdminStatistics', component: () => import('@/views/admin/AdminStatisticsView.vue'), meta: { title: '统计报表' } },
      { path: 'config', name: 'AdminConfig', component: () => import('@/views/admin/AdminConfigView.vue'), meta: { title: '系统配置' } },
      { path: 'logs', name: 'AdminLogs', component: () => import('@/views/admin/AdminLogView.vue'), meta: { title: '操作日志' } },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/AdminUserView.vue'), meta: { title: '用户管理' } },
      { path: 'borrows', name: 'AdminBorrows', component: () => import('@/views/admin/AdminBorrowView.vue'), meta: { title: '借阅管理' } },
      { path: 'inquiries', name: 'AdminInquiries', component: () => import('@/views/admin/AdminInquiryView.vue'), meta: { title: '咨询管理' } }
    ]
  },
  { path: '/404', name: 'NotFound', component: () => import('@/views/NotFoundView.vue'), meta: { title: '页面不存在' } },
  { path: '/:pathMatch(.*)*', redirect: '/404' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

setupRouterGuard(router)

export default router
