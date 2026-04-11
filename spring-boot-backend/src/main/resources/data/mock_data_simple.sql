-- =====================================================
-- 图书管理系统模拟数据生成脚本
-- 版本: V1.0.0
-- 日期: 2026-04-10
-- =====================================================

USE CityLibrary;

SET NAMES utf8mb4;

-- 用户数据
INSERT IGNORE INTO user_account (username, password, user_type, real_name, phone, email, role, language, status, deleted, create_time, update_time) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'ADMIN', '系统管理员', '13800138000', 'admin@library.com', 'ADMIN', 'zh-CN', 1, 0, NOW(), NOW()),
('librarian', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'ADMIN', '图书管理员', '13800138001', 'librarian@library.com', 'ADMIN', 'zh-CN', 1, 0, NOW(), NOW()),
('student001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '张三', '13900001001', 'student001@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('student002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '李四', '13900001002', 'student002@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('student003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '王五', '13900001003', 'student003@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('student004', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '赵六', '13900001004', 'student004@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('student005', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '孙七', '13900001005', 'student005@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('teacher001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '张教授', '13900002001', 'teacher001@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('teacher002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '李教授', '13900002002', 'teacher002@university.edu.cn', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW()),
('reader001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '陈读者', '13900003001', 'reader001@email.com', 'ROLE_READER', 'zh-CN', 1, 0, NOW(), NOW());

-- 图书分类数据
INSERT IGNORE INTO category (name, code, parent_id, sort_order, description, create_time, update_time) VALUES
('马克思主义、列宁主义、毛泽东思想、邓小平理论', 'A', NULL, 1, 'A类', NOW(), NOW()),
('哲学、宗教', 'B', NULL, 2, 'B类', NOW(), NOW()),
('社会科学总论', 'C', NULL, 3, 'C类', NOW(), NOW()),
('政治、法律', 'D', NULL, 4, 'D类', NOW(), NOW()),
('经济', 'F', NULL, 5, 'F类', NOW(), NOW()),
('文化、科学、教育、体育', 'G', NULL, 6, 'G类', NOW(), NOW()),
('语言、文字', 'H', NULL, 7, 'H类', NOW(), NOW()),
('文学', 'I', NULL, 8, 'I类', NOW(), NOW()),
('历史、地理', 'K', NULL, 9, 'K类', NOW(), NOW()),
('数理科学和化学', 'O', NULL, 10, 'O类', NOW(), NOW()),
('工业技术', 'T', NULL, 11, 'T类', NOW(), NOW());

-- 图书数据
INSERT INTO book (isbn, title, author, publisher, publish_year, category, category_name, language, pages, price, summary, status, total_copies, available_copies, location, shelf_no, deleted, create_time, update_time) VALUES
('9787010001234', '共产党宣言', '马克思 恩格斯', '人民出版社', '2018', 'A', '马克思主义', 'zh-CN', 120, 25.00, '马克思主义经典著作', '可借', 10, 8, '主馆2楼A区', 'A-01-01', 0, NOW(), NOW()),
('9787100012345', '西方哲学史', '罗素', '商务印书馆', '2016', 'B', '哲学', 'zh-CN', 850, 98.00, '西方哲学经典著作', '可借', 6, 4, '主馆2楼B区', 'B-01-01', 0, NOW(), NOW()),
('9787300123456', '经济学原理', '曼昆', '北京大学出版社', '2019', 'F', '经济', 'zh-CN', 680, 78.00, '经济学入门教材', '可借', 15, 12, '主馆2楼F区', 'F-01-01', 0, NOW(), NOW()),
('9787040123456', '教育学', '王道俊', '人民教育出版社', '2019', 'G', '教育', 'zh-CN', 480, 45.00, '教育学基础教材', '可借', 20, 15, '主馆2楼G区', 'G-01-01', 0, NOW(), NOW()),
('9787560012345', '新概念英语1', '亚历山大', '外语教学与研究出版社', '2017', 'H', '语言', 'zh-CN', 280, 38.00, '经典英语教材', '可借', 30, 25, '主馆2楼H区', 'H-01-01', 0, NOW(), NOW()),
('9787020012345', '红楼梦', '曹雪芹', '人民文学出版社', '2018', 'I', '文学', 'zh-CN', 1600, 88.00, '中国古典名著', '可借', 20, 15, '主馆2楼I区', 'I-01-01', 0, NOW(), NOW()),
('9787020012346', '三国演义', '罗贯中', '人民文学出版社', '2017', 'I', '文学', 'zh-CN', 980, 68.00, '中国古典名著', '可借', 18, 14, '主馆2楼I区', 'I-01-02', 0, NOW(), NOW()),
('9787101012345', '史记', '司马迁', '中华书局', '2016', 'K', '历史', 'zh-CN', 3200, 298.00, '中国史学经典', '可借', 8, 6, '主馆2楼K区', 'K-01-01', 0, NOW(), NOW()),
('9787040123457', '高等数学', '同济大学', '高等教育出版社', '2019', 'O', '数学', 'zh-CN', 480, 42.00, '高等数学教材', '可借', 40, 32, '主馆2楼O区', 'O-01-01', 0, NOW(), NOW()),
('9787111123456', 'Java核心技术', '霍斯特曼', '机械工业出版社', '2020', 'T', '计算机', 'zh-CN', 920, 128.00, 'Java编程经典', '可借', 25, 20, '主馆2楼T区', 'T-01-01', 0, NOW(), NOW()),
('9787111123457', 'Python编程从入门到实践', '马瑟斯', '人民邮电出版社', '2020', 'T', '计算机', 'zh-CN', 480, 89.00, 'Python编程畅销书', '可借', 30, 24, '主馆2楼T区', 'T-01-02', 0, NOW(), NOW());

-- 阅览室数据
INSERT INTO reading_room (name, location, capacity, total_seats, available_seats, open_time, close_time, description, status, deleted, create_time, update_time) VALUES
('综合阅览室', '主馆2楼', 60, 60, 60, '08:00:00', '22:00:00', '提供综合图书阅览服务', 1, 0, NOW(), NOW()),
('电子阅览室', '主馆3楼', 40, 40, 40, '08:00:00', '21:00:00', '配备电脑终端', 1, 0, NOW(), NOW()),
('静音自习室', '主馆4楼', 50, 50, 50, '07:00:00', '22:30:00', '严格静音环境', 1, 0, NOW(), NOW());

-- 座位数据(为每个阅览室创建部分座位)
INSERT INTO seat (room_id, seat_number, row_num, col_num, seat_type, has_power, has_lamp, status, deleted, create_time, update_time)
SELECT 1, CONCAT('A-', LPAD(n, 2, '0')), 
       CEIL(n/10),
       CASE WHEN n % 10 = 0 THEN 10 ELSE n % 10 END,
       CASE WHEN n <= 20 THEN 'POWER' ELSE 'NORMAL' END,
       CASE WHEN n <= 20 THEN 1 ELSE 0 END,
       CASE WHEN n <= 20 THEN 1 ELSE 0 END,
       1, 0, NOW(), NOW()
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25
) AS numbers;

INSERT INTO seat (room_id, seat_number, row_num, col_num, seat_type, has_power, has_lamp, status, deleted, create_time, update_time)
SELECT 2, CONCAT('PC-', LPAD(n, 2, '0')),
       CEIL(n/10),
       CASE WHEN n % 10 = 0 THEN 10 ELSE n % 10 END,
       'POWER', 1, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
) AS numbers;

INSERT INTO seat (room_id, seat_number, row_num, col_num, seat_type, has_power, has_lamp, status, deleted, create_time, update_time)
SELECT 3, CONCAT('Q-', LPAD(n, 2, '0')),
       CEIL(n/10),
       CASE WHEN n % 10 = 0 THEN 10 ELSE n % 10 END,
       'QUIET', 1, 1, 1, 0, NOW(), NOW()
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) AS numbers;

-- 借阅记录数据
INSERT INTO borrow_record (user_id, book_id, borrow_date, due_date, status, renew_count, deleted, create_time, update_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    DATE_ADD(NOW(), INTERVAL 15 DAY),
    0, 0, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY);

INSERT INTO borrow_record (user_id, book_id, borrow_date, due_date, status, renew_count, deleted, create_time, update_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    DATE_ADD(NOW(), INTERVAL 20 DAY),
    0, 1, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY);

INSERT INTO borrow_record (user_id, book_id, borrow_date, due_date, status, renew_count, deleted, create_time, update_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student003'),
    (SELECT id FROM book WHERE isbn = '9787111123457'),
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    DATE_ADD(NOW(), INTERVAL 25 DAY),
    0, 0, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY);

-- 预约记录数据
INSERT INTO reservation (user_id, book_id, reserve_date, expire_date, status, deleted, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student004'),
    (SELECT id FROM book WHERE isbn = '9787020012346'),
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    0, 0, NOW();

-- 通知数据
INSERT INTO notification (user_id, title, content, type, is_read, deleted, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    '借阅成功通知',
    '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。',
    'BORROW', 0, 0, NOW();

INSERT INTO notification (user_id, title, content, type, is_read, deleted, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    '到期提醒',
    '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。',
    'DUE_REMINDER', 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY);

-- 图书评价数据
INSERT INTO book_review (book_id, user_id, rating, content, status, create_time, update_time)
SELECT 
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    (SELECT id FROM user_account WHERE username = 'student001'),
    5,
    '《红楼梦》是中国文学的巅峰之作，每次阅读都有新的感悟。',
    1, NOW(), NOW();

INSERT INTO book_review (book_id, user_id, rating, content, status, create_time, update_time)
SELECT 
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    (SELECT id FROM user_account WHERE username = 'student002'),
    4,
    '《Java核心技术》内容全面，讲解详细，适合Java开发者深入学习。',
    1, NOW(), NOW();

-- 用户收藏数据
INSERT INTO user_favorite (user_id, book_id, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    NOW();

INSERT INTO user_favorite (user_id, book_id, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    NOW();

-- 用户行为数据
INSERT INTO user_behavior (user_id, book_id, action_type, action_detail, ip_address, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    'VIEW',
    '查看图书详情',
    '192.168.1.100',
    NOW();

INSERT INTO user_behavior (user_id, book_id, action_type, action_detail, ip_address, create_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    'BORROW',
    '借阅图书',
    '192.168.1.100',
    NOW();

-- 咨询记录数据
INSERT INTO inquiry (user_id, title, content, category, status, reply_content, replier_id, reply_date, deleted, create_time, update_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    '如何续借图书？',
    '请问图书到期后如何续借？续借次数有限制吗？',
    'BORROW',
    1,
    '您好！图书到期前可以在个人中心进行在线续借，每本书最多续借1次，续借期限为30天。',
    (SELECT id FROM user_account WHERE username = 'librarian'),
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY);

-- 采购申请数据
INSERT INTO purchase_request (requester_id, book_title, author, isbn, publisher, reason, quantity, status, deleted, create_time, update_time)
SELECT 
    (SELECT id FROM user_account WHERE username = 'teacher001'),
    '人工智能：一种现代方法',
    '罗素',
    '9787111545987',
    '人民邮电出版社',
    '本书是人工智能领域的经典教材，适合作为研究生课程参考书。',
    5,
    0,
    0, NOW(), NOW();

SELECT '模拟数据插入完成！' AS '执行结果';
