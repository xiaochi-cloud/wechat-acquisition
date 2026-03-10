#!/bin/bash
# 企业微信获客平台 - 一键安装脚本
# 服务器：47.97.3.29

set -e

echo "=========================================="
echo "  企业微信获客平台 - 一键安装"
echo "  服务器：47.97.3.29"
echo "=========================================="

APP_NAME="wechat-acquisition"
APP_DIR="/opt/${APP_NAME}"
LOG_DIR="/var/log/${APP_NAME}"
DATA_DIR="/data/${APP_NAME}"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否 root
if [ "$EUID" -ne 0 ]; then
    log_error "请使用 root 用户执行此脚本"
    exit 1
fi

# 1. 创建目录
log_info "[1/10] 创建目录..."
mkdir -p ${APP_DIR}
mkdir -p ${LOG_DIR}
mkdir -p ${DATA_DIR}/{mysql,mongodb,redis,rocketmq,nginx}

# 2. 检查并安装 Java
log_info "[2/10] 检查 Java 环境..."
if ! command -v java &> /dev/null; then
    log_info "Java 未安装，正在安装 Java 21..."
    yum install -y java-21-openjdk java-21-openjdk-devel || apt install -y openjdk-21-jdk
else
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    log_info "Java 已安装：$JAVA_VERSION"
fi

# 3. 检查并安装 Maven
log_info "[3/10] 检查 Maven 环境..."
if ! command -v mvn &> /dev/null; then
    log_info "Maven 未安装，正在安装..."
    yum install -y maven || apt install -y maven
else
    MVN_VERSION=$(mvn -version 2>&1 | head -1)
    log_info "Maven 已安装：$MVN_VERSION"
fi

# 4. 检查并安装 Docker
log_info "[4/10] 检查 Docker 环境..."
if ! command -v docker &> /dev/null; then
    log_info "Docker 未安装，正在安装..."
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker
    log_info "Docker 安装完成"
else
    log_info "Docker 已安装：$(docker --version)"
fi

# 5. 检查并安装 Docker Compose
log_info "[5/10] 检查 Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    log_info "Docker Compose 未安装，正在安装..."
    curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
else
    log_info "Docker Compose 已安装：$(docker-compose --version)"
fi

# 6. 克隆代码
log_info "[6/10] 克隆代码..."
cd ${APP_DIR}
if [ -d ".git" ]; then
    log_info "代码已存在，执行 git pull..."
    git pull
else
    log_info "正在克隆代码..."
    git clone git@github.com:xiaochi-cloud/wechat-acquisition.git .
fi

# 7. 配置环境变量
log_info "[7/10] 配置环境变量..."
cd ${APP_DIR}/deploy/cloud
if [ ! -f ".env" ]; then
    cp .env.example .env
    log_warn "请编辑 .env 文件配置必要的环境变量"
    log_warn "执行：vim .env"
    read -p "按回车继续..."
fi

# 8. 构建项目
log_info "[8/10] 构建项目..."
cd ${APP_DIR}
mvn clean package -DskipTests -q
log_info "构建完成"

# 9. 启动服务
log_info "[9/10] 启动服务..."
cd ${APP_DIR}/deploy/cloud
docker-compose up -d

# 10. 检查状态
log_info "[10/10] 检查服务状态..."
sleep 10
docker-compose ps

echo ""
echo "=========================================="
echo -e "${GREEN}  部署完成！${NC}"
echo "=========================================="
echo ""
echo "访问地址："
echo "  后端 API: http://47.97.3.29:8080/api"
echo "  健康检查：http://47.97.3.29:8080/api/health"
echo "  RocketMQ Console: http://47.97.3.29:8081"
echo "  XXL-Job: http://47.97.3.29:8082"
echo ""
echo "管理命令："
echo "  查看日志：cd ${APP_DIR}/deploy/cloud && docker-compose logs -f"
echo "  重启服务：cd ${APP_DIR}/deploy/cloud && docker-compose restart"
echo "  停止服务：cd ${APP_DIR}/deploy/cloud && docker-compose down"
echo ""
echo "配置文件："
echo "  环境变量：${APP_DIR}/deploy/cloud/.env"
echo "  应用日志：${LOG_DIR}/application.log"
echo ""
