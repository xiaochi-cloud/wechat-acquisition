import request from './request'

/**
 * 获取联系人列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getContactList(params) {
  return request.get('/contacts', params)
}

/**
 * 获取联系人详情
 * @param {string} id - 联系人 ID
 * @returns {Promise}
 */
export function getContactDetail(id) {
  return request.get(`/contacts/${id}`)
}

/**
 * 导入联系人 (Excel)
 * @param {File} file - Excel 文件
 * @param {string} campaignId - 活动 ID
 * @returns {Promise}
 */
export function importContacts(file, campaignId) {
  const formData = new FormData()
  formData.append('file', file)
  if (campaignId) {
    formData.append('campaignId', campaignId)
  }
  return request.post('/contacts/import/excel', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量添加好友
 * @param {Array<string>} contactIds - 联系人 ID 列表
 * @returns {Promise}
 */
export function batchAddFriends(contactIds) {
  return request.post('/contacts/batch-add', { contactIds })
}

/**
 * 更新联系人标签
 * @param {string} id - 联系人 ID
 * @param {Object} tags - 标签信息
 * @returns {Promise}
 */
export function updateContactTags(id, tags) {
  return request.post(`/contacts/${id}/tags`, tags)
}

/**
 * 删除联系人
 * @param {string} id - 联系人 ID
 * @returns {Promise}
 */
export function deleteContact(id) {
  return request.delete(`/contacts/${id}`)
}

/**
 * 导出联系人
 * @param {Object} params - 导出条件
 * @returns {Promise<Blob>}
 */
export function exportContacts(params) {
  return request.get('/contacts/export', {
    params,
    responseType: 'blob'
  })
}
