import request from './request'

export function getUserList(params) {
  return request({
    url: '/admin/users/list',
    method: 'get',
    params
  })
}

export function getActiveUsers(params) {
  return request({
    url: '/admin/users/active',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/admin/users/add',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/users/update',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/delete/${id}`,
    method: 'delete'
  })
}

export function enableUser(id) {
  return request({
    url: `/admin/users/enable/${id}`,
    method: 'post'
  })
}

export function disableUser(id) {
  return request({
    url: `/admin/users/disable/${id}`,
    method: 'post'
  })
}

export function resetUserPassword(id, password) {
  return request({
    url: `/admin/users/reset-password/${id}`,
    method: 'post',
    data: { password }
  })
}

export function addToWhiteList(userId, reason) {
  return request({
    url: `/admin/users/whitelist-add/${userId}`,
    method: 'post',
    data: { reason }
  })
}

export function removeFromWhiteList(userId) {
  return request({
    url: `/admin/users/whitelist-remove/${userId}`,
    method: 'post'
  })
}