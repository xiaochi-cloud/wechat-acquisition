#!/bin/bash
# 自主执行器 - 定时任务版本

LOG_FILE="/tmp/autonomous_runner.log"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

# 检查并启动自主执行器
check_executor() {
    local pid=$(ps aux | grep simple_autonomous.sh | grep -v grep | awk '{print $2}' | head -1)
    if [ -z "$pid" ]; then
        log "自主执行器未运行，启动..."
        cd /home/admin/.openclaw/workspace/wechat-acquisition
        nohup bash scripts/simple_autonomous.sh >> /tmp/autonomous_executor.log 2>&1 &
        log "✅ 自主执行器已启动 (PID: $!)"
    else
        log "✅ 自主执行器运行中 (PID: $pid)"
    fi
}

# 检查并启动同步汇报
check_sync() {
    local pid=$(ps aux | grep auto-sync-report.sh | grep -v grep | awk '{print $2}' | head -1)
    if [ -z "$pid" ]; then
        log "自主汇报未运行，启动..."
        cd /home/admin/.openclaw/workspace/wechat-acquisition
        nohup bash scripts/auto-sync-report.sh >> /tmp/auto_sync.log 2>&1 &
        log "✅ 自主汇报已启动 (PID: $!)"
    else
        log "✅ 自主汇报运行中 (PID: $pid)"
    fi
}

# 检查服务器状态
check_server() {
    log "检查服务器状态..."
    if ssh -i ~/.ssh/id_ed25519 -o ConnectTimeout=5 root@47.97.3.29 "echo OK" 2>/dev/null; then
        log "✅ 服务器连接正常"
        
        # 检查应用
        if ! ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "curl -s http://localhost:8080/api/health | grep -q UP" 2>/dev/null; then
            log "⚠️  应用未运行，尝试重启..."
            ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "
                cd /opt/wechat-acquisition && \
                pkill -f 'wechat-acquisition' && \
                sleep 2 && \
                nohup java -jar target/*.jar --server.port=8080 > /tmp/app.log 2>&1 &
            " &
        else
            log "✅ 应用运行正常"
        fi
        
        # 检查 MySQL
        if ! ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "docker ps | grep -q mysql" 2>/dev/null; then
            log "⚠️  MySQL 未运行，尝试启动..."
            ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "docker start wechat-mysql" &
        else
            log "✅ MySQL 运行正常"
        fi
        
        # 检查 Nginx
        if ! ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "ps aux | grep -q nginx" 2>/dev/null; then
            log "⚠️  Nginx 未运行，尝试启动..."
            ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 "service nginx start" &
        else
            log "✅ Nginx 运行正常"
        fi
    else
        log "⚠️  服务器连接超时，继续执行本地任务"
    fi
}

# 自动提交代码
auto_commit() {
    log "检查代码提交状态..."
    cd /home/admin/.openclaw/workspace/wechat-acquisition
    local git_status=$(git status --short 2>/dev/null | wc -l)
    
    if [ "$git_status" -gt "0" ]; then
        log "发现 $git_status 个未提交文件，自动提交..."
        git add -A
        git commit -m "auto: 定时任务自动提交 $(date '+%Y-%m-%d %H:%M')"
        git push -f origin main 2>&1 | tee /tmp/git_push.log
        log "✅ 代码已自动提交并推送"
    else
        log "✅ 无未提交代码"
    fi
}

# 主函数
main() {
    log "╔══════════════════════════════════════════════════════════╗"
    log "║   自主执行器 - 定时任务版本                              ║"
    log "╚══════════════════════════════════════════════════════════╝"
    
    check_executor
    check_sync
    check_server
    auto_commit
    
    log "✅ 本轮检查完成"
}

main
