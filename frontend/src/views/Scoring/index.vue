<template>
  <div class="scoring-page">
    <el-card>
      <h2>意向打分</h2>
      <p style="color: #999; margin-top: 10px;">功能开发中...</p>
      
      <div style="margin-top: 20px">
        <h3>打分维度</h3>
        <el-descriptions :column="2" border style="margin-top: 10px">
          <el-descriptions-item label="响应度">30%</el-descriptions-item>
          <el-descriptions-item label="兴趣度">35%</el-descriptions-item>
          <el-descriptions-item label="紧迫度">20%</el-descriptions-item>
          <el-descriptions-item label="匹配度">15%</el-descriptions-item>
        </el-descriptions>
        
        <h3 style="margin-top: 20px">意向等级</h3>
        <el-table :data="levelData" stripe style="margin-top: 10px">
          <el-table-column prop="level" label="等级" width="100" />
          <el-table-column prop="name" label="名称" width="120" />
          <el-table-column prop="range" label="分数范围" width="150" />
          <el-table-column prop="action" label="跟进策略" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const levelData = [
  { level: 'A', name: '高意向', range: '80-100', action: '立即人工跟进' },
  { level: 'B', name: '中意向', range: '60-79', action: '持续培育' },
  { level: 'C', name: '低意向', range: '40-59', action: '自动化触达' },
  { level: 'D', name: '无意向', range: '0-39', action: '归档/移除' }
]
</script>
