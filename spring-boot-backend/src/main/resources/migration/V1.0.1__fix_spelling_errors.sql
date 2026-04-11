-- =====================================================
-- 数据库拼写错误修复迁移脚本（续）
-- 版本: V1.0.1
-- 日期: 2026-04-10
-- 描述: 修复剩余的拼写错误字段名
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 开启事务
START TRANSACTION;

-- =====================================================
-- 修复剩余的拼写错误字段名
-- =====================================================

-- 1. 修复 export_task 表的字段名
SELECT '正在修复 export_task 表的字段名' AS '执行信息';

ALTER TABLE `export_task` CHANGE COLUMN `ile_name` `file_name` VARCHAR(200) NOT NULL COMMENT '文件名';
ALTER TABLE `export_task` CHANGE COLUMN `ile_path` `file_path` VARCHAR(500) COMMENT '文件路径';

-- 2. 修复 category 表的字段名
SELECT '正在修复 category 表的字段名' AS '执行信息';

ALTER TABLE `category` CHANGE COLUMN `ame` `name` VARCHAR(50) NOT NULL COMMENT '分类名称';

-- 3. 修复 operation_log 表的字段名
SELECT '正在修复 operation_log 表的字段名' AS '执行信息';

ALTER TABLE `operation_log` CHANGE COLUMN `esult` `result` TEXT COMMENT '返回结果';

-- 4. 修复 notification_template 表的字段名
SELECT '正在修复 notification_template 表的字段名' AS '执行信息';

ALTER TABLE `notification_template` CHANGE COLUMN `itle` `title` VARCHAR(200) NOT NULL COMMENT '模板标题';
ALTER TABLE `notification_template` CHANGE COLUMN `ype` `type` VARCHAR(50) NOT NULL COMMENT '通知类型';

-- =====================================================
-- 提交事务
-- =====================================================
COMMIT;

SELECT '迁移脚本执行完成！' AS '执行结果';
SELECT '所有剩余的拼写错误字段名已修复' AS '执行结果';
