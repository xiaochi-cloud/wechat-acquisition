import { get, post } from './request'

export interface Contact {
  id: string
  phoneNumber: string
  name: string
  status: string
  wechatId: string
  tags: Record<string, string>
  createdAt: string
}

/**
 * 获取联系人列表
 */
export function getContactList(params?: { campaignId?: string; status?: string; page?: number; size?: number }) {
  return get<{ list: Contact[]; total: number }>('/contacts', params)
}

/**
 * 获取联系人详情
 */
export function getContactDetail(id: string) {
  return get<Contact>(`/contacts/${id}`)
}

/**
 * Excel 导入
 */
export function importExcel(file: File, campaignId?: string) {
  const formData = new FormData()
  formData.append('file', file)
  if (campaignId) {
    formData.append('campaignId', campaignId)
  }
  return post('/contacts/import/excel', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 批量创建
 */
export function batchCreate(phoneNumbers: string[], campaignId: string) {
  return post('/contacts/batch', { phoneNumbers, campaignId })
}

/**
 * 更新标签
 */
export function updateTags(id: string, tags: Record<string, string>) {
  return post(`/contacts/${id}/tags`, tags)
}
