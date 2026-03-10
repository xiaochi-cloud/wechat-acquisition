<template>
  <div class="dashboard">
    <!-- 数据卡片 -->
    <el-row :gutter="20" class="data-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card">
            <div class="card-icon" style="background: #409EFF">
              <el-icon><User /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">10,245</div>
              <div class="card-label">总联系人</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card">
            <div class="card-icon" style="background: #67C23A">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">3,856</div>
              <div class="card-label">已添加好友</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card">
            <div class="card-icon" style="background: #E6A23C">
              <el-icon><ChatLineRound /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">2,104</div>
              <div class="card-label">进行中会话</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card">
            <div class="card-icon" style="background: #F56C6C">
              <el-icon><Star /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">428</div>
              <div class="card-label">高意向用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>意向分布</span>
            </div>
          </template>
          <div ref="intentChartRef" class="chart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近 7 日趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动列表 -->
    <el-card class="activity-list">
      <template #header>
        <div class="card-header">
          <span>进行中活动</span>
          <el-button type="primary" size="small" @click="$router.push('/campaign')">
            查看全部
          </el-button>
        </div>
      </template>
      
      <el-table :data="activities" stripe>
        <el-table-column prop="name" label="活动名称" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactCount" label="联系人" width="100" />
        <el-table-column prop="addedCount" label="已添加" width="100" />
        <el-table-column prop="conversationCount" label="会话数" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/campaign/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'

const intentChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()

const activities = ref([
  {
    id: '1',
    name: '3 月教育行业获客活动',
    status: 'RUNNING',
    contactCount: 10000,
    addedCount: 3500,
    conversationCount: 2800,
    createdAt: '2026-03-01'
  },
  {
    id: '2',
    name: '金融行业精准获客',
    status: 'RUNNING',
    contactCount: 5000,
    addedCount: 2100,
    conversationCount: 1500,
    createdAt: '2026-03-05'
  }
])

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'RUNNING': 'success',
    'PAUSED': 'warning',
    'STOPPED': 'info',
    'DRAFT': 'info'
  }
  return map[status] || 'info'
}

// 初始化意向分布图
const initIntentChart = () => {
  if (!intentChartRef.value) return
  
  const chart = echarts.init(intentChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: 428, name: 'A 类 (高意向)' },
          { value: 856, name: 'B 类 (中意向)' },
          { value: 1200, name: 'C 类 (低意向)' },
          { value: 620, name: 'D 类 (无意向)' }
        ],
        label: { formatter: '{b}: {c} ({d}%)' }
      }
    ]
  })
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  const chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['3-04', '3-05', '3-06', '3-07', '3-08', '3-09', '3-10']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '新增联系人',
        type: 'line',
        data: [1200, 1500, 1800, 1600, 2000, 1900, 2245],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '新增好友',
        type: 'line',
        data: [500, 600, 700, 650, 800, 750, 906],
        smooth: true,
        itemStyle: { color: '#67C23A' }
      }
    ]
  })
}

onMounted(() => {
  initIntentChart()
  initTrendChart()
  
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(intentChartRef.value!)?.resize()
    echarts.getInstanceByDom(trendChartRef.value!)?.resize()
  })
})
</script>

<style scoped lang="scss">
.dashboard {
  .data-cards {
    margin-bottom: 20px;
    
    .card {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .card-icon {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-icon {
          font-size: 30px;
          color: #fff;
        }
      }
      
      .card-content {
        .card-value {
          font-size: 24px;
          font-weight: bold;
          color: #333;
        }
        
        .card-label {
          font-size: 14px;
          color: #999;
          margin-top: 5px;
        }
      }
    }
  }
  
  .charts {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart {
      height: 300px;
    }
  }
  
  .activity-list {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
