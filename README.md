# WeChat Acquisition Platform - 企业微信获客平台

## 项目概述

基于 DDD + Spec 驱动的企业微信智能获客平台，支持千万级用户数据处理。

### 核心能力

- 📊 **多数据源接入** - Excel/API/Webhook
- 🤖 **企微自动化** - 自动加好友、智能对话
- 🧠 **大模型集成** - 通义千问对话生成
- 🎯 **意向打分** - 多维度线索评分引擎
- 📈 **管理后台** - 数据监控、任务配置、报表导出

---

## 技术栈

| 层级 | 技术选型 |
|------|----------|
| 后端 | Spring Boot 3 + Java 21 |
| 前端 | Vue 3 + Element Plus |
| 数据库 | MySQL 8 + MongoDB + Redis |
| 消息队列 | RocketMQ |
| 任务调度 | XXL-Job |
| 大模型 | DashScope (通义千问) |
| 部署 | Docker + Docker Compose |

---

## 项目结构

```
wechat-acquisition/
├── domain/                 # 领域层 (纯业务逻辑)
│   ├── acquisition/       # 获客域
│   ├── conversation/      # 对话域
│   ├── profile/          # 画像域
│   └── scoring/          # 打分域
├── application/           # 应用层 (用例编排)
├── infrastructure/        # 基础设施层
├── interfaces/           # 接口层
├── spec/                # Spec 驱动文档
└── deploy/              # 部署配置
```

---

## 快速开始

### 本地开发

```bash
cd deploy/local
docker-compose up -d
```

### 构建

```bash
mvn clean package -DskipTests
```

---

## 核心领域模型

| 聚合根 | 职责 |
|--------|------|
| Campaign | 获客活动管理 |
| Contact | 联系人信息 |
| Conversation | 会话管理 |
| IntentModel | 意向打分模型 |
| ScriptTemplate | 话术模板 |

---

## 意向打分模型

| 维度 | 权重 | 说明 |
|------|------|------|
| 响应度 | 30% | 回复速度、长度、主动性 |
| 兴趣度 | 35% | 产品关键词、价格询问 |
| 紧迫度 | 20% | 时间表达、决策周期 |
| 匹配度 | 15% | 用户画像匹配度 |

**意向等级：**
- A 类 (80-100 分): 高意向 → 立即人工跟进
- B 类 (60-79 分): 中意向 → 持续培育
- C 类 (40-59 分): 低意向 → 自动化触达
- D 类 (0-39 分): 无意向 → 归档

---

## 防封号策略

- 单号每日加人 ≤50
- 加好友间隔 30-120 秒随机
- 多账号池自动轮换
- 话术模板多样化

---

## 开发阶段

- [x] Phase 1: 项目骨架搭建
- [ ] Phase 2: 核心领域模型
- [ ] Phase 3: 数据导入模块
- [ ] Phase 4: 企微对接
- [ ] Phase 5: 大模型集成
- [ ] Phase 6: 意向打分引擎
- [ ] Phase 7: 管理后台
- [ ] Phase 8: 部署配置

---

## License

GPL-3.0
