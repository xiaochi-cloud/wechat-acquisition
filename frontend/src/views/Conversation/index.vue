<template>
  <el-card>
    <template #header><span>会话管理</span></template>
    <el-table :data="conversations" stripe>
      <el-table-column prop="id" label="会话 ID" width="180" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="turnCount" label="对话轮数" width="100" />
      <el-table-column prop="intentScore" label="意向分数" width="100">
        <template #default="{ row }">
          <el-tag :type="scoreType(row.intentScore)">{{ row.intentScore }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="lastMessageAt" label="最后消息" width="180" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" @click="handleView(row)">查看对话</el-button>
          <el-button size="small" type="primary" v-if="row.intentScore>=80">转人工</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'

const conversations = ref([
  { id: 'conv_001', contactName: '张先生', turnCount: 5, intentScore: 85, status: '进行中', lastMessageAt: '2026-03-17 10:30' },
  { id: 'conv_002', contactName: '李女士', turnCount: 3, intentScore: 72, status: '进行中', lastMessageAt: '2026-03-17 11:15' },
  { id: 'conv_003', contactName: '王总', turnCount: 8, intentScore: 92, status: '进行中', lastMessageAt: '2026-03-17 12:00' }
])

const scoreType = (s) => s >= 80 ? 'success' : s >= 60 ? 'warning' : 'info'
const handleView = (row) => alert('查看对话：' + row.id)
</script>
