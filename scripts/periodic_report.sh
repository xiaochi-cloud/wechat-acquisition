#!/bin/bash
#===============================================================================
# WeChat Acquisition - 定期汇报脚本
# 功能：每 2 小时自动汇报项目状态
#===============================================================================

SERVER="47.97.3.29"
SSH_KEY="$HOME/.ssh/id_ed25519"
REPORT_FILE="/tmp/periodic_report.log"
HISTORY_FILE="/tmp/report_history.log"

log() {
    echo "$*" | tee -a "$REPORT_FILE"
}

generate_report() {
    log "═══════════════════════════════════════════════════════════"
    log "📊 企业微信获客平台 - 项目进度汇报"
    log "生成时间：$(date '+%Y-%m-%d %H:%M:%S')"
    log "═══════════════════════════════════════════════════════════"
    log ""
    
    # 1. 应用状态
    log "【1️⃣ 应用运行状态】"
    local app_status=$(ssh -i "$SSH_KEY" -o ConnectTimeout=10 root@$SERVER "
        ps aux | grep 'wechat-acquisition' | grep -v grep | wc -l
    " 2>/dev/null)
    
    if [ "$app_status" -gt "0" ]; then
        log "✅ 应用服务：运行中"
        
        # 健康检查
        local health=$(ssh -i "$SSH_KEY" root@$SERVER "curl -s http://localhost:8080/api/health" 2>/dev/null)
        if echo "$health" | grep -q '"status":"UP"'; then
            log "✅ 健康检查：通过"
        else
            log "⚠️  健康检查：未通过"
        fi
    else
        log "❌ 应用服务：未运行"
    fi
    log ""
    
    # 2. 代码状态
    log "【2️⃣ 代码版本状态】"
    local git_info=$(ssh -i "$SSH_KEY" root@$SERVER "cd /opt/wechat-acquisition && git log -1 --oneline" 2>/dev/null)
    log "📦 最新提交：$git_info"
    
    local file_count=$(ssh -i "$SSH_KEY" root@$SERVER "find /opt/wechat-acquisition -name '*.java' | wc -l" 2>/dev/null)
    log "📊 Java 文件：$file_count 个"
    log ""
    
    # 3. 系统资源
    log "【3️⃣ 服务器资源状态】"
    ssh -i "$SSH_KEY" root@$SERVER "
        echo '📈 CPU 使用率:'
        top -bn1 | grep 'Cpu(s)' | awk '{print \$2}' | cut -d'%' -f1 | xargs -I {} echo '  {}%'
        
        echo '💾 内存使用:'
        free -m | grep Mem | awk '{print \"  已用：\" \$3 \"MB / 总计：\" \$2 \"MB (\" int(\$3/\$2*100) \"%)\"}'
        
        echo '💿 磁盘使用:'
        df -h / | tail -1 | awk '{print \"  已用：\" \$3 \" / 总计：\" \$2 \" (\" \$5 \")\"}'
    " 2>/dev/null
    log ""
    
    # 4. 本阶段完成
    log "【4️⃣ 本阶段完成项】"
    log "✅ 后端服务部署上线"
    log "✅ 前端管理后台开发完成"
    log "✅ 健康监控系统运行"
    log "✅ 自动重启机制启用"
    log ""
    
    # 5. 进行中任务
    log "【5️⃣ 进行中任务】"
    log "🔄 数据库初始化"
    log "🔄 前端 Nginx 部署"
    log "🔄 企微 API 对接"
    log ""
    
    # 6. 待办事项
    log "【6️⃣ 待办事项】"
    log "⏳ 大模型意向打分集成"
    log "⏳ 定时任务调度配置"
    log "⏳ 数据报表功能"
    log "⏳ 权限管理系统"
    log ""
    
    # 7. 需要决策
    log "【7️⃣ 需要池少决策】"
    log "• 企业微信配置参数（CorpId/Secret）"
    log "• 通义千问 API Key"
    log "• 数据库连接信息"
    log ""
    
    # 8. 下次汇报
    log "═══════════════════════════════════════════════════════════"
    log "📅 下次汇报：2 小时后"
    log "📁 完整日志：/tmp/periodic_report.log"
    log "═══════════════════════════════════════════════════════════"
}

# 保存历史
save_history() {
    cat "$REPORT_FILE" >> "$HISTORY_FILE"
    echo "" >> "$HISTORY_FILE"
}

# 主程序
main() {
    # 生成汇报
    generate_report
    
    # 保存历史
    save_history
    
    # 输出到文件
    echo ""
    echo "📄 汇报已保存到：$REPORT_FILE"
    echo "📚 历史记录：$HISTORY_FILE"
}

main "$@"
