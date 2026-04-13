import request from './request'

export function getBoardsList(params) {
  return request({
    url: '/admin/boards/list',
    method: 'get',
    params
  })
}

export function getAllBoards() {
  return request({
    url: '/admin/boards/all',
    method: 'get'
  })
}

export function getBoardById(id) {
  return request({
    url: `/admin/boards/${id}`,
    method: 'get'
  })
}

export function addBoard(data) {
  return request({
    url: '/admin/boards/add',
    method: 'post',
    data
  })
}

export function updateBoard(data) {
  return request({
    url: '/admin/boards/update',
    method: 'put',
    data
  })
}

export function deleteBoard(id) {
  return request({
    url: `/admin/boards/delete/${id}`,
    method: 'delete'
  })
}

export function enableBoard(id) {
  return request({
    url: `/admin/boards/enable/${id}`,
    method: 'post'
  })
}

export function disableBoard(id) {
  return request({
    url: `/admin/boards/disable/${id}`,
    method: 'post'
  })
}