<template>
  <div class="contact-page">
    <el-card>
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.phone"
            placeholder="手机号搜索"
            style="width: 200px"
            clearable
          />
          <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 120px">
            <el-option label="新增" value="NEW" />
            <el-option label="已导入" value="IMPORTED" />
            <el-option label="添加中" value="ADDING" />
            <el-option label="已添加" value="ADDED" />
            <el-option label="对话中" value="CONVERSING" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        
        <div class="action-box">
          <el-button type="success" @click="handleImport">
            <el-icon><Upload /></el-icon>
            Excel 导入
          </el-button>
          <el-button type="primary" @click="handleBatchAdd">
            <el-icon><User /></el-icon>
            批量添加
          </el-button>
          <el-button type="warning" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="phoneNumber" label="手机号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="wechatId" label="企微 ID" width="150" />
        <el-table-column prop="tags" label="标签" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="(value, key) in row.tags"
              :key="key"
              size="small"
              style="margin-right: 5px"
            >
              {{ key }}: {{ value }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastContactAt" label="最后联系时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleTag(row)">打标签</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[20, 50, 100, 200]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="Excel 导入联系人" width="500px">
      <el-upload
        ref="uploadRef"
        drag
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        class="upload-area"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，不超过 10MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const importDialogVisible = ref(false)
const selectedRows = ref<any[]>([])

const searchForm = reactive({
  phone: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const tableData = ref([
  {
    id: 'contact_001',
    phoneNumber: '138****8000',
    name: '张先生',
    status: 'ADDED',
    wechatId: 'wx_001',
    tags: { '意向等级': 'A', '行业': '教育' },
    lastContactAt: '2026-03-10 08:30:00'
  },
  {
    id: 'contact_002',
    phoneNumber: '139****9000',
    name: '李女士',
    status: 'CONVERSING',
    wechatId: 'wx_002',
    tags: { '意向等级': 'B', '行业': '金融' },
    lastContactAt: '2026-03-10 09:15:00'
  },
  {
    id: 'contact_003',
    phoneNumber: '136****6000',
    name: '王总',
    status: 'NEW',
    wechatId: '',
    tags: {},
    lastContactAt: ''
  }
])

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'NEW': 'info',
    'IMPORTED': 'info',
    'ADDING': 'warning',
    'ADDED': 'success',
    'CONVERSING': 'primary'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'NEW': '新增',
    'IMPORTED': '已导入',
    'ADDING': '添加中',
    'ADDED': '已添加',
    'CONVERSING': '对话中'
  }
  return map[status] || status
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleImport = () => {
  importDialogVisible.value = true
}

const handleUpload = () => {
  ElMessage.success('导入成功，共导入 1000 条联系人')
  importDialogVisible.value = false
  handleSearch()
}

const handleBatchAdd = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要添加的联系人')
    return
  }
  ElMessage.success(`开始批量添加 ${selectedRows.value.length} 个好友`)
}

const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const handleDetail = (row: any) => {
  ElMessage.info(`查看联系人详情：${row.name}`)
}

const handleTag = (row: any) => {
  ElMessage.info(`给 ${row.name} 打标签`)
}
</script>

<style scoped lang="scss">
.contact-page {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .search-box {
      display: flex;
      gap: 10px;
    }
    
    .action-box {
      display: flex;
      gap: 10px;
    }
  }
  
  .upload-area {
    width: 100%;
  }
}
</style>
