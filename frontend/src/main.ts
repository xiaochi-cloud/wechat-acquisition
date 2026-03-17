import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: () => import('./views/Dashboard/index.vue'), meta: { title: '仪表盘' } },
  { path: '/campaigns', component: () => import('./views/Campaign/index.vue'), meta: { title: '获客活动' } },
  { path: '/contacts', component: () => import('./views/Contact/index.vue'), meta: { title: '联系人' } },
  { path: '/conversations', component: () => import('./views/Conversation/index.vue'), meta: { title: '会话管理' } },
  { path: '/scoring', component: () => import('./views/Scoring/index.vue'), meta: { title: '意向打分' } },
  { path: '/settings', component: () => import('./views/Settings/index.vue'), meta: { title: '系统设置' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')

console.log('🚀 企业微信获客平台前端已启动')
