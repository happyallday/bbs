<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h1>员工论坛系统</h1>
        <p>使用企业微信登录</p>
      </div>
      
      <div class="auth-content">
        <el-form :model="loginForm" ref="loginFormRef">
          <el-form-item>
            <el-input 
              v-model="loginForm.code" 
              placeholder="请输入企业微信授权码"
              size="large"
            >
              <template #prepend>
                <el-button @click="handleWeChatLogin">
                  <el-icon><svg viewBox="0 0 1024 1024"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z" fill="#07C160"/></svg></el-icon>
                  企业微信登录
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          
          <el-button 
            type="primary" 
            size="large" 
            style="width: 100%"
            @click="handleLogin"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form>
        
        <div class="auth-footer">
          <p>仅支持企业微信工作台访问</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const loginForm = reactive({
  code: '',
  state: ''
})

const handleWeChatLogin = () => {
  const redirectUri = encodeURIComponent(window.location.origin + '/auth')
  const appId = 'wx_corp_id'
  const authUrl = `https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=${appId}&agentid=1000001&redirect_uri=${redirectUri}&state=login`
  window.location.href = authUrl
}

const handleLogin = async () => {
  if (!loginForm.code) {
    ElMessage.warning('请输入授权码')
    return
  }
  
  loading.value = true
  try {
    const success = await userStore.login(loginForm.code)
    if (success) {
      router.push('/home')
    }
  } catch (error) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}

const checkAuthCode = () => {
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  const state = urlParams.get('state')
  
  if (code) {
    loginForm.code = code
    loginForm.state = state
    handleLogin()
  }
}

checkAuthCode()
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.auth-header p {
  color: #666;
  font-size: 14px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}
</style>