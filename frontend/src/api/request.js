import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加 Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的状态码不是 200，说明接口有错误
    if (!res.success) {
      ElMessage.error(res.error?.message || '请求失败')
      
      // 401: 未授权，跳转到登录页
      if (res.error?.code === 'UNAUTHORIZED') {
        localStorage.removeItem('token')
        window.location.reload()
      }
      
      return Promise.reject(new Error(res.error?.message || '请求失败'))
    }
    
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('token')
          setTimeout(() => {
            window.location.reload()
          }, 1000)
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || message
      }
    } else if (error.request) {
      message = '服务器无响应，请检查网络连接'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 封装请求方法
export function get(url, params) {
  return service.get(url, { params })
}

export function post(url, data) {
  return service.post(url, data)
}

export function put(url, data) {
  return service.put(url, data)
}

export function del(url) {
  return service.delete(url)
}

export default service
