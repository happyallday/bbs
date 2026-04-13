import request from './request'

export function getWhiteList(params) {
  return request({
    url: '/admin/whitelist',
    method: 'get',
    params
  })
}

export function addToWhiteList(userId, reason) {
  return request({
    url: `/admin/whitelist-add/${userId}`,
    method: 'post',
    data: { reason }
  })
}

export function removeFromWhiteList(userId) {
  return request({
    url: `/admin/whitelist-remove/${userId}`,
    method: 'post'
  })
}

export function getAllUsers() {
  return request({
    url: '/admin/users/list',
    method: 'get',
    params: { current: 1, size: 1000 }
  })
}