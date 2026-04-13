import request from './request'

export function getNetworkList(params) {
  return request({
    url: '/admin/network/list',
    method: 'get',
    params
  })
}

export function getAllNetworks() {
  return request({
    url: '/admin/network/all',
    method: 'get'
  })
}

export function addNetwork(data) {
  return request({
    url: '/admin/network/add',
    method: 'post',
    data
  })
}

export function updateNetwork(data) {
  return request({
    url: '/admin/network/update',
    method: 'put',
    data
  })
}

export function deleteNetwork(id) {
  return request({
    url: `/admin/network/delete/${id}`,
    method: 'delete'
  })
}

export function enableNetwork(id) {
  return request({
    url: `/admin/network/enable/${id}`,
    method: 'post'
  })
}

export function disableNetwork(id) {
  return request({
    url: `/admin/network/disable/${id}`,
    method: 'post'
  })
}

export function testIp(ip) {
  return request({
    url: `/admin/network/test/${ip}`,
    method: 'get'
  })
}