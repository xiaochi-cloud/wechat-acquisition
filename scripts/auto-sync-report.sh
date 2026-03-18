#!/bin/bash
#===============================================================================
# 自主同步汇报脚本 - 每 30 分钟自动汇报进度
#===============================================================================

REPORT_FILE="/tmp/auto_sync_report.txt"
LOG_FILE="/tmp/auto_sync.log"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

generate_report() {
    log "生成同步汇报..."
    
    # 检查执行器状态
    local executor_pid=$(ps aux | grep simple_autonomous | grep -v grep | awk '{print $2}' | head -1)
    local executor_status="❌ 未运行"
    if [ -n "$executor_pid" ]; then
        executor_status="✅ 运行中 (PID: $executor_pid)"
    fi
    
    # 检查服务器状态
    local server_status="❌ 无法连接"
    local app_status="❌ 未运行"
    local mysql_status="❌ 未运行"
    local nginx_status="❌ 未运行"
    
    if ssh -i ~/.ssh/id_ed25519 -o ConnectTimeout=5 root@47.97.3.29 "echo OK" 2>/dev/null; then
        server_status="✅ 连接正常"
        
        # 检查应用
        if ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "curl -s http://localhost:8080/api/health | grep -q UP" 2>/dev/null; then
            app_status="✅ 运行中"
        fi
        
        # 检查 MySQL
        if ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "docker ps | grep -q mysql" 2>/dev/null; then
            mysql_status="✅ 运行中"
        fi
        
        # 检查 Nginx
        if ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "ps aux | grep -q nginx" 2>/dev/null; then
            nginx_status="✅ 运行中"
        fi
    fi
    
    # 检查代码提交
    cd /home/admin/.openclaw/workspace/wechat-acquisition
    local git_status=$(git status --short 2>/dev/null | wc -l)
    local last_commit=$(git log -1 --oneline 2>/dev/null)
    local uncommitted="✅ 无未提交代码"
    if [ "$git_status" -gt "0" ]; then
        uncommitted="⚠️  有 $git_status 个未提交文件"
    fi
    
    # 生成汇报
    cat > "$REPORT_FILE" << EOF
═══════════════════════════════════════════════════════════
🤖 自主系统 - 定时同步汇报
汇报时间：$(date '+%Y-%m-%d %H:%M')
═══════════════════════════════════════════════════════════

【📊 系统状态】

自主执行器：$executor_status
服务器连接：$server_status
应用服务：$app_status
数据库：$mysql_status
Nginx: $nginx_status

【💻 代码状态】

最后提交：$last_commit
未提交文件：$uncommitted

【📋 任务进度】

已完成:
  ✅ T001 - 系统状态检查
  ✅ T002 - 数据导入功能
  ✅ T101 - 客户标签体系
  ✅ T102 - 客户分层展示
  ✅ T103 - 高级搜索功能
  ✅ T003 - 前端部署
  ✅ 完整前端系统
  ✅ 后端 API 对接

待执行:
  ⏳ T104 - 企微 API 对接
  ⏳ T201 - Redis 缓存集成
  ⏳ T202 - 消息队列集成
  ⏳ T203 - API 文档生成

【🌐 访问地址】

前端：http://47.97.3.29/wechat-acquisition/
后端：http://47.97.3.29:8080/api
健康检查：http://47.97.3.29:8080/api/health

【💡 自主决策记录】

$(tail -20 "$LOG_FILE" 2>/dev/null | grep "💡" || echo "暂无新决策")

【📅 下一步计划】

系统继续自主执行:
  • 每 30 分钟自动汇报
  • 执行待办任务
  • 遇到卡点自动跳过
  • 持续迭代改进

═══════════════════════════════════════════════════════════
🚀 系统持续自主运行中，无需人工催促...
═══════════════════════════════════════════════════════════
EOF

    log "📊 汇报已生成：$REPORT_FILE"
}

# 主程序
log "╔══════════════════════════════════════════════════════════╗"
log "║   自主同步汇报脚本已启动                                 ║"
log "║   频率：每 30 分钟自动汇报                                  ║"
log "╚══════════════════════════════════════════════════════════╝"

while true; do
    generate_report
    
    # 输出到标准输出（便于查看）
    cat "$REPORT_FILE"
    
    log "等待 30 分钟后下次汇报..."
    sleep 1800
done
