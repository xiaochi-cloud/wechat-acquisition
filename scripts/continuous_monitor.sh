#!/bin/bash
#===============================================================================
# WeChat Acquisition - 持续监控和自修复脚本
# 功能：监控应用状态、自动重启、错误报告、自主修复
#===============================================================================

SERVER="47.97.3.29"
SSH_KEY="$HOME/.ssh/id_ed25519"
PROJECT_DIR="/opt/wechat-acquisition"
HEALTH_CHECK_INTERVAL=60  # 60 秒检查一次
MAX_RESTARTS=5
RESTART_DELAY=30

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a /tmp/monitor.log
}

# 健康检查
health_check() {
    local response=$(ssh -i "$SSH_KEY" root@$SERVER "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/health" 2>/dev/null)
    if [ "$response" = "200" ]; then
        return 0
    else
        return 1
    fi
}

# 检查进程
check_process() {
    local pid=$(ssh -i "$SSH_KEY" root@$SERVER "pgrep -f 'wechat-acquisition' | head -1" 2>/dev/null)
    if [ -n "$pid" ]; then
        log "✅ 应用运行中 (PID: $pid)"
        return 0
    else
        log "❌ 应用未运行"
        return 1
    fi
}

# 重启应用
restart_app() {
    log "🔄 重启应用..."
    ssh -i "$SSH_KEY" root@$SERVER "
        cd $PROJECT_DIR && \
        pkill -f 'wechat-acquisition' 2>/dev/null || true && \
        sleep 2 && \
        nohup java -jar target/*.jar --server.port=8080 > /tmp/app.log 2>&1 &
        sleep 5 && \
        pgrep -f 'wechat-acquisition'
    "
}

# 获取错误日志
get_error_log() {
    ssh -i "$SSH_KEY" root@$SERVER "tail -50 /tmp/app.log 2>/dev/null | grep -i error | tail -10"
}

# 分析错误类型
analyze_error() {
    local error_log=$(get_error_log)
    if echo "$error_log" | grep -q "OutOfMemoryError"; then
        echo "内存不足"
    elif echo "$error_log" | grep -q "Connection refused"; then
        echo "数据库连接失败"
    elif echo "$error_log" | grep -q "Port already in use"; then
        echo "端口被占用"
    else
        echo "未知错误"
    fi
}

# 发送报告
send_report() {
    local status="$1"
    local details="$2"
    log "📊 状态报告：$status - $details"
    # 这里可以扩展为发送钉钉/微信/邮件通知
}

# 主监控循环
main() {
    log "╔══════════════════════════════════════════════════════════╗"
    log "║   WeChat Acquisition - 持续监控系统                      ║"
    log "║   检查间隔：${HEALTH_CHECK_INTERVAL}秒                                     ║"
    log "╚══════════════════════════════════════════════════════════╝"
    
    local restart_count=0
    local consecutive_failures=0
    
    while true; do
        # 检查进程
        if ! check_process; then
            log "⚠️  应用进程消失，尝试重启..."
            restart_app
            restart_count=$((restart_count + 1))
            consecutive_failures=$((consecutive_failures + 1))
            
            if [ $restart_count -ge $MAX_RESTARTS ]; then
                send_report "CRITICAL" "达到最大重启次数 $MAX_RESTARTS，需要人工介入"
                restart_count=0
            fi
            
            sleep $RESTART_DELAY
            continue
        fi
        
        # 健康检查
        if ! health_check; then
            log "⚠️  健康检查失败"
            consecutive_failures=$((consecutive_failures + 1))
            
            if [ $consecutive_failures -ge 3 ]; then
                log "⚠️  连续 $consecutive_failures 次失败，分析原因..."
                local error_type=$(analyze_error)
                send_report "WARNING" "健康检查连续失败 - 可能原因：$error_type"
                
                log "🔄 尝试重启..."
                restart_app
                restart_count=$((restart_count + 1))
                consecutive_failures=0
            fi
            
            sleep $RESTART_DELAY
            continue
        fi
        
        # 成功
        consecutive_failures=0
        log "✅ 健康检查通过"
        
        # 定期报告（每 10 次成功检查报告一次）
        if [ $((RANDOM % 10)) -eq 0 ]; then
            send_report "OK" "应用运行正常"
        fi
        
        sleep $HEALTH_CHECK_INTERVAL
    done
}

# 启动监控
main "$@"
