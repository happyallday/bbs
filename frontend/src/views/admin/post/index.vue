<template>
  <div class="post-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="searchKeyword" placeholder="搜索帖子标题/内容" style="width: 200px; margin-right: 10px" />
        <el-select v-model="searchStatus" placeholder="状态" style="width: 120px; margin-right: 10px">
          <el-option label="全部" :value="null" />
          <el-option label="待审核" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="boardName" label="板块" width="100" />
        <el-table-column prop="username" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="auditStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.auditStatus === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.auditStatus === 1" type="success">已发布</el-tag>
            <el-tag v-else-if="row.auditStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <el-dialog v-model="viewDialogVisible" title="帖子详情" width="700px">
      <div v-if="currentPost" class="post-detail">
        <h2>{{ currentPost.title }}</h2>
        <div class="post-meta">
          <span>作者: {{ currentPost.username }}</span>
          <span>板块: {{ currentPost.boardName }}</span>
          <span>发布时间: {{ currentPost.createdTime }}</span>
        </div>
        <div class="post-content" v-html="currentPost.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPosts, deletePost } from '@/api/post'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const searchStatus = ref(null)

const viewDialogVisible = ref(false)
const currentPost = ref(null)

const loadData = async () => {
  try {
    const res = await getPosts({ 
      current: currentPage.value, 
      size: pageSize.value,
      keyword: searchKeyword.value,
      status: searchStatus.value
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleView = (row) => {
  currentPost.value = row
  viewDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePost(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.post-manage {
  width: 100%;
}

.toolbar {
  margin-bottom: 20px;
}

.post-detail h2 {
  margin-bottom: 15px;
}

.post-meta {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.post-meta span {
  margin-right: 20px;
}

.post-content {
  line-height: 1.8;
}
</style>