-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库(如果不存在)
CREATE DATABASE IF NOT EXISTS CityLibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE CityLibrary;

-- 设置数据库字符集
SET CHARACTER SET utf8mb4;

-- 用户账户表
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `user_type` VARCHAR(20) NOT NULL DEFAULT 'READER' COMMENT '用户类型:READER/STUDENT/TEACHER/INTERNATIONAL',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
  `id_card` VARCHAR(30) DEFAULT NULL COMMENT '身份证件号',
  `student_id` VARCHAR(20) DEFAULT NULL COMMENT '学生学号',
  `faculty_id` VARCHAR(20) DEFAULT NULL COMMENT '教职工号',
  `user_code` VARCHAR(20) DEFAULT NULL COMMENT '用户编号',
  `campus` VARCHAR(100) DEFAULT NULL COMMENT '校区',
  `college` VARCHAR(100) DEFAULT NULL COMMENT '院系',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
  `counselor` VARCHAR(50) DEFAULT NULL COMMENT '辅导员',
  `institution` VARCHAR(100) DEFAULT NULL COMMENT '所属机构',
  `role` VARCHAR(20) NOT NULL DEFAULT 'ROLE_READER' COMMENT '系统角色',
  `language` VARCHAR(10) NOT NULL DEFAULT 'zh-CN' COMMENT '界面语言',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  UNIQUE KEY `uk_faculty_id` (`faculty_id`),
  UNIQUE KEY `uk_user_code` (`user_code`),
  KEY `idx_campus` (`campus`),
  KEY `idx_college` (`college`),
  KEY `idx_grade` (`grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表';

-- 图书表
CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN号',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
  `title_en` VARCHAR(200) DEFAULT NULL COMMENT '英文书名',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `author_en` VARCHAR(100) DEFAULT NULL COMMENT '英文作者',
  `translator` VARCHAR(100) DEFAULT NULL COMMENT '译者',
  `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
  `publish_year` VARCHAR(10) DEFAULT NULL COMMENT '出版年份',
  `edition` VARCHAR(50) DEFAULT NULL COMMENT '版次',
  `category` VARCHAR(50) NOT NULL COMMENT '分类代码',
  `category_name` VARCHAR(50) DEFAULT NULL COMMENT '分类名称',
  `language` VARCHAR(20) NOT NULL DEFAULT 'zh-CN' COMMENT '文献语言',
  `pages` INT DEFAULT NULL COMMENT '页数',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
  `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `summary` TEXT DEFAULT NULL COMMENT '内容简介',
  `summary_en` TEXT DEFAULT NULL COMMENT '英文简介',
  `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态:available-可借,borrowed-已借出,reserved-预约,maintenance-维修,deleted-注销',
  `total_copies` INT NOT NULL DEFAULT 1 COMMENT '总馆藏数量',
  `available_copies` INT NOT NULL DEFAULT 1 COMMENT '可借数量',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '馆藏位置',
  `shelf_no` VARCHAR(50) DEFAULT NULL COMMENT '书架号',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_author` (`author`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书信息表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS `borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `book_barcode` VARCHAR(50) DEFAULT NULL COMMENT '图书条码',
  `borrow_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
  `due_date` DATETIME NOT NULL COMMENT '应还日期',
  `return_date` DATETIME DEFAULT NULL COMMENT '实际归还日期',
  `renew_count` INT NOT NULL DEFAULT 0 COMMENT '续借次数',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-借阅中,1-已归还,2-逾期,3-丢失',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  KEY `idx_borrow_date` (`borrow_date`),
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS `reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `reserve_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约日期',
  `expire_date` DATETIME NOT NULL COMMENT '预约过期日期',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-等待中,1-已通知,2-已取消,3-已失效',
  `notify_date` DATETIME DEFAULT NULL COMMENT '通知日期',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_reserve_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reserve_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';

-- 罚款记录表
CREATE TABLE IF NOT EXISTS `fine_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `borrow_record_id` BIGINT DEFAULT NULL COMMENT '借阅记录ID',
  `fine_type` VARCHAR(20) NOT NULL COMMENT '罚款类型:OVERDUE-逾期,DAMAGE-损坏,LOSS-丢失',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '罚款金额',
  `paid_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态:0-未支付,1-已支付',
  `paid_date` DATETIME DEFAULT NULL COMMENT '支付日期',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_borrow_record_id` (`borrow_record_id`),
  CONSTRAINT `fk_fine_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fine_borrow` FOREIGN KEY (`borrow_record_id`) REFERENCES `borrow_record` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='罚款记录表';

-- 咨询记录表
CREATE TABLE IF NOT EXISTS `inquiry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '提问用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '咨询标题',
  `content` TEXT NOT NULL COMMENT '咨询内容',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '咨询分类',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待回复,1-已回复,2-已关闭',
  `reply_content` TEXT DEFAULT NULL COMMENT '回复内容',
  `replier_id` BIGINT DEFAULT NULL COMMENT '回复人ID',
  `reply_date` DATETIME DEFAULT NULL COMMENT '回复日期',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_inquiry_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
  `config_type` VARCHAR(50) DEFAULT NULL COMMENT '配置类型',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `method` VARCHAR(100) DEFAULT NULL COMMENT '方法名',
  `params` TEXT DEFAULT NULL COMMENT '请求参数',
  `result` TEXT DEFAULT NULL COMMENT '返回结果',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `status` TINYINT DEFAULT NULL COMMENT '操作状态:0-失败,1-成功',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `execute_time` BIGINT DEFAULT NULL COMMENT '执行时长(ms)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_operation` (`operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` TEXT DEFAULT NULL COMMENT '通知内容',
  `type` VARCHAR(50) NOT NULL COMMENT '通知类型:SYSTEM/BORROW/RESERVATION/INQUIRY/FINE',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读:0-未读,1-已读',
  `channel` VARCHAR(20) NOT NULL DEFAULT 'IN_APP' COMMENT '发送渠道:IN_APP/SMS/EMAIL',
  `related_id` BIGINT DEFAULT NULL COMMENT '关联业务ID',
  `related_type` VARCHAR(30) DEFAULT NULL COMMENT '关联业务类型',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_user_status` (`user_id`, `is_read`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 通知模板表
CREATE TABLE IF NOT EXISTS `notification_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` VARCHAR(100) NOT NULL COMMENT '模板代码',
  `channel` VARCHAR(20) NOT NULL DEFAULT 'IN_APP' COMMENT '发送渠道:IN_APP/SMS/EMAIL',
  `title` VARCHAR(200) NOT NULL COMMENT '模板标题',
  `content` TEXT DEFAULT NULL COMMENT '模板内容',
  `variables` VARCHAR(500) DEFAULT NULL COMMENT '模板变量说明',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
  `type` VARCHAR(50) NOT NULL COMMENT '通知类型',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_channel` (`channel`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- 阅览室表
CREATE TABLE IF NOT EXISTS `reading_room` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '阅览室名称',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '位置',
  `capacity` INT NOT NULL DEFAULT 0 COMMENT '容量',
  `open_time` TIME DEFAULT NULL COMMENT '开放时间',
  `close_time` TIME DEFAULT NULL COMMENT '关闭时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-关闭,1-开放',
  `total_seats` INT NOT NULL DEFAULT 0 COMMENT '总座位数',
  `available_seats` INT NOT NULL DEFAULT 0 COMMENT '可用座位数',
  `description` TEXT DEFAULT NULL COMMENT '描述说明',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '阅览室图片',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅览室表';

-- 座位表
CREATE TABLE IF NOT EXISTS `seat` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` BIGINT NOT NULL COMMENT '阅览室ID',
  `seat_number` VARCHAR(20) NOT NULL COMMENT '座位号',
  `seat_type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '座位类型:NORMAL-普通,POWER-带电源,QUIET-静音区,GROUP-小组讨论',
  `has_power` TINYINT NOT NULL DEFAULT 0 COMMENT '是否有电源:0-否,1-是',
  `has_lamp` TINYINT NOT NULL DEFAULT 0 COMMENT '是否有台灯:0-否,1-是',
  `row_num` INT DEFAULT NULL COMMENT '行号',
  `col_num` INT DEFAULT NULL COMMENT '列号',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-可用,1-已预约,2-使用中,3-维护中',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_seat` (`room_id`, `seat_number`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_seat_type` (`seat_type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_seat_room` FOREIGN KEY (`room_id`) REFERENCES `reading_room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='座位表';

-- 座位预约表
CREATE TABLE IF NOT EXISTS `seat_reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `seat_id` BIGINT NOT NULL COMMENT '座位ID',
  `room_id` BIGINT NOT NULL COMMENT '阅览室ID',
  `reserve_date` DATE NOT NULL COMMENT '预约日期',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-已预约,1-已签到,2-已完成,3-已取消,4-违约',
  `check_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
  `check_out_time` DATETIME DEFAULT NULL COMMENT '签退时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seat_id` (`seat_id`),
  KEY `idx_reserve_date` (`reserve_date`),
  KEY `idx_status` (`status`),
  KEY `idx_room_date` (`room_id`, `reserve_date`),
  CONSTRAINT `fk_seat_res_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_seat_res_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_seat_reservation_room` FOREIGN KEY (`room_id`) REFERENCES `reading_room` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='座位预约表';

-- 采购申请表
CREATE TABLE IF NOT EXISTS `purchase_request` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `requester_id` BIGINT NOT NULL COMMENT '申请人ID',
  `book_title` VARCHAR(200) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
  `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '申请数量',
  `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
  `reason` TEXT DEFAULT NULL COMMENT '申请理由',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待审核,1-已批准,2-已拒绝,3-已完成',
  `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回理由',
  `remarks` TEXT DEFAULT NULL COMMENT '备注',
  `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `review_comment` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_requester_id` (`requester_id`),
  KEY `idx_status` (`status`),
  KEY `idx_isbn` (`isbn`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_purchase_user` FOREIGN KEY (`requester_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购申请表';

-- 导出任务表
CREATE TABLE IF NOT EXISTS `export_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '导出类型:BOOK_EXPORT/BORROW_STATISTICS/USER_STATISTICS',
  `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `format` VARCHAR(10) NOT NULL DEFAULT 'XLSX' COMMENT '导出格式:XLSX/PDF/CSV',
  `query_params` TEXT DEFAULT NULL COMMENT '查询参数(JSON)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING/PROCESSING/COMPLETED/FAILED',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_export_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导出任务表';

-- 图书分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `code` VARCHAR(20) NOT NULL COMMENT '分类编码',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级分类ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- 图书采编任务表
CREATE TABLE IF NOT EXISTS `acquisition_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT DEFAULT NULL COMMENT '图书ID',
  `book_title` VARCHAR(200) DEFAULT NULL COMMENT '书名',
  `book_isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN',
  `type` VARCHAR(20) NOT NULL COMMENT '采编类型:stock_in-入库,transfer-调拨,discard-报废',
  `source` VARCHAR(100) DEFAULT NULL COMMENT '来源',
  `assignee` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态:pending-待处理,processing-处理中,completed-已完成,discarded-已报废',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作员姓名',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书采编任务表';

-- 图书入库批次表
CREATE TABLE IF NOT EXISTS `book_import_batch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` VARCHAR(50) NOT NULL COMMENT '批次号',
  `operator_id` BIGINT NOT NULL COMMENT '操作员ID',
  `total_count` INT NOT NULL DEFAULT 0 COMMENT '总数量',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数量',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待处理,1-处理中,2-已完成',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_operator_id` (`operator_id`),
  CONSTRAINT `fk_batch_user` FOREIGN KEY (`operator_id`) REFERENCES `user_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书入库批次表';

-- 图书导入明细表
CREATE TABLE IF NOT EXISTS `book_import_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `row_number` INT NOT NULL COMMENT 'Excel行号',
  `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '书名',
  `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
  `status` VARCHAR(20) NOT NULL COMMENT '状态:SUCCESS-成功,FAILED-失败,SKIPPED-跳过',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `book_id` BIGINT DEFAULT NULL COMMENT '关联图书ID(成功时)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_batch_status` (`batch_id`, `status`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_isbn` (`isbn`),
  CONSTRAINT `fk_import_detail_batch` FOREIGN KEY (`batch_id`) REFERENCES `book_import_batch` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_import_detail_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书导入明细表';

-- 图书评价表
CREATE TABLE IF NOT EXISTS `book_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `rating` TINYINT NOT NULL COMMENT '评分1-5星',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0-隐藏,1-显示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_user` (`book_id`, `user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_review_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating` CHECK (`rating` >= 1 AND `rating` <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书评价表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 用户行为表
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT DEFAULT NULL COMMENT '图书ID',
  `action_type` VARCHAR(20) NOT NULL COMMENT '行为类型:VIEW/BORROW/RETURN/SEARCH/FAVORITE',
  `action_detail` VARCHAR(500) DEFAULT NULL COMMENT '行为详情',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_action` (`user_id`, `action_type`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_behavior_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_behavior_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- 图书推荐表
CREATE TABLE IF NOT EXISTS `book_recommendation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `score` DECIMAL(5,2) DEFAULT NULL COMMENT '推荐分数',
  `reason` VARCHAR(500) DEFAULT NULL COMMENT '推荐理由',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_recommendation_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_recommendation_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书推荐表';

-- 初始化管理员账户
INSERT INTO `user_account` (`username`, `password`, `user_type`, `real_name`, `role`, `status`, `deleted`)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqBqC3N3GxBfFM7t1V5F5X5pqMw0k3y', 'ADMIN', '系统管理员', 'ADMIN', 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `user_account` WHERE `username` = 'admin');

-- 初始化系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`) 
VALUES 
('borrow_days', '30', 'SYSTEM', '默认借阅天数'),
('renew_limit', '3', 'SYSTEM', '续借次数上限'),
('fine_per_day', '0.1', 'SYSTEM', '每日罚款金额'),
('max_borrow_count', '10', 'SYSTEM', '最大借阅数量'),
('reservation_expire_days', '7', 'SYSTEM', '预约过期天数')
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`);

-- 初始化通知模板
INSERT INTO `notification_template` (`code`, `title`, `content`, `type`, `channel`) 
VALUES 
('BORROW_SUCCESS', '借阅成功通知', '您已成功借阅《{bookTitle}》,应还日期为{dueDate}。', 'BORROW', 'IN_APP'),
('RETURN_SUCCESS', '归还成功通知', '您已成功归还《{bookTitle}》。', 'RETURN', 'IN_APP'),
('DUE_REMINDER', '到期提醒', '您借阅的《{bookTitle}》将于{dueDate}到期,请及时归还。', 'DUE_REMINDER', 'IN_APP'),
('OVERDUE_NOTICE', '逾期通知', '您借阅的《{bookTitle}》已逾期{overdueDays}天,请尽快归还。', 'OVERDUE', 'IN_APP')
ON DUPLICATE KEY UPDATE `content` = VALUES(`content`);

-- 初始化图书分类
INSERT INTO `category` (`name`, `code`, `parent_id`, `sort_order`, `description`) 
VALUES 
('马克思主义、列宁主义、毛泽东思想、邓小平理论', 'A', NULL, 1, 'A类:马克思主义、列宁主义、毛泽东思想、邓小平理论'),
('哲学、宗教', 'B', NULL, 2, 'B类:哲学、宗教'),
('社会科学总论', 'C', NULL, 3, 'C类:社会科学总论'),
('政治、法律', 'D', NULL, 4, 'D类:政治、法律'),
('军事', 'E', NULL, 5, 'E类:军事'),
('经济', 'F', NULL, 6, 'F类:经济'),
('文化、科学、教育、体育', 'G', NULL, 7, 'G类:文化、科学、教育、体育'),
('语言、文字', 'H', NULL, 8, 'H类:语言、文字'),
('文学', 'I', NULL, 9, 'I类:文学'),
('艺术', 'J', NULL, 10, 'J类:艺术'),
('历史、地理', 'K', NULL, 11, 'K类:历史、地理'),
('自然科学总论', 'N', NULL, 12, 'N类:自然科学总论'),
('数理科学和化学', 'O', NULL, 13, 'O类:数理科学和化学'),
('天文学、地球科学', 'P', NULL, 14, 'P类:天文学、地球科学'),
('生物科学', 'Q', NULL, 15, 'Q类:生物科学'),
('医药、卫生', 'R', NULL, 16, 'R类:医药、卫生'),
('农业科学', 'S', NULL, 17, 'S类:农业科学'),
('工业技术', 'T', NULL, 18, 'T类:工业技术'),
('交通运输', 'U', NULL, 19, 'U类:交通运输'),
('航空、航天', 'V', NULL, 20, 'V类:航空、航天'),
('环境科学、安全科学', 'X', NULL, 21, 'X类:环境科学、安全科学'),
('综合性图书', 'Z', NULL, 22, 'Z类:综合性图书')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
