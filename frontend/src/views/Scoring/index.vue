<template>
  <el-card>
    <template #header><span>意向打分规则</span></template>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="响应度">30% - 回复速度、回复长度、主动性</el-descriptions-item>
      <el-descriptions-item label="兴趣度">35% - 产品关键词、价格询问、功能细节</el-descriptions-item>
      <el-descriptions-item label="紧迫度">20% - 时间表达、决策周期</el-descriptions-item>
      <el-descriptions-item label="匹配度">15% - 用户画像匹配程度</el-descriptions-item>
    </el-descriptions>
    
    <h3 style="margin:20px 0 10px">意向等级</h3>
    <el-table :data="levels" stripe>
      <el-table-column prop="level" label="等级" width="100" />
      <el-table-column prop="name" label="名称" width="120" />
      <el-table-column prop="range" label="分数范围" width="150" />
      <el-table-column prop="action" label="跟进策略" />
    </el-table>
  </el-card>
</template>

<script setup>
const levels = [
  { level: 'A', name: '高意向', range: '80-100 分', action: '立即人工跟进' },
  { level: 'B', name: '中意向', range: '60-79 分', action: '持续培育' },
  { level: 'C', name: '低意向', range: '40-59 分', action: '自动化触达' },
  { level: 'D', name: '无意向', range: '0-39 分', action: '归档/移除' }
]
</script>
