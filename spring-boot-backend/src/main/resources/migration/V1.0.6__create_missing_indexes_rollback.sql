-- ============================================
-- 数据库迁移回滚脚本: V1.0.6
-- 功能: 删除性能优化索引
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- 字符集: utf8mb4
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 辅助存储过程: 删除索引(如果存在)
-- ============================================
DROP PROCEDURE IF EXISTS drop_index_if_exists;

DELIMITER $$
CREATE PROCEDURE drop_index_if_exists(
    IN p_table_name VARCHAR(100),
    IN p_index_name VARCHAR(100)
)
BEGIN
    DECLARE idx_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO idx_count
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND INDEX_NAME = p_index_name;
    
    IF idx_count > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` DROP INDEX `', p_index_name, '`');
        
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SELECT CONCAT('成功删除索引: ', p_index_name, ' (', p_table_name, ')') AS result;
    ELSE
        SELECT CONCAT('索引不存在: ', p_index_name, ' (', p_table_name, ')') AS result;
    END IF;
END$$
DELIMITER ;

-- ============================================
-- 1. notification 表索引删除
-- ============================================
CALL drop_index_if_exists('notification', 'idx_user_status');
CALL drop_index_if_exists('notification', 'idx_create_time');

-- ============================================
-- 2. notification_template 表索引删除
-- ============================================
CALL drop_index_if_exists('notification_template', 'idx_channel');
CALL drop_index_if_exists('notification_template', 'idx_status');

-- ============================================
-- 3. seat 表索引删除
-- ============================================
CALL drop_index_if_exists('seat', 'uk_room_seat');
CALL drop_index_if_exists('seat', 'idx_seat_type');
CALL drop_index_if_exists('seat', 'idx_status');

-- ============================================
-- 4. seat_reservation 表索引删除
-- ============================================
CALL drop_index_if_exists('seat_reservation', 'idx_room_date');

-- ============================================
-- 5. purchase_request 表索引删除
-- ============================================
CALL drop_index_if_exists('purchase_request', 'idx_isbn');
CALL drop_index_if_exists('purchase_request', 'idx_create_time');

-- ============================================
-- 清理存储过程
-- ============================================
DROP PROCEDURE IF EXISTS drop_index_if_exists;

-- 提交事务
COMMIT;

-- ============================================
-- 回滚脚本执行完成
-- 删除索引总数: 10
-- - notification: 2个索引
-- - notification_template: 2个索引
-- - seat: 3个索引
-- - seat_reservation: 1个索引
-- - purchase_request: 2个索引
-- ============================================
