# 管理后台前端 - Vue 3 + Element Plus

## ✅ 已完成页面

| 页面 | 路径 | 状态 | 说明 |
|------|------|------|------|
| 登录页 | `/login` | ✅ 完成 | 用户名密码登录 |
| 仪表盘 | `/dashboard` | ✅ 完成 | 数据卡片 + 意向分布图 + 趋势图 |
| 获客活动 | `/campaign` | ✅ 完成 | 列表 + 创建 + 启停控制 |
| 联系人管理 | `/contact` | ✅ 完成 | 列表 + Excel 导入 + 批量操作 |
| 会话管理 | `/conversation` | 🔄 占位 | 框架已搭建 |
| 意向打分 | `/scoring` | 🔄 占位 | 规则展示 |
| 话术模板 | `/template` | 🔄 占位 | 框架已搭建 |
| 数据报表 | `/data` | 🔄 占位 | 框架已搭建 |
| 系统设置 | `/settings` | ✅ 完成 | 企微/大模型/频率配置 |

## 技术栈

- **框架**: Vue 3.4 + TypeScript
- **UI 库**: Element Plus 2.5
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios
- **图表**: ECharts 5
- **构建**: Vite 5

## 快速开始

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问 http://localhost:3000
```

## 默认账号

```
用户名：admin
密码：admin123
```

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API 接口
│   │   ├── request.ts    # Axios 封装
│   │   ├── campaign.ts   # 活动 API
│   │   └── contact.ts    # 联系人 API
│   ├── layouts/          # 布局组件
│   │   └── MainLayout.vue
│   ├── router/           # 路由配置
│   ├── views/            # 页面组件
│   │   ├── Login.vue
│   │   ├── Dashboard/
│   │   ├── Campaign/
│   │   ├── Contact/
│   │   └── ...
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## API 代理

开发模式下，API 请求会代理到后端：
- 前端：`http://localhost:3000`
- 后端：`http://localhost:8080`
- 代理配置：`vite.config.ts`

## 构建生产

```bash
npm run build
```

构建产物在 `dist/` 目录，可部署到 Nginx 或其他静态服务器。

## 待开发功能

- [ ] 会话详情对话框
- [ ] 话术模板编辑器
- [ ] 数据报表导出
- [ ] 用户权限管理
- [ ] 操作日志查看
- [ ] 移动端适配

---

*版本：1.0 | 更新日期：2026-03-10*
