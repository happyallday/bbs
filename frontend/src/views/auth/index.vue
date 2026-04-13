<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h1>员工论坛系统</h1>
        <p>请选择登录方式</p>
      </div>
      
      <div class="auth-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="企业微信登录" name="wechat">
            <el-form :model="wechatForm" ref="wechatFormRef">
              <el-form-item>
                <el-input 
                  v-model="wechatForm.code" 
                  placeholder="请输入企业微信授权码"
                  size="large"
                >
                  <template #prepend>
                    <el-button @click="handleWeChatLogin">
                      <el-icon><svg viewBox="0 0 1024 1024"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z" fill="#07C160"/></svg></el-icon>
                      企业微信
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-button 
                type="primary" 
                size="large" 
                style="width: 100%"
                @click="handleWeChatLogin"
                :loading="loading"
              >
                企业微信扫码登录
              </el-button>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="用户名密码登录" name="password">
            <el-form :model="passwordForm" ref="passwordFormRef" :rules="passwordRules">
              <el-form-item prop="username">
                <el-input 
                  v-model="passwordForm.username" 
                  placeholder="请输入用户名"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input 
                  v-model="passwordForm.password" 
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  @keyup.enter="handlePasswordLogin"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-button 
                type="primary" 
                size="large" 
                style="width: 100%"
                @click="handlePasswordLogin"
                :loading="loading"
              >
                登录
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <div class="auth-footer">
        <p>内部用户建议使用企业微信登录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginByPassword } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const activeTab = ref('wechat')

const wechatForm = reactive({
  code: '',
  state: ''
})

const passwordForm = reactive({
  username: '',
  password: ''
})

const passwordRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleWeChatLogin = () => {
  const redirectUri = encodeURIComponent(window.location.origin + '/auth')
  const appId = 'wx_corp_id'
  const authUrl = `https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=${appId}&agentid=1000001&redirect_uri=${redirectUri}&state=login`
  window.location.href = authUrl
}

const handlePasswordLogin = async () => {
  if (!passwordForm.username || !passwordForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const res = await loginByPassword({
      username: passwordForm.username,
      password: passwordForm.password
    })
    
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.data.userInfo))
      await userStore.fetchUserInfo()
      router.push('/home')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败: ' + (error.response?.data?.message || '请检查用户名和密码'))
  } finally {
    loading.value = false
  }
}

const checkAuthCode = () => {
  const urlParams = new URLSearchParams(window.location.search)
  const code = urlParams.get('code')
  const state = urlParams.get('state')
  
  if (code) {
    wechatForm.code = code
    wechatForm.state = state
    handleWeChatLogin()
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
  width: 420px;
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

.auth-tabs {
  margin-top: 20px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}
</style>