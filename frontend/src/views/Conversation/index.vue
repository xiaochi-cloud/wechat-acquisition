<template>
  <div class="conversation-page">
    <el-card>
      <h2>会话管理</h2>
      <p style="color: #999; margin-top: 10px;">功能开发中...</p>
      
      <el-table :data="mockData" stripe style="margin-top: 20px">
        <el-table-column prop="id" label="会话 ID" width="180" />
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="turnCount" label="对话轮数" width="100" />
        <el-table-column prop="intentScore" label="意向分数" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.intentScore)">
              {{ row.intentScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="lastMessageAt" label="最后消息时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const mockData = [
  { id: 'conv_001', contactName: '张先生', turnCount: 5, intentScore: 85, status: '进行中', lastMessageAt: '2026-03-10 09:30:00' },
  { id: 'conv_002', contactName: '李女士', turnCount: 3, intentScore: 72, status: '进行中', lastMessageAt: '2026-03-10 09:15:00' }
]

const getScoreType = (score: number) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'info'
}
</script>
