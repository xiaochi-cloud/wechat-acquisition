# 生产环境部署指南

## 服务器信息
- **IP**: 47.97.3.29
- **用户**: root
- **应用目录**: /opt/wechat-acquisition

## 快速部署

### 1. SSH 登录服务器
```bash
ssh root@47.97.3.29
```

### 2. 克隆代码
```bash
cd /opt
git clone git@github.com:xiaochi-cloud/wechat-acquisition.git
cd wechat-acquisition
```

### 3. 配置环境变量
```bash
cd deploy/cloud
cp .env.example .env
vim .env  # 编辑配置
```

### 4. 启动服务
```bash
# 方式一：Docker Compose 一键启动（推荐）
docker-compose up -d

# 方式二：手动部署
bash deploy.sh
```

### 5. 验证部署
```bash
# 检查容器状态
docker-compose ps

# 检查应用健康
curl http://localhost:8080/api/health

# 查看日志
tail -f /var/log/wechat-acquisition/application.log
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80/443 | 前端 + 反向代理 |
| 后端 API | 8080 | Spring Boot |
| MySQL | 3306 | 数据库 |
| MongoDB | 27017 | 对话记录 |
| Redis | 6379 | 缓存 |
| RocketMQ Console | 8081 | 消息队列控制台 |
| XXL-Job | 8082 | 任务调度 |

## 配置说明

### 必须配置的环境变量
```bash
# 数据库
MYSQL_ROOT_PASSWORD=你的 root 密码
MYSQL_PASSWORD=你的用户密码

# 企业微信
WECHAT_CORP_ID=企业 ID
WECHAT_AGENT_ID=应用 ID
WECHAT_SECRET=应用密钥

# 大模型
DASHSCOPE_API_KEY=通义千问 API Key
```

## 常用命令

```bash
# 查看日志
docker-compose logs -f

# 重启应用
docker-compose restart

# 停止服务
docker-compose down

# 更新代码
cd /opt/wechat-acquisition
git pull
docker-compose restart
```

## 故障排查

### 应用无法启动
```bash
# 查看日志
docker-compose logs app

# 检查端口占用
netstat -tulpn | grep 8080
```

### 数据库连接失败
```bash
# 检查 MySQL 状态
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql
```

---

*版本：1.0 | 更新日期：2026-03-10*
