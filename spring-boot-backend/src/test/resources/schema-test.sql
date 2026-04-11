CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20),
    real_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    id_card VARCHAR(20),
    institution VARCHAR(200),
    role VARCHAR(20) DEFAULT 'USER',
    language VARCHAR(10) DEFAULT 'zh_CN',
    status INT DEFAULT 1,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE,
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    description VARCHAR(500),
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20),
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    title_en VARCHAR(500),
    author VARCHAR(200),
    author_en VARCHAR(200),
    translator VARCHAR(200),
    publisher VARCHAR(200),
    publish_year VARCHAR(10),
    edition VARCHAR(50),
    category VARCHAR(50),
    category_name VARCHAR(100),
    language VARCHAR(20) DEFAULT 'zh',
    pages INT,
    price DECIMAL(10,2),
    tags VARCHAR(500),
    cover_url VARCHAR(500),
    summary TEXT,
    summary_en TEXT,
    status VARCHAR(20) DEFAULT 'available',
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    location VARCHAR(100),
    shelf_no VARCHAR(50),
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    book_barcode VARCHAR(50),
    borrow_date DATETIME NOT NULL,
    due_date DATETIME NOT NULL,
    return_date DATETIME,
    renew_count INT DEFAULT 0,
    status INT DEFAULT 0,
    operator_id BIGINT,
    remarks VARCHAR(500),
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reserve_date DATETIME NOT NULL,
    expire_date DATETIME,
    status INT DEFAULT 0,
    notify_date DATETIME,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(50),
    is_read INT DEFAULT 0,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fine_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    borrow_record_id BIGINT,
    fine_type VARCHAR(50),
    amount DECIMAL(10,2),
    paid_status INT DEFAULT 0,
    paid_date DATETIME,
    operator_id BIGINT,
    remarks VARCHAR(500),
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(100),
    module VARCHAR(50),
    method VARCHAR(200),
    params TEXT,
    result TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    status INT,
    error_message TEXT,
    execute_time BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(100) NOT NULL,
    title_template VARCHAR(200),
    content_template TEXT,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inquiry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    reply TEXT,
    status INT DEFAULT 0,
    reply_time DATETIME,
    reply_user_id BIGINT,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reading_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    capacity INT DEFAULT 0,
    open_time VARCHAR(20),
    close_time VARCHAR(20),
    status INT DEFAULT 1,
    description VARCHAR(500),
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    status INT DEFAULT 1,
    has_power INT DEFAULT 0,
    position_x INT,
    position_y INT,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seat_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    reserve_date DATE NOT NULL,
    start_time VARCHAR(10) NOT NULL,
    end_time VARCHAR(10) NOT NULL,
    status INT DEFAULT 0,
    check_in_time DATETIME,
    check_out_time DATETIME,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchase_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    isbn VARCHAR(20),
    title VARCHAR(500) NOT NULL,
    author VARCHAR(200),
    publisher VARCHAR(200),
    reason TEXT,
    status INT DEFAULT 0,
    reply TEXT,
    reply_user_id BIGINT,
    reply_time DATETIME,
    deleted INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS book_import_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(50) NOT NULL UNIQUE,
    file_name VARCHAR(200),
    total_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    fail_count INT DEFAULT 0,
    status INT DEFAULT 0,
    operator_id BIGINT,
    error_message TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS export_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500),
    status INT DEFAULT 0,
    total_count INT DEFAULT 0,
    processed_count INT DEFAULT 0,
    operator_id BIGINT,
    error_message TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS acquisition_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    source VARCHAR(100),
    status INT DEFAULT 0,
    total_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    fail_count INT DEFAULT 0,
    operator_id BIGINT,
    error_message TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
