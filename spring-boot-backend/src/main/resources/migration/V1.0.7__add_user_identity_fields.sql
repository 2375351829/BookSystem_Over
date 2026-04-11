-- ============================================
-- 版本: V1.0.7
-- 描述: 为user_account表添加用户身份标识和档案字段
-- 作者: System
-- 日期: 2026-04-11
-- ============================================

USE CityLibrary;

-- 添加身份标识字段
ALTER TABLE `user_account`
  ADD COLUMN `student_id` VARCHAR(20) DEFAULT NULL COMMENT '学生学号' AFTER `id_card`,
  ADD COLUMN `faculty_id` VARCHAR(20) DEFAULT NULL COMMENT '教职工号' AFTER `student_id`,
  ADD COLUMN `user_code` VARCHAR(20) DEFAULT NULL COMMENT '用户编号' AFTER `faculty_id`;

-- 添加用户档案字段
ALTER TABLE `user_account`
  ADD COLUMN `campus` VARCHAR(100) DEFAULT NULL COMMENT '校区' AFTER `user_code`,
  ADD COLUMN `college` VARCHAR(100) DEFAULT NULL COMMENT '院系' AFTER `campus`,
  ADD COLUMN `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级' AFTER `college`,
  ADD COLUMN `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级' AFTER `grade`,
  ADD COLUMN `counselor` VARCHAR(50) DEFAULT NULL COMMENT '辅导员' AFTER `class_name`;

-- 创建唯一索引
CREATE UNIQUE INDEX `uk_student_id` ON `user_account` (`student_id`);
CREATE UNIQUE INDEX `uk_faculty_id` ON `user_account` (`faculty_id`);
CREATE UNIQUE INDEX `uk_user_code` ON `user_account` (`user_code`);

-- 创建普通索引以提高查询性能
CREATE INDEX `idx_campus` ON `user_account` (`campus`);
CREATE INDEX `idx_college` ON `user_account` (`college`);
CREATE INDEX `idx_grade` ON `user_account` (`grade`);
