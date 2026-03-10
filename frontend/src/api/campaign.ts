import { get, post } from './request'

export interface Campaign {
  id: string
  name: string
  status: 'DRAFT' | 'RUNNING' | 'PAUSED' | 'STOPPED' | 'COMPLETED'
  contactCount: number
  addedCount: number
  conversationCount: number
  createdAt: string
}

export interface CreateCampaignRequest {
  name: string
  dataSourceId: string
  targetAudience: any
  scheduleConfig: any
  rateLimitConfig: any
}

/**
 * 获取活动列表
 */
export function getCampaignList(params?: { status?: string; page?: number; size?: number }) {
  return get<{ list: Campaign[]; total: number }>('/campaigns', params)
}

/**
 * 获取活动详情
 */
export function getCampaignDetail(id: string) {
  return get<Campaign>(`/campaigns/${id}`)
}

/**
 * 创建活动
 */
export function createCampaign(data: CreateCampaignRequest) {
  return post('/campaigns', data)
}

/**
 * 启动活动
 */
export function startCampaign(id: string) {
  return post(`/campaigns/${id}/start`)
}

/**
 * 暂停活动
 */
export function pauseCampaign(id: string) {
  return post(`/campaigns/${id}/pause`)
}

/**
 * 停止活动
 */
export function stopCampaign(id: string) {
  return post(`/campaigns/${id}/stop`)
}
