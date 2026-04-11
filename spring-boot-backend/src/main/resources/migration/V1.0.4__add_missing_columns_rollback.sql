-- ========================================
-- 回滚脚本: V1.0.4__add_missing_columns_rollback.sql
-- 功能: 回滚迁移脚本V1.0.4添加的字段
-- 作者: System
-- 日期: 2026-04-10
-- ========================================

USE CityLibrary;

-- 开始事务
START TRANSACTION;

-- ========================================
-- 7. purchase_request表 - 删除4个字段
-- ========================================
ALTER TABLE purchase_request
DROP COLUMN IF EXISTS remarks;

ALTER TABLE purchase_request
DROP COLUMN IF EXISTS reject_reason;

ALTER TABLE purchase_request
DROP COLUMN IF EXISTS approve_time;

ALTER TABLE purchase_request
DROP COLUMN IF EXISTS quantity;

-- ========================================
-- 6. export_task表 - 删除3个字段
-- ========================================
ALTER TABLE export_task
DROP COLUMN IF EXISTS query_params;

ALTER TABLE export_task
DROP COLUMN IF EXISTS format;

ALTER TABLE export_task
DROP COLUMN IF EXISTS file_size;

-- ========================================
-- 5. seat_reservation表 - 删除外键和字段
-- ========================================
ALTER TABLE seat_reservation
DROP FOREIGN KEY IF EXISTS fk_seat_reservation_room;

ALTER TABLE seat_reservation
DROP COLUMN IF EXISTS room_id;

-- ========================================
-- 4. seat表 - 删除3个字段
-- ========================================
ALTER TABLE seat
DROP COLUMN IF EXISTS has_lamp;

ALTER TABLE seat
DROP COLUMN IF EXISTS has_power;

ALTER TABLE seat
DROP COLUMN IF EXISTS seat_type;

-- ========================================
-- 3. reading_room表 - 删除4个字段
-- ========================================
ALTER TABLE reading_room
DROP COLUMN IF EXISTS image_url;

ALTER TABLE reading_room
DROP COLUMN IF EXISTS description;

ALTER TABLE reading_room
DROP COLUMN IF EXISTS available_seats;

ALTER TABLE reading_room
DROP COLUMN IF EXISTS total_seats;

-- ========================================
-- 2. notification_template表 - 删除3个字段
-- ========================================
ALTER TABLE notification_template
DROP COLUMN IF EXISTS status;

ALTER TABLE notification_template
DROP COLUMN IF EXISTS variables;

ALTER TABLE notification_template
DROP COLUMN IF EXISTS channel;

-- ========================================
-- 1. notification表 - 删除4个字段
-- ========================================
ALTER TABLE notification
DROP COLUMN IF EXISTS read_time;

ALTER TABLE notification
DROP COLUMN IF EXISTS related_type;

ALTER TABLE notification
DROP COLUMN IF EXISTS related_id;

ALTER TABLE notification
DROP COLUMN IF EXISTS channel;

-- 提交事务
COMMIT;

-- ========================================
-- 验证回滚脚本执行结果
-- ========================================
SELECT '回滚脚本 V1.0.4 执行完成' AS message;
