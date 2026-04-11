-- ============================================
-- 数据库回滚脚本: V1.0.5
-- 功能: 删除添加的外键约束
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 辅助存储过程: 删除外键约束(如果存在)
-- ============================================
DROP PROCEDURE IF EXISTS drop_foreign_key_if_exists;

DELIMITER $$
CREATE PROCEDURE drop_foreign_key_if_exists(
    IN p_table_name VARCHAR(100),
    IN p_constraint_name VARCHAR(100)
)
BEGIN
    DECLARE fk_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO fk_count
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND CONSTRAINT_NAME = p_constraint_name
      AND CONSTRAINT_TYPE = 'FOREIGN KEY';
    
    IF fk_count > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` DROP FOREIGN KEY `', p_constraint_name, '`');
        
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SELECT CONCAT('成功删除外键: ', p_constraint_name, ' (表: ', p_table_name, ')') AS result;
    ELSE
        SELECT CONCAT('外键不存在: ', p_constraint_name) AS result;
    END IF;
END$$
DELIMITER ;

-- ============================================
-- 1. borrow_record 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('borrow_record', 'fk_borrow_user');
CALL drop_foreign_key_if_exists('borrow_record', 'fk_borrow_book');

-- ============================================
-- 2. reservation 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('reservation', 'fk_reserve_user');
CALL drop_foreign_key_if_exists('reservation', 'fk_reserve_book');

-- ============================================
-- 3. fine_record 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('fine_record', 'fk_fine_user');
CALL drop_foreign_key_if_exists('fine_record', 'fk_fine_borrow');

-- ============================================
-- 4. inquiry 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('inquiry', 'fk_inquiry_user');

-- ============================================
-- 5. notification 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('notification', 'fk_notification_user');

-- ============================================
-- 6. seat 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('seat', 'fk_seat_room');

-- ============================================
-- 7. seat_reservation 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('seat_reservation', 'fk_seat_res_user');
CALL drop_foreign_key_if_exists('seat_reservation', 'fk_seat_res_seat');

-- ============================================
-- 8. purchase_request 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('purchase_request', 'fk_purchase_user');

-- ============================================
-- 9. export_task 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('export_task', 'fk_export_user');

-- ============================================
-- 10. book_import_batch 表外键约束
-- ============================================
CALL drop_foreign_key_if_exists('book_import_batch', 'fk_batch_user');

-- ============================================
-- 清理存储过程
-- ============================================
DROP PROCEDURE IF EXISTS drop_foreign_key_if_exists;

-- 提交事务
COMMIT;

-- ============================================
-- 回滚脚本执行完成
-- 删除外键约束数量: 14
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
