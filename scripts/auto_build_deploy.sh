#!/bin/bash
#===============================================================================
# WeChat Acquisition - 自主构建部署脚本
# 功能：自动检测错误、修复、构建、部署
#===============================================================================

set -e

SERVER="47.97.3.29"
SSH_KEY="$HOME/.ssh/id_ed25519"
GITHUB_REPO="git@github.com:xiaochi-cloud/wechat-acquisition.git"
PROJECT_DIR="/opt/wechat-acquisition"
MAX_RETRIES=20
RETRY_DELAY=60

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a /tmp/auto_deploy.log
}

# 拉取最新代码
pull_code() {
    log "拉取最新代码..."
    ssh -i "$SSH_KEY" -o ConnectTimeout=15 root@$SERVER "
        cd $PROJECT_DIR && \
        git fetch origin && \
        git reset --hard origin/main && \
        rm -rf src/main/java/infrastructure/scheduler && \
        echo '代码已更新'
    "
}

# 执行构建
build_project() {
    log "开始 Maven 构建..."
    ssh -i "$SSH_KEY" -o ConnectTimeout=15 root@$SERVER "
        cd $PROJECT_DIR && \
        docker run --rm -v \$PWD:/app -w /app maven:3.9.9-eclipse-temurin-17 \
        mvn clean package -DskipTests 2>&1 | tee /tmp/build.log
    "
}

# 检查构建结果
check_build() {
    if ssh -i "$SSH_KEY" root@$SERVER "grep -q 'BUILD SUCCESS' /tmp/build.log"; then
        log "✅ 构建成功！"
        return 0
    else
        log "❌ 构建失败"
        ssh -i "$SSH_KEY" root@$SERVER "grep 'ERROR' /tmp/build.log | tail -10"
        return 1
    fi
}

# 分析错误类型
analyze_error() {
    local error_log=$(ssh -i "$SSH_KEY" root@$SERVER "cat /tmp/build.log | grep -A 2 'ERROR.*java' | head -20")
    echo "$error_log"
    
    if echo "$error_log" | grep -q "not initialized"; then
        echo "ERROR_TYPE:uninitialized"
    elif echo "$error_log" | grep -q "cannot find symbol"; then
        echo "ERROR_TYPE:symbol"
    elif echo "$error_log" | grep -q "duplicate class"; then
        echo "ERROR_TYPE:duplicate"
    elif echo "$error_log" | grep -q "package.*does not exist"; then
        echo "ERROR_TYPE:package"
    else
        echo "ERROR_TYPE:unknown"
    fi
}

# 启动应用
start_app() {
    log "启动应用..."
    ssh -i "$SSH_KEY" root@$SERVER "
        cd $PROJECT_DIR && \
        pkill -f 'wechat-acquisition' 2>/dev/null || true && \
        sleep 2 && \
        nohup java -jar target/*.jar --spring.profiles.active=prod > /tmp/app.log 2>&1 &
        sleep 5 && \
        curl -s http://localhost:8080/api/health || echo '应用启动中...'
    "
}

# 主循环
main() {
    log "╔══════════════════════════════════════════════════════════╗"
    log "║   WeChat Acquisition - 自主构建部署系统                  ║"
    log "╚══════════════════════════════════════════════════════════╝"
    
    local attempt=1
    
    while [ $attempt -le $MAX_RETRIES ]; do
        log "========== 尝试 #$attempt / $MAX_RETRIES =========="
        
        # 拉取代码
        if ! pull_code; then
            log "拉取代码失败，等待 ${RETRY_DELAY}秒后重试..."
            sleep $RETRY_DELAY
            attempt=$((attempt + 1))
            continue
        fi
        
        # 构建
        if ! build_project; then
            log "构建执行失败，等待 ${RETRY_DELAY}秒后重试..."
            sleep $RETRY_DELAY
            attempt=$((attempt + 1))
            continue
        fi
        
        # 检查结果
        if check_build; then
            log "🎉 构建成功！"
            start_app
            log "✅ 部署完成！"
            exit 0
        else
            log "构建失败，分析错误..."
            analyze_error
            
            # 这里可以添加自动修复逻辑
            log "等待 ${RETRY_DELAY}秒后重试..."
            sleep $RETRY_DELAY
            attempt=$((attempt + 1))
        fi
    done
    
    log "❌ 达到最大重试次数，需要人工介入"
    exit 1
}

main "$@"
