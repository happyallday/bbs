<template>
  <div class="sensitive-word-manage">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">添加敏感词</el-button>
        <el-button type="success" @click="handleBatchImport">批量导入</el-button>
        <el-button type="warning" @click="handleReload">重新加载</el-button>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="敏感词" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="wordType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.wordType === 1">敏感</el-tag>
            <el-tag v-else-if="row.wordType === 2" type="warning">警告</el-tag>
            <el-tag v-else type="info">替换</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="敏感词">
          <el-input v-model="form.word" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择">
            <el-option label="政治敏感" value="政治" />
            <el-option label="色情" value="色情" />
            <el-option label="暴力" value="暴力" />
            <el-option label="广告" value="广告" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.wordType">
            <el-radio :label="1">敏感</el-radio>
            <el-radio :label="2">警告</el-radio>
            <el-radio :label="3">替换</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="替换词">
          <el-input v-model="form.replacement" placeholder="替换词(仅替换类型生效)" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="importDialogVisible" title="批量导入敏感词" width="500px">
      <el-form :model="importForm" label-width="80px">
        <el-form-item label="分类">
          <el-select v-model="importForm.category" placeholder="请选择">
            <el-option label="政治敏感" value="政治" />
            <el-option label="色情" value="色情" />
            <el-option label="暴力" value="暴力" />
            <el-option label="广告" value="广告" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="敏感词">
          <el-input v-model="importForm.words" type="textarea" rows="10" placeholder="每行一个敏感词" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSensitiveWords, addSensitiveWord, updateSensitiveWord, deleteSensitiveWord, batchImportSensitiveWord, reloadSensitiveWords } from '@/api/sensitive-word'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('添加敏感词')
const form = ref({
  id: null,
  word: '',
  category: '',
  wordType: 1,
  replacement: '',
  status: 1
})

const importDialogVisible = ref(false)
const importForm = ref({
  category: '',
  words: ''
})

const loadData = async () => {
  try {
    const res = await getSensitiveWords({ current: currentPage.value, size: pageSize.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const handleAdd = () => {
  form.value = { id: null, word: '', category: '', wordType: 1, replacement: '', status: 1 }
  dialogTitle.value = '添加敏感词'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑敏感词'
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (form.value.id) {
      await updateSensitiveWord(form.value)
      ElMessage.success('更新成功')
    } else {
      await addSensitiveWord(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该敏感词吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSensitiveWord(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchImport = () => {
  importForm.value = { category: '', words: '' }
  importDialogVisible.value = true
}

const handleImport = async () => {
  if (!importForm.value.category || !importForm.value.words) {
    ElMessage.warning('请填写完整信息')
    return
  }
  const words = importForm.value.words.split('\n').filter(w => w.trim())
  try {
    await batchImportSensitiveWord({
      category: importForm.value.category,
      words: words
    })
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('导入失败')
  }
}

const handleReload = async () => {
  try {
    await reloadSensitiveWords()
    ElMessage.success('重新加载成功')
  } catch (error) {
    ElMessage.error('重新加载失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.sensitive-word-manage {
  width: 100%;
}

.toolbar {
  margin-bottom: 20px;
}
</style>