-- ============================================
-- 数据库回滚脚本: V1.0.2
-- 功能: 删除V1.0.2创建的数据库表
-- 创建时间: 2026-04-10
-- 数据库: CityLibrary
-- ============================================

-- 使用事务确保原子性
START TRANSACTION;

-- ============================================
-- 删除表的顺序需要考虑外键约束
-- 先删除依赖表，再删除被依赖表
-- ============================================

-- 1. 删除图书推荐表
DROP TABLE IF EXISTS `book_recommendation`;

-- 2. 删除用户行为表
DROP TABLE IF EXISTS `user_behavior`;

-- 3. 删除导入明细表
DROP TABLE IF EXISTS `book_import_detail`;

-- 4. 删除图书评价表
DROP TABLE IF EXISTS `book_review`;

-- 提交事务
COMMIT;

-- ============================================
-- 回滚脚本执行完成
-- 删除表数量: 4
-- - book_recommendation
-- - user_behavior
-- - book_import_detail
-- - book_review
-- ============================================
