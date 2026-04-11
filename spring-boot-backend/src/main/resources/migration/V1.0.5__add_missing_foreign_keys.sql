-- ============================================
-- 数据库迁移脚本: V1.0.5
-- 功能: 添加缺失的外键约束
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- 字符集: utf8mb4
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 辅助存储过程: 添加外键约束(如果不存在)
-- ============================================
DROP PROCEDURE IF EXISTS add_foreign_key_if_not_exists;

DELIMITER $$
CREATE PROCEDURE add_foreign_key_if_not_exists(
    IN p_table_name VARCHAR(100),
    IN p_constraint_name VARCHAR(100),
    IN p_column_name VARCHAR(100),
    IN p_ref_table VARCHAR(100),
    IN p_ref_column VARCHAR(100),
    IN p_on_delete VARCHAR(20),
    IN p_on_update VARCHAR(20)
)
BEGIN
    DECLARE fk_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO fk_count
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND CONSTRAINT_NAME = p_constraint_name
      AND CONSTRAINT_TYPE = 'FOREIGN KEY';
    
    IF fk_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` ',
                         'ADD CONSTRAINT `', p_constraint_name, '` ',
                         'FOREIGN KEY (`', p_column_name, '`) ',
                         'REFERENCES `', p_ref_table, '` (`', p_ref_column, '`) ',
                         'ON DELETE ', p_on_delete, ' ',
                         'ON UPDATE ', p_on_update);
        
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SELECT CONCAT('成功添加外键: ', p_constraint_name, ' (', p_table_name, '.', p_column_name, ' -> ', p_ref_table, '.', p_ref_column, ')') AS result;
    ELSE
        SELECT CONCAT('外键已存在: ', p_constraint_name) AS result;
    END IF;
END$$
DELIMITER ;

-- ============================================
-- 1. borrow_record 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('borrow_record', 'fk_borrow_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');
CALL add_foreign_key_if_not_exists('borrow_record', 'fk_borrow_book', 'book_id', 'book', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 2. reservation 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('reservation', 'fk_reserve_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');
CALL add_foreign_key_if_not_exists('reservation', 'fk_reserve_book', 'book_id', 'book', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 3. fine_record 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('fine_record', 'fk_fine_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');
CALL add_foreign_key_if_not_exists('fine_record', 'fk_fine_borrow', 'borrow_record_id', 'borrow_record', 'id', 'SET NULL', 'RESTRICT');

-- ============================================
-- 4. inquiry 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('inquiry', 'fk_inquiry_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 5. notification 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('notification', 'fk_notification_user', 'user_id', 'user_account', 'id', 'CASCADE', 'RESTRICT');

-- ============================================
-- 6. seat 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('seat', 'fk_seat_room', 'room_id', 'reading_room', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 7. seat_reservation 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('seat_reservation', 'fk_seat_res_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');
CALL add_foreign_key_if_not_exists('seat_reservation', 'fk_seat_res_seat', 'seat_id', 'seat', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 8. purchase_request 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('purchase_request', 'fk_purchase_user', 'requester_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 9. export_task 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('export_task', 'fk_export_user', 'user_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 10. book_import_batch 表外键约束
-- ============================================
CALL add_foreign_key_if_not_exists('book_import_batch', 'fk_batch_user', 'operator_id', 'user_account', 'id', 'RESTRICT', 'RESTRICT');

-- ============================================
-- 清理存储过程
-- ============================================
DROP PROCEDURE IF EXISTS add_foreign_key_if_not_exists;

-- 提交事务
COMMIT;

-- ============================================
-- 迁移脚本执行完成
-- 添加外键约束数量: 14
-- - borrow_record: 2个外键
-- - reservation: 2个外键
-- - fine_record: 2个外键
-- - inquiry: 1个外键
-- - notification: 1个外键
-- - seat: 1个外键
-- - seat_reservation: 2个外键
-- - purchase_request: 1个外键
-- - export_task: 1个外键
-- - book_import_batch: 1个外键
-- ============================================
