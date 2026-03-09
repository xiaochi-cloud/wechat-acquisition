# 管理后台前端 - Vue 3 + Element Plus

## 技术栈

- **框架**: Vue 3.4 + TypeScript
- **UI 库**: Element Plus 2.5
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios
- **图表**: ECharts 5
- **构建**: Vite 5

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API 接口
│   │   ├── campaign.ts
│   │   ├── contact.ts
│   │   ├── conversation.ts
│   │   └── scoring.ts
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   │   ├── Layout/
│   │   ├── Table/
│   │   ├── Chart/
│   │   └── Form/
│   ├── composables/      # 组合式函数
│   ├── directives/       # 自定义指令
│   ├── layouts/          # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态
│   │   ├── user.ts
│   │   ├── campaign.ts
│   │   └── settings.ts
│   ├── styles/           # 全局样式
│   ├── types/            # TypeScript 类型
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   │   ├── Dashboard/    # 仪表盘
│   │   ├── Campaign/     # 活动管理
│   │   ├── Contact/      # 联系人管理
│   │   ├── Conversation/ # 会话管理
│   │   ├── Scoring/      # 意向打分
│   │   ├── Template/     # 话术模板
│   │   ├── Data/         # 数据报表
│   │   └── Settings/     # 系统设置
│   ├── App.vue
│   └── main.ts
├── public/
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## 页面清单

### 仪表盘 (Dashboard)
- 数据概览卡片
- 实时数据图表
- 活动状态列表
- 快捷操作入口

### 活动管理 (Campaign)
- 活动列表 (表格 + 搜索)
- 创建活动 (表单向导)
- 活动详情
- 活动编辑
- 启动/暂停/停止操作

### 联系人管理 (Contact)
- 联系人列表
- 批量导入 (Excel 上传)
- 联系人详情
- 标签管理
- 数据导出

### 会话管理 (Conversation)
- 会话列表
- 会话详情 (对话记录)
- 意向打分展示
- 人工介入入口

### 意向打分 (Scoring)
- 打分规则配置
- 意向分布图表
- 批量打分任务
- 打分历史记录

### 话术模板 (Template)
- 模板列表
- 模板创建/编辑
- 模板分类管理
- 话术测试

### 数据报表 (Data)
- 日报/周报/月报
- 渠道效果分析
- 意向转化漏斗
- 数据导出

### 系统设置 (Settings)
- 租户配置
- 企微配置
- 大模型配置
- 权限管理

## 快速开始

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产
npm run build

# 预览构建
npm run preview
```

## 环境变量

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=企业微信获客平台
```

## 组件示例

### 数据表格
```vue
<template>
  <el-table :data="contacts" stripe style="width: 100%">
    <el-table-column prop="phoneNumber" label="手机号" />
    <el-table-column prop="name" label="姓名" />
    <el-table-column prop="status" label="状态">
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ row.status }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
```

### 图表组件
```vue
<template>
  <div ref="chartRef" style="width: 100%; height: 400px"></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref<HTMLElement>()

onMounted(() => {
  const chart = echarts.init(chartRef.value!)
  chart.setOption({
    title: { text: '意向分布' },
    xAxis: { type: 'category', data: ['A', 'B', 'C', 'D'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [100, 200, 150, 80] }]
  })
})
</script>
```

## 开发规范

### 代码风格
- 使用 Composition API
- TypeScript 严格模式
- ESLint + Prettier 格式化

### 命名规范
- 组件：PascalCase (ContactList.vue)
- 文件：kebab-case (contact-api.ts)
- 变量：camelCase

### Git 提交
```bash
feat: 新增联系人导入功能
fix: 修复活动列表分页 bug
docs: 更新 API 文档
style: 代码格式化
refactor: 重构计费模块
```

---

*待开发 - Phase 2*
