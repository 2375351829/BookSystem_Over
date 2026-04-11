-- ============================================
-- 数据库迁移脚本: V1.0.3
-- 功能: 创建用户收藏表
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- 字符集: utf8mb4
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 用户收藏表 (user_favorite)
-- 用途: 记录用户收藏的图书
-- ============================================
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 提交事务
COMMIT;

-- ============================================
-- 迁移脚本执行完成
-- 创建表数量: 1
-- - user_favorite (4个字段, 2个外键, 3个索引)
-- ============================================
