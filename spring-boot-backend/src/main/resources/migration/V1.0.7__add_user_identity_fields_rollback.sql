-- ============================================
-- 版本: V1.0.7
-- 描述: 回滚user_account表的用户身份标识和档案字段
-- 作者: System
-- 日期: 2026-04-11
-- ============================================

USE CityLibrary;

-- 删除索引
DROP INDEX `uk_user_code` ON `user_account`;
DROP INDEX `uk_faculty_id` ON `user_account`;
DROP INDEX `uk_student_id` ON `user_account`;
DROP INDEX `idx_grade` ON `user_account`;
DROP INDEX `idx_college` ON `user_account`;
DROP INDEX `idx_campus` ON `user_account`;

-- 删除用户档案字段
ALTER TABLE `user_account`
  DROP COLUMN `counselor`,
  DROP COLUMN `class_name`,
  DROP COLUMN `grade`,
  DROP COLUMN `college`,
  DROP COLUMN `campus`;

-- 删除身份标识字段
ALTER TABLE `user_account`
  DROP COLUMN `user_code`,
  DROP COLUMN `faculty_id`,
  DROP COLUMN `student_id`;
