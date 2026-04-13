import request from './request'

export function getPostList(params) {
  return request({
    url: '/posts',
    method: 'get',
    params
  })
}

export function getPostById(id) {
  return request({
    url: `/posts/${id}`,
    method: 'get'
  })
}

export function getPostByNumber(postNumber) {
  return request({
    url: `/posts/number/${postNumber}`,
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

export function getHotPosts(params) {
  return request({
    url: '/posts/hot',
    method: 'get',
    params
  })
}

export function getRecentPosts(params) {
  return request({
    url: '/posts/recent',
    method: 'get',
    params
  })
}