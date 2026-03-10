#!/bin/bash
# 企业微信获客平台 - 云服务器部署脚本
# 服务器：47.97.3.29

set -e

echo "=========================================="
echo "  企业微信获客平台 部署脚本"
echo "  服务器：47.97.3.29"
echo "=========================================="

# 配置变量
APP_NAME="wechat-acquisition"
APP_DIR="/opt/${APP_NAME}"
LOG_DIR="/var/log/${APP_NAME}"
BACKUP_DIR="/backup/${APP_NAME}"
JAVA_VERSION="21"

echo "[1/8] 创建目录..."
mkdir -p ${APP_DIR}
mkdir -p ${LOG_DIR}
mkdir -p ${BACKUP_DIR}

echo "[2/8] 检查 Java 环境..."
if ! command -v java &> /dev/null; then
    echo "Java 未安装，正在安装 Java ${JAVA_VERSION}..."
    apt update
    apt install -y openjdk-${JAVA_VERSION}-jdk
else
    java -version
fi

echo "[3/8] 检查 Maven 环境..."
if ! command -v mvn &> /dev/null; then
    echo "Maven 未安装，正在安装..."
    apt install -y maven
else
    mvn -version
fi

echo "[4/8] 检查 Docker 环境..."
if ! command -v docker &> /dev/null; then
    echo "Docker 未安装，正在安装..."
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker
else
    docker --version
fi

echo "[5/8] 拉取代码..."
cd ${APP_DIR}
if [ -d ".git" ]; then
    echo "更新现有代码..."
    git pull
else
    echo "克隆新代码..."
    # 需要 Git 仓库地址
    # git clone <REPO_URL> .
    echo "⚠️ 请提供 Git 仓库地址后手动执行：git clone <REPO_URL> ."
fi

echo "[6/8] 构建项目..."
cd ${APP_DIR}
mvn clean package -DskipTests

echo "[7/8] 启动应用..."
# 停止旧进程
PID=$(ps aux | grep "${APP_NAME}" | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    echo "停止旧进程：$PID"
    kill -15 $PID
    sleep 5
fi

# 启动新进程
nohup java -jar \
    -Xms512m \
    -Xmx2g \
    -XX:+UseG1GC \
    -Dspring.profiles.active=prod \
    target/${APP_NAME}-*.jar \
    > ${LOG_DIR}/application.log 2>&1 &

echo "[8/8] 检查启动状态..."
sleep 5
PID=$(ps aux | grep "${APP_NAME}" | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    echo "✅ 应用启动成功，PID: $PID"
    echo "日志文件：${LOG_DIR}/application.log"
else
    echo "❌ 应用启动失败，请检查日志"
    tail -50 ${LOG_DIR}/application.log
    exit 1
fi

echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "访问地址："
echo "  后端 API: http://47.97.3.29:8080/api"
echo "  健康检查：http://47.97.3.29:8080/api/health"
echo ""
echo "管理命令："
echo "  查看日志：tail -f ${LOG_DIR}/application.log"
echo "  停止服务：kill -15 $PID"
echo "  重启服务：bash ${APP_DIR}/deploy/cloud/deploy.sh"
