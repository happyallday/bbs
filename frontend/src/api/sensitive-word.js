import request from './request'

export function getSensitiveWords(params) {
  return request({
    url: '/admin/sensitive-words/list',
    method: 'get',
    params
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

export function batchImportSensitiveWord(data) {
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