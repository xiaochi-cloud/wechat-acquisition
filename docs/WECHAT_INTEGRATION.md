# 企业微信对接文档

## 一、前置准备

### 1.1 开通企业微信
1. 下载企业微信 APP
2. 注册企业 (需营业执照)
3. 完成企业认证 (¥300/年)

### 1.2 创建应用
1. 登录 [企业微信管理后台](https://work.weixin.qq.com/)
2. 进入「应用管理」→「应用」→「自建」
3. 创建应用，填写：
   - 应用名称：获客助手
   - 应用图标：上传 logo
   - 可见范围：选择使用员工

### 1.3 获取配置信息
创建应用后，记录以下信息：

| 配置项 | 获取位置 | 示例 |
|--------|---------|------|
| CorpId | 我的企业 → 企业信息 | `ww1234567890abcdef` |
| AgentId | 应用管理 → 应用详情 | `1000001` |
| Secret | 应用管理 → 应用详情 → 查看 Secret | `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` |
| Token | 应用详情 → 接收消息服务器配置 | 自定义，如 `wechat_token_2026` |
| EncodingAESKey | 应用详情 → 接收消息服务器配置 | 自动生成，43 位字符串 |

### 1.4 配置回调 URL
1. 进入「应用详情」→「接收消息服务器配置」
2. 填写：
   - **URL**: `https://your-domain.com/api/wechat/callback`
   - **Token**: 自定义 token
   - **EncodingAESKey**: 点击随机生成
3. 点击「验证并保存」

---

## 二、API 对接

### 2.1 获取 Access Token

**请求:**
```http
GET https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CORPID&corpsecret=SECRET
```

**响应:**
```json
{
  "errcode": 0,
  "errmsg": "ok",
  "access_token": "ACCESS_TOKEN",
  "expires_in": 7200
}
```

**代码实现:**
```java
String accessToken = weChatApiClient.getAccessToken();
```

---

### 2.2 添加客户好友 (获客助手)

**重要：** 添加好友需要使用「获客助手」能力，需额外申请开通。

**申请路径：**
企业微信管理后台 → 客户联系 → 获客助手 → 申请开通

**API 文档：**
https://developer.work.weixin.qq.com/document/path/95437

**请求:**
```http
POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_friend?access_token=ACCESS_TOKEN
Content-Type: application/json

{
  "name": "获客助手",
  "mobile": "13800138000",
  "email": "",
  "external_contact": {
    "type": "mobile",
    "attr": "13800138000"
  },
  "welcome_msg": {
    "type": "text",
    "text": {
      "content": "您好，欢迎咨询！"
    }
  }
}
```

**响应:**
```json
{
  "errcode": 0,
  "errmsg": "ok",
  "user_id": "USER_ID"
}
```

**代码实现:**
```java
WeChatApiClient.AddFriendResult result = weChatApiClient.addFriend(
    "13800138000", 
    "您好，欢迎咨询！"
);

if (result.isSuccess()) {
    System.out.println("添加成功，userId: " + result.getUserId());
} else {
    System.out.println("添加失败：" + result.getErrorMsg());
}
```

**常见错误码:**
| 错误码 | 说明 | 解决方案 |
|-------|------|---------|
| 60020 | 手机号未注册微信 | 确认用户手机号正确 |
| 60021 | 已是好友 | 跳过添加 |
| 60022 | 添加频率过高 | 降低频率，稍后重试 |
| 60028 | 获客助手未开通 | 申请开通获客助手 |
| 45009 | 达到每日上限 | 切换账号或明天再试 |

---

### 2.3 发送应用消息

**API 文档：**
https://developer.work.weixin.qq.com/document/path/90236

**请求:**
```http
POST https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN
Content-Type: application/json

{
  "touser": "USER_ID",
  "msgtype": "text",
  "agentid": 1000001,
  "text": {
    "content": "您好，这是测试消息"
  }
}
```

**响应:**
```json
{
  "errcode": 0,
  "errmsg": "ok",
  "msgid": "MSG_ID"
}
```

**代码实现:**
```java
String msgId = weChatApiClient.sendMessage(
    "USER_ID", 
    "您好，这是测试消息", 
    "text"
);
```

---

### 2.4 获取客户详情

**API 文档：**
https://developer.work.weixin.qq.com/document/path/92114

**请求:**
```http
GET https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=ACCESS_TOKEN&external_userid=EXTERNAL_USER_ID
```

**响应:**
```json
{
  "errcode": 0,
  "errmsg": "ok",
  "external_contact": {
    "external_userid": "USER_ID",
    "name": "张三",
    "avatar": "AVATAR_URL",
    "gender": 1,
    "unionid": "UNION_ID"
  }
}
```

---

## 三、回调处理

### 3.1 回调事件类型

| 事件类型 | ChangeType | 说明 |
|---------|-----------|------|
| add_external_contact | 新增联系人 | 成员添加了新客户 |
| edit_external_contact | 编辑联系人 | 客户信息变更 |
| del_external_contact | 删除联系人 | 成员删除了客户 |

### 3.2 回调验证

企微会发送 GET 请求验证 URL 有效性：

```
GET /wechat/callback?msg_signature=xxx&timestamp=xxx&nonce=xxx&echostr=xxx
```

**响应:** 直接返回 echostr 参数内容

### 3.3 接收事件

**请求:**
```http
POST /wechat/callback?msg_signature=xxx&timestamp=xxx&nonce=xxx
Content-Type: application/json

{
  "CorpId": "CORP_ID",
  "Event": "change_external_contact",
  "ChangeType": "add_external_contact",
  "UserID": "USER_ID",
  "ExternalUserId": "EXTERNAL_USER_ID"
}
```

**代码实现:**
```java
@PostMapping("/wechat/callback")
public ResponseEntity receiveCallback(
    @RequestParam String msg_signature,
    @RequestParam String timestamp,
    @RequestParam String nonce,
    @RequestBody String requestBody) {
    
    // 验证签名
    if (!checkSignature(msg_signature, timestamp, nonce)) {
        return ResponseEntity.badRequest().build();
    }
    
    // 解析事件
    Map<String, Object> event = JSONUtil.toBean(requestBody, Map.class);
    String changeType = (String) event.get("ChangeType");
    
    if ("add_external_contact".equals(changeType)) {
        // 处理新增好友
        String userId = (String) event.get("UserID");
        String externalUserId = (String) event.get("ExternalUserId");
        // TODO: 发布领域事件
    }
    
    return ResponseEntity.ok(Map.of("success", true));
}
```

---

## 四、频率限制

### 4.1 API 调用频率

| API | 频率限制 |
|-----|---------|
| gettoken | 2000 次/分钟 |
| add_contact_friend | 100 次/分钟 |
| message/send | 2000 次/分钟 |

### 4.2 加好友限制 (防封号)

| 账号类型 | 每日上限 | 每小时上限 | 建议间隔 |
|---------|---------|-----------|---------|
| 新账号 (<30 天) | 20 | 5 | 120 秒 |
| 普通账号 | 50 | 10 | 60 秒 |
| 老账号 (>180 天) | 100 | 15 | 30 秒 |

**代码实现频率控制:**
```java
// 在定时任务中控制频率
RateLimitConfig config = RateLimitConfig.createSafeDefault();

for (Contact contact : pendingContacts) {
    if (!checkRateLimit()) {
        break; // 达到限制，停止
    }
    
    weChatApiClient.addFriend(contact.getPhoneNumber(), "您好");
    
    // 随机延迟
    Thread.sleep(config.getRandomInterval() * 1000L);
}
```

---

## 五、配置说明

### 5.1 application.yml 配置

```yaml
wechat:
  corp-id: ${WECHAT_CORP_ID:}          # 必填
  agent-id: ${WECHAT_AGENT_ID:}        # 必填
  secret: ${WECHAT_SECRET:}            # 必填
  token: ${WECHAT_TOKEN:}              # 回调验证用
  encoding-aes-key: ${WECHAT_ENCODING_AES_KEY:}  # 消息加密用
  
  # 频率限制配置
  rate-limit:
    daily-add-limit: 50
    hourly-add-limit: 10
    minute-add-limit: 2
    add-interval-min: 30
    add-interval-max: 120
```

### 5.2 环境变量

```bash
export WECHAT_CORP_ID=ww1234567890abcdef
export WECHAT_AGENT_ID=1000001
export WECHAT_SECRET=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
export WECHAT_TOKEN=wechat_token_2026
export WECHAT_ENCODING_AES_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

## 六、测试步骤

### 6.1 单元测试

```java
@SpringBootTest
class WeChatApiClientTest {
    
    @Autowired
    private WeChatApiClient weChatClient;
    
    @Test
    void testGetAccessToken() {
        String token = weChatClient.getAccessToken();
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void testAddFriend() {
        WeChatApiClient.AddFriendResult result = 
            weChatClient.addFriend("13800138000", "您好");
        
        System.out.println("结果：" + result);
    }
}
```

### 6.2 集成测试

1. 配置真实企微信息
2. 启动应用
3. 调用 API 测试
4. 检查企微后台

```bash
# 获取 Token
curl "http://localhost:8080/api/wechat/token"

# 添加好友
curl -X POST "http://localhost:8080/api/wechat/friends/add" \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "13800138000", "message": "您好"}'
```

---

## 七、常见问题

### Q1: 获客助手如何开通？
**A:** 企业微信管理后台 → 客户联系 → 获客助手 → 申请开通
- 需企业认证
- 需说明使用场景
- 审核 1-3 个工作日

### Q2: 加好友失败怎么办？
**A:** 检查以下几点：
1. 手机号是否正确
2. 对方是否已注册微信
3. 是否已是好友
4. 是否超过频率限制
5. 账号是否被限制

### Q3: 回调收不到消息？
**A:** 检查：
1. URL 是否可公网访问
2. Token 配置是否一致
3. 签名验证是否正确
4. 服务器防火墙设置

### Q4: 如何避免封号？
**A:** 
1. 严格控制频率 (每日≤50)
2. 使用多账号轮换
3. 话术不要重复
4. 避免短时间批量操作
5. 新账号养号 30 天再用

---

## 八、参考资料

- [企业微信开发者文档](https://developer.work.weixin.qq.com/)
- [获客助手 API](https://developer.work.weixin.qq.com/document/path/95437)
- [客户联系 API](https://developer.work.weixin.qq.com/document/path/92114)
- [消息推送 API](https://developer.work.weixin.qq.com/document/path/90236)
- [回调接口说明](https://developer.work.weixin.qq.com/document/path/90930)

---

*版本：1.0 | 更新日期：2026-03-10*
