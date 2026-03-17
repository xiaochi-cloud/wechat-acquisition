<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="search" placeholder="搜索活动" style="width:200px" clearable />
      <el-button type="primary" @click="handleCreate">+ 创建活动</el-button>
    </div>
    
    <el-table :data="campaigns" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="contactCount" label="联系人" width="100" />
      <el-table-column prop="addedCount" label="已添加" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleStart(row)" v-if="row.status==='DRAFT'">启动</el-button>
          <el-button size="small" @click="handlePause(row)" v-if="row.status==='RUNNING'">暂停</el-button>
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="dialogVisible" title="创建活动">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="每日上限"><el-input-number v-model="form.dailyLimit" :min="1" :max="500" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">创建</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const search = ref('')
const dialogVisible = ref(false)
const form = reactive({ name: '', dailyLimit: 50 })

const campaigns = ref([
  { id: 'camp_001', name: '3 月教育行业获客', status: 'RUNNING', contactCount: 10000, addedCount: 3500, createdAt: '2026-03-01' },
  { id: 'camp_002', name: '金融行业精准获客', status: 'RUNNING', contactCount: 5000, addedCount: 2100, createdAt: '2026-03-05' },
  { id: 'camp_003', name: '电商客户召回', status: 'DRAFT', contactCount: 0, addedCount: 0, createdAt: '2026-03-10' }
])

const statusType = (s) => ({ RUNNING: 'success', PAUSED: 'warning', STOPPED: 'info', DRAFT: 'info' }[s] || 'info')
const statusText = (s) => ({ RUNNING: '运行中', PAUSED: '已暂停', STOPPED: '已停止', DRAFT: '草稿' }[s] || s)

const handleCreate = () => dialogVisible.value = true
const handleSubmit = () => { ElMessage.success('活动创建成功'); dialogVisible.value = false }
const handleStart = (row) => ElMessage.success(`启动活动：${row.name}`)
const handlePause = (row) => ElMessage.success(`暂停活动：${row.name}`)
const handleDetail = (row) => ElMessage.info(`查看活动：${row.name}`)
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
</style>
