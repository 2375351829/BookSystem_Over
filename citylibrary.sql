/*
 Navicat Premium Dump SQL

 Source Server         : C盘Mysql
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : citylibrary

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 12/04/2026 16:25:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for acquisition_task
-- ----------------------------
DROP TABLE IF EXISTS `acquisition_task`;
CREATE TABLE `acquisition_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NULL DEFAULT NULL,
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `book_isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `assignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending',
  `operator_id` bigint NULL DEFAULT NULL,
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of acquisition_task
-- ----------------------------

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `translator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publish_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `edition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'zh-CN',
  `pages` int NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `summary_en` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'available',
  `total_copies` int NOT NULL DEFAULT 1,
  `available_copies` int NOT NULL DEFAULT 1,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `shelf_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_author`(`author` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (1, '9787010001234', '共产党宣言', '', '', '马克思 恩格斯', '', '', '人民出版社', '2018', '', 'A', '马克思主义', 'zh-CN', 120, 25.00, '', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '马克思主义经典著作', '', '0', 10, 5, '主馆2楼A区', 'A1-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (2, '9787100012345', '西方哲学史', '', '', '罗素', '', '', '商务印书馆', '2016', '', 'B', '哲学', 'zh-CN', 850, 98.00, '', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '西方哲学经典著作', '', '可借', 6, 4, '主馆2楼B区', 'B1-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (3, '9787300123456', '经济学原理', '', '', '曼昆', '', '', '北京大学出版社', '2019', '', 'F', '经济', 'zh-CN', 680, 78.00, '', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '经济学入门教材', '', '可借', 15, 12, '主馆2楼F区', 'F1-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (4, '9787040123456', '教育学', NULL, NULL, '王道俊', NULL, NULL, '人民教育出版社', '2019', NULL, 'G', '教育', 'zh-CN', 480, 45.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '教育学基础教材', NULL, '可借', 20, 15, '主馆2楼G区', 'G-01-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (5, '9787560012345', '新概念英语1', NULL, NULL, '亚历山大', NULL, NULL, '外语教学与研究出版社', '2017', NULL, 'H', '语言', 'zh-CN', 280, 38.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '经典英语教材', NULL, 'borrowed', 30, 24, '主馆2楼H区', 'H-01-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (6, '9787020012345', '红楼梦', NULL, NULL, '曹雪芹', NULL, NULL, '人民文学出版社', '2018', NULL, 'I', '文学', 'zh-CN', 1600, 88.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '中国古典名著', NULL, '可借', 20, 15, '主馆2楼I区', 'I-01-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (7, '9787020012346', '三国演义', NULL, NULL, '罗贯中', NULL, NULL, '人民文学出版社', '2017', NULL, 'I', '文学', 'zh-CN', 980, 68.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '中国古典名著', NULL, '可借', 18, 14, '主馆2楼I区', 'I-01-02', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (8, '9787101012345', '史记', NULL, NULL, '司马迁', NULL, NULL, '中华书局', '2016', NULL, 'K', '历史', 'zh-CN', 3200, 298.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '中国史学经典', NULL, '可借', 8, 6, '主馆2楼K区', 'K-01-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (9, '9787040123457', '高等数学', '', '', '同济大学', '', '', '高等教育出版社', '2019', '', 'O', '数学', 'zh-CN', 480, 42.00, '', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '高等数学教材', '', '可借', 40, 32, '主馆2楼O区', 'O1-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (10, '9787111123456', 'Java核心技术', NULL, NULL, '霍斯特曼', NULL, NULL, '机械工业出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 920, 128.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', 'Java编程经典', NULL, '可借', 25, 20, '主馆2楼T区', 'T-01-01', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:20');
INSERT INTO `book` VALUES (11, '9787111123457', 'Python编程从入门到实践', NULL, NULL, '马瑟斯', NULL, NULL, '人民邮电出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 480, 89.00, NULL, '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', 'Python编程畅销书', NULL, '可借', 30, 24, '主馆2楼T区', 'T-01-02', 0, '2026-04-10 13:09:22', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (12, '9787010001234', '共产党宣言', NULL, NULL, '马克思 恩格斯', NULL, NULL, '人民出版社', '2018', NULL, 'A', '马克思主义', 'zh-CN', 120, 25.00, NULL, NULL, '马克思主义经典著作', NULL, '可借', 10, 8, '主馆2楼A区', 'A-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (13, '9787100012345', '西方哲学史', NULL, NULL, '罗素', NULL, NULL, '商务印书馆', '2016', NULL, 'B', '哲学', 'zh-CN', 850, 98.00, NULL, NULL, '西方哲学经典著作', NULL, '可借', 6, 4, '主馆2楼B区', 'B-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (14, '9787300123456', '经济学原理', NULL, NULL, '曼昆', NULL, NULL, '北京大学出版社', '2019', NULL, 'F', '经济', 'zh-CN', 680, 78.00, NULL, NULL, '经济学入门教材', NULL, '可借', 15, 12, '主馆2楼F区', 'F-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (15, '9787040123456', '教育学', NULL, NULL, '王道俊', NULL, NULL, '人民教育出版社', '2019', NULL, 'G', '教育', 'zh-CN', 480, 45.00, NULL, NULL, '教育学基础教材', NULL, '可借', 20, 15, '主馆2楼G区', 'G-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (16, '9787560012345', '新概念英语1', NULL, NULL, '亚历山大', NULL, NULL, '外语教学与研究出版社', '2017', NULL, 'H', '语言', 'zh-CN', 280, 38.00, NULL, NULL, '经典英语教材', NULL, '可借', 30, 25, '主馆2楼H区', 'H-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (17, '9787020012345', '红楼梦', NULL, NULL, '曹雪芹', NULL, NULL, '人民文学出版社', '2018', NULL, 'I', '文学', 'zh-CN', 1600, 88.00, NULL, NULL, '中国古典名著', NULL, '可借', 20, 15, '主馆2楼I区', 'I-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (18, '9787020012346', '三国演义', NULL, NULL, '罗贯中', NULL, NULL, '人民文学出版社', '2017', NULL, 'I', '文学', 'zh-CN', 980, 68.00, NULL, NULL, '中国古典名著', NULL, '可借', 18, 14, '主馆2楼I区', 'I-01-02', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (19, '9787101012345', '史记', NULL, NULL, '司马迁', NULL, NULL, '中华书局', '2016', NULL, 'K', '历史', 'zh-CN', 3200, 298.00, NULL, NULL, '中国史学经典', NULL, '可借', 8, 6, '主馆2楼K区', 'K-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (20, '9787040123457', '高等数学', NULL, NULL, '同济大学', NULL, NULL, '高等教育出版社', '2019', NULL, 'O', '数学', 'zh-CN', 480, 42.00, NULL, NULL, '高等数学教材', NULL, '可借', 40, 32, '主馆2楼O区', 'O-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (21, '9787111123456', 'Java核心技术', NULL, NULL, '霍斯特曼', NULL, NULL, '机械工业出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 920, 128.00, NULL, NULL, 'Java编程经典', NULL, '可借', 25, 20, '主馆2楼T区', 'T-01-01', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (22, '9787111123457', 'Python编程从入门到实践', NULL, NULL, '马瑟斯', NULL, NULL, '人民邮电出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 480, 89.00, NULL, NULL, 'Python编程畅销书', NULL, '可借', 30, 24, '主馆2楼T区', 'T-01-02', 1, '2026-04-10 13:10:22', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (23, '9787010001234', '共产党宣言', NULL, NULL, '马克思 恩格斯', NULL, NULL, '人民出版社', '2018', NULL, 'A', '马克思主义', 'zh-CN', 120, 25.00, NULL, NULL, '马克思主义经典著作', NULL, '可借', 10, 8, '主馆2楼A区', 'A-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (24, '9787100012345', '西方哲学史', NULL, NULL, '罗素', NULL, NULL, '商务印书馆', '2016', NULL, 'B', '哲学', 'zh-CN', 850, 98.00, NULL, NULL, '西方哲学经典著作', NULL, '可借', 6, 4, '主馆2楼B区', 'B-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (25, '9787300123456', '经济学原理', NULL, NULL, '曼昆', NULL, NULL, '北京大学出版社', '2019', NULL, 'F', '经济', 'zh-CN', 680, 78.00, NULL, NULL, '经济学入门教材', NULL, '可借', 15, 12, '主馆2楼F区', 'F-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (26, '9787040123456', '教育学', NULL, NULL, '王道俊', NULL, NULL, '人民教育出版社', '2019', NULL, 'G', '教育', 'zh-CN', 480, 45.00, NULL, NULL, '教育学基础教材', NULL, '可借', 20, 15, '主馆2楼G区', 'G-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (27, '9787560012345', '新概念英语1', NULL, NULL, '亚历山大', NULL, NULL, '外语教学与研究出版社', '2017', NULL, 'H', '语言', 'zh-CN', 280, 38.00, NULL, NULL, '经典英语教材', NULL, '可借', 30, 25, '主馆2楼H区', 'H-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (28, '9787020012345', '红楼梦', NULL, NULL, '曹雪芹', NULL, NULL, '人民文学出版社', '2018', NULL, 'I', '文学', 'zh-CN', 1600, 88.00, NULL, NULL, '中国古典名著', NULL, '可借', 20, 15, '主馆2楼I区', 'I-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (29, '9787020012346', '三国演义', NULL, NULL, '罗贯中', NULL, NULL, '人民文学出版社', '2017', NULL, 'I', '文学', 'zh-CN', 980, 68.00, NULL, NULL, '中国古典名著', NULL, '可借', 18, 14, '主馆2楼I区', 'I-01-02', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (30, '9787101012345', '史记', NULL, NULL, '司马迁', NULL, NULL, '中华书局', '2016', NULL, 'K', '历史', 'zh-CN', 3200, 298.00, NULL, NULL, '中国史学经典', NULL, '可借', 8, 6, '主馆2楼K区', 'K-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (31, '9787040123457', '高等数学', NULL, NULL, '同济大学', NULL, NULL, '高等教育出版社', '2019', NULL, 'O', '数学', 'zh-CN', 480, 42.00, NULL, NULL, '高等数学教材', NULL, '可借', 40, 32, '主馆2楼O区', 'O-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (32, '9787111123456', 'Java核心技术', NULL, NULL, '霍斯特曼', NULL, NULL, '机械工业出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 920, 128.00, NULL, NULL, 'Java编程经典', NULL, '可借', 25, 20, '主馆2楼T区', 'T-01-01', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (33, '9787111123457', 'Python编程从入门到实践', NULL, NULL, '马瑟斯', NULL, NULL, '人民邮电出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 480, 89.00, NULL, NULL, 'Python编程畅销书', NULL, '可借', 30, 24, '主馆2楼T区', 'T-01-02', 1, '2026-04-10 13:11:08', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (34, '9787010001234', '共产党宣言', NULL, NULL, '马克思 恩格斯', NULL, NULL, '人民出版社', '2018', NULL, 'A', '马克思主义', 'zh-CN', 120, 25.00, NULL, NULL, '马克思主义经典著作', NULL, '可借', 10, 8, '主馆2楼A区', 'A-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (35, '9787100012345', '西方哲学史', NULL, NULL, '罗素', NULL, NULL, '商务印书馆', '2016', NULL, 'B', '哲学', 'zh-CN', 850, 98.00, NULL, NULL, '西方哲学经典著作', NULL, '可借', 6, 4, '主馆2楼B区', 'B-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (36, '9787300123456', '经济学原理', NULL, NULL, '曼昆', NULL, NULL, '北京大学出版社', '2019', NULL, 'F', '经济', 'zh-CN', 680, 78.00, NULL, NULL, '经济学入门教材', NULL, '可借', 15, 12, '主馆2楼F区', 'F-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (37, '9787040123456', '教育学', NULL, NULL, '王道俊', NULL, NULL, '人民教育出版社', '2019', NULL, 'G', '教育', 'zh-CN', 480, 45.00, NULL, NULL, '教育学基础教材', NULL, '可借', 20, 15, '主馆2楼G区', 'G-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (38, '9787560012345', '新概念英语1', NULL, NULL, '亚历山大', NULL, NULL, '外语教学与研究出版社', '2017', NULL, 'H', '语言', 'zh-CN', 280, 38.00, NULL, NULL, '经典英语教材', NULL, '可借', 30, 25, '主馆2楼H区', 'H-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (39, '9787020012345', '红楼梦', NULL, NULL, '曹雪芹', NULL, NULL, '人民文学出版社', '2018', NULL, 'I', '文学', 'zh-CN', 1600, 88.00, NULL, NULL, '中国古典名著', NULL, '可借', 20, 15, '主馆2楼I区', 'I-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (40, '9787020012346', '三国演义', NULL, NULL, '罗贯中', NULL, NULL, '人民文学出版社', '2017', NULL, 'I', '文学', 'zh-CN', 980, 68.00, NULL, NULL, '中国古典名著', NULL, '可借', 18, 14, '主馆2楼I区', 'I-01-02', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (41, '9787101012345', '史记', NULL, NULL, '司马迁', NULL, NULL, '中华书局', '2016', NULL, 'K', '历史', 'zh-CN', 3200, 298.00, NULL, NULL, '中国史学经典', NULL, '可借', 8, 6, '主馆2楼K区', 'K-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (42, '9787040123457', '高等数学', NULL, NULL, '同济大学', NULL, NULL, '高等教育出版社', '2019', NULL, 'O', '数学', 'zh-CN', 480, 42.00, NULL, NULL, '高等数学教材', NULL, '可借', 40, 32, '主馆2楼O区', 'O-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (43, '9787111123456', 'Java核心技术', NULL, NULL, '霍斯特曼', NULL, NULL, '机械工业出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 920, 128.00, NULL, NULL, 'Java编程经典', NULL, '可借', 25, 20, '主馆2楼T区', 'T-01-01', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (44, '9787111123457', 'Python编程从入门到实践', NULL, NULL, '马瑟斯', NULL, NULL, '人民邮电出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 480, 89.00, NULL, NULL, 'Python编程畅销书', NULL, '可借', 30, 24, '主馆2楼T区', 'T-01-02', 1, '2026-04-10 15:12:35', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (45, '9787010001234', '共产党宣言', NULL, NULL, '马克思 恩格斯', NULL, NULL, '人民出版社', '2018', NULL, 'A', '马克思主义', 'zh-CN', 120, 25.00, NULL, NULL, '马克思主义经典著作', NULL, '可借', 10, 8, '主馆2楼A区', 'A-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (46, '9787100012345', '西方哲学史', NULL, NULL, '罗素', NULL, NULL, '商务印书馆', '2016', NULL, 'B', '哲学', 'zh-CN', 850, 98.00, NULL, NULL, '西方哲学经典著作', NULL, '可借', 6, 4, '主馆2楼B区', 'B-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (47, '9787300123456', '经济学原理', NULL, NULL, '曼昆', NULL, NULL, '北京大学出版社', '2019', NULL, 'F', '经济', 'zh-CN', 680, 78.00, NULL, NULL, '经济学入门教材', NULL, '可借', 15, 12, '主馆2楼F区', 'F-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (48, '9787040123456', '教育学', NULL, NULL, '王道俊', NULL, NULL, '人民教育出版社', '2019', NULL, 'G', '教育', 'zh-CN', 480, 45.00, NULL, NULL, '教育学基础教材', NULL, '可借', 20, 15, '主馆2楼G区', 'G-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (49, '9787560012345', '新概念英语1', NULL, NULL, '亚历山大', NULL, NULL, '外语教学与研究出版社', '2017', NULL, 'H', '语言', 'zh-CN', 280, 38.00, NULL, NULL, '经典英语教材', NULL, '可借', 30, 25, '主馆2楼H区', 'H-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (50, '9787020012345', '红楼梦', NULL, NULL, '曹雪芹', NULL, NULL, '人民文学出版社', '2018', NULL, 'I', '文学', 'zh-CN', 1600, 88.00, NULL, NULL, '中国古典名著', NULL, '可借', 20, 15, '主馆2楼I区', 'I-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (51, '9787020012346', '三国演义', NULL, NULL, '罗贯中', NULL, NULL, '人民文学出版社', '2017', NULL, 'I', '文学', 'zh-CN', 980, 68.00, NULL, NULL, '中国古典名著', NULL, '可借', 18, 14, '主馆2楼I区', 'I-01-02', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (52, '9787101012345', '史记', NULL, NULL, '司马迁', NULL, NULL, '中华书局', '2016', NULL, 'K', '历史', 'zh-CN', 3200, 298.00, NULL, NULL, '中国史学经典', NULL, '可借', 8, 6, '主馆2楼K区', 'K-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (53, '9787040123457', '高等数学', NULL, NULL, '同济大学', NULL, NULL, '高等教育出版社', '2019', NULL, 'O', '数学', 'zh-CN', 480, 42.00, NULL, NULL, '高等数学教材', NULL, '可借', 40, 32, '主馆2楼O区', 'O-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (54, '9787111123456', 'Java核心技术', NULL, NULL, '霍斯特曼', NULL, NULL, '机械工业出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 920, 128.00, NULL, NULL, 'Java编程经典', NULL, '可借', 25, 20, '主馆2楼T区', 'T-01-01', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (55, '9787111123457', 'Python编程从入门到实践', NULL, NULL, '马瑟斯', NULL, NULL, '人民邮电出版社', '2020', NULL, 'T', '计算机', 'zh-CN', 480, 89.00, NULL, NULL, 'Python编程畅销书', NULL, '可借', 30, 24, '主馆2楼T区', 'T-01-02', 1, '2026-04-10 15:12:40', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (56, '9787115428028', 'JavaScript高级程序设计', '第4版', NULL, 'Nicholas C. Zakas', NULL, '李松峰', '人民邮电出版社', '2020', '4', '计算机科学', NULL, 'zh-CN', 864, 129.00, 'JavaScript,前端', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是JavaScript超级畅销书的全新升级版。 ECMAScript和DOM标准的不断发展使JavaScript的世界不断变化...', NULL, '1', 1, 1, '2A', 'A1-05', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (57, '9787115430786', 'Python编程：从入门到实践', '', NULL, 'Eric Matthes', NULL, '袁国忠', '人民邮电出版社', '2019', '2', '计算机科学', NULL, 'zh-CN', 476, 89.00, 'Python,编程入门', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是一本针对所有层次的Python读者而作的Python入门书。', NULL, '1', 1, 1, '2A', 'B2-03', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (58, '9787121339169', '深入理解计算机系统', '第3版', NULL, 'Randal E. Bryant', NULL, '龚奕', '电子工业出版社', '2018', '3', '计算机科学', NULL, 'zh-CN', 737, 139.00, '计算机系统,底层', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是一本将软件和硬件理论完美融合的教材。', NULL, '1', 1, 1, '3A', 'C1-08', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (59, '9787111407016', '算法导论', '第3版', NULL, 'Thomas H. Cormen', NULL, '殷建平', '机械工业出版社', '2013', '3', '计算机科学', NULL, 'zh-CN', 780, 128.00, '算法,数据结构', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是算法领域的经典教材，全面介绍了算法领域。', NULL, '1', 1, 1, '3A', 'C2-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (60, '9787111075752', '设计模式：可复用面向对象软件的基础', '', NULL, 'Erich Gamma', NULL, '李英军', '机械工业出版社', '2000', '1', '计算机科学', NULL, 'zh-CN', 360, 59.00, '设计模式,软件工程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是软件设计领域的经典著作，介绍了23种设计模式。', NULL, '1', 1, 1, '3B', 'C3-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (61, '9787115217509', '代码整洁之道', '', NULL, 'Robert C. Martin', NULL, '韩磊', '人民邮电出版社', '2010', '1', '计算机科学', NULL, 'zh-CN', 336, 59.00, '编程实践,代码质量', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书教导程序员如何编写清晰、可维护的代码。', NULL, '1', 1, 1, '3B', 'C3-05', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (62, '9787111122104', '重构：改善既有代码的设计', '', NULL, 'Martin Fowler', NULL, '熊节', '机械工业出版社', '2003', '1', '计算机科学', NULL, 'zh-CN', 400, 69.00, '重构,代码优化', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书系统地介绍了重构的方法和技巧。', NULL, '1', 1, 1, '3B', 'C3-08', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (63, '9787111421900', '深入理解Java虚拟机', '第2版', NULL, '周志明', NULL, '', '机械工业出版社', '2013', '2', '计算机科学', NULL, 'zh-CN', 450, 79.00, 'Java,虚拟机', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书深入剖析了Java虚拟机的内部机制。', NULL, '1', 1, 1, '3C', 'D1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (64, '9787111612728', 'Effective Java', '第3版', NULL, 'Joshua Bloch', NULL, '陈昊鹏', '机械工业出版社', '2018', '3', '计算机科学', NULL, 'zh-CN', 368, 99.00, 'Java,最佳实践', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Java程序员必读的经典之作。', NULL, '1', 1, 1, '3C', 'D1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (65, '9787121198854', '高性能MySQL', '第3版', NULL, 'Baron Schwartz', NULL, '宁海元', '电子工业出版社', '2013', '3', '计算机科学', NULL, 'zh-CN', 600, 99.00, 'MySQL,数据库优化', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是MySQL性能优化的权威指南。', NULL, '1', 1, 1, '3D', 'E1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (66, '9787111403333', '数据库系统概念', '第6版', NULL, 'Abraham Silberschatz', NULL, '杨冬青', '机械工业出版社', '2012', '6', '计算机科学', NULL, 'zh-CN', 780, 118.00, '数据库,理论', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是数据库领域的经典教材。', NULL, '1', 1, 1, '3D', 'E1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (67, '9787302311805', '计算机网络', '第5版', NULL, 'Andrew S. Tanenbaum', NULL, '严伟', '清华大学出版社', '2012', '5', '计算机科学', NULL, 'zh-CN', 800, 99.00, '计算机网络,协议', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书全面介绍了计算机网络的原理和协议。', NULL, '1', 1, 1, '3E', 'F1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (68, '9787111403326', '操作系统概念', '第9版', NULL, 'Abraham Silberschatz', NULL, '郑扣根', '机械工业出版社', '2013', '9', '计算机科学', NULL, 'zh-CN', 800, 118.00, '操作系统,原理', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是操作系统领域的经典教材。', NULL, '1', 1, 1, '3F', 'G1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (69, '9787111403340', '编译原理', '第2版', NULL, 'Alfred V. Aho', NULL, '赵望达', '机械工业出版社', '2013', '2', '计算机科学', NULL, 'zh-CN', 600, 99.00, '编译原理,编译器', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是编译原理领域的经典教材。', NULL, '1', 1, 1, '3G', 'H1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (70, '9787111403357', '数据结构与算法分析：C语言描述', '第2版', NULL, 'Mark Allen Weiss', NULL, '冯舜玺', '机械工业出版社', '2013', '2', '计算机科学', NULL, 'zh-CN', 400, 59.00, '数据结构,算法,C语言', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是数据结构和算法分析的经典教材。', NULL, '1', 1, 1, '3A', 'C2-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (71, '9787121155352', 'C++ Primer', '第5版', NULL, 'Stanley B. Lippman', NULL, '李普曼', '电子工业出版社', '2013', '5', '计算机科学', NULL, 'zh-CN', 800, 128.00, 'C++,编程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是C++编程的经典入门教材。', NULL, '1', 1, 1, '3H', 'I1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (72, '9787111128069', 'C程序设计语言', '第2版', NULL, 'Brian W. Kernighan', NULL, '徐宝文', '机械工业出版社', '2004', '2', '计算机科学', NULL, 'zh-CN', 256, 49.00, 'C语言,编程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是C语言之父编写的经典教材。', NULL, '1', 1, 1, '3H', 'I1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (73, '9787115123456', 'UNIX环境高级编程', '第3版', NULL, 'W. Richard Stevens', NULL, '戚正伟', '人民邮电出版社', '2014', '3', '计算机科学', NULL, 'zh-CN', 700, 128.00, 'UNIX,系统编程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是UNIX系统编程的权威指南。', NULL, '1', 1, 1, '3I', 'J1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (74, '9787121123456', 'Linux设备驱动程序', '第3版', NULL, 'Jonathan Corbet', NULL, '魏永明', '电子工业出版社', '2011', '3', '计算机科学', NULL, 'zh-CN', 500, 89.00, 'Linux,驱动程序', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Linux驱动开发的经典教材。', NULL, '1', 1, 1, '3I', 'J1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (75, '9787111123457', 'TCP/IP详解 卷1：协议', '', NULL, 'W. Richard Stevens', NULL, '范建华', '机械工业出版社', '2000', '1', '计算机科学', NULL, 'zh-CN', 600, 99.00, 'TCP/IP,网络协议', NULL, '本书是TCP/IP协议的经典著作。', NULL, '1', 1, 1, '3E', 'F1-02', 1, '2026-04-11 02:28:11', '2026-04-11 02:38:25');
INSERT INTO `book` VALUES (76, '9787121234567', 'Web前端黑客技术揭秘', '', NULL, '钟馥', NULL, '', '电子工业出版社', '2014', '1', '计算机科学', NULL, 'zh-CN', 400, 69.00, 'Web安全,黑客技术', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书介绍了Web前端的安全问题和攻击技术。', NULL, '1', 1, 1, '3J', 'K1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (77, '9787115247803', '黑客与画家', '', NULL, 'Paul Graham', NULL, '阮一峰', '人民邮电出版社', '2011', '1', '计算机科学', NULL, 'zh-CN', 300, 49.00, '黑客文化,创业', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是硅谷创业教父的经典文集。', NULL, '1', 1, 1, '3J', 'K1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (78, '9787115123458', '编程珠玑', '第2版', NULL, 'Jon Bentley', NULL, '钱丽艳', '人民邮电出版社', '2008', '2', '计算机科学', NULL, 'zh-CN', 240, 39.00, '编程技巧,算法', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是程序员必读的经典之作。', NULL, '1', 1, 1, '3B', 'C3-10', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (79, '9787121023456', '代码大全', '第2版', NULL, 'Steve McConnell', NULL, '金戈', '电子工业出版社', '2006', '2', '计算机科学', NULL, 'zh-CN', 800, 98.00, '软件工程,编程实践', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是软件构建的权威指南。', NULL, '1', 1, 1, '3B', 'C3-11', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (80, '9787302123456', '人月神话', '', NULL, 'Frederick P. Brooks Jr.', NULL, '汪颖', '清华大学出版社', '2007', '1', '计算机科学', NULL, 'zh-CN', 320, 59.00, '项目管理,软件工程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是软件项目管理领域的经典著作。', NULL, '1', 1, 1, '3K', 'L1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (81, '9787302123457', '敏捷软件开发：原则、模式与实践', '', NULL, 'Robert C. Martin', NULL, '邓辉', '清华大学出版社', '2003', '1', '计算机科学', NULL, 'zh-CN', 400, 69.00, '敏捷开发,软件工程', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书介绍了敏捷软件开发的原则和实践。', NULL, '1', 1, 1, '3K', 'L1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (82, '9787121234568', '领域驱动设计', '', NULL, 'Eric Evans', NULL, '赵俐', '电子工业出版社', '2010', '1', '计算机科学', NULL, 'zh-CN', 500, 89.00, '领域驱动设计,软件架构', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是领域驱动设计的经典著作。', NULL, '1', 1, 1, '3L', 'M1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (83, '9787121345678', '微服务架构设计模式', '', NULL, 'Chris Richardson', NULL, '王磊', '电子工业出版社', '2019', '1', '计算机科学', NULL, 'zh-CN', 450, 99.00, '微服务,架构设计', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书介绍了微服务架构的设计模式和最佳实践。', NULL, '1', 1, 1, '3L', 'M1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (84, '9787111478901', 'Docker技术入门与实战', '第3版', NULL, '杨保华', NULL, '', '机械工业出版社', '2018', '3', '计算机科学', NULL, 'zh-CN', 400, 69.00, 'Docker,容器技术', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Docker技术的入门指南。', NULL, '1', 1, 1, '3M', 'N1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (85, '9787121345679', 'Kubernetes权威指南', '第2版', NULL, '龚正', NULL, '', '电子工业出版社', '2019', '2', '计算机科学', NULL, 'zh-CN', 500, 99.00, 'Kubernetes,容器编排', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Kubernetes的权威指南。', NULL, '1', 1, 1, '3M', 'N1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (86, '9787111467890', 'Redis设计与实现', '第2版', NULL, '黄健宏', NULL, '', '机械工业出版社', '2014', '2', '计算机科学', NULL, 'zh-CN', 400, 79.00, 'Redis,数据库', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书深入剖析了Redis的内部实现。', NULL, '1', 1, 1, '3D', 'E1-03', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (87, '9787121234570', 'MongoDB权威指南', '', NULL, 'Kristina Chodorow', NULL, '李瑞丰', '电子工业出版社', '2014', '1', '计算机科学', NULL, 'zh-CN', 400, 79.00, 'MongoDB,NoSQL', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是MongoDB的权威指南。', NULL, '1', 1, 1, '3D', 'E1-04', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (88, '9787121234571', 'Elasticsearch权威指南', '', NULL, 'Clinton Gormley', NULL, '李瑞丰', '电子工业出版社', '2015', '1', '计算机科学', NULL, 'zh-CN', 500, 89.00, 'Elasticsearch,搜索引擎', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Elasticsearch的权威指南。', NULL, '1', 1, 1, '3N', 'O1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (89, '9787121234572', 'Spark快速大数据分析', '', NULL, 'Holden Karau', NULL, '李瑞丰', '电子工业出版社', '2015', '1', '计算机科学', NULL, 'zh-CN', 300, 69.00, 'Spark,大数据', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书介绍了Spark大数据处理技术。', NULL, '1', 1, 1, '3O', 'P1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (90, '9787121234573', 'Hadoop权威指南', '第3版', NULL, 'Tom White', NULL, '李瑞丰', '电子工业出版社', '2015', '3', '计算机科学', NULL, 'zh-CN', 600, 99.00, 'Hadoop,大数据', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是Hadoop的权威指南。', NULL, '1', 1, 1, '3O', 'P1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (91, '9787121345680', '数据科学导论', '', NULL, 'Djallel Bouneffouf', NULL, '王磊', '电子工业出版社', '2019', '1', '计算机科学', NULL, 'zh-CN', 400, 79.00, '数据科学,机器学习', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书介绍了数据科学的基础知识。', NULL, '1', 1, 1, '3P', 'Q1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (92, '9787302423456', '机器学习', '', NULL, '周志华', NULL, '', '清华大学出版社', '2016', '1', '计算机科学', NULL, 'zh-CN', 500, 89.00, '机器学习,人工智能', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是机器学习领域的经典教材。', NULL, '1', 1, 1, '3P', 'Q1-02', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (93, '9787115423456', '深度学习', '', NULL, 'Ian Goodfellow', NULL, '赵申剑', '人民邮电出版社', '2017', '1', '计算机科学', NULL, 'zh-CN', 600, 129.00, '深度学习,人工智能', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是深度学习领域的奠基性著作。', NULL, '1', 1, 1, '3P', 'Q1-03', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (94, '9787302275826', '统计学习方法', '', NULL, '李航', NULL, '', '清华大学出版社', '2012', '1', '计算机科学', NULL, 'zh-CN', 300, 69.00, '机器学习,统计模型', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书系统阐述了统计学习的主要方法。', NULL, '1', 1, 1, '3P', 'Q1-04', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (95, '9787115210000', '自然语言处理综论', '第2版', NULL, 'Daniel Jurafsky', NULL, '冯志伟', '人民邮电出版社', '2005', '2', '计算机科学', NULL, 'zh-CN', 700, 118.00, '自然语言处理,语言学', '/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg', '本书是自然语言处理领域的权威教材。', NULL, '1', 1, 1, '3Q', 'R1-01', 0, '2026-04-11 02:28:11', '2026-04-11 16:39:21');
INSERT INTO `book` VALUES (96, '9787115210011', '计算机图形学', '第3版', NULL, 'Donald Hearn', NULL, '蔡士杰', '人民邮电出版社', '2010', '3', '计算机科学', NULL, 'zh-CN', 600, 99.00, '计算机图形学,渲染', NULL, '本书全面介绍了计算机图形学的基本原理。', NULL, '1', 1, 1, '3R', 'S1-01', 0, '2026-04-11 02:28:11', '2026-04-11 02:28:11');
INSERT INTO `book` VALUES (97, '9787115210022', '人机交互：以用户为中心的设计', '第4版', NULL, 'Ben Shneiderman', NULL, '刘宏', '人民邮电出版社', '2012', '4', '计算机科学', NULL, 'zh-CN', 500, 89.00, '人机交互,用户体验', NULL, '本书是人机交互领域的经典教材。', NULL, '1', 1, 1, '3S', 'T1-01', 0, '2026-04-11 02:28:11', '2026-04-11 02:28:11');
INSERT INTO `book` VALUES (98, '9787302123458', '信息论、推理与学习算法', '', NULL, 'David J.C. MacKay', NULL, '马春鹏', '清华大学出版社', '2004', '1', '计算机科学', NULL, 'zh-CN', 600, 79.00, '信息论,机器学习', NULL, '本书将信息论与机器学习紧密结合。', NULL, '1', 1, 1, '3T', 'U1-01', 0, '2026-04-11 02:28:11', '2026-04-11 02:28:11');
INSERT INTO `book` VALUES (99, '9787115210033', '编译器设计', '第2版', NULL, 'Keith D. Cooper', NULL, '刘旭东', '人民邮电出版社', '2011', '2', '计算机科学', NULL, 'zh-CN', 500, 98.00, '编译器,程序分析', NULL, '本书是现代编译器设计的优秀教材。', NULL, '1', 1, 1, '3G', 'H1-02', 0, '2026-04-11 02:28:11', '2026-04-11 02:28:11');

-- ----------------------------
-- Table structure for book_import_batch
-- ----------------------------
DROP TABLE IF EXISTS `book_import_batch`;
CREATE TABLE `book_import_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `operator_id` bigint NOT NULL,
  `total_count` int NOT NULL DEFAULT 0,
  `success_count` int NOT NULL DEFAULT 0,
  `fail_count` int NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  CONSTRAINT `fk_batch_user` FOREIGN KEY (`operator_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_import_batch
-- ----------------------------
INSERT INTO `book_import_batch` VALUES (1, 'BATCH-1775845691265', 1, 44, 44, 0, 1, 0, '2026-04-11 02:28:11');

-- ----------------------------
-- Table structure for book_import_detail
-- ----------------------------
DROP TABLE IF EXISTS `book_import_detail`;
CREATE TABLE `book_import_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `batch_id` bigint NOT NULL COMMENT '鎵规?ID',
  `row_number` int NOT NULL COMMENT 'Excel琛屽彿',
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ISBN',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '涔﹀悕',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浣滆?',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐘舵?锛歋UCCESS-鎴愬姛锛孎AILED-澶辫触锛孲KIPPED-璺宠繃',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '閿欒?淇℃伅',
  `book_id` bigint NULL DEFAULT NULL COMMENT '鍏宠仈鍥句功ID锛堟垚鍔熸椂锛',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_batch_status`(`batch_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_isbn`(`isbn` ASC) USING BTREE,
  CONSTRAINT `fk_import_detail_batch` FOREIGN KEY (`batch_id`) REFERENCES `book_import_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_import_detail_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '瀵煎叆鏄庣粏琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_import_detail
-- ----------------------------

-- ----------------------------
-- Table structure for book_recommendation
-- ----------------------------
DROP TABLE IF EXISTS `book_recommendation`;
CREATE TABLE `book_recommendation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `book_id` bigint NOT NULL COMMENT '鍥句功ID',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '鎺ㄨ崘鍒嗘暟',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鎺ㄨ崘鐞嗙敱',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `fk_recommendation_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendation_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍥句功鎺ㄨ崘琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_recommendation
-- ----------------------------

-- ----------------------------
-- Table structure for book_review
-- ----------------------------
DROP TABLE IF EXISTS `book_review`;
CREATE TABLE `book_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `book_id` bigint NOT NULL COMMENT '鍥句功ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `rating` tinyint NOT NULL COMMENT '璇勫垎1-5鏄',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '璇勪环鍐呭?',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '鐘舵?锛?-闅愯棌锛?-鏄剧ず',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_book_user`(`book_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  CONSTRAINT `fk_review_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating` CHECK ((`rating` >= 1) and (`rating` <= 5))
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鍥句功璇勪环琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_review
-- ----------------------------

-- ----------------------------
-- Table structure for borrow_record
-- ----------------------------
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `book_barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `borrow_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` datetime NOT NULL,
  `return_date` datetime NULL DEFAULT NULL,
  `renew_count` int NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 0,
  `operator_id` bigint NULL DEFAULT NULL,
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_borrow_date`(`borrow_date` ASC) USING BTREE,
  CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow_record
-- ----------------------------
INSERT INTO `borrow_record` VALUES (13, 14, 1, 'N/A', '2026-04-11 01:20:49', '2026-07-10 01:20:49', '2026-04-11 01:34:38', 2, 1, 14, NULL, 0, '2026-04-11 01:20:49', '2026-04-11 01:34:38');
INSERT INTO `borrow_record` VALUES (14, 104, 1, 'N/A', '2026-04-11 03:57:53', '2026-05-11 03:57:53', NULL, 0, 0, 104, NULL, 0, '2026-04-11 03:57:53', '2026-04-11 03:57:53');
INSERT INTO `borrow_record` VALUES (15, 105, 1, 'N/A', '2026-04-11 03:58:43', '2026-05-11 03:58:43', NULL, 0, 0, 105, NULL, 0, '2026-04-11 03:58:43', '2026-04-11 03:58:43');
INSERT INTO `borrow_record` VALUES (16, 106, 1, 'N/A', '2026-04-11 03:59:32', '2026-05-11 03:59:32', NULL, 0, 0, 106, NULL, 0, '2026-04-11 03:59:32', '2026-04-11 03:59:32');
INSERT INTO `borrow_record` VALUES (17, 106, 5, 'N/A', '2026-04-11 03:59:42', '2026-05-11 03:59:42', NULL, 0, 0, 106, NULL, 0, '2026-04-11 03:59:42', '2026-04-11 03:59:42');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT 0,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '马克思主义', 'A', NULL, 1, 'Category A', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (2, '哲学', 'B', NULL, 2, 'Category B', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (3, '社会科学', 'C', NULL, 3, 'Category C', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (4, '政治', 'D', NULL, 4, 'Category D', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (5, '军事', 'E', NULL, 5, 'Category E', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (6, '经济', 'F', NULL, 6, 'Category F', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (7, '文化', 'G', NULL, 7, 'Category G', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (8, '语言文字', 'H', NULL, 8, 'Category H', 0, '2026-04-10 01:06:19', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (9, '文学', 'I', NULL, 9, 'Category I', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (10, '艺术', 'J', NULL, 10, 'Category J', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (11, '历史', 'K', NULL, 11, 'Category K', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (12, '地理', 'N', NULL, 12, 'Category N', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (13, '自然科学', 'O', NULL, 13, 'Category O', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (14, '数理科学', 'P', NULL, 14, 'Category P', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (15, '化学', 'Q', NULL, 15, 'Category Q', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (16, '天文学', 'R', NULL, 16, 'Category R', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (17, '生物科学', 'S', NULL, 17, 'Category S', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (18, '医药卫生', 'T', NULL, 18, 'Category T', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (19, '农业科学', 'U', NULL, 19, 'Category U', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (20, '工程技术', 'V', NULL, 20, 'Category V', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (21, '环境科学', 'X', NULL, 21, 'Category X', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');
INSERT INTO `category` VALUES (22, '综合性图书', 'Z', NULL, 22, 'Category Z', 0, '2026-04-10 01:06:20', '2026-04-10 23:35:19');

-- ----------------------------
-- Table structure for export_task
-- ----------------------------
DROP TABLE IF EXISTS `export_task`;
CREATE TABLE `export_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??????',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint NULL DEFAULT NULL COMMENT 'file size in bytes',
  `format` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'XLSX' COMMENT 'format: XLSX/PDF/CSV',
  `query_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'query params JSON',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `complete_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_export_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of export_task
-- ----------------------------

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (1, 13, 0, 0, '2026-04-10 13:11:08');
INSERT INTO `favorite` VALUES (2, 14, 0, 0, '2026-04-10 13:11:08');
INSERT INTO `favorite` VALUES (3, 13, 0, 0, '2026-04-10 15:12:35');
INSERT INTO `favorite` VALUES (4, 14, 0, 0, '2026-04-10 15:12:35');
INSERT INTO `favorite` VALUES (5, 13, 0, 0, '2026-04-10 15:12:40');
INSERT INTO `favorite` VALUES (6, 14, 0, 0, '2026-04-10 15:12:40');

-- ----------------------------
-- Table structure for fine_record
-- ----------------------------
DROP TABLE IF EXISTS `fine_record`;
CREATE TABLE `fine_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `borrow_record_id` bigint NULL DEFAULT NULL,
  `fine_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `amount` decimal(10, 2) NOT NULL,
  `paid_status` tinyint NOT NULL DEFAULT 0,
  `paid_date` datetime NULL DEFAULT NULL,
  `operator_id` bigint NULL DEFAULT NULL,
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_borrow_record_id`(`borrow_record_id` ASC) USING BTREE,
  CONSTRAINT `fk_fine_borrow` FOREIGN KEY (`borrow_record_id`) REFERENCES `borrow_record` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_fine_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fine_record
-- ----------------------------

-- ----------------------------
-- Table structure for inquiry
-- ----------------------------
DROP TABLE IF EXISTS `inquiry`;
CREATE TABLE `inquiry`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??????',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '??????',
  `replier_id` bigint NULL DEFAULT NULL COMMENT '?????D',
  `reply_date` datetime NULL DEFAULT NULL COMMENT '??????',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_inquiry_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inquiry
-- ----------------------------
INSERT INTO `inquiry` VALUES (1, 13, '如何续借图书？', '请问图书到期后如何续借？续借次数有限制吗？', 'BORROW', 1, '您好！图书到期前可以在个人中心进行在线续借，每本书最多续借1次，续借期限为30天。', 12, '2026-04-11 13:11:08', 0, '2026-04-08 13:11:08', '2026-04-11 13:11:08');
INSERT INTO `inquiry` VALUES (2, 13, '如何续借图书？', '请问图书到期后如何续借？续借次数有限制吗？', 'BORROW', 1, '您好！图书到期前可以在个人中心进行在线续借，每本书最多续借1次，续借期限为30天。', 12, '2026-04-11 15:12:35', 0, '2026-04-08 15:12:35', '2026-04-11 15:12:35');
INSERT INTO `inquiry` VALUES (3, 13, '如何续借图书？', '请问图书到期后如何续借？续借次数有限制吗？', 'BORROW', 1, '您好！图书到期前可以在个人中心进行在线续借，每本书最多续借1次，续借期限为30天。', 12, '2026-04-11 15:12:40', 0, '2026-04-08 15:12:40', '2026-04-11 15:12:40');
INSERT INTO `inquiry` VALUES (4, 16, '阿松大阿三大苏打', '啊实打实阿三大苏打阿三大苏打', 'borrow', 0, NULL, NULL, NULL, 0, '2026-04-10 23:11:47', '2026-04-10 23:11:47');
INSERT INTO `inquiry` VALUES (5, 18, '关于图书的建议', '123456789a', 'account', 1, 'asda ', 1, '2026-04-11 00:09:28', 0, '2026-04-11 00:08:54', '2026-04-11 00:09:28');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT 0,
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'IN_APP' COMMENT 'channel: IN_APP/SMS/EMAIL',
  `related_id` bigint NULL DEFAULT NULL COMMENT 'related business id',
  `related_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'related business type',
  `read_time` datetime NULL DEFAULT NULL COMMENT 'read time',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 13, '借阅成功通知', '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。', 'BORROW', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-10 13:10:22');
INSERT INTO `notification` VALUES (2, 14, '到期提醒', '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。', 'DUE_REMINDER', 1, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-09 13:10:22');
INSERT INTO `notification` VALUES (3, 13, '借阅成功通知', '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。', 'BORROW', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-10 13:11:08');
INSERT INTO `notification` VALUES (4, 14, '到期提醒', '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。', 'DUE_REMINDER', 1, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-09 13:11:08');
INSERT INTO `notification` VALUES (5, 13, '借阅成功通知', '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。', 'BORROW', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-10 15:12:35');
INSERT INTO `notification` VALUES (6, 14, '到期提醒', '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。', 'DUE_REMINDER', 1, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-09 15:12:35');
INSERT INTO `notification` VALUES (7, 13, '借阅成功通知', '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。', 'BORROW', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-10 15:12:40');
INSERT INTO `notification` VALUES (8, 14, '到期提醒', '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。', 'DUE_REMINDER', 1, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-09 15:12:40');
INSERT INTO `notification` VALUES (9, 105, '座位预约成功', '您已成功预约座位 A-06，预约日期：2026-04-11，时间段：08:00-12:00', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 04:50:26');
INSERT INTO `notification` VALUES (10, 105, '座位预约取消', '您已取消座位 A-06 的预约，预约日期：2026-04-11', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 04:52:31');
INSERT INTO `notification` VALUES (11, 105, '座位预约成功', '您已成功预约座位 A-06，预约日期：2026-04-11，时间段：08:00-12:00', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 04:52:35');
INSERT INTO `notification` VALUES (12, 105, '座位预约超时取消通知', '您预约的座位A-06（预约时间：2026-04-11 00:00 08:00:00）因超时未签到已自动取消。座位已释放供其他用户预约。', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 13:45:00');
INSERT INTO `notification` VALUES (13, 104, '座位预约成功', '您已成功预约座位 A-25，预约日期：2026-04-11，时间段：08:00-12:00', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 15:23:16');
INSERT INTO `notification` VALUES (14, 104, '座位预约超时取消通知', '您预约的座位A-25（预约时间：2026-04-11 00:00 08:00:00）因超时未签到已自动取消。座位已释放供其他用户预约。', 'SYSTEM', 0, 'IN_APP', NULL, NULL, NULL, 0, '2026-04-11 15:25:00');

-- ----------------------------
-- Table structure for notification_template
-- ----------------------------
DROP TABLE IF EXISTS `notification_template`;
CREATE TABLE `notification_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'IN_APP' COMMENT 'channel',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `variables` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'template variables description',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT 'status: 0-disabled, 1-enabled',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_channel`(`channel` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_template
-- ----------------------------
INSERT INTO `notification_template` VALUES (1, 'BORROW_SUCCESS', 'IN_APP', 'Borrow Success', 'You borrowed book: {bookTitle}', NULL, 1, 'BORROW', 0, '2026-04-10 01:06:19', '2026-04-10 01:06:19');
INSERT INTO `notification_template` VALUES (2, 'RETURN_SUCCESS', 'IN_APP', 'Return Success', 'You returned book: {bookTitle}', NULL, 1, 'RETURN', 0, '2026-04-10 01:06:19', '2026-04-10 01:06:19');
INSERT INTO `notification_template` VALUES (3, 'DUE_REMINDER', 'IN_APP', 'Due Reminder', 'Book {bookTitle} will be due on {dueDate}', NULL, 1, 'DUE_REMINDER', 0, '2026-04-10 01:06:19', '2026-04-10 01:06:19');
INSERT INTO `notification_template` VALUES (4, 'OVERDUE_NOTICE', 'IN_APP', 'Overdue Notice', 'Book {bookTitle} is overdue {overdueDays} days', NULL, 1, 'OVERDUE', 0, '2026-04-10 01:06:19', '2026-04-10 01:06:19');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回结果',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `execute_time` bigint NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_operation`(`operation` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'admin', '登录', '认证管理', 'com.example.demo.controller.AuthController.loginUser', '{\"loginRequest\":{\"id\":null,\"username\":\"admin\",\"password\":\"cifs123456\",\"userType\":null,\"realName\":null,\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":null,\"language\":null,\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":null,\"deleted\":null,\"violationCount\":null,\"lastViolationTime\":null,\"createTime\":null,\"updateTime\":null}}', '{\"headers\":{},\"body\":{\"code\":200,\"identityType\":null,\"identityId\":null,\"message\":\"Login successful\",\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NTg4OTczMywiZXhwIjoxNzc1OTc2MTMzfQ.IDvpdOPuxeidvL-LUETR7-8-6VfGlSs9AuLGbXFtUUivWLEx1k-2n49RF1-DecATvK1xe1A1IGoQ_IR6_t0_Ww\",\"tokenType\":\"Bearer\",\"user\":{\"id\":1,\"username\":\"admin\",\"password\":\"$2a$10$VHs8flVJseMc0Aj9YZkwNuVSRuQ2GpZsJinTtGKS.C7RpOD1RSnVC\",\"userType\":\"ADMIN\",\"realName\":\"admin\",\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":\"ADMIN\",\"language\":\"zh-CN\",\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":1,\"deleted\":0,\"violationCount\":0,\"lastViolationTime\":null,\"createTime\":\"2026-04-09T17:06:19.000+00:00\",\"updateTime\":\"2026-04-11T06:41:54.000+00:00\"},\"refreshToken\":\"542cac18-4632-43c9-b174-7ec22db82034\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, NULL, 245, '2026-04-11 14:42:14');
INSERT INTO `operation_log` VALUES (2, NULL, NULL, '登录', '认证管理', 'com.example.demo.controller.AuthController.loginUser', '{\"loginRequest\":{\"id\":null,\"username\":\"USR20240008\",\"password\":\"cifs12346\",\"userType\":null,\"realName\":null,\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":null,\"language\":null,\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":null,\"deleted\":null,\"violationCount\":null,\"lastViolationTime\":null,\"createTime\":null,\"updateTime\":null}}', '{\"headers\":{},\"body\":{\"code\":401,\"message\":\"Login failed: 用户名或密码错误\"},\"statusCode\":\"UNAUTHORIZED\",\"statusCodeValue\":401}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 262, '2026-04-11 15:22:56');
INSERT INTO `operation_log` VALUES (3, 104, 'USR20240008', '登录', '认证管理', 'com.example.demo.controller.AuthController.loginUser', '{\"loginRequest\":{\"id\":null,\"username\":\"USR20240008\",\"password\":\"cifs123456\",\"userType\":null,\"realName\":null,\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":null,\"language\":null,\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":null,\"deleted\":null,\"violationCount\":null,\"lastViolationTime\":null,\"createTime\":null,\"updateTime\":null}}', '{\"headers\":{},\"body\":{\"code\":200,\"identityType\":\"reader\",\"identityId\":\"USR20240008\",\"message\":\"Login successful\",\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJVU1IyMDI0MDAwOCIsImlhdCI6MTc3NTg5MjE4MCwiZXhwIjoxNzc1OTc4NTgwfQ.xQt5wRBd71fioCTqNzxIkO-5WB1yRnihUPLg-4vMmvt1Pza__e-mRE3Q_78WAtmY-rrstytry9hyg3tc94Dong\",\"tokenType\":\"Bearer\",\"user\":{\"id\":104,\"username\":\"USR20240008\",\"password\":\"$2a$10$MaIXfb2MP7GbEeiM/I29Q.gxBrqU.A/xNhlxM2YMFmITUPmLGVVlu\",\"userType\":\"READER\",\"realName\":\"周进修\",\"phone\":\"13700137007\",\"email\":\"zhoustudy@example.com\",\"idCard\":null,\"institution\":null,\"role\":\"ROLE_READER\",\"language\":\"zh-CN\",\"studentId\":null,\"facultyId\":null,\"userId\":\"USR20240008\",\"campus\":\"主校区\",\"college\":\"培训中心\",\"grade\":\"\",\"className\":\"\",\"counselor\":\"\",\"status\":1,\"deleted\":0,\"violationCount\":0,\"lastViolationTime\":null,\"createTime\":\"2026-04-10T19:44:54.000+00:00\",\"updateTime\":\"2026-04-11T07:05:11.000+00:00\"},\"refreshToken\":\"252bfa64-232f-48f4-9bb3-f56d3ef77248\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 166, '2026-04-11 15:23:01');
INSERT INTO `operation_log` VALUES (4, 104, 'USR20240008', '预约座位', '座位管理', 'com.example.demo.controller.SeatController.reserveSeat', '{\"seatId\":25,\"startTime\":\"08:00\",\"endTime\":\"12:00\",\"reserveDate\":\"2026-04-10T16:00:00.000+00:00\"}', '{\"data\":{\"checkInTime\":null,\"seatNumber\":\"A-25\",\"roomId\":1,\"roomName\":\"综合阅览室\",\"checkOutTime\":null,\"createTime\":\"2026-04-11T07:23:16.103+00:00\",\"seatId\":25,\"startTime\":\"08:00\",\"roomLocation\":\"主馆2楼\",\"id\":3,\"endTime\":\"12:00\",\"reserveDate\":\"2026-04-11\",\"status\":0},\"success\":true,\"message\":\"预约成功\"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 20, '2026-04-11 15:23:16');
INSERT INTO `operation_log` VALUES (5, NULL, NULL, '登录', '认证管理', 'com.example.demo.controller.AuthController.loginUser', '{\"loginRequest\":{\"id\":null,\"username\":\"admin\",\"password\":\"cifs12346\",\"userType\":null,\"realName\":null,\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":null,\"language\":null,\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":null,\"deleted\":null,\"violationCount\":null,\"lastViolationTime\":null,\"createTime\":null,\"updateTime\":null}}', '{\"headers\":{},\"body\":{\"code\":401,\"message\":\"Login failed: 用户名或密码错误\"},\"statusCode\":\"UNAUTHORIZED\",\"statusCodeValue\":401}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 306, '2026-04-11 15:32:15');
INSERT INTO `operation_log` VALUES (6, 1, 'admin', '登录', '认证管理', 'com.example.demo.controller.AuthController.loginUser', '{\"loginRequest\":{\"id\":null,\"username\":\"admin\",\"password\":\"cifs123456\",\"userType\":null,\"realName\":null,\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":null,\"language\":null,\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":null,\"deleted\":null,\"violationCount\":null,\"lastViolationTime\":null,\"createTime\":null,\"updateTime\":null}}', '{\"headers\":{},\"body\":{\"code\":200,\"identityType\":null,\"identityId\":null,\"message\":\"Login successful\",\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NTg5MjczOSwiZXhwIjoxNzc1OTc5MTM5fQ.k-5gkoBb6fgfXdJj23c4q6wyxrxxXpbYGy-a1pMShObZUIlw855hhPA9HxrtUd_pmVsiVqmi7AZf5Ym7QrDghQ\",\"tokenType\":\"Bearer\",\"user\":{\"id\":1,\"username\":\"admin\",\"password\":\"$2a$10$RoqqwDVkl2vdtmSt/z9eyOLIuWmGfOJphRHqdbAb0R.hTwGLYEysK\",\"userType\":\"ADMIN\",\"realName\":\"admin\",\"phone\":null,\"email\":null,\"idCard\":null,\"institution\":null,\"role\":\"ADMIN\",\"language\":\"zh-CN\",\"studentId\":null,\"facultyId\":null,\"userId\":null,\"campus\":null,\"college\":null,\"grade\":null,\"className\":null,\"counselor\":null,\"status\":1,\"deleted\":0,\"violationCount\":0,\"lastViolationTime\":null,\"createTime\":\"2026-04-09T17:06:19.000+00:00\",\"updateTime\":\"2026-04-11T07:31:24.000+00:00\"},\"refreshToken\":\"1be5b1fb-7346-4923-8dfc-b435f4d9a7a2\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 183, '2026-04-11 15:32:19');
INSERT INTO `operation_log` VALUES (7, 1, 'admin', '修改图书', '图书管理', 'com.example.demo.controller.BookController.updateBook', '{\"book\":{\"id\":1,\"isbn\":\"9787010001234\",\"title\":\"共产党宣言\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"马克思 恩格斯\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"人民出版社\",\"publishYear\":\"2018\",\"edition\":\"\",\"category\":\"A\",\"categoryName\":\"马克思主义\",\"language\":\"zh-CN\",\"pages\":120,\"price\":25,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/b04d1259-5126-479f-9be9-75e82a90a924.webp\",\"summary\":\"马克思主义经典著作\",\"summaryEn\":\"\",\"status\":\"0\",\"totalCopies\":10,\"availableCopies\":5,\"location\":\"主馆2楼A区\",\"shelfNo\":\"A1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:55:18.475+00:00\"},\"id\":1}', '{\"headers\":{},\"body\":{\"id\":1,\"isbn\":\"9787010001234\",\"title\":\"共产党宣言\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"马克思 恩格斯\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"人民出版社\",\"publishYear\":\"2018\",\"edition\":\"\",\"category\":\"A\",\"categoryName\":\"马克思主义\",\"language\":\"zh-CN\",\"pages\":120,\"price\":25,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/b04d1259-5126-479f-9be9-75e82a90a924.webp\",\"summary\":\"马克思主义经典著作\",\"summaryEn\":\"\",\"status\":\"0\",\"totalCopies\":10,\"availableCopies\":5,\"location\":\"主馆2楼A区\",\"shelfNo\":\"A1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:55:18.475+00:00\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 11, '2026-04-11 15:55:18');
INSERT INTO `operation_log` VALUES (8, 1, 'admin', '修改图书', '图书管理', 'com.example.demo.controller.BookController.updateBook', '{\"book\":{\"id\":2,\"isbn\":\"9787100012345\",\"title\":\"西方哲学史\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"罗素\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"商务印书馆\",\"publishYear\":\"2016\",\"edition\":\"\",\"category\":\"B\",\"categoryName\":\"哲学\",\"language\":\"zh-CN\",\"pages\":850,\"price\":98,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/12014a94-b6c6-4bd3-8d90-9b55ae27799d.webp\",\"summary\":\"西方哲学经典著作\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":6,\"availableCopies\":4,\"location\":\"主馆2楼B区\",\"shelfNo\":\"B-01-01\",\"deleted\":null,\"createTime\":null,\"updateTime\":null},\"id\":2}', '{\"headers\":{},\"body\":{\"code\":400,\"message\":\"书架号格式不正确，应为如 A1-01 格式\"},\"statusCode\":\"BAD_REQUEST\",\"statusCodeValue\":400}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 2, '2026-04-11 15:56:30');
INSERT INTO `operation_log` VALUES (9, 1, 'admin', '修改图书', '图书管理', 'com.example.demo.controller.BookController.updateBook', '{\"book\":{\"id\":2,\"isbn\":\"9787100012345\",\"title\":\"西方哲学史\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"罗素\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"商务印书馆\",\"publishYear\":\"2016\",\"edition\":\"\",\"category\":\"B\",\"categoryName\":\"哲学\",\"language\":\"zh-CN\",\"pages\":850,\"price\":98,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/12014a94-b6c6-4bd3-8d90-9b55ae27799d.webp\",\"summary\":\"西方哲学经典著作\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":6,\"availableCopies\":4,\"location\":\"主馆2楼B区\",\"shelfNo\":\"B1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:56:36.301+00:00\"},\"id\":2}', '{\"headers\":{},\"body\":{\"id\":2,\"isbn\":\"9787100012345\",\"title\":\"西方哲学史\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"罗素\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"商务印书馆\",\"publishYear\":\"2016\",\"edition\":\"\",\"category\":\"B\",\"categoryName\":\"哲学\",\"language\":\"zh-CN\",\"pages\":850,\"price\":98,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/12014a94-b6c6-4bd3-8d90-9b55ae27799d.webp\",\"summary\":\"西方哲学经典著作\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":6,\"availableCopies\":4,\"location\":\"主馆2楼B区\",\"shelfNo\":\"B1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:56:36.301+00:00\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 5, '2026-04-11 15:56:36');
INSERT INTO `operation_log` VALUES (10, 1, 'admin', '修改图书', '图书管理', 'com.example.demo.controller.BookController.updateBook', '{\"book\":{\"id\":3,\"isbn\":\"9787300123456\",\"title\":\"经济学原理\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"曼昆\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"北京大学出版社\",\"publishYear\":\"2019\",\"edition\":\"\",\"category\":\"F\",\"categoryName\":\"经济\",\"language\":\"zh-CN\",\"pages\":680,\"price\":78,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/4839488e-5adb-4e40-bbd9-e2253c33ef88.webp\",\"summary\":\"经济学入门教材\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":15,\"availableCopies\":12,\"location\":\"主馆2楼F区\",\"shelfNo\":\"F1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:57:47.577+00:00\"},\"id\":3}', '{\"headers\":{},\"body\":{\"id\":3,\"isbn\":\"9787300123456\",\"title\":\"经济学原理\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"曼昆\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"北京大学出版社\",\"publishYear\":\"2019\",\"edition\":\"\",\"category\":\"F\",\"categoryName\":\"经济\",\"language\":\"zh-CN\",\"pages\":680,\"price\":78,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/4839488e-5adb-4e40-bbd9-e2253c33ef88.webp\",\"summary\":\"经济学入门教材\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":15,\"availableCopies\":12,\"location\":\"主馆2楼F区\",\"shelfNo\":\"F1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T07:57:47.577+00:00\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 6, '2026-04-11 15:57:48');
INSERT INTO `operation_log` VALUES (11, 1, 'admin', '修改图书', '图书管理', 'com.example.demo.controller.BookController.updateBook', '{\"book\":{\"id\":9,\"isbn\":\"9787040123457\",\"title\":\"高等数学\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"同济大学\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"高等教育出版社\",\"publishYear\":\"2019\",\"edition\":\"\",\"category\":\"O\",\"categoryName\":\"数学\",\"language\":\"zh-CN\",\"pages\":480,\"price\":42,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/60600837-71cd-4b39-8594-3dfd681227ba.jpeg\",\"summary\":\"高等数学教材\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":40,\"availableCopies\":32,\"location\":\"主馆2楼O区\",\"shelfNo\":\"O1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T08:24:00.189+00:00\"},\"id\":9}', '{\"headers\":{},\"body\":{\"id\":9,\"isbn\":\"9787040123457\",\"title\":\"高等数学\",\"subtitle\":\"\",\"titleEn\":\"\",\"author\":\"同济大学\",\"authorEn\":\"\",\"translator\":\"\",\"publisher\":\"高等教育出版社\",\"publishYear\":\"2019\",\"edition\":\"\",\"category\":\"O\",\"categoryName\":\"数学\",\"language\":\"zh-CN\",\"pages\":480,\"price\":42,\"tags\":\"\",\"coverUrl\":\"/uploads/covers/60600837-71cd-4b39-8594-3dfd681227ba.jpeg\",\"summary\":\"高等数学教材\",\"summaryEn\":\"\",\"status\":\"可借\",\"totalCopies\":40,\"availableCopies\":32,\"location\":\"主馆2楼O区\",\"shelfNo\":\"O1-01\",\"deleted\":0,\"createTime\":\"2026-04-10T05:09:22.000+00:00\",\"updateTime\":\"2026-04-11T08:24:00.189+00:00\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, NULL, 7, '2026-04-11 16:24:00');
INSERT INTO `operation_log` VALUES (12, 1, 'admin', '批量编辑', '图书管理', 'com.example.demo.controller.BookController.batchUpdateBooks', '{\"request\":{\"ids\":[1,2,3,4,5,6,7,8,9,10,11,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95],\"data\":{\"coverUrl\":\"/uploads/covers/184b8fff-5199-45fb-9dc4-ef1529d70ad5.jpeg\"}}}', '{\"headers\":{},\"body\":{\"success\":true,\"message\":\"成功修改 50 本图书\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', 1, NULL, 388, '2026-04-11 16:39:21');

-- ----------------------------
-- Table structure for purchase_request
-- ----------------------------
DROP TABLE IF EXISTS `purchase_request`;
CREATE TABLE `purchase_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `requester_id` bigint NOT NULL,
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT 1 COMMENT 'quantity',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `approve_time` datetime NULL DEFAULT NULL COMMENT 'approve time',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'reject reason',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'remarks',
  `reviewer_id` bigint NULL DEFAULT NULL,
  `review_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_requester_id`(`requester_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_isbn`(`isbn` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_user` FOREIGN KEY (`requester_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_request
-- ----------------------------
INSERT INTO `purchase_request` VALUES (1, 18, '人工智能：一种现代方法', '罗素', '9787111545987', 5, '人民邮电出版社', '本书是人工智能领域的经典教材，适合作为研究生课程参考书。', 0, NULL, NULL, NULL, NULL, NULL, 0, '2026-04-10 13:11:08', '2026-04-10 13:11:08');
INSERT INTO `purchase_request` VALUES (2, 18, '人工智能：一种现代方法', '罗素', '9787111545987', 5, '人民邮电出版社', '本书是人工智能领域的经典教材，适合作为研究生课程参考书。', 0, NULL, NULL, NULL, NULL, NULL, 0, '2026-04-10 15:12:35', '2026-04-10 15:12:35');
INSERT INTO `purchase_request` VALUES (3, 18, '人工智能：一种现代方法', '罗素', '9787111545987', 5, '人民邮电出版社', '本书是人工智能领域的经典教材，适合作为研究生课程参考书。', 0, NULL, NULL, NULL, NULL, NULL, 0, '2026-04-10 15:12:40', '2026-04-10 15:12:40');

-- ----------------------------
-- Table structure for reading_room
-- ----------------------------
DROP TABLE IF EXISTS `reading_room`;
CREATE TABLE `reading_room`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `capacity` int NOT NULL DEFAULT 0,
  `open_time` time NULL DEFAULT NULL,
  `close_time` time NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `total_seats` int NOT NULL DEFAULT 0 COMMENT 'total seats',
  `available_seats` int NOT NULL DEFAULT 0 COMMENT 'available seats',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'description',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'image url',
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reading_room
-- ----------------------------
INSERT INTO `reading_room` VALUES (1, '综合阅览室', '主馆2楼', 60, '00:00:00', '23:59:00', 1, 60, 60, '提供综合图书阅览服务', NULL, 0, '2026-04-10 13:09:22', '2026-04-10 13:09:22');
INSERT INTO `reading_room` VALUES (2, '电子阅览室', '主馆3楼', 40, '08:00:00', '21:00:00', 1, 40, 40, '配备电脑终端', NULL, 0, '2026-04-10 13:09:22', '2026-04-10 13:09:22');
INSERT INTO `reading_room` VALUES (3, '静音自习室', '主馆4楼', 50, '07:00:00', '22:30:00', 1, 50, 50, '严格静音环境', NULL, 0, '2026-04-10 13:09:22', '2026-04-10 13:09:22');

-- ----------------------------
-- Table structure for refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `expiry_date` datetime NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `revoked` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_token`(`token`(255) ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refresh_token
-- ----------------------------
INSERT INTO `refresh_token` VALUES (1, 1, '1be5b1fb-7346-4923-8dfc-b435f4d9a7a2', '2026-04-18 15:32:19', '2026-04-10 21:31:17', 0);
INSERT INTO `refresh_token` VALUES (2, 16, '1f415abc-50e4-4aab-9eac-59efafa23d0e', '2026-04-17 23:45:21', '2026-04-10 23:05:21', 0);
INSERT INTO `refresh_token` VALUES (3, 18, 'd755458a-c481-4a0f-8bd2-13b2f8dc770b', '2026-04-18 00:50:49', '2026-04-11 00:05:30', 0);
INSERT INTO `refresh_token` VALUES (4, 14, 'de630b62-afa2-4935-905f-5622cfd22201', '2026-04-18 01:17:58', '2026-04-11 01:17:58', 0);
INSERT INTO `refresh_token` VALUES (5, 104, '252bfa64-232f-48f4-9bb3-f56d3ef77248', '2026-04-18 15:23:01', '2026-04-11 03:56:56', 0);
INSERT INTO `refresh_token` VALUES (6, 105, '139eb476-e81c-4870-938d-317a901f5f5a', '2026-04-18 04:13:19', '2026-04-11 03:58:35', 0);
INSERT INTO `refresh_token` VALUES (7, 106, '4fcf406e-c900-4db0-9166-03ffdcaea5a7', '2026-04-18 03:59:03', '2026-04-11 03:59:03', 0);

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `reserve_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_date` datetime NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `notify_date` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_reserve_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reserve_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation
-- ----------------------------

-- ----------------------------
-- Table structure for seat
-- ----------------------------
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `seat_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seat_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NORMAL' COMMENT 'type: NORMAL, POWER, QUIET, GROUP',
  `has_power` tinyint NOT NULL DEFAULT 0 COMMENT 'has power: 0-no, 1-yes',
  `has_lamp` tinyint NOT NULL DEFAULT 0 COMMENT 'has lamp: 0-no, 1-yes',
  `row_num` int NULL DEFAULT NULL,
  `col_num` int NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_seat`(`room_id` ASC, `seat_number` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_seat_type`(`seat_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_seat_room` FOREIGN KEY (`room_id`) REFERENCES `reading_room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seat
-- ----------------------------
INSERT INTO `seat` VALUES (1, 1, 'A-01', 'POWER', 1, 1, 1, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (2, 1, 'A-02', 'POWER', 1, 1, 1, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (3, 1, 'A-03', 'POWER', 1, 1, 1, 3, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (4, 1, 'A-04', 'POWER', 1, 1, 1, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (5, 1, 'A-05', 'POWER', 1, 1, 1, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (6, 1, 'A-06', 'POWER', 1, 1, 1, 6, 0, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (7, 1, 'A-07', 'POWER', 1, 1, 1, 7, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (8, 1, 'A-08', 'POWER', 1, 1, 1, 8, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (9, 1, 'A-09', 'POWER', 1, 1, 1, 9, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (10, 1, 'A-10', 'POWER', 1, 1, 1, 10, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (11, 1, 'A-11', 'POWER', 1, 1, 2, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (12, 1, 'A-12', 'POWER', 1, 1, 2, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (13, 1, 'A-13', 'POWER', 1, 1, 2, 3, 0, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (14, 1, 'A-14', 'POWER', 1, 1, 2, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (15, 1, 'A-15', 'POWER', 1, 1, 2, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (16, 1, 'A-16', 'POWER', 1, 1, 2, 6, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (17, 1, 'A-17', 'POWER', 1, 1, 2, 7, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (18, 1, 'A-18', 'POWER', 1, 1, 2, 8, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (19, 1, 'A-19', 'POWER', 1, 1, 2, 9, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (20, 1, 'A-20', 'POWER', 1, 1, 2, 10, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (21, 1, 'A-21', 'NORMAL', 0, 0, 3, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (22, 1, 'A-22', 'NORMAL', 0, 0, 3, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (23, 1, 'A-23', 'NORMAL', 0, 0, 3, 3, 0, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (24, 1, 'A-24', 'NORMAL', 0, 0, 3, 4, 2, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (25, 1, 'A-25', 'NORMAL', 0, 0, 3, 5, 0, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (32, 2, 'PC-01', 'POWER', 1, 1, 1, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (33, 2, 'PC-02', 'POWER', 1, 1, 1, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (34, 2, 'PC-03', 'POWER', 1, 1, 1, 3, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (35, 2, 'PC-04', 'POWER', 1, 1, 1, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (36, 2, 'PC-05', 'POWER', 1, 1, 1, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (37, 2, 'PC-06', 'POWER', 1, 1, 1, 6, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (38, 2, 'PC-07', 'POWER', 1, 1, 1, 7, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (39, 2, 'PC-08', 'POWER', 1, 1, 1, 8, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (40, 2, 'PC-09', 'POWER', 1, 1, 1, 9, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (41, 2, 'PC-10', 'POWER', 1, 1, 1, 10, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (42, 2, 'PC-11', 'POWER', 1, 1, 2, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (43, 2, 'PC-12', 'POWER', 1, 1, 2, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (44, 2, 'PC-13', 'POWER', 1, 1, 2, 3, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (45, 2, 'PC-14', 'POWER', 1, 1, 2, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (46, 2, 'PC-15', 'POWER', 1, 1, 2, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (47, 3, 'Q-01', 'QUIET', 1, 1, 1, 1, 0, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (48, 3, 'Q-02', 'QUIET', 1, 1, 1, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (49, 3, 'Q-03', 'QUIET', 1, 1, 1, 3, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (50, 3, 'Q-04', 'QUIET', 1, 1, 1, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (51, 3, 'Q-05', 'QUIET', 1, 1, 1, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (52, 3, 'Q-06', 'QUIET', 1, 1, 1, 6, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (53, 3, 'Q-07', 'QUIET', 1, 1, 1, 7, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (54, 3, 'Q-08', 'QUIET', 1, 1, 1, 8, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (55, 3, 'Q-09', 'QUIET', 1, 1, 1, 9, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (56, 3, 'Q-10', 'QUIET', 1, 1, 1, 10, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (57, 3, 'Q-11', 'QUIET', 1, 1, 2, 1, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (58, 3, 'Q-12', 'QUIET', 1, 1, 2, 2, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (59, 3, 'Q-13', 'QUIET', 1, 1, 2, 3, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (60, 3, 'Q-14', 'QUIET', 1, 1, 2, 4, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (61, 3, 'Q-15', 'QUIET', 1, 1, 2, 5, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (62, 3, 'Q-16', 'QUIET', 1, 1, 2, 6, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (63, 3, 'Q-17', 'QUIET', 1, 1, 2, 7, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (64, 3, 'Q-18', 'QUIET', 1, 1, 2, 8, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (65, 3, 'Q-19', 'QUIET', 1, 1, 2, 9, 1, 0, '2026-04-10 13:10:22');
INSERT INTO `seat` VALUES (66, 3, 'Q-20', 'QUIET', 1, 1, 2, 10, 1, 0, '2026-04-10 13:10:22');

-- ----------------------------
-- Table structure for seat_reservation
-- ----------------------------
DROP TABLE IF EXISTS `seat_reservation`;
CREATE TABLE `seat_reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `seat_id` bigint NOT NULL,
  `room_id` bigint NOT NULL COMMENT 'room id',
  `reserve_date` date NOT NULL COMMENT '??????',
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `check_in_time` datetime NULL DEFAULT NULL,
  `check_out_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_seat_id`(`seat_id` ASC) USING BTREE,
  INDEX `idx_reserve_date`(`reserve_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_room_date`(`room_id` ASC, `reserve_date` ASC) USING BTREE,
  CONSTRAINT `fk_seat_res_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_seat_res_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_seat_reservation_room` FOREIGN KEY (`room_id`) REFERENCES `reading_room` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seat_reservation
-- ----------------------------
INSERT INTO `seat_reservation` VALUES (1, 105, 6, 1, '2026-04-11', '08:00:00', '12:00:00', 3, NULL, NULL, 0, '2026-04-11 04:50:26');
INSERT INTO `seat_reservation` VALUES (2, 105, 6, 1, '2026-04-11', '08:00:00', '12:00:00', 4, NULL, NULL, 0, '2026-04-11 04:52:35');
INSERT INTO `seat_reservation` VALUES (3, 104, 25, 1, '2026-04-11', '08:00:00', '12:00:00', 4, NULL, NULL, 0, '2026-04-11 15:23:16');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'borrow_days', '30', 'SYSTEM', 'Default borrow days', '2026-04-10 01:06:19');
INSERT INTO `system_config` VALUES (2, 'renew_limit', '3', 'SYSTEM', 'Renew limit', '2026-04-10 01:06:19');
INSERT INTO `system_config` VALUES (3, 'fine_per_day', '0.1', 'SYSTEM', 'Fine per day', '2026-04-10 01:06:19');
INSERT INTO `system_config` VALUES (4, 'max_borrow_count', '10', 'SYSTEM', 'Max borrow count', '2026-04-10 01:06:19');
INSERT INTO `system_config` VALUES (5, 'reservation_expire_days', '7', 'SYSTEM', 'Reservation expire days', '2026-04-10 01:06:19');

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'READER',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `id_card` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生学号',
  `faculty_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教职工号',
  `user_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户编号',
  `institution` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `campus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '院系',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `counselor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '辅导员',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ROLE_READER' COMMENT '系统角色',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'zh-CN',
  `status` tinyint NOT NULL DEFAULT 1,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `violation_count` int NULL DEFAULT 0,
  `last_violation_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_account
-- ----------------------------
INSERT INTO `user_account` VALUES (1, 'admin', '$2a$10$s2hU7G/hXFP284jYiKuTg.krA4j.jmes4KB1EzkiP5fqrLXQtt9AW', 'ADMIN', 'admin', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ADMIN', 'zh-CN', 1, 0, '2026-04-10 01:06:19', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (12, 'librarian', '$2a$10$cNON9h.6mu0gpRDCChQrKOg2YkDmUsAbKEv/30I4CeGMX9fxs.UQm', 'ADMIN', '图书管理员', '13800138001', 'librarian@library.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ADMIN', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (13, 'student001', '$2a$10$LnOypEIaMlxfpvrXrIstGeArCUfADviM6tAnLUMiWKhJpQ/Rlg5fm', 'STUDENT', '张三', '13900001001', 'student001@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (14, 'student002', '$2a$10$fATt51FBrBCnVGqmyfWqdeIJj9YXmUVn5NdM5Q1wQNRZ1kl4ta546', 'STUDENT', '李四', '13900001002', 'student002@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (15, 'student003', '$2a$10$WgK0s5gLh7PLWlz.rxfttuiI0KReOWr8TJWppC4PO/mLdQsuA8AvW', 'STUDENT', '王五', '13900001003', 'student003@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (16, 'student004', '$2a$10$36CPe7i3Ff4suV7WOOCSB.JWEvYIcv4GQ6WwrUwErKumCPNzqohae', 'STUDENT', '赵六', '13900001004', 'student004@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (17, 'student005', '$2a$10$cZHPCHsPy7Vl8sB1i8/pl.oJQDjVC893jS4xTK4G8tlmCOHOjls3y', 'STUDENT', '孙七', '13900001005', 'student005@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (18, 'teacher001', '$2a$10$6yw/dFUdzpDBtzbsZ8Pz1OTLiM8GJ9mINZlHQazhj2DOwGmHz/PGG', 'TEACHER', '张教授', '13900002001', 'teacher001@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (19, 'teacher002', '$2a$10$7K.Ea53FEvWj0JlOPI8AKeyVFj3bwIphQrt91XhEm9bGaIqt5NM0a', 'TEACHER', '李教授', '13900002002', 'teacher002@university.edu.cn', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (20, 'reader001', '$2a$10$/joZ4gQunRcd/3kwlJon6ezgsc.2iK2WTjnoDQ.NRsZcFjQssv8a6', 'READER', '陈读者', '13900003001', 'reader001@email.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-10 13:09:22', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (64, 'STU20240001', '$2a$10$2PF0A5rrcJk3PpFgSaRVpOux1HhLrRcEC76VH7A22dwI/apD3h1Tq', 'STUDENT', '张三', '13800138000', 'zhangsan@example.com', NULL, 'STU20240001', NULL, NULL, NULL, '主校区', '计算机学院', '2024级', '软件工程2401班', '李老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:50', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (65, 'FAC20240001', '$2a$10$9SNb2Ne3K5nlNjhdhuBq5.I/mBqeAW55t4dHFDV6Bl7f0RLAWd3vm', 'TEACHER', '李教授', '13900139000', 'lilaoshi@example.com', NULL, NULL, 'FAC20240001', NULL, NULL, '主校区', '计算机学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:50', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (66, 'USR20240001', '$2a$10$W/JvqmFMDO.Liik.FPE3LezMtrhzYy86sOBV1vRO1cXSyDEmt0gDW', 'READER', '王读者', '13700137000', 'wang@example.com', NULL, NULL, NULL, 'USR20240001', NULL, '主校区', '图书馆', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (67, 'STU20240002', '$2a$10$iyfX/mRN6NNap1M0Jrrr1um4Yp5XRdRaQd7Rb.l3wBKV.sd1ynr0e', 'STUDENT', '李明', '13800138001', 'liming@example.com', NULL, 'STU20240002', NULL, NULL, NULL, '主校区', '计算机学院', '2024级', '软件工程2401班', '李老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:38', 0, NULL);
INSERT INTO `user_account` VALUES (68, 'STU20240003', '$2a$10$7M0blgAtxkw234wSR5nPP.MSGVi3Qb1wddE3DsLvL8Q21QqwKuFpS', 'STUDENT', '王芳', '13800138002', 'wangfang@example.com', NULL, 'STU20240003', NULL, NULL, NULL, '主校区', '计算机学院', '2024级', '软件工程2402班', '张老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (69, 'STU20240004', '$2a$10$iVzG7/duDDYDSJi28wg0D.0Hxu7rZIiBZaDzHdcLmrZ3l7YiSVwYC', 'STUDENT', '陈晨', '13800138003', 'chenchen@example.com', NULL, 'STU20240004', NULL, NULL, NULL, '主校区', '电子信息学院', '2024级', '通信工程2401班', '刘老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (70, 'STU20240005', '$2a$10$6hAnpHugfAS7z/YIKwK5zeBbJWAE5RFZvV21nZX.l9eQ0vjysZPqy', 'STUDENT', '赵磊', '13800138004', 'zhaolei@example.com', NULL, 'STU20240005', NULL, NULL, NULL, '主校区', '电子信息学院', '2024级', '电子科学2402班', '王老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (71, 'STU20240006', '$2a$10$flM0YKcOD75oYumC.OsC9uIQYatZMXVzWIjmx/5xYI48jB83kRKOm', 'STUDENT', '周杰', '13800138005', 'zhoujie@example.com', NULL, 'STU20240006', NULL, NULL, NULL, '主校区', '机械工程学院', '2024级', '机械设计2401班', '孙老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (72, 'STU20240007', '$2a$10$Pt3IwRz06s1qr4Gj8yn3x.5J9Nr4WPOhBbwj1ZrNaayIpN.WCtnV2', 'STUDENT', '吴婷', '13800138006', 'wuting@example.com', NULL, 'STU20240007', NULL, NULL, NULL, '主校区', '机械工程学院', '2024级', '机械制造2402班', '陈老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (73, 'STU20240008', '$2a$10$JmpVjUaO4iglFnBU94h40uEIbzYPnSegQvPb9dSPt/icquMiTJAm2', 'STUDENT', '徐浩', '13800138007', 'xuhao@example.com', NULL, 'STU20240008', NULL, NULL, NULL, '主校区', '经济管理学院', '2024级', '工商管理2401班', '杨老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (74, 'STU20240009', '$2a$10$TVLbr/KwH6AIacCsnKhoPO9Gzaal07CXJNolQZ14WA0NA3wiHEjIa', 'STUDENT', '黄莉', '13800138008', 'huangli@example.com', NULL, 'STU20240009', NULL, NULL, NULL, '主校区', '经济管理学院', '2024级', '会计学2402班', '周老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (75, 'STU20240010', '$2a$10$uCmjEt9xkJhebP2Gy8Q3le3zlpfFclmGMXLYnHe9OzJAdbij9XrKO', 'STUDENT', '高强', '13800138009', 'gaoqiang@example.com', NULL, 'STU20240010', NULL, NULL, NULL, '主校区', '外国语学院', '2024级', '英语2401班', '吴老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (76, 'STU20240011', '$2a$10$TjoPx3Z3BUUa3ARioJhBD.CwW20QNyy265THgvUSJBL4C4btSrMm6', 'STUDENT', '林雪', '13800138010', 'linxue@example.com', NULL, 'STU20240011', NULL, NULL, NULL, '主校区', '外国语学院', '2024级', '日语2402班', '徐老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (77, 'STU20240012', '$2a$10$wYesz69YDetyUWQQSHq8gOY1JfeDlgUo.6VzIeRCF9obcddrhjN9S', 'STUDENT', '郑伟', '13800138011', 'zhengwei@example.com', NULL, 'STU20240012', NULL, NULL, NULL, '分校区', '计算机学院', '2024级', '网络工程2401班', '李老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (78, 'STU20240013', '$2a$10$31F36rWegoOAUBXVsHId6.8ouHHATPVLAAJKBvEATp79KVSZSvNh2', 'STUDENT', '孙悦', '13800138012', 'sunyue@example.com', NULL, 'STU20240013', NULL, NULL, NULL, '分校区', '计算机学院', '2024级', '信息安全2402班', '张老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:51', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (79, 'STU20240014', '$2a$10$jtKiuk5/Vkie0w2wEutnqOmdDZHBh180cf7JcjkOhKrcDt96a3efu', 'STUDENT', '朱勇', '13800138013', 'zhuyong@example.com', NULL, 'STU20240014', NULL, NULL, NULL, '分校区', '化学化工学院', '2024级', '化学工程2401班', '刘老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (80, 'STU20240015', '$2a$10$nYVyzsNX10rWZGFPe4bfuOJ5.9UuePNL98cnYoKN8D3VhRru1POUC', 'STUDENT', '胡静', '13800138014', 'hujing@example.com', NULL, 'STU20240015', NULL, NULL, NULL, '分校区', '化学化工学院', '2024级', '应用化学2402班', '王老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (81, 'STU20240016', '$2a$10$7dWFeafWVE5oNFutCAYXZO61foEw9KMPkCryD8q2iBduXc5bW3C.y', 'STUDENT', '何鹏', '13800138015', 'hepeng@example.com', NULL, 'STU20240016', NULL, NULL, NULL, '分校区', '材料科学学院', '2024级', '材料物理2401班', '孙老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:39', 0, NULL);
INSERT INTO `user_account` VALUES (82, 'STU20240017', '$2a$10$HIiaOdLo75JG6r3sIV84HORslg/HZ4PI6dBkxMkIhAZ7DJkDPfV0a', 'STUDENT', '邓娜', '13800138016', 'dengna@example.com', NULL, 'STU20240017', NULL, NULL, NULL, '分校区', '材料科学学院', '2024级', '金属材料2402班', '陈老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (83, 'STU20240018', '$2a$10$VOrAiUjvkXbPo9ibuCBwU.7E9U.g8lrWEGe4YwfJpmp3YEIQe9tY6', 'STUDENT', '曹阳', '13800138017', 'caoyang@example.com', NULL, 'STU20240018', NULL, NULL, NULL, '分校区', '法学院', '2024级', '法学2401班', '杨老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (84, 'STU20240019', '$2a$10$c1agEeDwVreOB8R7R1c1RO9LZKzWXtcqb0I5DQ4NDCULiId1A8Yaq', 'STUDENT', '彭娟', '13800138018', 'pengjuan@example.com', NULL, 'STU20240019', NULL, NULL, NULL, '分校区', '法学院', '2024级', '知识产权2402班', '周老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (85, 'STU20240020', '$2a$10$XkQYPA570TOv6aEnCfw8buMarBJme5SSW4ZQQF64iqN0p29TN/6RW', 'STUDENT', '韩冰', '13800138019', 'hanbing@example.com', NULL, 'STU20240020', NULL, NULL, NULL, '分校区', '艺术学院', '2024级', '视觉传达2401班', '吴老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (86, 'STU20240021', '$2a$10$PeNqsl.A50SJ7lcl1wrACOrGDBceXrzVswk5FWvknaD7NeEEXGSaC', 'STUDENT', '潘虹', '13800138020', 'panhong@example.com', NULL, 'STU20240021', NULL, NULL, NULL, '分校区', '艺术学院', '2024级', '音乐表演2402班', '徐老师', 'ROLE_STUDENT', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (87, 'FAC20240002', '$2a$10$uy6JO4Hp/qHTx2e2mG6Iv.zq1piaqaRO1TTNmXpTHS/4u/17xwOba', 'TEACHER', '王副教授', '13900139001', 'wangfu@example.com', NULL, NULL, 'FAC20240002', NULL, NULL, '主校区', '计算机学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (88, 'FAC20240003', '$2a$10$PXH8jLEZ4C7/Nm3lwT20aeDKCBjgvKBxiH8/kbWURnUcQP008R/X6', 'TEACHER', '刘博士', '13900139002', 'liubo@example.com', NULL, NULL, 'FAC20240003', NULL, NULL, '主校区', '电子信息学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (89, 'FAC20240004', '$2a$10$iwZDjyOoA.RDpw5hOQmkZO5uybZkvAK9GylbhQbk03WZSzIKj2oZq', 'TEACHER', '张教授', '13900139003', 'zhangprof@example.com', NULL, NULL, 'FAC20240004', NULL, NULL, '主校区', '机械工程学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (90, 'FAC20240005', '$2a$10$aVseri2X93etGDjhjmsfRuibc6vCzzyXxbZ9pd7kHxzT8UpaMITg6', 'TEACHER', '陈讲师', '13900139004', 'chenlect@example.com', NULL, NULL, 'FAC20240005', NULL, NULL, '主校区', '经济管理学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (91, 'FAC20240006', '$2a$10$.qghoU9S4HZ0Xum8E9oQreKWkRzfC43hxdo0.Lo9PsDbE/xcere5e', 'TEACHER', '杨老师', '13900139005', 'yangteacher@example.com', NULL, NULL, 'FAC20240006', NULL, NULL, '主校区', '外国语学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:52', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (92, 'FAC20240007', '$2a$10$nAWlE/Dsv.6pFhp/qL2WtOly.euwWN1f8ZPMvSdNoCt9OQfo9NK5.', 'TEACHER', '孙研究员', '13900139006', 'sunres@example.com', NULL, NULL, 'FAC20240007', NULL, NULL, '分校区', '化学化工学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (93, 'FAC20240008', '$2a$10$IL1uK.8O/i40rX3gNd.pM.8CRWwLP1P7A.Ow4dLcvO2ieYIqGdg/6', 'TEACHER', '周高工', '13900139007', 'zhoueng@example.com', NULL, NULL, 'FAC20240008', NULL, NULL, '分校区', '材料科学学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (94, 'FAC20240009', '$2a$10$0QoffSVxiFToGdFDi5CEDOhOepLF3Uu41AHDZt5cEMymSmhDvp62O', 'TEACHER', '吴主任', '13900139008', 'wudir@example.com', NULL, NULL, 'FAC20240009', NULL, NULL, '分校区', '法学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:40', 0, NULL);
INSERT INTO `user_account` VALUES (95, 'FAC20240010', '$2a$10$0GwSwLdEyjYG4beRFCPoE.9dRojCrwa3K/4zMv0FX6ERwhQDln7ba', 'TEACHER', '徐导师', '13900139009', 'xuguide@example.com', NULL, NULL, 'FAC20240010', NULL, NULL, '分校区', '艺术学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (96, 'FAC20240011', '$2a$10$dz2GPJoyyBIM7dZQs0.oGeMOnURF9zbe2w84bVTDy9khiOTD3UlvK', 'TEACHER', '朱教授', '13900139010', 'zhuprof@example.com', NULL, NULL, 'FAC20240011', NULL, NULL, '主校区', '数学学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (97, 'FAC20240012', '$2a$10$AA.SOi1iVfNpcISiFC3uRO3vGOXGItavJ8LrSty6W02hgQS1uoLYi', 'TEACHER', '胡博士', '13900139011', 'hubo@example.com', NULL, NULL, 'FAC20240012', NULL, NULL, '主校区', '物理学院', '', '', '', 'ROLE_TEACHER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (98, 'USR20240002', '$2a$10$6H9zMWhbjB6qyXLmsYabaeC6bH.nQYkhfBG0kKIh5JBK64uAWfXJS', 'READER', '刘读者', '13700137001', 'liureader@example.com', NULL, NULL, NULL, 'USR20240002', NULL, '主校区', '图书馆', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (99, 'USR20240003', '$2a$10$PFoHkd5q4CDqWgjd0rXAHOy5mrBQ/z1zLemvHnlj3w8CiDMtLQskO', 'READER', '陈访客', '13700137002', 'chenvisitor@example.com', NULL, NULL, NULL, 'USR20240003', NULL, '主校区', '信息中心', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (100, 'USR20240004', '$2a$10$3v.vGn7w2gFWEZkDik6.cu1wPUVUiZNX8iq38CdnLnx9VCzWbQE56', 'READER', '张校友', '13700137003', 'zhangalum@example.com', NULL, NULL, NULL, 'USR20240004', NULL, '主校区', '校友办', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (101, 'USR20240005', '$2a$10$nWbAZwzP1OkjotrtjafIKOZVVVv97mDpz8AFh56gFkIOO0c2KoEqW', 'READER', '王合作', '13700137004', 'wangcollab@example.com', NULL, NULL, NULL, 'USR20240005', NULL, '分校区', '科研处', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (102, 'USR20240006', '$2a$10$fd3BrRiCh51bGvmt4JpfH.jXQUFJIvGWV.SB.zcvg.HbrcKiD99FG', 'READER', '赵访问', '13700137005', 'zhaovisit@example.com', NULL, NULL, NULL, 'USR20240006', NULL, '分校区', '国际交流处', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (103, 'USR20240007', '$2a$10$M5CognNferGc4wh6efTC8.vAOvB4qBIytBZG03uVF97ooIx/HCygK', 'READER', '孙校外', '13700137006', 'sunexternal@example.com', NULL, NULL, NULL, 'USR20240007', NULL, '分校区', '继续教育学院', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:53', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (104, 'USR20240008', '$2a$10$9wJ7H/OAVfTdd4eJ2J468uZXAdzQUJVq6aFh4/RChd12KlaqJvQPW', 'READER', '周进修', '13700137007', 'zhoustudy@example.com', NULL, NULL, NULL, 'USR20240008', NULL, '主校区', '培训中心', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:54', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (105, 'USR20240009', '$2a$10$89p5YVFuNImnz52H7C1EAuDfOkgbH3id6QTmx2L3pFgKum0K0Dnym', 'READER', '吴旁听', '13700137008', 'wuaudit@example.com', NULL, NULL, NULL, 'USR20240009', NULL, '主校区', '教务处', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:54', '2026-04-11 16:37:41', 0, NULL);
INSERT INTO `user_account` VALUES (106, 'USR20240010', '$2a$10$YWXqWQav2Zsv1NJRAnD8.uKQq7j0P/2u3KIiJR4yR7kAYMsnihWMC', 'READER', '郑临时', '13700137009', 'zhengtemp@example.com', NULL, NULL, NULL, 'USR20240010', NULL, '分校区', '后勤集团', '', '', '', 'ROLE_READER', 'zh-CN', 1, 0, '2026-04-11 03:44:54', '2026-04-11 16:37:41', 0, NULL);

-- ----------------------------
-- Table structure for user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `user_behavior`;
CREATE TABLE `user_behavior`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `book_id` bigint NULL DEFAULT NULL COMMENT '鍥句功ID',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '琛屼负绫诲瀷锛歏IEW/BORROW/RETURN/SEARCH/FAVORITE',
  `action_detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '琛屼负璇︽儏',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP鍦板潃',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '鐢ㄦ埛浠ｇ悊',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_action`(`user_id` ASC, `action_type` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_behavior_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_behavior_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '鐢ㄦ埛琛屼负琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behavior
-- ----------------------------
INSERT INTO `user_behavior` VALUES (1, 13, NULL, 'VIEW', '查看图书详情', '192.168.1.100', NULL, '2026-04-10 13:11:08');
INSERT INTO `user_behavior` VALUES (2, 13, NULL, 'BORROW', '借阅图书', '192.168.1.100', NULL, '2026-04-10 13:11:08');
INSERT INTO `user_behavior` VALUES (3, 13, NULL, 'VIEW', '查看图书详情', '192.168.1.100', NULL, '2026-04-10 15:12:35');
INSERT INTO `user_behavior` VALUES (4, 13, NULL, 'BORROW', '借阅图书', '192.168.1.100', NULL, '2026-04-10 15:12:35');
INSERT INTO `user_behavior` VALUES (5, 13, NULL, 'VIEW', '查看图书详情', '192.168.1.100', NULL, '2026-04-10 15:12:40');
INSERT INTO `user_behavior` VALUES (6, 13, NULL, 'BORROW', '借阅图书', '192.168.1.100', NULL, '2026-04-10 15:12:40');

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------

-- ----------------------------
-- Procedure structure for add_column_if_not_exists
-- ----------------------------
DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;
delimiter ;;
CREATE PROCEDURE `add_column_if_not_exists`(IN table_name_param VARCHAR(100),
    IN column_name_param VARCHAR(100),
    IN column_definition VARCHAR(500))
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = table_name_param
      AND COLUMN_NAME = column_name_param;
    
    IF column_exists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', table_name_param, ' ADD COLUMN ', column_name_param, ' ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
