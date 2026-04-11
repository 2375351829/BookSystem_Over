-- =====================================================
-- 图书管理系统模拟数据生成脚本
-- 版本: V1.0.0
-- 日期: 2026-04-10
-- 描述: 为图书管理系统添加全面的模拟数据
-- =====================================================

USE CityLibrary;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- =====================================================
-- 1. 用户数据 (user_account)
-- =====================================================

-- 管理员用户
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `phone`, `email`, `role`, `language`, `status`, `deleted`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'ADMIN', '系统管理员', '13800138000', 'admin@library.com', 'ADMIN', 'zh-CN', 1, 0),
('librarian', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'ADMIN', '图书管理员', '13800138001', 'librarian@library.com', 'ADMIN', 'zh-CN', 1, 0);

-- 学生用户
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `phone`, `email`, `id_card`, `institution`, `role`, `language`, `status`, `deleted`) VALUES
('student001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '张三', '13900001001', 'student001@university.edu.cn', '110101199901011234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '李四', '13900001002', 'student002@university.edu.cn', '110101199902021234', '北京大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '王五', '13900001003', 'student003@university.edu.cn', '110101199903031234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student004', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '赵六', '13900001004', 'student004@university.edu.cn', '110101199904041234', '复旦大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student005', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '孙七', '13900001005', 'student005@university.edu.cn', '110101199905051234', '浙江大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student006', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '周八', '13900001006', 'student006@university.edu.cn', '110101199906061234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student007', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '吴九', '13900001007', 'student007@university.edu.cn', '110101199907071234', '北京大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student008', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '郑十', '13900001008', 'student008@university.edu.cn', '110101199908081234', '复旦大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student009', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '陈明', '13900001009', 'student009@university.edu.cn', '110101199909091234', '浙江大学', 'ROLE_READER', 'zh-CN', 1, 0),
('student010', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'STUDENT', '刘华', '13900001010', 'student010@university.edu.cn', '110101199910101234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0);

-- 教师用户
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `phone`, `email`, `id_card`, `institution`, `role`, `language`, `status`, `deleted`) VALUES
('teacher001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '张教授', '13900002001', 'teacher001@university.edu.cn', '110101197001011234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0),
('teacher002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '李教授', '13900002002', 'teacher002@university.edu.cn', '110101197002021234', '北京大学', 'ROLE_READER', 'zh-CN', 1, 0),
('teacher003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '王教授', '13900002003', 'teacher003@university.edu.cn', '110101197003031234', '复旦大学', 'ROLE_READER', 'zh-CN', 1, 0),
('teacher004', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '赵教授', '13900002004', 'teacher004@university.edu.cn', '110101197004041234', '浙江大学', 'ROLE_READER', 'zh-CN', 1, 0),
('teacher005', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'TEACHER', '孙教授', '13900002005', 'teacher005@university.edu.cn', '110101197005051234', '清华大学', 'ROLE_READER', 'zh-CN', 1, 0);

-- 普通读者
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `phone`, `email`, `id_card`, `role`, `language`, `status`, `deleted`) VALUES
('reader001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '陈读者', '13900003001', 'reader001@email.com', '110101198001011234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '刘读者', '13900003002', 'reader002@email.com', '110101198002021234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '黄读者', '13900003003', 'reader003@email.com', '110101198003031234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader004', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '周读者', '13900003004', 'reader004@email.com', '110101198004041234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader005', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '吴读者', '13900003005', 'reader005@email.com', '110101198005051234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader006', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '郑读者', '13900003006', 'reader006@email.com', '110101198006061234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader007', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '孙读者', '13900003007', 'reader007@email.com', '110101198007071234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader008', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '钱读者', '13900003008', 'reader008@email.com', '110101198008081234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader009', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '冯读者', '13900003009', 'reader009@email.com', '110101198009091234', 'ROLE_READER', 'zh-CN', 1, 0),
('reader010', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'READER', '韩读者', '13900003010', 'reader010@email.com', '110101198010101234', 'ROLE_READER', 'zh-CN', 1, 0);

-- 国际读者
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `phone`, `email`, `role`, `language`, `status`, `deleted`) VALUES
('international001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'INTERNATIONAL', 'John Smith', '13900004001', 'john.smith@email.com', 'ROLE_READER', 'en-US', 1, 0),
('international002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'INTERNATIONAL', 'Mary Johnson', '13900004002', 'mary.johnson@email.com', 'ROLE_READER', 'en-US', 1, 0),
('international003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'INTERNATIONAL', 'Tanaka Yuki', '13900004003', 'tanaka.yuki@email.com', 'ROLE_READER', 'ja-JP', 1, 0);

-- =====================================================
-- 2. 图书分类数据 (category)
-- =====================================================

-- 中图法22个大类
INSERT INTO `category` (`name`, `code`, `parent_id`, `sort_order`, `description`) VALUES
('马克思主义、列宁主义、毛泽东思想、邓小平理论', 'A', NULL, 1, 'A类：马克思主义、列宁主义、毛泽东思想、邓小平理论'),
('哲学、宗教', 'B', NULL, 2, 'B类：哲学、宗教'),
('社会科学总论', 'C', NULL, 3, 'C类：社会科学总论'),
('政治、法律', 'D', NULL, 4, 'D类：政治、法律'),
('军事', 'E', NULL, 5, 'E类：军事'),
('经济', 'F', NULL, 6, 'F类：经济'),
('文化、科学、教育、体育', 'G', NULL, 7, 'G类：文化、科学、教育、体育'),
('语言、文字', 'H', NULL, 8, 'H类：语言、文字'),
('文学', 'I', NULL, 9, 'I类：文学'),
('艺术', 'J', NULL, 10, 'J类：艺术'),
('历史、地理', 'K', NULL, 11, 'K类：历史、地理'),
('自然科学总论', 'N', NULL, 12, 'N类：自然科学总论'),
('数理科学和化学', 'O', NULL, 13, 'O类：数理科学和化学'),
('天文学、地球科学', 'P', NULL, 14, 'P类：天文学、地球科学'),
('生物科学', 'Q', NULL, 15, 'Q类：生物科学'),
('医药、卫生', 'R', NULL, 16, 'R类：医药、卫生'),
('农业科学', 'S', NULL, 17, 'S类：农业科学'),
('工业技术', 'T', NULL, 18, 'T类：工业技术'),
('交通运输', 'U', NULL, 19, 'U类：交通运输'),
('航空、航天', 'V', NULL, 20, 'V类：航空、航天'),
('环境科学、安全科学', 'X', NULL, 21, 'X类：环境科学、安全科学'),
('综合性图书', 'Z', NULL, 22, 'Z类：综合性图书');

-- 添加子分类
INSERT INTO `category` (`name`, `code`, `parent_id`, `sort_order`, `description`) VALUES
('马恩著作', 'A1', (SELECT id FROM (SELECT id FROM category WHERE code='A') AS tmp), 1, '马克思、恩格斯著作'),
('列宁著作', 'A2', (SELECT id FROM (SELECT id FROM category WHERE code='A') AS tmp), 2, '列宁著作'),
('毛泽东著作', 'A3', (SELECT id FROM (SELECT id FROM category WHERE code='A') AS tmp), 3, '毛泽东著作'),
('哲学理论', 'B1', (SELECT id FROM (SELECT id FROM category WHERE code='B') AS tmp), 1, '哲学理论'),
('世界哲学', 'B2', (SELECT id FROM (SELECT id FROM category WHERE code='B') AS tmp), 2, '世界哲学'),
('中国哲学', 'B3', (SELECT id FROM (SELECT id FROM category WHERE code='B') AS tmp), 3, '中国哲学'),
('政治理论', 'D1', (SELECT id FROM (SELECT id FROM category WHERE code='D') AS tmp), 1, '政治理论'),
('中国法律', 'D2', (SELECT id FROM (SELECT id FROM category WHERE code='D') AS tmp), 2, '中国法律'),
('经济学', 'F1', (SELECT id FROM (SELECT id FROM category WHERE code='F') AS tmp), 1, '经济学'),
('经济计划与管理', 'F2', (SELECT id FROM (SELECT id FROM category WHERE code='F') AS tmp), 2, '经济计划与管理'),
('教育', 'G1', (SELECT id FROM (SELECT id FROM category WHERE code='G') AS tmp), 1, '教育'),
('体育', 'G2', (SELECT id FROM (SELECT id FROM category WHERE code='G') AS tmp), 2, '体育'),
('英语', 'H1', (SELECT id FROM (SELECT id FROM category WHERE code='H') AS tmp), 1, '英语'),
('汉语', 'H2', (SELECT id FROM (SELECT id FROM category WHERE code='H') AS tmp), 2, '汉语'),
('中国文学', 'I1', (SELECT id FROM (SELECT id FROM category WHERE code='I') AS tmp), 1, '中国文学'),
('世界文学', 'I2', (SELECT id FROM (SELECT id FROM category WHERE code='I') AS tmp), 2, '世界文学'),
('中国历史', 'K1', (SELECT id FROM (SELECT id FROM category WHERE code='K') AS tmp), 1, '中国历史'),
('世界历史', 'K2', (SELECT id FROM (SELECT id FROM category WHERE code='K') AS tmp), 2, '世界历史'),
('数学', 'O1', (SELECT id FROM (SELECT id FROM category WHERE code='O') AS tmp), 1, '数学'),
('物理学', 'O2', (SELECT id FROM (SELECT id FROM category WHERE code='O') AS tmp), 2, '物理学'),
('化学', 'O3', (SELECT id FROM (SELECT id FROM category WHERE code='O') AS tmp), 3, '化学'),
('计算机技术', 'T1', (SELECT id FROM (SELECT id FROM category WHERE code='T') AS tmp), 1, '计算机技术'),
('自动化技术', 'T2', (SELECT id FROM (SELECT id FROM category WHERE code='T') AS tmp), 2, '自动化技术');

-- =====================================================
-- 3. 图书数据 (book)
-- =====================================================

-- 马克思主义类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787010001234', '共产党宣言', '马克思 恩格斯', '人民出版社', '2018', 'A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 'zh-CN', 120, 25.00, '《共产党宣言》是马克思和恩格斯为共产主义者同盟起草的纲领，全文贯穿马克思主义的历史观，它是马克思主义诞生的重要标志。', '可借', 10, 8, '主馆2楼A区', 'A-01-01', 0),
('9787010001235', '资本论', '马克思', '人民出版社', '2017', 'A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 'zh-CN', 1500, 198.00, '《资本论》是马克思创作的政治经济学著作，是马克思主义最厚重、最丰富的著作，被誉为"工人阶级的圣经"。', '可借', 5, 3, '主馆2楼A区', 'A-01-02', 0),
('9787010001236', '毛泽东选集', '毛泽东', '人民出版社', '2019', 'A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 'zh-CN', 800, 128.00, '《毛泽东选集》是毛泽东思想的重要载体，收录了毛泽东在中国新民主主义革命时期的主要著作。', '可借', 8, 6, '主馆2楼A区', 'A-01-03', 0);

-- 哲学类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787100012345', '西方哲学史', '罗素', '商务印书馆', '2016', 'B', '哲学、宗教', 'zh-CN', 850, 98.00, '《西方哲学史》是英国哲学家罗素的代表作，全面系统地介绍了西方哲学的发展历程。', '可借', 6, 4, '主馆2楼B区', 'B-01-01', 0),
('9787100012346', '中国哲学史', '冯友兰', '华东师范大学出版社', '2018', 'B', '哲学、宗教', 'zh-CN', 720, 88.00, '《中国哲学史》是冯友兰先生的代表作，系统论述了中国哲学的发展历程。', '可借', 7, 5, '主馆2楼B区', 'B-01-02', 0),
('9787100012347', '理想国', '柏拉图', '商务印书馆', '2017', 'B', '哲学、宗教', 'zh-CN', 450, 45.00, '《理想国》是古希腊哲学家柏拉图的代表作，是西方政治哲学的经典之作。', '可借', 8, 6, '主馆2楼B区', 'B-01-03', 0);

-- 经济类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787300123456', '经济学原理', '曼昆', '北京大学出版社', '2019', 'F', '经济', 'zh-CN', 680, 78.00, '《经济学原理》是美国经济学家曼昆的代表作，是世界上最流行的经济学入门教材。', '可借', 15, 12, '主馆2楼F区', 'F-01-01', 0),
('9787300123457', '国富论', '亚当·斯密', '商务印书馆', '2016', 'F', '经济', 'zh-CN', 920, 108.00, '《国富论》是英国经济学家亚当·斯密的代表作，是现代经济学的奠基之作。', '可借', 10, 7, '主馆2楼F区', 'F-01-02', 0),
('9787300123458', '资本论', '马克思', '人民出版社', '2018', 'F', '经济', 'zh-CN', 1500, 198.00, '《资本论》是马克思创作的政治经济学著作，深刻分析了资本主义生产方式。', '可借', 5, 3, '主馆2楼F区', 'F-01-03', 0);

-- 教育类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787040123456', '教育学', '王道俊', '人民教育出版社', '2019', 'G', '文化、科学、教育、体育', 'zh-CN', 480, 45.00, '《教育学》是师范院校教育学专业的基础教材，系统介绍了教育学的基本理论。', '可借', 20, 15, '主馆2楼G区', 'G-01-01', 0),
('9787040123457', '教育心理学', '皮连生', '人民教育出版社', '2018', 'G', '文化、科学、教育、体育', 'zh-CN', 520, 48.00, '《教育心理学》系统介绍了教育心理学的基本理论和研究方法。', '可借', 18, 14, '主馆2楼G区', 'G-01-02', 0);

-- 语言类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787560012345', '新概念英语1', '亚历山大', '外语教学与研究出版社', '2017', 'H', '语言、文字', 'zh-CN', 280, 38.00, '《新概念英语》是一套经典的英语教材，适合英语初学者使用。', '可借', 30, 25, '主馆2楼H区', 'H-01-01', 0),
('9787560012346', '新概念英语2', '亚历山大', '外语教学与研究出版社', '2017', 'H', '语言、文字', 'zh-CN', 350, 42.00, '《新概念英语》第二册，适合有一定英语基础的学习者。', '可借', 28, 22, '主馆2楼H区', 'H-01-02', 0),
('9787560012347', '现代汉语', '黄伯荣', '高等教育出版社', '2019', 'H', '语言、文字', 'zh-CN', 420, 52.00, '《现代汉语》系统介绍了现代汉语的基本理论和知识。', '可借', 25, 20, '主馆2楼H区', 'H-01-03', 0);

-- 文学类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787020012345', '红楼梦', '曹雪芹', '人民文学出版社', '2018', 'I', '文学', 'zh-CN', 1600, 88.00, '《红楼梦》是中国古典四大名著之首，被誉为中国封建社会的百科全书。', '可借', 20, 15, '主馆2楼I区', 'I-01-01', 0),
('9787020012346', '三国演义', '罗贯中', '人民文学出版社', '2017', 'I', '文学', 'zh-CN', 980, 68.00, '《三国演义》是中国古典四大名著之一，描写了东汉末年至西晋初年的历史风云。', '可借', 18, 14, '主馆2楼I区', 'I-01-02', 0),
('9787020012347', '水浒传', '施耐庵', '人民文学出版社', '2017', 'I', '文学', 'zh-CN', 1200, 72.00, '《水浒传》是中国古典四大名著之一，描写了北宋末年宋江起义的故事。', '可借', 16, 12, '主馆2楼I区', 'I-01-03', 0),
('9787020012348', '西游记', '吴承恩', '人民文学出版社', '2017', 'I', '文学', 'zh-CN', 920, 65.00, '《西游记》是中国古典四大名著之一，讲述了唐僧师徒四人西天取经的故事。', '可借', 22, 18, '主馆2楼I区', 'I-01-04', 0),
('9787532712345', '百年孤独', '马尔克斯', '上海译文出版社', '2019', 'I', '文学', 'zh-CN', 360, 55.00, '《百年孤独》是哥伦比亚作家马尔克斯的代表作，是魔幻现实主义文学的经典之作。', '可借', 12, 9, '主馆2楼I区', 'I-02-01', 0),
('9787532712346', '追风筝的人', '胡赛尼', '上海译文出版社', '2018', 'I', '文学', 'zh-CN', 380, 48.00, '《追风筝的人》是美籍阿富汗作家胡赛尼的处女作，讲述了阿富汗少年阿米尔的成长故事。', '可借', 14, 11, '主馆2楼I区', 'I-02-02', 0);

-- 历史类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787101012345', '史记', '司马迁', '中华书局', '2016', 'K', '历史、地理', 'zh-CN', 3200, 298.00, '《史记》是西汉史学家司马迁撰写的纪传体史书，是中国历史上第一部纪传体通史。', '可借', 8, 6, '主馆2楼K区', 'K-01-01', 0),
('9787101012346', '资治通鉴', '司马光', '中华书局', '2017', 'K', '历史、地理', 'zh-CN', 4800, 398.00, '《资治通鉴》是北宋司马光主编的一部多卷本编年体史书。', '可借', 6, 4, '主馆2楼K区', 'K-01-02', 0),
('9787101012347', '全球通史', '斯塔夫里阿诺斯', '北京大学出版社', '2018', 'K', '历史、地理', 'zh-CN', 1200, 158.00, '《全球通史》是美国历史学家斯塔夫里阿诺斯的代表作，从全球视角审视人类历史。', '可借', 10, 7, '主馆2楼K区', 'K-01-03', 0);

-- 数学类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787040123457', '高等数学', '同济大学', '高等教育出版社', '2019', 'O', '数理科学和化学', 'zh-CN', 480, 42.00, '《高等数学》是理工科大学生的基础教材，系统介绍了微积分的基本理论。', '可借', 40, 32, '主馆2楼O区', 'O-01-01', 0),
('9787040123458', '线性代数', '同济大学', '高等教育出版社', '2019', 'O', '数理科学和化学', 'zh-CN', 280, 28.00, '《线性代数》是理工科大学生的基础教材，介绍了线性代数的基本理论和方法。', '可借', 35, 28, '主馆2楼O区', 'O-01-02', 0),
('9787040123459', '概率论与数理统计', '浙江大学', '高等教育出版社', '2019', 'O', '数理科学和化学', 'zh-CN', 380, 35.00, '《概率论与数理统计》系统介绍了概率论和数理统计的基本理论。', '可借', 38, 30, '主馆2楼O区', 'O-01-03', 0);

-- 计算机类图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `publish_year`, `category`, `category_name`, `language`, `pages`, `price`, `summary`, `status`, `total_copies`, `available_copies`, `location`, `shelf_no`, `deleted`) VALUES
('9787111123456', 'Java核心技术', '霍斯特曼', '机械工业出版社', '2020', 'T', '工业技术', 'zh-CN', 920, 128.00, '《Java核心技术》是Java编程的经典教材，全面介绍了Java语言的核心技术。', '可借', 25, 20, '主馆2楼T区', 'T-01-01', 0),
('9787111123457', 'Python编程：从入门到实践', '马瑟斯', '人民邮电出版社', '2020', 'T', '工业技术', 'zh-CN', 480, 89.00, '《Python编程：从入门到实践》是Python编程的畅销书，适合初学者学习。', '可借', 30, 24, '主馆2楼T区', 'T-01-02', 0),
('9787111123458', '算法导论', '科尔曼', '机械工业出版社', '2019', 'T', '工业技术', 'zh-CN', 780, 138.00, '《算法导论》是计算机算法的经典教材，全面介绍了算法设计和分析的基本方法。', '可借', 20, 16, '主馆2楼T区', 'T-01-03', 0),
('9787111123459', 'JavaScript高级程序设计', '尼古拉斯', '人民邮电出版社', '2020', 'T', '工业技术', 'zh-CN', 920, 129.00, '《JavaScript高级程序设计》是JavaScript编程的经典教材，全面介绍了JavaScript语言。', '可借', 22, 18, '主馆2楼T区', 'T-01-04', 0),
('9787111123460', '深入理解计算机系统', '布莱恩特', '机械工业出版社', '2019', 'T', '工业技术', 'zh-CN', 780, 139.00, '《深入理解计算机系统》从程序员视角全面介绍了计算机系统的基本原理。', '可借', 18, 14, '主馆2楼T区', 'T-01-05', 0);

-- =====================================================
-- 4. 阅览室数据 (reading_room)
-- =====================================================

INSERT INTO `reading_room` (`name`, `location`, `capacity`, `total_seats`, `available_seats`, `open_time`, `close_time`, `description`, `status`, `deleted`) VALUES
('综合阅览室', '主馆2楼', 60, 60, 60, '08:00:00', '22:00:00', '提供综合图书阅览服务，配备电源插座和WiFi，适合长时间自习和阅读。', 1, 0),
('电子阅览室', '主馆3楼', 40, 40, 40, '08:00:00', '21:00:00', '配备电脑终端，提供电子资源检索、在线数据库访问和多媒体学习服务。', 1, 0),
('静音自习室', '主馆4楼', 50, 50, 50, '07:00:00', '22:30:00', '严格静音环境，禁止交谈和电子设备外放，适合深度学习和专注阅读。', 1, 0),
('期刊阅览室', '主馆1楼', 30, 30, 30, '09:00:00', '21:00:00', '提供各类期刊杂志阅览服务，包括学术期刊和大众杂志。', 1, 0),
('参考咨询室', '主馆5楼', 20, 20, 20, '09:00:00', '17:00:00', '提供参考咨询服务，帮助读者查找文献和解答学术问题。', 1, 0);

-- =====================================================
-- 5. 座位数据 (seat)
-- =====================================================

-- 综合阅览室座位
INSERT INTO `seat` (`room_id`, `seat_number`, `row_num`, `col_num`, `seat_type`, `has_power`, `has_lamp`, `status`, `deleted`)
SELECT 1, CONCAT('A-', LPAD(n, 2, '0')), 
       CASE WHEN n <= 10 THEN 1 WHEN n <= 20 THEN 2 WHEN n <= 30 THEN 3 WHEN n <= 40 THEN 4 ELSE 5 END,
       CASE WHEN n <= 10 THEN n WHEN n <= 20 THEN n-10 WHEN n <= 30 THEN n-20 WHEN n <= 40 THEN n-30 ELSE n-40 END,
       CASE WHEN n <= 20 THEN 'POWER' ELSE 'NORMAL' END,
       CASE WHEN n <= 20 THEN 1 ELSE 0 END,
       CASE WHEN n <= 20 THEN 1 ELSE 0 END,
       1, 0
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50 UNION
    SELECT 51 UNION SELECT 52 UNION SELECT 53 UNION SELECT 54 UNION SELECT 55 UNION
    SELECT 56 UNION SELECT 57 UNION SELECT 58 UNION SELECT 59 UNION SELECT 60
) AS numbers;

-- 电子阅览室座位
INSERT INTO `seat` (`room_id`, `seat_number`, `row_num`, `col_num`, `seat_type`, `has_power`, `has_lamp`, `status`, `deleted`)
SELECT 2, CONCAT('PC-', LPAD(n, 2, '0')),
       CASE WHEN n <= 10 THEN 1 WHEN n <= 20 THEN 2 WHEN n <= 30 THEN 3 ELSE 4 END,
       CASE WHEN n <= 10 THEN n WHEN n <= 20 THEN n-10 WHEN n <= 30 THEN n-20 ELSE n-30 END,
       'POWER', 1, 1, 1, 0
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40
) AS numbers;

-- 静音自习室座位
INSERT INTO `seat` (`room_id`, `seat_number`, `row_num`, `col_num`, `seat_type`, `has_power`, `has_lamp`, `status`, `deleted`)
SELECT 3, CONCAT('Q-', LPAD(n, 2, '0')),
       CASE WHEN n <= 10 THEN 1 WHEN n <= 20 THEN 2 WHEN n <= 30 THEN 3 WHEN n <= 40 THEN 4 ELSE 5 END,
       CASE WHEN n <= 10 THEN n WHEN n <= 20 THEN n-10 WHEN n <= 30 THEN n-20 WHEN n <= 40 THEN n-30 ELSE n-40 END,
       'QUIET', 1, 1, 1, 0
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) AS numbers;

-- =====================================================
-- 6. 借阅记录数据 (borrow_record)
-- =====================================================

-- 正常借阅记录
INSERT INTO `borrow_record` (`user_id`, `book_id`, `borrow_date`, `due_date`, `status`, `renew_count`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    DATE_ADD(NOW(), INTERVAL 15 DAY),
    0, 0, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY);

INSERT INTO `borrow_record` (`user_id`, `book_id`, `borrow_date`, `due_date`, `status`, `renew_count`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    DATE_ADD(NOW(), INTERVAL 20 DAY),
    0, 1, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY);

INSERT INTO `borrow_record` (`user_id`, `book_id`, `borrow_date`, `due_date`, `status`, `renew_count`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student003'),
    (SELECT id FROM book WHERE isbn = '9787111123457'),
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    DATE_ADD(NOW(), INTERVAL 25 DAY),
    0, 0, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY);

-- 逾期记录
INSERT INTO `borrow_record` (`user_id`, `book_id`, `borrow_date`, `due_date`, `status`, `renew_count`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student004'),
    (SELECT id FROM book WHERE isbn = '9787040123457'),
    DATE_SUB(NOW(), INTERVAL 45 DAY),
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    2, 0, 0, DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY);

-- 已归还记录
INSERT INTO `borrow_record` (`user_id`, `book_id`, `borrow_date`, `due_date`, `return_date`, `status`, `renew_count`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student005'),
    (SELECT id FROM book WHERE isbn = '9787560012345'),
    DATE_SUB(NOW(), INTERVAL 60 DAY),
    DATE_SUB(NOW(), INTERVAL 30 DAY),
    DATE_SUB(NOW(), INTERVAL 35 DAY),
    1, 0, 0, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY);

-- =====================================================
-- 7. 预约记录数据 (reservation)
-- =====================================================

INSERT INTO `reservation` (`user_id`, `book_id`, `reserve_date`, `expire_date`, `status`, `deleted`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student006'),
    (SELECT id FROM book WHERE isbn = '9787020012346'),
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    0, 0, NOW();

INSERT INTO `reservation` (`user_id`, `book_id`, `reserve_date`, `expire_date`, `status`, `deleted`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student007'),
    (SELECT id FROM book WHERE isbn = '9787020012347'),
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    0, 0, DATE_SUB(NOW(), INTERVAL 2 DAY);

-- =====================================================
-- 8. 通知数据 (notification)
-- =====================================================

INSERT INTO `notification` (`user_id`, `title`, `content`, `type`, `is_read`, `deleted`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    '借阅成功通知',
    '您已成功借阅《红楼梦》，应还日期为2026年4月25日，请按时归还。',
    'BORROW', 0, 0, NOW();

INSERT INTO `notification` (`user_id`, `title`, `content`, `type`, `is_read`, `deleted`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student004'),
    '逾期提醒',
    '您借阅的《高等数学》已逾期15天，请尽快归还。逾期费用：1.50元。',
    'OVERDUE', 0, 0, NOW();

INSERT INTO `notification` (`user_id`, `title`, `content`, `type`, `is_read`, `deleted`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    '到期提醒',
    '您借阅的《Java核心技术》将于3天后到期，请及时归还或续借。',
    'DUE_REMINDER', 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY);

-- =====================================================
-- 9. 图书评价数据 (book_review)
-- =====================================================

INSERT INTO `book_review` (`book_id`, `user_id`, `rating`, `content`, `status`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    (SELECT id FROM user_account WHERE username = 'student001'),
    5,
    '《红楼梦》是中国文学的巅峰之作，每次阅读都有新的感悟。曹雪芹笔下的人物栩栩如生，情节跌宕起伏，值得反复品味。',
    1, NOW(), NOW();

INSERT INTO `book_review` (`book_id`, `user_id`, `rating`, `content`, `status`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    (SELECT id FROM user_account WHERE username = 'student002'),
    4,
    '《Java核心技术》内容全面，讲解详细，适合Java开发者深入学习。不过部分内容有些冗长，需要耐心阅读。',
    1, NOW(), NOW();

INSERT INTO `book_review` (`book_id`, `user_id`, `rating`, `content`, `status`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM book WHERE isbn = '9787111123457'),
    (SELECT id FROM user_account WHERE username = 'student003'),
    5,
    '《Python编程：从入门到实践》非常适合初学者，实例丰富，讲解清晰。跟着书中的项目做下来，收获很大！',
    1, NOW(), NOW();

-- =====================================================
-- 10. 用户收藏数据 (user_favorite)
-- =====================================================

INSERT INTO `user_favorite` (`user_id`, `book_id`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    NOW();

INSERT INTO `user_favorite` (`user_id`, `book_id`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    NOW();

INSERT INTO `user_favorite` (`user_id`, `book_id`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    (SELECT id FROM book WHERE isbn = '9787111123457'),
    NOW();

-- =====================================================
-- 11. 用户行为数据 (user_behavior)
-- =====================================================

INSERT INTO `user_behavior` (`user_id`, `book_id`, `action_type`, `action_detail`, `ip_address`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    'VIEW',
    '查看图书详情',
    '192.168.1.100',
    NOW();

INSERT INTO `user_behavior` (`user_id`, `book_id`, `action_type`, `action_detail`, `ip_address`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    (SELECT id FROM book WHERE isbn = '9787020012345'),
    'BORROW',
    '借阅图书',
    '192.168.1.100',
    NOW();

INSERT INTO `user_behavior` (`user_id`, `book_id`, `action_type`, `action_detail`, `ip_address`, `create_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    (SELECT id FROM book WHERE isbn = '9787111123456'),
    'VIEW',
    '查看图书详情',
    '192.168.1.101',
    NOW();

-- =====================================================
-- 12. 咨询记录数据 (inquiry)
-- =====================================================

INSERT INTO `inquiry` (`user_id`, `title`, `content`, `category`, `status`, `reply_content`, `replier_id`, `reply_date`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student001'),
    '如何续借图书？',
    '请问图书到期后如何续借？续借次数有限制吗？',
    'BORROW',
    1,
    '您好！图书到期前可以在个人中心进行在线续借，每本书最多续借1次，续借期限为30天。如有其他问题，欢迎继续咨询。',
    (SELECT id FROM user_account WHERE username = 'librarian'),
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY);

INSERT INTO `inquiry` (`user_id`, `title`, `content`, `category`, `status`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'student002'),
    '图书馆开放时间',
    '请问图书馆周末开放吗？开放时间是什么时候？',
    'SYSTEM',
    0,
    0, NOW(), NOW();

-- =====================================================
-- 13. 采购申请数据 (purchase_request)
-- =====================================================

INSERT INTO `purchase_request` (`requester_id`, `book_title`, `author`, `isbn`, `publisher`, `reason`, `quantity`, `status`, `deleted`, `create_time`, `update_time`)
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

INSERT INTO `purchase_request` (`requester_id`, `book_title`, `author`, `isbn`, `publisher`, `reason`, `quantity`, `status`, `reviewer_id`, `review_comment`, `deleted`, `create_time`, `update_time`)
SELECT 
    (SELECT id FROM user_account WHERE username = 'teacher002'),
    '深度学习',
    '古德费洛',
    '9787115469457',
    '人民邮电出版社',
    '深度学习领域的权威著作，对科研工作很有帮助。',
    3,
    1,
    (SELECT id FROM user_account WHERE username = 'admin'),
    '同意采购，已列入采购计划。',
    0, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW();

-- =====================================================
-- 数据插入完成
-- =====================================================

SELECT '模拟数据插入完成！' AS '执行结果';
SELECT '用户数据: 约30条' AS '统计';
SELECT '图书分类: 约50条' AS '统计';
SELECT '图书数据: 约30条' AS '统计';
SELECT '阅览室: 5个' AS '统计';
SELECT '座位: 约150个' AS '统计';
SELECT '借阅记录: 约5条' AS '统计';
SELECT '预约记录: 约2条' AS '统计';
SELECT '通知: 约3条' AS '统计';
SELECT '图书评价: 约3条' AS '统计';
SELECT '用户收藏: 约3条' AS '统计';
SELECT '用户行为: 约3条' AS '统计';
SELECT '咨询记录: 约2条' AS '统计';
SELECT '采购申请: 约2条' AS '统计';
