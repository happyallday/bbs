<template>
  <div class="home-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>员工论坛系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.realName }} ({{ userStore.userInfo?.department }})
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <div class="welcome-section">
          <el-card class="welcome-card">
            <h3>欢迎使用员工论坛系统</h3>
            <p>这里是一个企业内部交流的平台，请遵守社区规范，文明交流。</p>
            <div class="stats">
              <el-statistic title="帖子总数" :value="stats.postCount" />
              <el-statistic title="用户数量" :value="stats.userCount" />
              <el-statistic title="在线用户" :value="stats.onlineCount" />
            </div>
          </el-card>
        </div>
        
        <div class="boards-section">
          <h3>论坛板块</h3>
          <el-row :gutter="20">
            <el-col v-for="board in boards" :key="board.id" :span="6">
              <el-card class="board-card" @click="handleBoardClick(board)">
                <div class="board-icon">
                  <el-icon size="40"><chat-line-square /></el-icon>
                </div>
                <h4>{{ board.boardName }}</h4>
                <p>{{ board.description }}</p>
                <div class="board-stats">
                  <span>{{ board.postCount }} 帖子</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ChatLineSquare } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  postCount: 156,
  userCount: 89,
  onlineCount: 23
})

const boards = ref([
  { id: 1, boardName: '技术交流', description: '技术问题讨论与经验分享', postCount: 45 },
  { id: 2, boardName: '公告通知', description: '公司公告与通知', postCount: 12 },
  { id: 3, boardName: '生活吐槽', description: '生活分享与吐槽', postCount: 67 },
  { id: 4, boardName: '招聘求职', description: '内部招聘与求职', postCount: 32 }
])

const handleCommand = (command) => {
  if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logout()
      router.push('/auth')
    })
  }
}

const handleBoardClick = (board) => {
  ElMessage.info(`进入${board.boardName}板块 - 功能开发中`)
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/auth')
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0;
  color: #333;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: #333;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  text-align: center;
}

.welcome-card h3 {
  margin-top: 0;
  color: #333;
}

.stats {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin-top: 20px;
}

.boards-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.board-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.board-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.board-icon {
  text-align: center;
  color: #409EFF;
  margin-bottom: 10px;
}

.board-card h4 {
  margin: 10px 0;
  color: #333;
}

.board-card p {
  color: #666;
  font-size: 12px;
  margin-bottom: 10px;
}

.board-stats {
  color: #999;
  font-size: 12px;
  text-align: center;
}
</style>