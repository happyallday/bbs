<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header">
        <h1>员工论坛系统</h1>
        <p>请使用用户名密码登录</p>
      </div>
      
      <div class="auth-content">
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
      </div>
      
      <div class="auth-footer">
        <p>内部员工论坛系统</p>
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
    
    console.log('登录响应:', res)
    
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      await userStore.getUserInfo()
      router.push('/home')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败: ' + (error.message || error.response?.data?.message || '请检查用户名和密码'))
  } finally {
    loading.value = false
  }
}
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

.auth-content {
  margin-top: 20px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}
</style>