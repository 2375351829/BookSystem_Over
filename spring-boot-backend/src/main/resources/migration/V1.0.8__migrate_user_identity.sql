-- ============================================
-- 版本: V1.0.8
-- 描述: 数据迁移脚本 - 为现有用户生成身份标识ID
-- 作者: System
-- 日期: 2026-04-11
-- ============================================

-- 1. 为学生用户生成学号 (student_id)
-- 使用用户名前缀 + ID 作为默认学号
UPDATE user_account
SET student_id = CONCAT('STU', LPAD(id, 6, '0'))
WHERE user_type = 'STUDENT'
  AND (student_id IS NULL OR student_id = '')
  AND deleted = 0;

-- 2. 为教师用户生成教职工号 (faculty_id)
UPDATE user_account
SET faculty_id = CONCAT('FAC', LPAD(id, 6, '0'))
WHERE user_type = 'TEACHER'
  AND (faculty_id IS NULL OR faculty_id = '')
  AND deleted = 0;

-- 3. 为普通读者/VIP读者生成用户编号 (user_id)
UPDATE user_account
SET user_id = CONCAT('USR', LPAD(id, 6, '0'))
WHERE user_type IN ('READER', 'VIP')
  AND (user_id IS NULL OR user_id = '')
  AND deleted = 0;

-- 4. 迁移 institution 字段到 college 字段
UPDATE user_account
SET college = institution
WHERE (college IS NULL OR college = '')
  AND institution IS NOT NULL
  AND institution != ''
  AND deleted = 0;

-- 5. 设置默认校区（如果为空）
UPDATE user_account
SET campus = '主校区'
WHERE (campus IS NULL OR campus = '')
  AND deleted = 0;

-- 验证迁移结果
SELECT 
    COUNT(*) AS total_users,
    SUM(CASE WHEN student_id IS NOT NULL AND student_id != '' THEN 1 ELSE 0 END) AS users_with_student_id,
    SUM(CASE WHEN faculty_id IS NOT NULL AND faculty_id != '' THEN 1 ELSE 0 END) AS users_with_faculty_id,
    SUM(CASE WHEN user_id IS NOT NULL AND user_id != '' THEN 1 ELSE 0 END) AS users_with_user_id,
    SUM(CASE WHEN college IS NOT NULL AND college != '' THEN 1 ELSE 0 END) AS users_with_college,
    SUM(CASE WHEN campus IS NOT NULL AND campus != '' THEN 1 ELSE 0 END) AS users_with_campus
FROM user_account
WHERE deleted = 0;
