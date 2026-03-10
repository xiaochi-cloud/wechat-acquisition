import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    if (res.success) {
      return res
    } else {
      ElMessage.error(res.error?.message || '请求失败')
      return Promise.reject(new Error(res.error?.message || '请求失败'))
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export function get<T>(url: string, params?: any): Promise<T> {
  return service.get(url, { params }).then(res => res.data as T)
}

export function post<T>(url: string, data?: any): Promise<T> {
  return service.post(url, data).then(res => res.data as T)
}

export function put<T>(url: string, data?: any): Promise<T> {
  return service.put(url, data).then(res => res.data as T)
}

export function del<T>(url: string): Promise<T> {
  return service.delete(url).then(res => res.data as T)
}

export default service
