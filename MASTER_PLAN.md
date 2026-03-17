# WeChat Acquisition Platform - 总体规划文档

**版本**: 1.0
**更新日期**: 2026-03-17
**规划周期**: 4 周

---

## 🎯 一、项目愿景

打造企业级微信智能获客平台，支持 10 万级用户量级，实现：
- 自动化获客流程
- AI 驱动意向识别
- 数据驱动决策
- 企业级安全标准

---

## 📊 二、现状分析

### 2.1 已完成 (Phase 1)

| 模块 | 完成度 | 文件数 | 说明 |
|------|--------|--------|------|
| **后端框架** | ✅ 100% | 47 Java | DDD 架构，REST API |
| **前端框架** | ⚠️ 50% | 6 Vue | 基础页面，待完善 |
| **数据库设计** | ✅ 100% | 1 SQL | 6 张核心表 |
| **监控系统** | ✅ 100% | 4 Scripts | 健康检查 + 自动重启 |
| **部署脚本** | ✅ 100% | 3 Scripts | Docker + 手动部署 |

### 2.2 待完成

| 模块 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| **前端完善** | 🔴 P0 | 3 天 | 完整管理后台 |
| **数据库集成** | 🔴 P0 | 1 天 | MyBatis-Plus 集成 |
| **Redis 缓存** | 🟠 P1 | 1 天 | 热点数据缓存 |
| **消息队列** | 🟠 P1 | 1 天 | 异步任务处理 |
| **权限管理** | 🟡 P2 | 2 天 | Spring Security+JWT |
| **API 文档** | 🟡 P2 | 0.5 天 | Swagger/Knife4j |
| **单元测试** | 🟡 P2 | 2 天 | JUnit+Mockito |

---

## 🎨 三、前端详细规划

### 3.1 页面清单

#### 核心页面 (P0 - 本周完成)

| 页面 | 路由 | 功能 | 优先级 |
|------|------|------|--------|
| **登录页** | `/login` | 用户登录、权限验证 | P0 |
| **仪表盘** | `/dashboard` | 数据概览、图表展示 | P0 |
| **活动管理** | `/campaigns` | 列表、创建、编辑、启停 | P0 |
| **联系人管理** | `/contacts` | 列表、搜索、导入、导出 | P0 |
| **会话管理** | `/conversations` | 列表、详情、意向分数 | P0 |
| **意向打分** | `/scoring` | 规则配置、打分记录 | P0 |
| **系统设置** | `/settings` | 企微配置、大模型配置 | P0 |

#### 增强页面 (P1 - 下周完成)

| 页面 | 路由 | 功能 | 优先级 |
|------|------|------|--------|
| **数据报表** | `/reports` | 日报/周报/月报、导出 | P1 |
| **话术模板** | `/templates` | 模板管理、变量配置 | P1 |
| **账号管理** | `/accounts` | 企微账号池管理 | P1 |
| **用户管理** | `/users` | 用户、角色、权限 | P1 |
| **操作日志** | `/logs` | 审计日志、查询 | P1 |

#### 商业页面 (P2 - 第 3 周完成)

| 页面 | 路由 | 功能 | 优先级 |
|------|------|------|--------|
| **计费中心** | `/billing` | 套餐、用量、账单 | P2 |
| **客户管理** | `/customers` | 客户信息、合同 | P2 |
| **帮助中心** | `/help` | 文档、FAQ | P2 |

### 3.2 组件库

#### 基础组件

```
components/
├── Layout/
│   ├── Header.vue       # 顶部导航
│   ├── Sidebar.vue      # 侧边栏
│   ├── Footer.vue       # 页脚
│   └── index.js
├── Table/
│   ├── DataTable.vue    # 数据表格
│   ├── SearchBar.vue    # 搜索栏
│   └── Pagination.vue   # 分页
├── Form/
│   ├── FormInput.vue    # 输入框
│   ├── FormSelect.vue   # 选择框
│   └── FormUpload.vue   # 上传
├── Chart/
│   ├── LineChart.vue    # 折线图
│   ├── BarChart.vue     # 柱状图
│   └── PieChart.vue     # 饼图
└── Card/
    ├── StatCard.vue     # 统计卡片
    └── InfoCard.vue     # 信息卡片
```

#### 业务组件

```
components/Business/
├── Campaign/
│   ├── CampaignList.vue     # 活动列表
│   ├── CampaignForm.vue     # 活动表单
│   └── CampaignStat.vue     # 活动统计
├── Contact/
│   ├── ContactList.vue      # 联系人列表
│   ├── ContactImport.vue    # 导入对话框
│   └── ContactDetail.vue    # 联系人详情
├── Conversation/
│   ├── ConversationList.vue # 会话列表
│   ├── ChatWindow.vue       # 聊天窗口
│   └── IntentBadge.vue      # 意向标签
└── Scoring/
    ├── ScoringRules.vue     # 打分规则
    └── ScoreHistory.vue     # 打分历史
```

### 3.3 API 接口规范

```javascript
// API 基础配置
const API_BASE = '/api'

// 认证
POST   /api/auth/login          // 登录
POST   /api/auth/logout         // 登出
GET    /api/auth/profile        // 获取用户信息

// 活动管理
GET    /api/campaigns           // 列表
POST   /api/campaigns           // 创建
GET    /api/campaigns/:id       // 详情
PUT    /api/campaigns/:id       // 更新
DELETE /api/campaigns/:id       // 删除
POST   /api/campaigns/:id/start // 启动
POST   /api/campaigns/:id/pause // 暂停

// 联系人管理
GET    /api/contacts            // 列表
POST   /api/contacts/import     // 导入
POST   /api/contacts/batch-add  // 批量添加
GET    /api/contacts/:id        // 详情
PUT    /api/contacts/:id/tags   // 更新标签

// 会话管理
GET    /api/conversations       // 列表
GET    /api/conversations/:id   // 详情 (含消息记录)
POST   /api/conversations/:id/message // 发送消息

// 意向打分
POST   /api/scoring/score       // 实时打分
GET    /api/scoring/rules       // 打分规则
PUT    /api/scoring/rules       // 更新规则

// 系统设置
GET    /api/settings            // 获取配置
PUT    /api/settings            // 更新配置
```

---

## 📁 四、目录结构规划

```
wechat-acquisition/
├── frontend/                    # 前端项目
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── api/                # API 接口
│   │   │   ├── request.js      # Axios 封装
│   │   │   ├── auth.js         # 认证 API
│   │   │   ├── campaign.js     # 活动 API
│   │   │   ├── contact.js      # 联系人 API
│   │   │   └── index.js
│   │   ├── assets/             # 静态资源
│   │   │   ├── images/
│   │   │   └── styles/
│   │   ├── components/         # 组件库
│   │   │   ├── Layout/
│   │   │   ├── Table/
│   │   │   ├── Form/
│   │   │   ├── Chart/
│   │   │   └── Business/
│   │   ├── router/             # 路由配置
│   │   │   └── index.js
│   │   ├── stores/             # 状态管理 (Pinia)
│   │   │   ├── user.js
│   │   │   ├── campaign.js
│   │   │   └── index.js
│   │   ├── utils/              # 工具函数
│   │   │   ├── format.js
│   │   │   └── validate.js
│   │   ├── views/              # 页面组件
│   │   │   ├── Login/
│   │   │   ├── Dashboard/
│   │   │   ├── Campaign/
│   │   │   ├── Contact/
│   │   │   ├── Conversation/
│   │   │   ├── Scoring/
│   │   │   ├── Reports/
│   │   │   ├── Templates/
│   │   │   └── Settings/
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
├── backend/                     # 后端项目
│   └── src/main/java/...
├── deploy/                      # 部署配置
│   ├── docker-compose.yml
│   └── scripts/
└── docs/                        # 文档
    ├── API.md
    ├── DEPLOY.md
    └── USER_GUIDE.md
```

---

## ⏱️ 五、时间规划

### Week 1: 前端完善 + 数据库集成

| Day | 任务 | 交付物 |
|-----|------|--------|
| Mon | 前端框架搭建 | 完整目录结构、基础组件 |
| Tue | 登录页 + 仪表盘 | 可登录、数据展示 |
| Wed | 活动管理页面 | CRUD 完整功能 |
| Thu | 联系人管理页面 | 列表 + 导入 + 搜索 |
| Fri | 会话管理页面 | 列表 + 详情 + 聊天窗口 |
| Sat | 数据库集成 | MyBatis-Plus + Repository |
| Sun | 联调测试 | 前后端联调 |

### Week 2: 增强功能

| Day | 任务 | 交付物 |
|-----|------|--------|
| Mon | 意向打分页面 | 规则配置 + 打分记录 |
| Tue | 系统设置页面 | 企微/大模型配置 |
| Wed | Redis 缓存集成 | 热点数据缓存 |
| Thu | 消息队列集成 | 异步任务处理 |
| Fri | 数据报表页面 | 日报/周报 |
| Sat | 性能优化 | 响应时间 < 500ms |
| Sun | 测试修复 | Bug 修复 |

### Week 3: 商业化功能

| Day | 任务 | 交付物 |
|-----|------|--------|
| Mon | 权限管理系统 | Spring Security+JWT |
| Tue | 多租户架构 | 租户隔离 |
| Wed | 计费系统 | 套餐 + 用量统计 |
| Thu | 支付集成 | 微信支付 + 支付宝 |
| Fri | API 文档 | Swagger/Knife4j |
| Sat | 单元测试 | 覆盖率 > 60% |
| Sun | 文档完善 | 用户手册 + API 文档 |

### Week 4: 上线准备

| Day | 任务 | 交付物 |
|-----|------|--------|
| Mon | 压力测试 | 支持 1000 QPS |
| Tue | 安全加固 | SQL 注入/XSS 防护 |
| Wed | 监控完善 | Prometheus+Grafana |
| Thu | 备份策略 | 数据备份方案 |
| Fri | 部署演练 | 部署文档 |
| Sat | 验收测试 | 验收报告 |
| Sun | 正式上线 | 🎉 |

---

## 📈 六、成功标准

### 功能标准

- [ ] 所有 P0 页面完成
- [ ] 所有 P1 页面完成 80%
- [ ] 核心流程可完整走通
- [ ] 无 Critical/Major Bug

### 性能标准

- [ ] API 响应时间 P95 < 500ms
- [ ] 页面加载时间 < 3s
- [ ] 支持 1000 并发用户
- [ ] 数据库查询 < 100ms

### 质量标准

- [ ] 单元测试覆盖率 > 60%
- [ ] 代码审查通过率 100%
- [ ] 文档完整度 > 90%
- [ ] 安全扫描无高危漏洞

---

**批准人**: 池少
**批准日期**: 2026-03-17
**下次审查**: 2026-03-24
