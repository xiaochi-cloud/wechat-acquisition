import request from './request'

/**
 * 获取活动列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getCampaignList(params) {
  return request.get('/campaigns', params)
}

/**
 * 获取活动详情
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function getCampaignDetail(id) {
  return request.get(`/campaigns/${id}`)
}

/**
 * 创建活动
 * @param {Object} data - 活动信息
 * @returns {Promise}
 */
export function createCampaign(data) {
  return request.post('/campaigns', data)
}

/**
 * 更新活动
 * @param {string} id - 活动 ID
 * @param {Object} data - 活动信息
 * @returns {Promise}
 */
export function updateCampaign(id, data) {
  return request.put(`/campaigns/${id}`, data)
}

/**
 * 删除活动
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function deleteCampaign(id) {
  return request.delete(`/campaigns/${id}`)
}

/**
 * 启动活动
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function startCampaign(id) {
  return request.post(`/campaigns/${id}/start`)
}

/**
 * 暂停活动
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function pauseCampaign(id) {
  return request.post(`/campaigns/${id}/pause`)
}

/**
 * 停止活动
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function stopCampaign(id) {
  return request.post(`/campaigns/${id}/stop`)
}

/**
 * 获取活动统计
 * @param {string} id - 活动 ID
 * @returns {Promise}
 */
export function getCampaignStats(id) {
  return request.get(`/campaigns/${id}/stats`)
}
