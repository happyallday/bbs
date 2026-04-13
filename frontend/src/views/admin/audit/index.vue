<template>
  <div class="audit-manage">
    <el-card>
      <div class="toolbar">
        <el-radio-group v-model="auditType" @change="handleTypeChange">
          <el-radio-button label="pending">待审核</el-radio-button>
          <el-radio-button label="history">审核历史</el-radio-button>
        </el-radio-group>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="targetType" label="类型" width="80">
          <template #default="{ row }">
            {{ row.targetType === 1 ? '帖子' : '评论' }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="submitterName" label="提交人" width="100" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column v-if="auditType === 'history'" prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">驳回</el-tag>
            <el-tag v-else type="info">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="auditType === 'history'" prop="auditTime" label="审核时间" width="180" />
        <el-table-column v-if="auditType === 'pending'" label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(row)">驳回</el-button>
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
    
    <el-dialog v-model="rejectDialogVisible" title="驳回原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" rows="4" placeholder="请输入驳回原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingAudits, getAuditHistory, approveAudit, rejectAudit } from '@/api/audit'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const auditType = ref('pending')

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentAuditId = ref(null)

const loadData = async () => {
  try {
    let res
    if (auditType.value === 'pending') {
      res = await getPendingAudits({ current: currentPage.value, size: pageSize.value })
    } else {
      res = await getAuditHistory({ current: currentPage.value, size: pageSize.value })
    }
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

const handleTypeChange = () => {
  currentPage.value = 1
  loadData()
}

const handleApprove = async (row) => {
  try {
    await approveAudit(row.id, { remark: '' })
    ElMessage.success('审核通过')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = (row) => {
  currentAuditId.value = row.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  try {
    await rejectAudit(currentAuditId.value, { remark: rejectReason.value })
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.audit-manage {
  width: 100%;
}

.toolbar {
  margin-bottom: 20px;
}
</style>