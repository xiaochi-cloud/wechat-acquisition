<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6"><el-card><div class="stat-card"><div class="icon" style="background:#409EFF"><el-icon><User /></el-icon></div><div><div class="value">{{ stats.contacts }}</div><div class="label">总联系人</div></div></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat-card"><div class="icon" style="background:#67C23A"><el-icon><CircleCheck /></el-icon></div><div><div class="value">{{ stats.added }}</div><div class="label">已添加好友</div></div></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat-card"><div class="icon" style="background:#E6A23C"><el-icon><ChatLineRound /></el-icon></div><div><div class="value">{{ stats.conversations }}</div><div class="label">会话数</div></div></div></el-card></el-col>
      <el-col :span="6"><el-card><div class="stat-card"><div class="icon" style="background:#F56C6C"><el-icon><Star /></el-icon></div><div><div class="value">{{ stats.highIntent }}</div><div class="label">高意向用户</div></div></div></el-card></el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><template #header><span>意向分布</span></template><div ref="intentChart" style="height:300px"></div></el-card></el-col>
      <el-col :span="12"><el-card><template #header><span>添加趋势</span></template><div ref="trendChart" style="height:300px"></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const stats = ref({ contacts: 10245, added: 3856, conversations: 2104, highIntent: 428 })
const intentChart = ref(null)
const trendChart = ref(null)

onMounted(() => {
  const intent = echarts.init(intentChart.value)
  intent.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: [
        { value: 428, name: 'A 类 (高意向)' },
        { value: 856, name: 'B 类 (中意向)' },
        { value: 1200, name: 'C 类 (低意向)' },
        { value: 620, name: 'D 类 (无意向)' }
      ]
    }]
  })
  
  const trend = echarts.init(trendChart.value)
  trend.setOption({
    xAxis: { type: 'category', data: ['3-11','3-12','3-13','3-14','3-15','3-16','3-17'] },
    yAxis: { type: 'value' },
    series: [
      { name: '新增联系人', type: 'line', smooth: true, data: [1200,1500,1800,1600,2000,1900,2245], itemStyle: { color: '#409EFF' } },
      { name: '新增好友', type: 'line', smooth: true, data: [500,600,700,650,800,750,906], itemStyle: { color: '#67C23A' } }
    ]
  })
})
</script>

<style scoped>
.stat-card { display: flex; align-items: center; gap: 15px; }
.icon { width: 60px; height: 60px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.icon .el-icon { font-size: 30px; color: #fff; }
.value { font-size: 24px; font-weight: bold; color: #333; }
.label { font-size: 14px; color: #999; margin-top: 5px; }
</style>
