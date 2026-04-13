import request from '../request'

export function getPosts(params) {
  return request({
    url: '/posts',
    method: 'get',
    params
  })
}

export function getPost(id) {
  return request({
    url: `/posts/${id}`,
    method: 'get'
  })
}

export function createPost(data) {
  return request({
    url: '/posts',
    method: 'post',
    data
  })
}

export function updatePost(data) {
  return request({
    url: '/posts',
    method: 'put',
    data
  })
}

export function deletePost(id) {
  return request({
    url: `/posts/${id}`,
    method: 'delete'
  })
}

export function likePost(id) {
  return request({
    url: `/posts/${id}/like`,
    method: 'post'
  })
}

export function unlikePost(id) {
  return request({
    url: `/posts/${id}/unlike`,
    method: 'post'
  })
}