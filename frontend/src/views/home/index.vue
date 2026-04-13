<template>
  <div class="home-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>员工论坛系统</h2>
        </div>
        <div class="header-right">
          <el-button type="primary" size="small" @click="handleCreatePost">发布帖子</el-button>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userInfo.realName || userInfo.username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="userInfo.isWhiteList === 1 || userInfo.roleId === 1" command="admin">管理后台</el-dropdown-item>
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
    
    <el-dialog v-model="postDialogVisible" title="发布帖子" width="700px">
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="板块">
          <el-select v-model="postForm.boardId" placeholder="选择板块" style="width: 100%">
            <el-option v-for="board in boards" :key="board.id" :label="board.boardName" :value="board.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postForm.content" type="textarea" rows="10" placeholder="请输入帖子内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ChatLineSquare } from '@element-plus/icons-vue'
import { getAllBoards } from '@/api/board'
import { createPost } from '@/api/post'

const router = useRouter()
const userStore = useUserStore()

const userInfo = reactive(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const stats = reactive({
  postCount: 156,
  userCount: 89,
  onlineCount: 23
})

const boards = ref([])

const postDialogVisible = ref(false)
const postForm = reactive({
  boardId: null,
  title: '',
  content: ''
})

const loadBoards = async () => {
  try {
    const res = await getAllBoards()
    boards.value = res.data || []
  } catch (error) {
    console.error('加载板块失败')
  }
}

const handleCommand = (command) => {
  if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  } else if (command === 'admin') {
    router.push('/admin')
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

const handleCreatePost = () => {
  if (!userInfo.isWhiteList && userInfo.roleId !== 1) {
    ElMessage.warning('只有白名单用户才能直接发布帖子，其他用户发布的帖子需要审核')
  }
  postForm.boardId = boards.value[0]?.id || null
  postForm.title = ''
  postForm.content = ''
  postDialogVisible.value = true
}

const handleSubmitPost = async () => {
  if (!postForm.boardId || !postForm.title || !postForm.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await createPost(postForm)
    ElMessage.success('发布成功')
    postDialogVisible.value = false
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

onMounted(() => {
  loadBoards()
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

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  color: #333;
}

.el-main {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 30px;
}

.welcome-card {
  text-align: center;
}

.welcome-card h3 {
  margin-bottom: 10px;
}

.stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 20px;
}

.boards-section h3 {
  margin-bottom: 20px;
}

.board-card {
  cursor: pointer;
  transition: all 0.3s;
}

.board-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.board-icon {
  text-align: center;
  color: #409eff;
  margin-bottom: 10px;
}

.board-card h4 {
  margin: 10px 0;
  text-align: center;
}

.board-card p {
  color: #666;
  font-size: 14px;
  text-align: center;
  margin-bottom: 10px;
}

.board-stats {
  text-align: center;
  color: #999;
  font-size: 12px;
}
</style>