<template>
  <div class="whitelist-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">添加白名单</el-button>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="reason" label="添加原因" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createdTime" label="添加时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleRemove(row)">移除</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="添加白名单" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户">
          <el-select v-model="form.userId" filterable placeholder="搜索用户" style="width: 100%">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.username + ' - ' + user.realName"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.reason" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWhiteList, addToWhiteList, removeFromWhiteList, getAllUsers } from '@/api/whitelist'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const userList = ref([])

const dialogVisible = ref(false)
const form = ref({
  userId: null,
  reason: ''
})

const loadData = async () => {
  try {
    const res = await getWhiteList({ current: currentPage.value, size: pageSize.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const loadUsers = async () => {
  try {
    const res = await getAllUsers()
    userList.value = res.data || []
  } catch (error) {
    console.error('加载用户列表失败')
  }
}

const handleAdd = () => {
  form.value = { userId: null, reason: '' }
  dialogVisible.value = true
  loadUsers()
}

const handleSave = async () => {
  if (!form.value.userId || !form.value.reason) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await addToWhiteList(form.value.userId, form.value.reason)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRemove = (row) => {
  ElMessageBox.confirm('确定移除该用户的白名单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await removeFromWhiteList(row.userId)
      ElMessage.success('移除成功')
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.whitelist-manage {
  width: 100%;
}

.toolbar {
  margin-bottom: 20px;
}
</style>