-- WeChat Acquisition Platform Database Initialization
-- 企业微信获客平台数据库初始化脚本

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ================================
-- 获客活动表
-- ================================
DROP TABLE IF EXISTS `campaign`;
CREATE TABLE `campaign` (
  `id` varchar(64) NOT NULL COMMENT '活动 ID',
  `name` varchar(255) NOT NULL COMMENT '活动名称',
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT '活动状态',
  `data_source_id` varchar(64) DEFAULT NULL COMMENT '数据源 ID',
  `target_audience_config` json DEFAULT NULL COMMENT '目标人群配置',
  `schedule_config` json DEFAULT NULL COMMENT '调度配置',
  `rate_limit_config` json DEFAULT NULL COMMENT '频率限制配置',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获客活动表';

-- ================================
-- 数据源表
-- ================================
DROP TABLE IF EXISTS `data_source`;
CREATE TABLE `data_source` (
  `id` varchar(64) NOT NULL COMMENT '数据源 ID',
  `name` varchar(255) NOT NULL COMMENT '数据源名称',
  `type` varchar(32) NOT NULL COMMENT '数据源类型 (EXCEL/API/WEBHOOK)',
  `config` json NOT NULL COMMENT '数据源配置',
  `import_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '导入状态',
  `contact_count` int NOT NULL DEFAULT 0 COMMENT '联系人数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_import_status` (`import_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源表';

-- ================================
-- 联系人表
-- ================================
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
  `id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `phone_number` varchar(20) NOT NULL COMMENT '手机号',
  `wechat_id` varchar(128) DEFAULT NULL COMMENT '企微 ID',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `status` varchar(32) NOT NULL DEFAULT 'NEW' COMMENT '联系人状态',
  `profile_data` json DEFAULT NULL COMMENT '用户画像数据',
  `tags` json DEFAULT NULL COMMENT '标签',
  `source_id` varchar(64) DEFAULT NULL COMMENT '来源数据源 ID',
  `campaign_id` varchar(64) DEFAULT NULL COMMENT '所属活动 ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_contact_at` datetime DEFAULT NULL COMMENT '最后联系时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone_number`),
  KEY `idx_status` (`status`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_source_id` (`source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

-- ================================
-- 会话表
-- ================================
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation` (
  `id` varchar(64) NOT NULL COMMENT '会话 ID',
  `contact_id` varchar(64) NOT NULL COMMENT '联系人 ID',
  `campaign_id` varchar(64) NOT NULL COMMENT '活动 ID',
  `wechat_account_id` varchar(64) DEFAULT NULL COMMENT '企微账号 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态',
  `mode` varchar(32) NOT NULL DEFAULT 'FREE' COMMENT '对话模式',
  `scenario` varchar(64) DEFAULT NULL COMMENT '对话场景',
  `intent_score` decimal(5,2) DEFAULT NULL COMMENT '意向总分',
  `intent_level` varchar(8) DEFAULT NULL COMMENT '意向等级',
  `turn_count` int NOT NULL DEFAULT 0 COMMENT '对话轮数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_message_at` datetime DEFAULT NULL COMMENT '最后消息时间',
  PRIMARY KEY (`id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_status` (`status`),
  KEY `idx_intent_level` (`intent_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- ================================
-- 话术模板表
-- ================================
DROP TABLE IF EXISTS `script_template`;
CREATE TABLE `script_template` (
  `id` varchar(64) NOT NULL COMMENT '模板 ID',
  `name` varchar(255) NOT NULL COMMENT '模板名称',
  `scenario` varchar(64) NOT NULL COMMENT '适用场景',
  `type` varchar(32) NOT NULL DEFAULT 'TEXT' COMMENT '模板类型',
  `content` text NOT NULL COMMENT '模板内容',
  `variables` json DEFAULT NULL COMMENT '变量列表',
  `conditions` json DEFAULT NULL COMMENT '使用条件',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '模板状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenario` (`scenario`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术模板表';

-- ================================
-- 企微账号表
-- ================================
DROP TABLE IF EXISTS `wechat_account`;
CREATE TABLE `wechat_account` (
  `id` varchar(64) NOT NULL COMMENT '账号 ID',
  `name` varchar(100) NOT NULL COMMENT '账号名称',
  `user_id` varchar(128) NOT NULL COMMENT '企微用户 ID',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',
  `daily_add_count` int NOT NULL DEFAULT 0 COMMENT '今日加人数',
  `daily_message_count` int NOT NULL DEFAULT 0 COMMENT '今日发消息数',
  `risk_level` varchar(32) NOT NULL DEFAULT 'LOW' COMMENT '风险等级',
  `last_reset_at` date DEFAULT NULL COMMENT '计数重置时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企微账号表';

-- ================================
-- 意向打分规则表
-- ================================
DROP TABLE IF EXISTS `scoring_rule`;
CREATE TABLE `scoring_rule` (
  `id` varchar(64) NOT NULL COMMENT '规则 ID',
  `dimension` varchar(32) NOT NULL COMMENT '打分维度',
  `metric_name` varchar(64) NOT NULL COMMENT '指标名称',
  `weight` decimal(3,2) NOT NULL COMMENT '权重',
  `score_rule` text NOT NULL COMMENT '打分规则',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dimension` (`dimension`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意向打分规则表';

-- ================================
-- 初始化打分规则
-- ================================
INSERT INTO `scoring_rule` VALUES 
('rule_001', 'responsiveness', 'reply_speed', 0.10, 'speed < 60 ? 100 : speed < 300 ? 80 : speed < 600 ? 60 : 40', 1, NOW(), NOW()),
('rule_002', 'responsiveness', 'reply_length', 0.10, 'length > 50 ? 100 : length > 20 ? 80 : length > 10 ? 60 : 40', 1, NOW(), NOW()),
('rule_003', 'responsiveness', 'initiative', 0.10, 'count > 3 ? 100 : count > 1 ? 70 : 40', 1, NOW(), NOW()),
('rule_004', 'interest', 'product_keywords', 0.15, 'count > 5 ? 100 : count > 2 ? 80 : count > 0 ? 60 : 30', 1, NOW(), NOW()),
('rule_005', 'interest', 'price_inquiry', 0.10, 'has_inquiry ? 100 : 50', 1, NOW(), NOW()),
('rule_006', 'interest', 'feature_detail', 0.10, 'count > 3 ? 100 : count > 1 ? 70 : 40', 1, NOW(), NOW()),
('rule_007', 'urgency', 'time_expression', 0.10, 'has_expression ? 100 : 50', 1, NOW(), NOW()),
('rule_008', 'urgency', 'decision_cycle', 0.10, 'days < 7 ? 100 : days < 30 ? 70 : days < 90 ? 50 : 30', 1, NOW(), NOW()),
('rule_009', 'match', 'profile_match', 0.10, 'calculated_by_rule_engine', 1, NOW(), NOW()),
('rule_010', 'match', 'behavior_match', 0.05, 'calculated_by_rule_engine', 1, NOW(), NOW());

-- ================================
-- 初始化话术模板
-- ================================
INSERT INTO `script_template` VALUES 
('tpl_001', '开场白', 'opening', 'TEXT', '您好，{name}！我是{company}的{agent_name}。看到您在{industry}行业深耕，我们有一套{product}方案可能对您有帮助，方便聊聊吗？', '["name", "company", "agent_name", "industry", "product"]', NULL, 1, 'ACTIVE', NOW(), NOW()),
('tpl_002', '产品介绍', 'product_intro', 'TEXT', '我们的{product}主要帮助您解决{pain_point}问题，已经服务了{case_count}+家{industry}企业，平均提升效率{efficiency}%。', '["product", "pain_point", "case_count", "industry", "efficiency"]', NULL, 2, 'ACTIVE', NOW(), NOW()),
('tpl_003', '价格回复', 'price_inquiry', 'TEXT', '我们的方案根据您的需求定制，基础版{base_price}起，具体要看您的使用规模。您大概需要服务多少用户/月呢？', '["base_price"]', NULL, 3, 'ACTIVE', NOW(), NOW()),
('tpl_004', '跟进话术', 'followup', 'TEXT', '{name}总，上次聊的{product}方案，您考虑得怎么样了？最近我们有{promotion}活动，现在签约可以享受{discount}优惠。', '["name", "product", "promotion", "discount"]', NULL, 4, 'ACTIVE', NOW(), NOW()),
('tpl_005', '结束话术', 'closing', 'TEXT', '感谢您的时间！后续有任何问题随时联系我。祝您工作顺利！', '[]', NULL, 5, 'ACTIVE', NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ================================
-- XXL-Job 数据库 (任务调度)
-- ================================
CREATE DATABASE IF NOT EXISTS `xxl_job` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
