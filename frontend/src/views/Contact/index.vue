<template>
  <el-card>
    <div class="toolbar">
      <div class="search">
        <el-input v-model="search" placeholder="搜索手机号" style="width:200px" clearable />
        <el-select v-model="status" placeholder="状态" clearable style="width:120px">
          <el-option label="新增" value="NEW" />
          <el-option label="已添加" value="ADDED" />
          <el-option label="对话中" value="CONVERSING" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <div class="actions">
        <el-button type="success" @click="handleImport"><el-icon><Upload /></el-icon> Excel 导入</el-button>
        <el-button type="primary" @click="handleBatchAdd"><el-icon><User /></el-icon> 批量添加</el-button>
      </div>
    </div>
    
    <el-table :data="contacts" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="phoneNumber" label="手机号" width="150" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="wechatId" label="企微 ID" width="150" />
      <el-table-column prop="tags" label="标签" width="200">
        <template #default="{ row }">
          <el-tag v-for="(v,k) in row.tags" :key="k" size="small" style="margin-right:5px">{{ k }}:{{ v }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastContactAt" label="最后联系" width="180" />
    </el-table>
    
    <el-pagination v-model:current-page="page" :total="total" :page-size="20" layout="total,prev,pager,next" style="margin-top:20px;justify-content:flex-end" />
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const search = ref('')
const status = ref('')
const page = ref(1)
const total = ref(100)

const contacts = ref([
  { id: 'c_001', phoneNumber: '138****8000', name: '张先生', status: 'ADDED', wechatId: 'wx_001', tags: { '意向': 'A', '行业': '教育' }, lastContactAt: '2026-03-17 10:30' },
  { id: 'c_002', phoneNumber: '139****9000', name: '李女士', status: 'CONVERSING', wechatId: 'wx_002', tags: { '意向': 'B', '行业': '金融' }, lastContactAt: '2026-03-17 11:15' },
  { id: 'c_003', phoneNumber: '136****6000', name: '王总', status: 'NEW', wechatId: '', tags: {}, lastContactAt: '' }
])

const statusType = (s) => ({ NEW: 'info', ADDED: 'success', CONVERSING: 'primary' }[s] || 'info')
const statusText = (s) => ({ NEW: '新增', ADDED: '已添加', CONVERSING: '对话中' }[s] || s)

const handleSearch = () => ElMessage.success('搜索完成')
const handleImport = () => ElMessage.info('打开 Excel 导入对话框')
const handleBatchAdd = () => ElMessage.success('开始批量添加好友')
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 20px; }
.search { display: flex; gap: 10px; }
</style>
