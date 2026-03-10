import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'campaign',
        name: 'Campaign',
        component: () => import('@/views/Campaign/index.vue'),
        meta: { title: '获客活动' }
      },
      {
        path: 'contact',
        name: 'Contact',
        component: () => import('@/views/Contact/index.vue'),
        meta: { title: '联系人管理' }
      },
      {
        path: 'conversation',
        name: 'Conversation',
        component: () => import('@/views/Conversation/index.vue'),
        meta: { title: '会话管理' }
      },
      {
        path: 'scoring',
        name: 'Scoring',
        component: () => import('@/views/Scoring/index.vue'),
        meta: { title: '意向打分' }
      },
      {
        path: 'template',
        name: 'Template',
        component: () => import('@/views/Template/index.vue'),
        meta: { title: '话术模板' }
      },
      {
        path: 'data',
        name: 'Data',
        component: () => import('@/views/Data/index.vue'),
        meta: { title: '数据报表' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/Settings/index.vue'),
        meta: { title: '系统设置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
