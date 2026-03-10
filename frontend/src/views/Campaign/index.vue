<template>
  <div class="campaign-page">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="活动名称"
          style="width: 200px"
          clearable
        />
        <el-select v-model="searchForm.status" placeholder="活动状态" clearable style="width: 150px">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="运行中" value="RUNNING" />
          <el-option label="已暂停" value="PAUSED" />
          <el-option label="已停止" value="STOPPED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleCreate">+ 创建活动</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="name" label="活动名称" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactCount" label="联系人" width="100" />
        <el-table-column prop="addedCount" label="已添加" width="100" />
        <el-table-column prop="addedRate" label="添加率" width="100">
          <template #default="{ row }">
            {{ ((row.addedCount / row.contactCount) * 100).toFixed(1) }}%
          </template>
        </el-table-column>
        <el-table-column prop="conversationCount" label="会话数" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'DRAFT'"
              type="success"
              size="small"
              @click="handleStart(row)"
            >
              启动
            </el-button>
            <el-button
              v-if="row.status === 'RUNNING'"
              type="warning"
              size="small"
              @click="handlePause(row)"
            >
              暂停
            </el-button>
            <el-button
              v-if="row.status === 'PAUSED'"
              type="success"
              size="small"
              @click="handleResume(row)"
            >
              恢复
            </el-button>
            <el-button
              v-if="['RUNNING', 'PAUSED'].includes(row.status)"
              type="info"
              size="small"
              @click="handleStop(row)"
            >
              停止
            </el-button>
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 创建活动对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="创建获客活动"
      width="600px"
    >
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="活动名称" required>
          <el-input v-model="createForm.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="数据源" required>
          <el-select v-model="createForm.dataSourceId" placeholder="请选择数据源" style="width: 100%">
            <el-option label="Excel 导入 -20260301" value="1" />
            <el-option label="API 同步 - 教育行业" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标人群">
          <el-input
            v-model="createForm.targetAudienceDesc"
            type="textarea"
            placeholder="描述目标人群特征"
          />
        </el-form-item>
        <el-form-item label="每日添加上限">
          <el-input-number v-model="createForm.dailyLimit" :min="1" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCampaignList, startCampaign, pauseCampaign, stopCampaign } from '@/api/campaign'

const loading = ref(false)
const dialogVisible = ref(false)

const searchForm = reactive({
  name: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const tableData = ref([
  {
    id: 'camp_001',
    name: '3 月教育行业获客活动',
    status: 'RUNNING',
    contactCount: 10000,
    addedCount: 3500,
    conversationCount: 2800,
    createdAt: '2026-03-01 10:00:00'
  },
  {
    id: 'camp_002',
    name: '金融行业精准获客',
    status: 'RUNNING',
    contactCount: 5000,
    addedCount: 2100,
    conversationCount: 1500,
    createdAt: '2026-03-05 14:30:00'
  },
  {
    id: 'camp_003',
    name: '电商客户召回计划',
    status: 'DRAFT',
    contactCount: 0,
    addedCount: 0,
    conversationCount: 0,
    createdAt: '2026-03-08 09:15:00'
  }
])

const createForm = reactive({
  name: '',
  dataSourceId: '',
  targetAudienceDesc: '',
  dailyLimit: 50
})

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'RUNNING': 'success',
    'PAUSED': 'warning',
    'STOPPED': 'info',
    'DRAFT': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'RUNNING': '运行中',
    'PAUSED': '已暂停',
    'STOPPED': '已停止',
    'DRAFT': '草稿'
  }
  return map[status] || status
}

const handleSearch = () => {
  loading.value = true
  // TODO: 调用 API
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const handleCreate = () => {
  dialogVisible.value = true
}

const handleSubmit = () => {
  // TODO: 调用创建 API
  ElMessage.success('活动创建成功')
  dialogVisible.value = false
  handleSearch()
}

const handleStart = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要启动活动"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await startCampaign(row.id)
    ElMessage.success('活动已启动')
    handleSearch()
  } catch (e) {}
}

const handlePause = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要暂停活动"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await pauseCampaign(row.id)
    ElMessage.success('活动已暂停')
    handleSearch()
  } catch (e) {}
}

const handleResume = () => {
  ElMessage.info('恢复功能开发中')
}

const handleStop = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要停止活动"${row.name}"吗？停止后无法恢复！`, '警告', {
      type: 'error',
      confirmButtonText: '确定停止',
      confirmButtonClass: 'el-button--danger'
    })
    await stopCampaign(row.id)
    ElMessage.success('活动已停止')
    handleSearch()
  } catch (e) {}
}

const handleDetail = (row: any) => {
  ElMessage.info(`查看活动详情：${row.name}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.campaign-page {
  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
}
</style>
