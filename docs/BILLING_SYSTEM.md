# 计费系统设计

## 一、计费模型

### 1.1 计费维度

| 维度 | 说明 | 计费方式 |
|-----|------|---------|
| 联系人数量 | 导入的联系人总数 | 阶梯定价 |
| 企微账号数 | 绑定的企微账号数量 | 按个计费 |
| 对话轮数 | AI 对话总轮数 | 按量计费 |
| 大模型调用 | Token 消耗量 | 按量计费 |
| 存储空间 | MongoDB 存储用量 | 按 GB 计费 |

### 1.2 套餐设计

#### 基础版 (¥999/月)
```yaml
contacts_limit: 10000
wechat_accounts: 3
conversation_turns: 10000
ai_calls: 5000
storage_gb: 10
features:
  - 基础对话
  - Excel 导入
  - 基础报表
```

#### 专业版 (¥2999/月)
```yaml
contacts_limit: 100000
wechat_accounts: 10
conversation_turns: 100000
ai_calls: 50000
storage_gb: 100
features:
  - 基础版所有功能
  - AI 意向打分
  - 话术模板库
  - 高级报表
  - API 访问
```

#### 企业版 (¥9999/月)
```yaml
contacts_limit: 1000000
wechat_accounts: 50
conversation_turns: 1000000
ai_calls: 500000
storage_gb: 1000
features:
  - 专业版所有功能
  - 定制模型
  - 专属客服
  - SLA 保障
  - 私有化部署选项
```

### 1.3 超量计费

| 资源 | 超量价格 |
|-----|---------|
| 联系人 | ¥0.01/个/月 |
| 企微账号 | ¥100/个/月 |
| 对话轮数 | ¥0.001/轮 |
| AI 调用 | ¥0.01/次 |
| 存储 | ¥1/GB/月 |

---

## 二、计费架构

```
┌─────────────────────────────────────────────────────────────┐
│                      计费网关                                │
│  请求拦截 → 租户识别 → 配额检查 → 计数累加 → 超额拦截         │
├─────────────────────────────────────────────────────────────┤
│                      计费服务                                │
│  UsageService │ QuotaService │ BillingService │ InvoiceService │
├─────────────────────────────────────────────────────────────┤
│                      数据存储                                │
│  MySQL(账单) + Redis(计数) + MongoDB(用量明细)                │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、数据库设计

### 3.1 租户表
```sql
CREATE TABLE tenant (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  plan_type VARCHAR(32) NOT NULL,  -- BASIC/PRO/ENTERPRISE
  status VARCHAR(32) NOT NULL,     -- ACTIVE/SUSPENDED/EXPIRED
  expire_date DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 3.2 套餐表
```sql
CREATE TABLE billing_plan (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(32) NOT NULL,
  price_monthly DECIMAL(10,2) NOT NULL,
  contacts_limit INT,
  wechat_accounts_limit INT,
  conversation_turns_limit INT,
  ai_calls_limit INT,
  storage_gb_limit INT,
  features JSON
);
```

### 3.3 用量表
```sql
CREATE TABLE usage_record (
  id VARCHAR(64) PRIMARY KEY,
  tenant_id VARCHAR(64) NOT NULL,
  resource_type VARCHAR(32) NOT NULL,  -- CONTACT/CONVERSATION/AI_CALL/STORAGE
  resource_id VARCHAR(64),
  quantity INT NOT NULL,
  unit VARCHAR(32),
  recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_tenant_date (tenant_id, recorded_at)
);
```

### 3.4 账单表
```sql
CREATE TABLE invoice (
  id VARCHAR(64) PRIMARY KEY,
  tenant_id VARCHAR(64) NOT NULL,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  base_amount DECIMAL(10,2) NOT NULL,
  overage_amount DECIMAL(10,2) DEFAULT 0,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(32) NOT NULL,  -- UNPAID/PAID/OVERDUE
  paid_at DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 四、核心流程

### 4.1 请求计费流程
```
1. 请求进入 → 2. 解析 Token → 3. 获取租户 ID
       ↓
4. Redis 检查用量配额
       ↓
   ┌──────┴──────┐
   ↓             ↓
未超量         已超量
   ↓             ↓
5. 放行请求    6. 返回 429
   ↓
7. 异步记录用量
```

### 4.2 账单生成流程
```
1. 每月 1 日触发
       ↓
2. 统计上月用量
       ↓
3. 计算基础费用 + 超量费用
       ↓
4. 生成账单
       ↓
5. 发送邮件/短信通知
       ↓
6. 更新租户状态 (如欠费)
```

---

## 五、技术实现

### 5.1 Redis 计数
```java
// Key 设计
usage:{tenant_id}:{resource_type}:{date}

// 命令
INCR usage:tenant_001:CONTACT:2026-03-10
INCR usage:tenant_001:AI_CALL:2026-03-10

// 过期时间：30 天
EXPIRE usage:tenant_001:CONTACT:2026-03-10 2592000
```

### 5.2 配额检查
```java
public class QuotaService {
    
    public boolean checkQuota(String tenantId, ResourceType type) {
        // 获取套餐配额
        BillingPlan plan = getTenantPlan(tenantId);
        
        // 获取当前用量
        long used = getCurrentUsage(tenantId, type);
        
        // 检查是否超量
        long limit = plan.getLimit(type);
        return used < limit;
    }
    
    public void recordUsage(String tenantId, ResourceType type, int quantity) {
        String key = buildKey(tenantId, type, LocalDate.now());
        redisTemplate.opsForValue().increment(key, quantity);
        
        // 异步写入数据库
        usageRecordRepository.save(...);
    }
}
```

### 5.3 超额拦截器
```java
@Component
public class QuotaInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        String tenantId = TenantContext.getTenantId();
        ResourceType type = getResourceType(request);
        
        if (!quotaService.checkQuota(tenantId, type)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "success": false,
                  "error": {
                    "code": "QUOTA_EXCEEDED",
                    "message": "用量已超限额，请升级套餐"
                  }
                }
                """);
            return false;
        }
        
        return true;
    }
}
```

---

## 六、支付集成

### 6.1 支付方式
- 微信支付
- 支付宝
- 银行转账 (企业客户)
- 线下汇款

### 6.2 支付流程
```
1. 用户选择套餐 → 2. 创建支付订单
       ↓
3. 跳转支付页面 → 4. 用户支付
       ↓
5. 支付回调 → 6. 更新订单状态
       ↓
7. 开通/续期服务 → 8. 发送通知
```

---

## 七、风控策略

### 7.1 防刷机制
- 单 IP 请求频率限制
- 异常用量检测
- 黑名单机制

### 7.2 欠费处理
| 欠费时长 | 处理方式 |
|---------|---------|
| 1-3 天 | 短信提醒 |
| 4-7 天 | 限制新增功能 |
| 8-15 天 | 暂停服务 |
| 15 天 + | 数据归档 |

### 7.3 退款政策
- 7 天无理由退款 (未超量)
- 按比例退款 (中途退订)
- 超量部分不退

---

## 八、报表统计

### 8.1 租户视角
- 本月用量统计
- 费用明细
- 历史账单
- 用量趋势

### 8.2 平台视角
- 总收入统计
- 各套餐分布
- 续费率
- 流失率

---

*版本：1.0 | 更新日期：2026-03-10*
