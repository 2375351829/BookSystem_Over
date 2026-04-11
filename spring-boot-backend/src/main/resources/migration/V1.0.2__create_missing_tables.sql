-- ============================================
-- 数据库迁移脚本: V1.0.2
-- 功能: 创建缺失的数据库表
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- 字符集: utf8mb4
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 1. 图书评价表 (book_review)
-- 用途: 用户对图书的评分和评论
-- ============================================
CREATE TABLE IF NOT EXISTS `book_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `rating` TINYINT NOT NULL COMMENT '评分1-5星',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_user` (`book_id`, `user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_review_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating` CHECK (`rating` >= 1 AND `rating` <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书评价表';

-- ============================================
-- 2. 导入明细表 (book_import_detail)
-- 用途: 记录图书导入的详细记录
-- ============================================
CREATE TABLE IF NOT EXISTS `book_import_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `row_number` INT NOT NULL COMMENT 'Excel行号',
  `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '书名',
  `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
  `status` VARCHAR(20) NOT NULL COMMENT '状态：SUCCESS-成功，FAILED-失败，SKIPPED-跳过',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `book_id` BIGINT DEFAULT NULL COMMENT '关联图书ID（成功时）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_batch_status` (`batch_id`, `status`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_isbn` (`isbn`),
  CONSTRAINT `fk_import_detail_batch` FOREIGN KEY (`batch_id`) REFERENCES `book_import_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_import_detail_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入明细表';

-- ============================================
-- 3. 用户行为表 (user_behavior)
-- 用途: 记录用户的浏览、借阅、搜索等行为
-- ============================================
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT DEFAULT NULL COMMENT '图书ID',
  `action_type` VARCHAR(20) NOT NULL COMMENT '行为类型：VIEW/BORROW/RETURN/SEARCH/FAVORITE',
  `action_detail` VARCHAR(500) DEFAULT NULL COMMENT '行为详情',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_action` (`user_id`, `action_type`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_behavior_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_behavior_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- ============================================
-- 4. 图书推荐表 (book_recommendation)
-- 用途: 记录图书推荐信息
-- ============================================
CREATE TABLE IF NOT EXISTS `book_recommendation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '推荐分数',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '推荐理由',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_recommendation_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendation_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书推荐表';

-- 提交事务
COMMIT;

-- ============================================
-- 迁移脚本执行完成
-- 创建表数量: 4
-- - book_review (8个字段, 2个外键, 4个索引, 1个CHECK约束)
-- - book_import_detail (10个字段, 2个外键, 4个索引)
-- - user_behavior (8个字段, 2个外键, 3个索引)
-- - book_recommendation (6个字段, 2个外键, 2个索引)
-- ============================================
