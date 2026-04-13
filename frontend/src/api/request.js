import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    console.log('响应原始:', response)
    const res = response.data
    console.log('响应数据:', res)
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/auth'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('请求错误:', error)
    console.error('错误响应:', error.response)
    
    if (error.response?.status === 403) {
      const msg = error.response?.data?.message || '访问被拒绝'
      ElMessage.error(msg)
      if (msg.includes('办公网') || msg.includes('企业微信')) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/auth'
      }
    } else {
      ElMessage.error(error.message || '网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default request