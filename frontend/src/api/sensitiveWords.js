import request from './request'

export function getSensitiveWordList(params) {
  return request({
    url: '/admin/sensitive-words/list',
    method: 'get',
    params
  })
}

export function getSensitiveWordCategories() {
  return request({
    url: '/admin/sensitive-words/categories',
    method: 'get'
  })
}

export function getSensitiveWordById(id) {
  return request({
    url: `/admin/sensitive-words/${id}`,
    method: 'get'
  })
}

export function addSensitiveWord(data) {
  return request({
    url: '/admin/sensitive-words/add',
    method: 'post',
    data
  })
}

export function updateSensitiveWord(data) {
  return request({
    url: '/admin/sensitive-words/update',
    method: 'put',
    data
  })
}

export function deleteSensitiveWord(id) {
  return request({
    url: `/admin/sensitive-words/delete/${id}`,
    method: 'delete'
  })
}

export function testSensitiveWord(data) {
  return request({
    url: '/admin/sensitive-words/test',
    method: 'post',
    data
  })
}

export function batchImportSensitiveWords(data) {
  return request({
    url: '/admin/sensitive-words/batch-import',
    method: 'post',
    data
  })
}

export function reloadSensitiveWords() {
  return request({
    url: '/admin/sensitive-words/reload',
    method: 'post'
  })
}

export function getSensitiveWordStats() {
  return request({
    url: '/admin/sensitive-words/stats',
    method: 'get'
  })
}