# API 文档 - WeChat Acquisition Platform

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

---

## 认证

### 获取 Token
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 7200
  }
}
```

---

## 获客活动 API

### 创建活动
```http
POST /campaigns
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "3 月教育行业获客活动",
  "dataSourceId": "ds_001",
  "targetAudience": {
    "targetCities": ["北京", "上海", "广州"],
    "targetIndustries": ["教育"],
    "ageRange": {
      "minAge": 25,
      "maxAge": 45
    }
  },
  "scheduleConfig": {
    "type": "CRON",
    "cronExpression": "0 0 9 * * ?",
    "batchSize": 100
  },
  "rateLimitConfig": {
    "dailyAddLimit": 50,
    "hourlyAddLimit": 10
  }
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "campaignId": "camp_001",
    "name": "3 月教育行业获客活动",
    "status": "DRAFT",
    "createdAt": "2026-03-10T10:00:00"
  }
}
```

### 获取活动列表
```http
GET /campaigns?status=RUNNING&page=1&size=20
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": "camp_001",
      "name": "3 月教育行业获客活动",
      "status": "RUNNING",
      "contactCount": 10000,
      "addedCount": 3500,
      "conversationCount": 2800,
      "createdAt": "2026-03-10T10:00:00"
    }
  ],
  "total": 1,
  "page": 1,
  "size": 20
}
```

### 启动活动
```http
POST /campaigns/{campaignId}/start
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "message": "活动已启动"
}
```

### 暂停活动
```http
POST /campaigns/{campaignId}/pause
Authorization: Bearer {token}
```

### 停止活动
```http
POST /campaigns/{campaignId}/stop
Authorization: Bearer {token}
```

---

## 联系人 API

### Excel 导入
```http
POST /contacts/import/excel?campaignId=camp_001
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [Excel 文件]
```

**响应:**
```json
{
  "success": true,
  "data": {
    "importId": "import_001",
    "totalCount": 10000,
    "validCount": 9850,
    "invalidCount": 150
  }
}
```

### 批量创建
```http
POST /contacts/batch
Authorization: Bearer {token}
Content-Type: application/json

{
  "campaignId": "camp_001",
  "phoneNumbers": ["13800138000", "13900139000", ...]
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "count": 2,
    "contacts": [...]
  }
}
```

### 获取联系人列表
```http
GET /contacts?campaignId=camp_001&status=ADDED&page=1&size=20
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": "contact_001",
      "phoneNumber": "138****8000",
      "name": "张先生",
      "status": "ADDED",
      "wechatId": "wx_001",
      "tags": ["高意向", "教育行业"],
      "createdAt": "2026-03-10T10:00:00"
    }
  ],
  "total": 3500,
  "page": 1,
  "size": 20
}
```

### 获取联系人详情
```http
GET /contacts/{contactId}
Authorization: Bearer {token}
```

### 更新标签
```http
POST /contacts/{contactId}/tags
Authorization: Bearer {token}
Content-Type: application/json

{
  "意向等级": "A",
  "行业": "教育",
  "备注": "已沟通 3 次，意向强烈"
}
```

---

## 会话 API

### 获取会话列表
```http
GET /conversations?campaignId=camp_001&status=ACTIVE&page=1&size=20
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": "conv_001",
      "contactId": "contact_001",
      "contactName": "张先生",
      "status": "ACTIVE",
      "mode": "HYBRID",
      "turnCount": 5,
      "intentScore": 85.5,
      "intentLevel": "A",
      "lastMessageAt": "2026-03-10T15:30:00"
    }
  ],
  "total": 2800
}
```

### 获取会话详情
```http
GET /conversations/{conversationId}
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": "conv_001",
    "contactId": "contact_001",
    "messages": [
      {
        "id": "msg_001",
        "direction": "FROM_AI",
        "type": "TEXT",
        "content": "您好，我是 XX 教育的顾问...",
        "createdAt": "2026-03-10T10:00:00"
      },
      {
        "id": "msg_002",
        "direction": "FROM_USER",
        "type": "TEXT",
        "content": "你好，我想了解一下课程...",
        "createdAt": "2026-03-10T10:05:00"
      }
    ],
    "intentScore": {
      "totalScore": 85.5,
      "level": "A",
      "dimensions": {
        "responsiveness": 90,
        "interest": 85,
        "urgency": 80,
        "match": 85
      },
      "reasoning": "用户回复及时，主动询问课程详情..."
    }
  }
}
```

---

## 意向打分 API

### 实时打分
```http
POST /scoring/score
Authorization: Bearer {token}
Content-Type: application/json

{
  "conversationId": "conv_001"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "totalScore": 85.5,
    "level": "A",
    "levelName": "高意向",
    "action": "立即人工跟进",
    "dimensions": {
      "responsiveness": 90,
      "interest": 85,
      "urgency": 80,
      "match": 85
    },
    "reasoning": "用户回复及时，主动询问课程详情，表达强烈学习意愿",
    "suggestion": "🔥 高意向用户，建议 1 小时内人工跟进！"
  }
}
```

### 批量打分
```http
POST /scoring/batch
Authorization: Bearer {token}
Content-Type: application/json

{
  "conversationIds": ["conv_001", "conv_002", ...]
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "taskId": "task_001",
    "totalCount": 100,
    "completedCount": 100,
    "results": {
      "conv_001": {
        "totalScore": 85.5,
        "level": "A"
      },
      ...
    }
  }
}
```

### 获取打分规则
```http
GET /scoring/rules
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "dimensions": [
      {"name": "responsiveness", "label": "响应度", "weight": 0.30},
      {"name": "interest", "label": "兴趣度", "weight": 0.35},
      {"name": "urgency", "label": "紧迫度", "weight": 0.20},
      {"name": "match", "label": "匹配度", "weight": 0.15}
    ],
    "levels": [
      {"level": "A", "name": "高意向", "range": "80-100", "action": "立即人工跟进"},
      {"level": "B", "name": "中意向", "range": "60-79", "action": "持续培育"},
      {"level": "C", "name": "低意向", "range": "40-59", "action": "自动化触达"},
      {"level": "D", "name": "无意向", "range": "0-39", "action": "归档/移除"}
    ]
  }
}
```

---

## 健康检查 API

### 健康检查
```http
GET /health
```

**响应:**
```json
{
  "status": "UP",
  "timestamp": "2026-03-10T10:00:00",
  "version": "1.0.0-SNAPSHOT"
}
```

### 就绪检查
```http
GET /ready
```

**响应:**
```json
{
  "status": "UP",
  "checks": {
    "database": "UP",
    "redis": "UP",
    "mongodb": "UP",
    "rocketmq": "UP"
  }
}
```

### 系统信息
```http
GET /info
```

**响应:**
```json
{
  "application": "WeChat Acquisition Platform",
  "description": "企业微信获客平台",
  "version": "1.0.0-SNAPSHOT",
  "java_version": "21.0.0",
  "os_name": "Linux",
  "available_processors": 8,
  "total_memory_mb": 4096,
  "max_memory_mb": 8192
}
```

---

## 错误码

| 错误码 | HTTP 状态 | 说明 |
|-------|----------|------|
| VALIDATION_ERROR | 400 | 参数校验失败 |
| BIND_ERROR | 400 | 参数绑定失败 |
| BUSINESS_ERROR | 400 | 业务错误 |
| UNAUTHORIZED | 401 | 未授权 |
| FORBIDDEN | 403 | 禁止访问 |
| NOT_FOUND | 404 | 资源不存在 |
| RATE_LIMIT | 429 | 频率限制 |
| INTERNAL_ERROR | 500 | 系统内部错误 |

---

## 限流说明

| API | 限流 |
|-----|------|
| POST /contacts/import/excel | 5 次/分钟 |
| POST /scoring/score | 100 次/分钟 |
| POST /scoring/batch | 10 次/分钟 |
| 其他 API | 1000 次/分钟 |

---

*版本：1.0 | 更新日期：2026-03-10*
