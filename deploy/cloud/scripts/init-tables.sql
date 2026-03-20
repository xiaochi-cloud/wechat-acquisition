-- ================================================================
-- WeChat Acquisition Platform - 数据表初始化脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- ================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 联系人表
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
  `id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `phone_number` varchar(20) NOT NULL COMMENT '手机号',
  `wechat_id` varchar(128) DEFAULT NULL COMMENT '企微 ID',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `status` varchar(32) NOT NULL DEFAULT 'NEW' COMMENT '联系人状态',
  `campaign_id` varchar(64) DEFAULT NULL COMMENT '活动 ID',
  `intent_score` decimal(5,2) DEFAULT NULL COMMENT '意向分数',
  `intent_level` varchar(8) DEFAULT NULL COMMENT '意向等级',
  `tags` json DEFAULT NULL COMMENT '标签',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_contact_at` datetime DEFAULT NULL COMMENT '最后联系时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone_number`),
  KEY `idx_status` (`status`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_intent_level` (`intent_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

-- ----------------------------
-- 2. 活动表
-- ----------------------------
DROP TABLE IF EXISTS `campaign`;
CREATE TABLE `campaign` (
  `id` varchar(64) NOT NULL COMMENT '活动 ID',
  `name` varchar(255) NOT NULL COMMENT '活动名称',
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT '活动状态',
  `contact_count` int NOT NULL DEFAULT 0 COMMENT '联系人总数',
  `added_count` int NOT NULL DEFAULT 0 COMMENT '已添加数',
  `conversation_count` int NOT NULL DEFAULT 0 COMMENT '会话数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- ----------------------------
-- 3. 会话表
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation` (
  `id` varchar(64) NOT NULL COMMENT '会话 ID',
  `contact_id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `campaign_id` varchar(64) NOT NULL COMMENT '活动 ID',
  `wechat_account_id` varchar(64) DEFAULT NULL COMMENT '企微账号 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态',
  `mode` varchar(32) NOT NULL DEFAULT 'FREE' COMMENT '对话模式',
  `turn_count` int NOT NULL DEFAULT 0 COMMENT '对话轮数',
  `intent_score` decimal(5,2) DEFAULT NULL COMMENT '意向分数',
  `intent_level` varchar(8) DEFAULT NULL COMMENT '意向等级',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_message_at` datetime DEFAULT NULL COMMENT '最后消息时间',
  PRIMARY KEY (`id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_campaign_id` (`campaign_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- ----------------------------
-- 4. 企微账号表
-- ----------------------------
DROP TABLE IF EXISTS `wechat_account`;
CREATE TABLE `wechat_account` (
  `id` varchar(64) NOT NULL COMMENT '账号 ID',
  `name` varchar(100) NOT NULL COMMENT '账号名称',
  `user_id` varchar(128) NOT NULL COMMENT '企微用户 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',
  `daily_add_count` int NOT NULL DEFAULT 0 COMMENT '今日添加数',
  `risk_level` varchar(32) NOT NULL DEFAULT 'LOW' COMMENT '风险等级',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企微账号表';

-- ----------------------------
-- 5. 标签表
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` varchar(64) NOT NULL COMMENT '标签 ID',
  `name` varchar(100) NOT NULL COMMENT '标签名称',
  `category` varchar(32) DEFAULT 'default' COMMENT '分类',
  `color` varchar(20) DEFAULT '#409EFF' COMMENT '颜色',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- 6. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` varchar(64) NOT NULL COMMENT '日志 ID',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户 ID',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作',
  `module` varchar(50) DEFAULT NULL COMMENT '模块',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP 地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT 'User-Agent',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `message` varchar(500) DEFAULT NULL COMMENT '消息',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------

-- 活动数据
INSERT INTO `campaign` VALUES 
('camp_001', '3 月教育行业获客', 'RUNNING', 0, 0, 0, NOW(), NOW()),
('camp_002', '金融行业精准获客', 'RUNNING', 0, 0, 0, NOW(), NOW());

-- 标签数据
INSERT INTO `tag` VALUES 
('tag_001', '高意向', 'intent', '#F56C6C', '高意向客户', NOW(), NOW()),
('tag_002', '中意向', 'intent', '#E6A23C', '中意向客户', NOW(), NOW()),
('tag_003', '低意向', 'intent', '#409EFF', '低意向客户', NOW(), NOW()),
('tag_004', '教育', 'industry', '#409EFF', '教育行业', NOW(), NOW()),
('tag_005', '金融', 'industry', '#67C23A', '金融行业', NOW(), NOW());

-- 企微账号数据
INSERT INTO `wechat_account` VALUES 
('wa_001', '客服 01', 'wx_001', 'ACTIVE', 0, 'LOW', NOW(), NOW()),
('wa_002', '客服 02', 'wx_002', 'ACTIVE', 0, 'LOW', NOW(), NOW()),
('wa_003', '客服 03', 'wx_003', 'ACTIVE', 0, 'MEDIUM', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 完成提示
-- ----------------------------
SELECT '数据库初始化完成！' AS message;
