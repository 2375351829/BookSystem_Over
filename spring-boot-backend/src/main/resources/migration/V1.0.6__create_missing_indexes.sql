-- ============================================
-- 数据库迁移脚本: V1.0.6
-- 功能: 创建性能优化索引
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- 字符集: utf8mb4
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 辅助存储过程: 创建索引(如果不存在)
-- ============================================
DROP PROCEDURE IF EXISTS create_index_if_not_exists;

DELIMITER $$
CREATE PROCEDURE create_index_if_not_exists(
    IN p_table_name VARCHAR(100),
    IN p_index_name VARCHAR(100),
    IN p_index_type VARCHAR(20),
    IN p_columns VARCHAR(500)
)
BEGIN
    DECLARE idx_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO idx_count
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND INDEX_NAME = p_index_name;
    
    IF idx_count = 0 THEN
        SET @sql = CONCAT('CREATE ', p_index_type, ' `', p_index_name, '` ON `', p_table_name, '` (', p_columns, ')');
        
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SELECT CONCAT('成功创建索引: ', p_index_name, ' (', p_table_name, ')') AS result;
    ELSE
        SELECT CONCAT('索引已存在: ', p_index_name, ' (', p_table_name, ')') AS result;
    END IF;
END$$
DELIMITER ;

-- ============================================
-- 1. notification 表索引
-- ============================================
-- 组合索引: 查询用户未读通知
CALL create_index_if_not_exists('notification', 'idx_user_status', 'INDEX', '`user_id`, `is_read`');

-- 单列索引: 按时间排序
CALL create_index_if_not_exists('notification', 'idx_create_time', 'INDEX', '`create_time`');

-- ============================================
-- 2. notification_template 表索引
-- ============================================
-- 单列索引: 按渠道筛选
CALL create_index_if_not_exists('notification_template', 'idx_channel', 'INDEX', '`channel`');

-- 单列索引: 筛选启用状态
CALL create_index_if_not_exists('notification_template', 'idx_status', 'INDEX', '`status`');

-- ============================================
-- 3. seat 表索引
-- ============================================
-- 唯一索引: 同一阅览室内座位编号唯一
CALL create_index_if_not_exists('seat', 'uk_room_seat', 'UNIQUE INDEX', '`room_id`, `seat_number`');

-- 单列索引: 按座位类型筛选
CALL create_index_if_not_exists('seat', 'idx_seat_type', 'INDEX', '`seat_type`');

-- 单列索引: 筛选可用座位
CALL create_index_if_not_exists('seat', 'idx_status', 'INDEX', '`status`');

-- ============================================
-- 4. seat_reservation 表索引
-- ============================================
-- 组合索引: 查询阅览室某天的预约情况
CALL create_index_if_not_exists('seat_reservation', 'idx_room_date', 'INDEX', '`room_id`, `reserve_date`');

-- ============================================
-- 5. purchase_request 表索引
-- ============================================
-- 单列索引: 按ISBN查询去重
CALL create_index_if_not_exists('purchase_request', 'idx_isbn', 'INDEX', '`isbn`');

-- 单列索引: 按时间排序
CALL create_index_if_not_exists('purchase_request', 'idx_create_time', 'INDEX', '`create_time`');

-- ============================================
-- 清理存储过程
-- ============================================
DROP PROCEDURE IF EXISTS create_index_if_not_exists;

-- 提交事务
COMMIT;

-- ============================================
-- 迁移脚本执行完成
-- 创建索引总数: 10
-- - notification: 2个索引 (1个组合索引, 1个单列索引)
-- - notification_template: 2个索引 (2个单列索引)
-- - seat: 3个索引 (1个唯一索引, 2个单列索引)
-- - seat_reservation: 1个索引 (1个组合索引)
-- - purchase_request: 2个索引 (2个单列索引)
-- ============================================
