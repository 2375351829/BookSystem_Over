-- =====================================================
-- 数据库拼写错误修复回滚脚本
-- 版本: V1.0.1
-- 日期: 2026-04-10
-- 描述: 回滚拼写错误修复，恢复到原始状态
-- 注意: 此脚本仅用于回滚字段名修复，无法恢复已删除的表
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 开启事务
START TRANSACTION;

-- =====================================================
-- 第一部分: 回滚表名修复
-- =====================================================

-- 1. 重新创建 eading_room 表（从 reading_room 复制结构）
SELECT '正在重新创建 eading_room 表' AS '执行信息';
CREATE TABLE IF NOT EXISTS `eading_room` LIKE `reading_room`;

-- 2. 重新创建 otification 表（从 notification 复制结构）
SELECT '正在重新创建 otification 表' AS '执行信息';
CREATE TABLE IF NOT EXISTS `otification` LIKE `notification`;

-- 3. 重命名表: notification_template -> otification_template
SELECT '正在回滚表名: notification_template -> otification_template' AS '执行信息';
RENAME TABLE `notification_template` TO `otification_template`;

-- =====================================================
-- 第二部分: 回滚字段名修复
-- =====================================================

-- 4. 回滚 inquiry 表的字段名
SELECT '正在回滚 inquiry 表的字段名' AS '执行信息';

ALTER TABLE `inquiry` CHANGE COLUMN `title` `itle` VARCHAR(200) NOT NULL COMMENT '咨询标题';
ALTER TABLE `inquiry` CHANGE COLUMN `reply_content` `eply_content` TEXT COMMENT '回复内容';
ALTER TABLE `inquiry` CHANGE COLUMN `replier_id` `eplier_id` BIGINT COMMENT '回复人ID';
ALTER TABLE `inquiry` CHANGE COLUMN `reply_date` `eply_date` DATETIME COMMENT '回复日期';

-- 5. 回滚 seat_reservation 表的字段名
SELECT '正在回滚 seat_reservation 表的字段名' AS '执行信息';

ALTER TABLE `seat_reservation` CHANGE COLUMN `reserve_date` `eserve_date` DATE NOT NULL COMMENT '预约日期';

-- 6. 回滚 export_task 表的字段名
SELECT '正在回滚 export_task 表的字段名' AS '执行信息';

ALTER TABLE `export_task` CHANGE COLUMN `type` `ype` VARCHAR(50) NOT NULL COMMENT '导出类型';
ALTER TABLE `export_task` CHANGE COLUMN `file_name` `ile_name` VARCHAR(200) NOT NULL COMMENT '文件名';
ALTER TABLE `export_task` CHANGE COLUMN `file_path` `ile_path` VARCHAR(500) COMMENT '文件路径';

-- 7. 回滚 category 表的字段名
SELECT '正在回滚 category 表的字段名' AS '执行信息';

ALTER TABLE `category` CHANGE COLUMN `name` `ame` VARCHAR(50) NOT NULL COMMENT '分类名称';

-- 8. 回滚 operation_log 表的字段名
SELECT '正在回滚 operation_log 表的字段名' AS '执行信息';

ALTER TABLE `operation_log` CHANGE COLUMN `result` `esult` TEXT COMMENT '返回结果';

-- 9. 回滚 otification_template 表的字段名
SELECT '正在回滚 otification_template 表的字段名' AS '执行信息';

ALTER TABLE `otification_template` CHANGE COLUMN `title` `itle` VARCHAR(200) NOT NULL COMMENT '模板标题';
ALTER TABLE `otification_template` CHANGE COLUMN `type` `ype` VARCHAR(50) NOT NULL COMMENT '通知类型';

-- =====================================================
-- 提交事务
-- =====================================================
COMMIT;

SELECT '回滚脚本执行完成！' AS '执行结果';
SELECT '字段名修复已回滚到原始状态' AS '执行结果';
SELECT '注意: 已删除的表无法恢复数据，仅恢复了表结构' AS '警告';
