import request from './request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @returns {Promise}
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request.post('/auth/logout')
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getCurrentUser() {
  return request.get('/auth/profile')
}

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @returns {Promise}
 */
export function changePassword(data) {
  return request.put('/auth/password', data)
}
