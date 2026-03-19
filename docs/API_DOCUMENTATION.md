# 企业微信获客平台 - API 文档

**版本**: 1.0.0  
**更新时间**: 2026-03-19  
**访问地址**: http://47.97.3.29:8080/api

---

## 📋 目录

1. [认证接口](#认证接口)
2. [联系人管理](#联系人管理)
3. [活动管理](#活动管理)
4. [会话管理](#会话管理)
5. [企微对接](#企微对接)
6. [意向打分](#意向打分)

---

## 认证接口

### 1. 用户登录

**接口**: `POST /api/auth/login`

**请求**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

---

## 联系人管理

### 1. 获取联系人列表

**接口**: `GET /api/contacts`

**参数**:
- `page` (可选): 页码，默认 1
- `size` (可选): 每页数量，默认 20
- `status` (可选): 状态筛选
- `keyword` (可选): 搜索关键词

**响应**:
```json
{
  "success": true,
  "data": {
    "total": 100,
    "page": 1,
    "size": 20,
    "data": [
      {
        "id": "c_001",
        "phoneNumber": "138****8000",
        "name": "张先生",
        "status": "ADDED",
        "wechatId": "wx_001",
        "tags": {"意向": "A", "行业": "教育"},
        "lastContactAt": "2026-03-17 10:30"
      }
    ]
  }
}
```

### 2. 创建联系人

**接口**: `POST /api/contacts`

**请求**:
```json
{
  "phoneNumber": "13800138000",
  "name": "张三",
  "campaignId": "camp_001"
}
```

### 3. 批量创建联系人

**接口**: `POST /api/contacts/batch`

**请求**:
```json
{
  "campaignId": "camp_001",
  "contacts": [
    {"phoneNumber": "13800138001", "name": "张三"},
    {"phoneNumber": "13800138002", "name": "李四"}
  ]
}
```

### 4. Excel 导入联系人

**接口**: `POST /api/contacts/import/excel`

**参数**:
- `file`: Excel 文件 (multipart/form-data)
- `campaignId` (可选): 活动 ID

**响应**:
```json
{
  "success": true,
  "data": {
    "totalCount": 100,
    "successCount": 98,
    "failCount": 2,
    "message": "导入成功"
  }
}
```

### 5. 更新联系人标签

**接口**: `POST /api/contacts/{id}/tags`

**请求**:
```json
{
  "意向": "A",
  "行业": "教育",
  "备注": "高意向客户"
}
```

### 6. 删除联系人

**接口**: `DELETE /api/contacts/{id}`

### 7. 批量删除联系人

**接口**: `DELETE /api/contacts/batch`

**请求**:
```json
["c_001", "c_002", "c_003"]
```

### 8. 获取联系人统计

**接口**: `GET /api/contacts/stats`

**响应**:
```json
{
  "success": true,
  "data": {
    "total": 10245,
    "added": 3856,
    "conversing": 2104,
    "highIntent": 428
  }
}
```

---

## 活动管理

### 1. 获取活动列表

**接口**: `GET /api/campaigns`

**参数**:
- `page`: 页码
- `size`: 每页数量
- `status`: 状态筛选 (DRAFT/RUNNING/PAUSED/STOPPED/COMPLETED)

**响应**:
```json
{
  "success": true,
  "data": {
    "total": 10,
    "page": 1,
    "size": 20,
    "data": [
      {
        "id": "camp_001",
        "name": "3 月教育行业获客",
        "status": "RUNNING",
        "contactCount": 10000,
        "addedCount": 3500,
        "conversationCount": 2800,
        "createdAt": "2026-03-01"
      }
    ]
  }
}
```

### 2. 创建活动

**接口**: `POST /api/campaigns`

**请求**:
```json
{
  "name": "4 月金融行业获客"
}
```

### 3. 启动活动

**接口**: `POST /api/campaigns/{id}/start`

### 4. 暂停活动

**接口**: `POST /api/campaigns/{id}/pause`

### 5. 停止活动

**接口**: `POST /api/campaigns/{id}/stop`

### 6. 获取活动统计

**接口**: `GET /api/campaigns/{id}/stats`

---

## 会话管理

### 1. 获取会话列表

**接口**: `GET /api/conversations`

**参数**:
- `page`: 页码
- `size`: 每页数量
- `status`: 状态筛选
- `contactId`: 联系人 ID

**响应**:
```json
{
  "success": true,
  "data": {
    "total": 50,
    "page": 1,
    "size": 20,
    "data": [
      {
        "id": "conv_001",
        "contactId": "c_001",
        "contactName": "张先生",
        "turnCount": 5,
        "intentScore": 85.5,
        "intentLevel": "A",
        "status": "ACTIVE",
        "lastMessageAt": "2026-03-17 10:30"
      }
    ]
  }
}
```

### 2. 获取会话详情

**接口**: `GET /api/conversations/{id}`

**响应**:
```json
{
  "success": true,
  "data": {
    "conversation": {...},
    "messages": [
      {
        "id": "msg_001",
        "direction": "FROM_AI",
        "content": "您好，我是企业微信助手...",
        "createdAt": "2026-03-17 10:00"
      },
      {
        "id": "msg_002",
        "direction": "FROM_USER",
        "content": "你好，我想了解一下...",
        "createdAt": "2026-03-17 10:05"
      }
    ]
  }
}
```

### 3. 发送消息

**接口**: `POST /api/conversations/{id}/messages`

**请求**:
```json
{
  "content": "您好，有什么可以帮您？",
  "direction": "FROM_AI"
}
```

---

## 企微对接

### 1. 添加好友

**接口**: `POST /api/wechat/friends/add`

**请求**:
```json
{
  "phoneNumber": "13800138000",
  "message": "您好，我是企业微信助手！"
}
```

**响应**:
```json
{
  "success": true,
  "userId": "wx_001",
  "message": "添加成功"
}
```

### 2. 批量添加好友

**接口**: `POST /api/wechat/friends/batch-add`

**请求**:
```json
{
  "phoneNumbers": ["13800138001", "13800138002"],
  "message": "您好，我是企业微信助手！"
}
```

**响应**:
```json
{
  "successCount": 2,
  "failCount": 0,
  "failedPhones": []
}
```

### 3. 发送消息

**接口**: `POST /api/wechat/messages/send`

**请求**:
```json
{
  "userId": "wx_001",
  "content": "您好，有什么可以帮您？"
}
```

### 4. 获取账号状态

**接口**: `GET /api/wechat/account/status`

**响应**:
```json
{
  "success": true,
  "data": {
    "connected": true,
    "dailyLimit": 50,
    "dailyUsed": 35,
    "hourlyLimit": 10,
    "hourlyUsed": 5
  }
}
```

---

## 意向打分

### 1. 实时打分

**接口**: `POST /api/scoring/score`

**请求**:
```json
{
  "conversationId": "conv_001"
}
```

**响应**:
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
    "reasoning": "用户回复及时，主动询问产品详情..."
  }
}
```

### 2. 获取打分规则

**接口**: `GET /api/scoring/rules`

**响应**:
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

## 错误码说明

| 错误码 | 说明 | HTTP 状态 |
|--------|------|---------|
| VALIDATION_ERROR | 参数校验失败 | 400 |
| UNAUTHORIZED | 未授权 | 401 |
| NOT_FOUND | 资源不存在 | 404 |
| BUSINESS_ERROR | 业务错误 | 400 |
| INTERNAL_ERROR | 系统内部错误 | 500 |

---

*最后更新：2026-03-19*
