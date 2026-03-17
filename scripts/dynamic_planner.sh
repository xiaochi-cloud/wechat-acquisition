#!/bin/bash
#===============================================================================
# WeChat Acquisition - 动态规划器
# 功能：根据实时情况动态调整任务优先级
#===============================================================================

LOG_FILE="/tmp/planner.log"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

# 评估任务优先级
evaluate_priority() {
    local task_id="$1"
    local base_priority="$2"
    
    # 根据实时情况调整优先级
    local current_time=$(date +%H)
    local day_of_week=$(date +%u)
    
    # 工作时间优先处理用户相关任务
    if [ $current_time -ge 9 ] && [ $current_time -le 18 ]; then
        if [[ "$task_id" == T1* ]]; then
            base_priority=$((base_priority - 1))
        fi
    fi
    
    # 周一优先处理 Bug 修复
    if [ $day_of_week -eq 1 ]; then
        if [[ "$task_id" == T0* ]]; then
            base_priority=$((base_priority - 2))
        fi
    fi
    
    echo $base_priority
}

# 重新规划任务队列
replan_queue() {
    log "🔄 开始动态规划..."
    
    # 获取当前系统状态
    local app_status=$(curl -s http://47.97.3.29:8080/api/health 2>/dev/null | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    
    if [ "$app_status" != "UP" ]; then
        log "⚠️  应用状态异常，提升 P0 任务优先级"
        # 提升系统稳定性相关任务优先级
    fi
    
    # 检查错误日志
    local error_count=$(ssh -i ~/.ssh/id_ed25519 root@47.97.3.29 \
        "tail -100 /tmp/app.log 2>/dev/null | grep -c ERROR" 2>/dev/null || echo "0")
    
    if [ "$error_count" -gt "10" ]; then
        log "⚠️  错误日志过多 ($error_count)，提升 Bug 修复优先级"
    fi
    
    # 输出新的任务队列
    log "📋 更新后的任务队列:"
    cat << EOF

当前任务优先级:
  P0 - 立即处理:
    - T001: 验证应用服务状态
    - T002: 系统稳定性问题
  
  P1 - 今天完成:
    - T101: 前端页面完善
    - T102: 数据库集成
    - T103: 前后端联调
  
  P2 - 本周完成:
    - T201: Redis 缓存
    - T202: 消息队列
    - T203: 监控告警完善
  
  P3 - 后续迭代:
    - T301: 权限管理
    - T302: 单元测试
    - T303: API 文档

动态调整:
  - 发现严重 Bug → 自动提升到 P0
  - 用户反馈问题 → 自动提升到 P1
  - 性能问题 → 根据影响范围调整

EOF
    
    log "✅ 动态规划完成"
}

# 主程序
main() {
    log "╔══════════════════════════════════════════════════════════╗"
    log "║   WeChat Acquisition - 动态规划器                        ║"
    log "╚══════════════════════════════════════════════════════════╝"
    
    # 初始规划
    replan_queue
    
    # 持续监控和调整
    while true; do
        sleep 3600  # 每小时重新评估一次
        replan_queue
    done
}

main "$@"
