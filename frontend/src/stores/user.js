import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  
  actions: {
    async login(code) {
      try {
        const response = await loginApi({ code })
        this.token = response.data.token
        this.userInfo = response.data.userInfo
        localStorage.setItem('token', this.token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        ElMessage.success('登录成功')
        return true
      } catch (error) {
        ElMessage.error('登录失败')
        return false
      }
    },
    
    async getUserInfo() {
      try {
        const response = await getUserInfoApi()
        this.userInfo = response.data
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return response.data
      } catch (error) {
        ElMessage.error('获取用户信息失败')
        return null
      }
    },
    
    async logout() {
      try {
        await logoutApi()
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.success('退出成功')
      } catch (error) {
        console.error('退出失败', error)
      }
    }
  }
})