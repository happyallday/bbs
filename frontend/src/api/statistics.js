import request from './request'

export function getOverallStatistics() {
  return request({
    url: '/admin/statistics/overall',
    method: 'get'
  })
}

export function getPostTrend(days = 7) {
  return request({
    url: '/admin/statistics/post-trend',
    method: 'get',
    params: { days }
  })
}

export function getBoardStatistics() {
  return request({
    url: '/admin/statistics/boards',
    method: 'get'
  })
}

export function getTopPosts(limit = 10) {
  return request({
    url: '/admin/statistics/top-posts',
    method: 'get',
    params: { limit }
  })
}

export function getActiveUsers(limit = 10) {
  return request({
    url: '/admin/statistics/active-users',
    method: 'get',
    params: { limit }
  })
}

export function getUserActivityStatistics(userId) {
  return request({
    url: `/admin/statistics/user/${userId}`,
    method: 'get'
  })
}