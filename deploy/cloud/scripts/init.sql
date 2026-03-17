-- ================================================================
-- WeChat Acquisition Platform - 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- ================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 获客活动表
-- ----------------------------
DROP TABLE IF EXISTS `campaign`;
CREATE TABLE `campaign` (
  `id` varchar(64) NOT NULL COMMENT '活动 ID',
  `name` varchar(255) NOT NULL COMMENT '活动名称',
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT '活动状态 (DRAFT/RUNNING/PAUSED/STOPPED/COMPLETED)',
  `data_source_id` varchar(64) DEFAULT NULL COMMENT '数据源 ID',
  `contact_count` int NOT NULL DEFAULT 0 COMMENT '联系人总数',
  `added_count` int NOT NULL DEFAULT 0 COMMENT '已添加好友数',
  `conversation_count` int NOT NULL DEFAULT 0 COMMENT '会话数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获客活动表';

-- ----------------------------
-- 2. 联系人表
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
  `id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `phone_number` varchar(20) NOT NULL COMMENT '手机号',
  `wechat_id` varchar(128) DEFAULT NULL COMMENT '企微用户 ID',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `status` varchar(32) NOT NULL DEFAULT 'NEW' COMMENT '联系人状态 (NEW/IMPORTED/ADDING/ADDED/CONVERSING/SCORED/CONVERTED)',
  `campaign_id` varchar(64) DEFAULT NULL COMMENT '所属活动 ID',
  `intent_score` decimal(5,2) DEFAULT NULL COMMENT '意向分数 (0-100)',
  `intent_level` varchar(8) DEFAULT NULL COMMENT '意向等级 (A/B/C/D)',
  `tags` json DEFAULT NULL COMMENT '标签 (JSON 格式)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_contact_at` datetime DEFAULT NULL COMMENT '最后联系时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone_number`),
  KEY `idx_status` (`status`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_intent_level` (`intent_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

-- ----------------------------
-- 3. 会话表
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation` (
  `id` varchar(64) NOT NULL COMMENT '会话 ID',
  `contact_id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `campaign_id` varchar(64) NOT NULL COMMENT '活动 ID',
  `wechat_account_id` varchar(64) DEFAULT NULL COMMENT '企微账号 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态 (ACTIVE/PAUSED/COMPLETED/ABANDONED/TRANSFERRED)',
  `mode` varchar(32) NOT NULL DEFAULT 'FREE' COMMENT '对话模式 (PRESET/FREE/HYBRID)',
  `turn_count` int NOT NULL DEFAULT 0 COMMENT '对话轮数',
  `intent_score` decimal(5,2) DEFAULT NULL COMMENT '意向分数',
  `intent_level` varchar(8) DEFAULT NULL COMMENT '意向等级',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_message_at` datetime DEFAULT NULL COMMENT '最后消息时间',
  PRIMARY KEY (`id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- ----------------------------
-- 4. 话术模板表
-- ----------------------------
DROP TABLE IF EXISTS `script_template`;
CREATE TABLE `script_template` (
  `id` varchar(64) NOT NULL COMMENT '模板 ID',
  `name` varchar(255) NOT NULL COMMENT '模板名称',
  `scenario` varchar(64) NOT NULL COMMENT '适用场景',
  `type` varchar(32) NOT NULL DEFAULT 'TEXT' COMMENT '模板类型 (TEXT/IMAGE_TEXT/LINK/CARD)',
  `content` text NOT NULL COMMENT '模板内容',
  `variables` json DEFAULT NULL COMMENT '变量列表',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '模板状态 (DRAFT/ACTIVE/DISABLED/ARCHIVED)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenario` (`scenario`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术模板表';

-- ----------------------------
-- 5. 企微账号表
-- ----------------------------
DROP TABLE IF EXISTS `wechat_account`;
CREATE TABLE `wechat_account` (
  `id` varchar(64) NOT NULL COMMENT '账号 ID',
  `name` varchar(100) NOT NULL COMMENT '账号名称',
  `user_id` varchar(128) NOT NULL COMMENT '企微用户 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态 (ACTIVE/INACTIVE/BLOCKED)',
  `daily_add_count` int NOT NULL DEFAULT 0 COMMENT '今日加人数',
  `daily_message_count` int NOT NULL DEFAULT 0 COMMENT '今日发消息数',
  `risk_level` varchar(32) NOT NULL DEFAULT 'LOW' COMMENT '风险等级 (LOW/MEDIUM/HIGH)',
  `last_reset_at` date DEFAULT NULL COMMENT '计数重置时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企微账号表';

-- ----------------------------
-- 6. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` varchar(64) NOT NULL COMMENT '配置 ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 初始化话术模板
INSERT INTO `script_template` VALUES 
('tpl_001', '开场白', 'opening', 'TEXT', '您好，{name}！我是{company}的{agent_name}。看到您在{industry}行业深耕，我们有一套{product}方案可能对您有帮助，方便聊聊吗？', '["name","company","agent_name","industry","product"]', 1, 'ACTIVE', NOW(), NOW()),
('tpl_002', '产品介绍', 'product_intro', 'TEXT', '我们的{product}主要帮助您解决{pain_point}问题，已经服务了{case_count}+家{industry}企业，平均提升效率{efficiency}%。', '["product","pain_point","case_count","industry","efficiency"]', 2, 'ACTIVE', NOW(), NOW()),
('tpl_003', '价格回复', 'price_inquiry', 'TEXT', '我们的方案根据您的需求定制，基础版{base_price}起，具体要看您的使用规模。您大概需要服务多少用户/月呢？', '["base_price"]', 3, 'ACTIVE', NOW(), NOW()),
('tpl_004', '跟进话术', 'followup', 'TEXT', '{name}总，上次聊的{product}方案，您考虑得怎么样了？最近我们有{promotion}活动，现在签约可以享受{discount}优惠。', '["name","product","promotion","discount"]', 4, 'ACTIVE', NOW(), NOW()),
('tpl_005', '结束话术', 'closing', 'TEXT', '感谢您的时间！后续有任何问题随时联系我。祝您工作顺利！', '[]', 5, 'ACTIVE', NOW(), NOW());

-- 初始化系统配置
INSERT INTO `system_config` VALUES 
('cfg_001', 'wechat.daily_add_limit', '50', '企微单号每日加人上限', NOW(), NOW()),
('cfg_002', 'wechat.hourly_add_limit', '10', '企微单号每小时加人上限', NOW(), NOW()),
('cfg_003', 'wechat.add_interval_min', '30', '加好友最小间隔 (秒)', NOW(), NOW()),
('cfg_004', 'llm.provider', 'dashscope', '大模型服务商', NOW(), NOW()),
('cfg_005', 'llm.model', 'qwen-max', '大模型模型', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 数据变更记录
-- ----------------------------
-- 2026-03-17: 初始版本，创建基础表结构
-- ================================================================
