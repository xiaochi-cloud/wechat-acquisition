# 项目开发进度

## Phase 1: MVP (目标：2 周)

### ✅ 已完成

#### 1. 项目骨架搭建
- [x] 项目目录结构创建
- [x] Maven 配置 (pom.xml)
- [x] README 文档
- [x] Docker Compose 本地部署配置

#### 2. DDD 领域模型
- [x] **获客域 (acquisition)**
  - [x] Campaign (获客活动聚合根)
  - [x] DataSource (数据源聚合根)
  - [x] Contact (联系人聚合根)
  - [x] TargetAudience (目标人群)
  - [x] ScheduleConfig (调度配置)
  - [x] RateLimitConfig (频率限制配置) - 防封号策略

- [x] **对话域 (conversation)**
  - [x] Conversation (会话聚合根)
  - [x] Message (消息实体)
  - [x] ScriptTemplate (话术模板聚合根)
  - [x] DialogueFlow (对话流程)

- [x] **打分域 (scoring)**
  - [x] IntentScore (意向打分值对象)
  - [x] IntentLevel (意向等级枚举)

#### 3. Spec 驱动文档
- [x] data-ingestion.yaml - 数据导入能力规格
- [x] wechat-integration.yaml - 企微对接能力规格
- [x] llm-integration.yaml - 大模型集成能力规格
- [x] intent-scoring.yaml - 意向打分能力规格

#### 4. 基础设施配置
- [x] Spring Boot 启动类
- [x] application.yml 配置文件
- [x] Docker Compose (MySQL + MongoDB + Redis + RocketMQ + XXL-Job)
- [x] 数据库初始化脚本 (含表结构和初始数据)

---

### 🔄 进行中

#### 5. 应用层服务
- [ ] CampaignApplicationService - 活动管理
- [ ] ContactImportService - 联系人导入
- [ ] ConversationService - 会话管理
- [ ] IntentScoringService - 意向打分

#### 6. 基础设施层实现
- [ ] WeChatApiClient - 企微 API 对接
- [ ] DashScopeClient - 通义千问对接
- [ ] RocketMQProducer - 消息发送
- [ ] RedisRateLimiter - 频率控制

#### 7. 接口层 API
- [ ] CampaignController - 活动管理 API
- [ ] ContactController - 联系人管理 API
- [ ] ConversationController - 会话管理 API
- [ ] ScoringController - 打分 API

---

### ⏳ 待开发

#### 8. 管理后台 (前端)
- [ ] Vue 3 项目初始化
- [ ] 活动管理页面
- [ ] 联系人列表页面
- [ ] 会话监控页面
- [ ] 数据报表页面

#### 9. 定时任务
- [ ] 联系人导入任务
- [ ] 自动加好友任务
- [ ] 会话跟进任务
- [ ] 数据清理任务

#### 10. 测试
- [ ] 单元测试
- [ ] 集成测试
- [ ] 压力测试 (10 万用户量级)

---

## 关键里程碑

| 里程碑 | 预计完成时间 | 状态 |
|--------|-------------|------|
| 项目骨架 + 领域模型 | Day 1 | ✅ 已完成 |
| 核心服务实现 | Day 3-5 | 🔄 进行中 |
| 企微 API 对接 | Day 6-8 | ⏳ 待开始 |
| 大模型集成 | Day 9-10 | ⏳ 待开始 |
| 管理后台 | Day 11-13 | ⏳ 待开始 |
| 测试 + 部署 | Day 14 | ⏳ 待开始 |

---

## 技术决策记录

### 数据库选型
- **MySQL**: 核心元数据 (用户、活动、配置)
- **MongoDB**: 对话记录、消息日志 (海量数据)
- **Redis**: 热点数据、频率控制、分布式锁

### 防封号策略
- 单号每日加人 ≤50
- 加好友间隔 30-120 秒随机
- 多账号池自动轮换
- 话术模板多样化

### 意向打分模型
- 4 个维度：响应度 (30%)、兴趣度 (35%)、紧迫度 (20%)、匹配度 (15%)
- 4 个等级：A(80-100)、B(60-79)、C(40-59)、D(0-39)
- 规则引擎 + 大模型混合打分

---

## 下一步行动

1. **实现应用层服务** - 核心业务逻辑编排
2. **对接企微 API** - 需要池少提供企微应用配置
3. **配置大模型** - 需要 DashScope API Key
4. **开发管理后台** - Vue 3 前端

---

## 风险与问题

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 企微 API 权限不足 | 高 | 提前确认 API 权限范围 |
| 大模型成本超预算 | 中 | 规则引擎前置，分级策略 |
| 数据合规问题 | 高 | 确认用户数据来源合法性 |
| 封号风险 | 高 | 严格控制频率，多账号轮换 |

---

*最后更新：2026-03-10*
