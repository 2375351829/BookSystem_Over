# 城市图书馆管理系统 - 项目文档

> 项目名称: CityLibrarySystem | 版本: 1.0.0 | 更新日期: 2026-04-23

---

## 目录

- [第一章：项目概述](#第一章项目概述)
- [第二章：软件架构设计](#第二章软件架构设计)
- [第三章：功能模块结构说明](#第三章功能模块结构说明)
- [第四章：数据库ER图及设计说明](#第四章数据库er图及设计说明)
- [第五章：核心业务流程说明](#第五章核心业务流程说明)
- [第六章：接口文档](#第六章接口文档)
- [第七章：系统部署架构说明](#第七章系统部署架构说明)

---

## 第一章：项目概述

### 1.1 项目名称

**城市图书馆管理系统 (CityLibrarySystem)**

### 1.2 项目简介

城市图书馆管理系统是一套面向城市公共图书馆/高校图书馆的现代化、智能化图书管理平台。系统提供从图书采编、流通管理、读者服务到数据统计分析的全流程数字化管理解决方案，支持多校区、多角色、多语言的复杂业务场景。

### 1.3 核心价值

| 价值维度 | 说明 |
|----------|------|
| 全流程数字化 | 涵盖图书采编、入库、借阅、归还、预约、罚款等完整业务链 |
| 多角色权限体系 | 支持管理员、普通读者、学生、教师、国际读者等多种角色，细粒度权限控制 |
| 智能座位预约 | 提供阅览室管理、座位预约、签到签退、违约处理等完整功能 |
| 数据驱动决策 | 仪表盘、统计报表、借阅趋势、用户行为分析 |
| 多渠道通知 | 站内消息、通知模板、借阅到期提醒、逾期通知 |
| 国际化支持 | 多语言界面（中文/英文）、多语言图书管理 |

### 1.4 技术栈

#### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.0 | 后端框架 |
| Spring Security | - | 安全认证 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL Connector/J | 8.0.33 | 数据库驱动 |
| JWT (jjwt) | 0.12.5 | 令牌认证 |
| Lombok | 1.18.30 | 代码简化 |
| Apache POI | 5.2.3 | Excel导出 |
| iText PDF | 5.5.13.3 | PDF生成 |
| Commons Lang3 | 3.12.0 | 工具库 |
| H2 Database | - | 测试数据库 |

#### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 3.5.30 | 前端框架 |
| TypeScript | 5.9.2 | 类型系统 |
| Element Plus | 2.13.6 | UI组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由管理 |
| Vite | 4.5.0 | 构建工具 |
| Axios | 1.14.0 | HTTP客户端 |
| ECharts | 5.6.0 | 数据可视化 |
| Vue I18n | 9.14.5 | 国际化 |
| XLSX | 0.18.5 | Excel处理 |

#### 运行环境

| 环境 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 16+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |

---

## 第二章：软件架构设计

### 2.1 系统分层架构

系统采用经典的分层架构，自上而下分为以下层次：

```
┌─────────────────────────────────────────────────┐
│                  表现层 (Presentation)            │
│  Vue 3 + Element Plus + TypeScript + Vite        │
├─────────────────────────────────────────────────┤
│                  接口层 (Controller)              │
│  RESTful API + 参数校验 + 统一响应               │
├─────────────────────────────────────────────────┤
│                  业务层 (Service)                 │
│  业务逻辑 + 事务管理 + 通知推送                  │
├─────────────────────────────────────────────────┤
│                  数据访问层 (Repository)           │
│  MyBatis-Plus Mapper + 条件构造器                 │
├─────────────────────────────────────────────────┤
│                  模型层 (Model)                   │
│  实体类 + DTO + 枚举 + 注解                      │
├─────────────────────────────────────────────────┤
│                  数据库层 (Database)              │
│  MySQL 8.0 + utf8mb4 + InnoDB                    │
└─────────────────────────────────────────────────┘
```

### 2.2 前后端分离架构

```
┌──────────────┐         HTTPS/HTTP          ┌──────────────┐
│   前端应用    │  ────────────────────────►  │   后端服务    │
│  Vue 3 SPA   │  ◄────────────────────────  │ Spring Boot  │
│  :5173       │      JSON 数据交换           │  :8080       │
└──────────────┘                             └──────────────┘
                                                    │
                                                    ▼
                                              ┌──────────────┐
                                              │    MySQL      │
                                              │   :3306       │
                                              └──────────────┘
```

| 特性 | 前端 | 后端 |
|------|------|------|
| 端口 | 5173 (Vite Dev) | 8080 (Spring Boot) |
| 跨域 | Axios 拦截器 | CORS 配置 |
| 认证 | JWT Token (localStorage) | Spring Security + JWT Filter |
| 路由 | Vue Router (History模式) | @RequestMapping 映射 |

### 2.3 技术选型依据

| 技术 | 选型理由 |
|------|----------|
| Spring Boot 3.x | 成熟的Java生态，约定优于配置，丰富的Starter组件 |
| MyBatis-Plus | 相比JPA更灵活，适合复杂SQL查询，内置分页和CRUD增强 |
| Vue 3 + Composition API | 响应式性能优秀，组合式API逻辑复用能力强 |
| Element Plus | 企业级组件库，与Vue 3深度适配，组件丰富 |
| JWT 无状态认证 | 适合前后端分离架构，支持水平扩展，双Token机制保证安全性 |
| MySQL 8.0 | 成熟稳定的关系型数据库，JSON支持、窗口函数等特性 |

### 2.4 设计原则

| 原则 | 说明 | 项目中的体现 |
|------|------|-------------|
| SOLID | 单一职责、开闭原则等 | Controller只负责HTTP交互，Service处理业务逻辑 |
| DRY | 避免重复代码 | 统一响应格式、全局异常处理、公共Mapper方法 |
| KISS | 保持简单 | 使用MyBatis-Plus简化CRUD，避免过度设计 |

### 2.5 安全架构

```
┌─────────────────────────────────────────────────────┐
│                    安全架构                          │
├────────────┬────────────┬────────────┬──────────────┤
│  JWT认证   │ Spring     │ BCrypt     │ 操作日志      │
│  无状态    │ Security   │ 密码加密   │ AOP切面       │
│  双Token   │ 角色权限   │ 加盐哈希   │ 审计追踪      │
└────────────┴────────────┴────────────┴──────────────┘
```

| 安全机制 | 实现方式 | 说明 |
|----------|----------|------|
| JWT认证 | jjwt 0.12.5 | Access Token (24h) + Refresh Token |
| Spring Security | JwtAuthenticationFilter | 请求拦截、Token验证、权限校验 |
| 密码加密 | BCryptPasswordEncoder | 加盐哈希，防止彩虹表攻击 |
| 操作日志 | @OperationLog 注解 + AOP | 记录用户操作、IP、耗时、结果 |
| 逻辑删除 | deleted字段 | 数据安全，支持恢复 |
| SQL注入防护 | MyBatis-Plus 参数化查询 | 防止SQL注入 |

### 2.6 认证机制

**基于JWT的无状态认证，双令牌机制**

| 令牌类型 | 有效期 | 用途 |
|----------|--------|------|
| Access Token | 24小时 | 日常API请求认证 |
| Refresh Token | 30天（可配置） | 获取新的Access Token，避免频繁登录 |

**认证流程：**
1. 用户登录 → 验证账号密码 → 生成 Access Token + Refresh Token
2. Refresh Token 存储于数据库 `refresh_token` 表
3. 后续请求携带 Access Token (Bearer认证)
4. Token过期后使用 Refresh Token 获取新 Access Token
5. 登出时销毁 Refresh Token

---

## 第三章：功能模块结构说明

### 3.1 Controller层（22个控制器）

| 控制器 | 路径 | 职责 |
|--------|------|------|
| AuthController | `/api/auth` | 用户认证：登录、注册、验证码、Token刷新、登出 |
| UserController | `/api/users` | 用户管理：CRUD、批量操作、权限管理、用户导入 |
| BookController | `/api/books` | 图书管理：CRUD、搜索、高级搜索、分类、新书 |
| BorrowController | `/api/borrow` | 借阅管理：借阅、归还、续借、我的借阅记录 |
| BorrowRuleController | `/api/borrow-rules` | 借阅规则：规则查询、管理（按用户类型差异化） |
| ReservationController | `/api/reservations` | 图书预约：预约、取消、我的预约、通知 |
| SeatController | `/api/seats` | 座位预约：阅览室管理、座位预约、签到签退 |
| FineController | `/api/fines` | 罚款管理：罚款查询、支付、统计 |
| FaqController | `/api/faq` | 常见问题：FAQ列表、分类、搜索 |
| StatisticsController | `/api/statistics` | 统计报表：仪表盘、借阅统计、用户统计 |
| AnnouncementController | `/api/announcements` | 公告管理：发布、查看、管理公告 |
| CategoryController | `/api/categories` | 分类管理：图书分类CRUD |
| RecommendationController | `/api/recommendations` | 推荐服务：相似图书推荐 |
| ReviewController | `/api/reviews` | 图书评价：评分、评价CRUD |
| ExportController | `/api/export` | 数据导出：Excel/PDF导出、任务管理 |
| UploadController | `/api/upload` | 文件上传：封面图片上传 |
| LogController | `/api/logs` | 操作日志：日志查询、筛选 |
| LogStreamController | `/api/logs/stream` | 实时日志流：WebSocket推送日志 |
| LibraryLocationController | `/api/library-locations` | 馆藏位置：位置查询、管理 |
| IsbnController | `/api/isbn` | ISBN查询：ISBN信息获取 |
| HealthController | `/api/health` | 健康检查：系统状态检测 |
| StatusController | `/api/status` | 状态码字典：统一状态码查询 |

### 3.2 Service层（20个服务）

| 服务 | 职责 |
|------|------|
| UserService | 用户管理：CRUD、密码管理、批量操作、导入 |
| BookService | 图书服务：CRUD、分页查询、状态管理 |
| BorrowService | 借阅服务：借阅、归还、续借、逾期处理 |
| BorrowRuleService | 借阅规则服务：规则初始化、按用户类型获取规则 |
| ReservationService | 预约服务：预约创建、取消、超时处理 |
| SeatService | 座位服务：阅览室/座位管理、预约、签到签退、违约处理 |
| FineRecordService | 罚款服务：罚款创建、查询、支付 |
| NotificationService | 通知服务：发送通知、模板渲染、标记已读 |
| FaqService | FAQ服务：常见问题CRUD、分类、搜索 |
| LogService | 日志服务：操作日志分页查询、筛选 |
| ExportService | 导出服务：Excel/PDF生成、任务管理 |
| BookReviewService | 评价服务：评分、评价CRUD、平均分统计 |
| UserBehaviorService | 用户行为服务：行为记录、统计分析 |
| BookRecommendationService | 推荐服务：基于借阅历史的相似推荐 |
| LibraryLocationService | 馆藏位置服务：位置CRUD、图书分布 |
| RefreshTokenService | 刷新令牌服务：Token创建、验证、销毁 |
| UserDetailsServiceImpl | Spring Security用户详情加载 |
| AnnouncementService | 公告服务：公告CRUD、发布管理 |
| BookImportDetailService | 图书导入服务：批量导入明细管理 |
| PurchaseRequestService | 采购申请服务：申请提交、审核 |

### 3.3 Model实体层（25个实体）

| 实体 | 表名 | 说明 |
|------|------|------|
| UserAccount | `user_account` | 用户账户 |
| Book | `book` | 图书信息 |
| BorrowRecord | `borrow_record` | 借阅记录 |
| Reservation | `reservation` | 图书预约 |
| FineRecord | `fine_record` | 罚款记录 |
| Inquiry | `inquiry` | 咨询记录 |
| SystemConfig | `system_config` | 系统配置 |
| OperationLog | `operation_log` | 操作日志 |
| Notification | `notification` | 通知 |
| NotificationTemplate | `notification_template` | 通知模板 |
| ReadingRoom | `reading_room` | 阅览室 |
| Seat | `seat` | 座位 |
| SeatReservation | `seat_reservation` | 座位预约 |
| PurchaseRequest | `purchase_request` | 采购申请 |
| ExportTask | `export_task` | 导出任务 |
| Category | `category` | 图书分类 |
| AcquisitionTask | `acquisition_task` | 图书采编任务 |
| BookImportBatch | `book_import_batch` | 图书入库批次 |
| BookImportDetail | `book_import_detail` | 图书导入明细 |
| BookReview | `book_review` | 图书评价 |
| Favorite | `user_favorite` | 用户收藏 |
| UserBehavior | `user_behavior` | 用户行为 |
| BookRecommendation | `book_recommendation` | 图书推荐 |
| Faq | `faq` | 常见问题 |
| Announcement | `announcement` | 公告 |
| RefreshToken | `refresh_token` | 刷新令牌 |
| LibraryLocation | `library_location` | 馆藏位置 |
| BorrowRule | `borrow_rule` | 借阅规则 |

**枚举类型：**
| 枚举 | 说明 |
|------|------|
| UserType | 用户类型：READER/STUDENT/TEACHER/INTERNATIONAL |
| FineType | 罚款类型：OVERDUE/DAMAGE/LOSS |
| FineStatus | 罚款状态：UNPAID/PAID |

### 3.4 模块间交互关系

```
AuthController ───────────────────────────────────────────────────┐
    │                                                              │
    ▼                                                              ▼
UserController ──► UserService ──► UserAccountMapper          RefreshTokenService
    │                                                              ▲
    ▼                                                              │
BookController ──► BookService ──► BookMapper                 JWT Filter
    │                                                              │
    ▼                                                              │
BorrowController ──► BorrowService ──┬──► BorrowRecordMapper      │
    │                                ├──► BookMapper              │
    │                                ├──► NotificationService ────┘
    │                                ├──► FineRecordService
    │                                └──► BorrowRuleService
    │
ReservationController ──► ReservationService ──► NotificationService
    │
SeatController ──► SeatService ──┬──► ReadingRoomMapper
    │                            ├──► SeatMapper
    │                            ├──► SeatReservationMapper
    │                            └──► NotificationService
    │
FineController ──► FineRecordService
    │
StatisticsController ──► (直接调用各Mapper进行统计)
    │
ExportController ──► ExportService ──► Apache POI / iText
    │
LogController ──► LogService ──► OperationLogMapper
    │
ReviewController ──► BookReviewService ──► BookReviewMapper
    │
RecommendationController ──► BookRecommendationService
    │
FaqController ──► FaqService
    │
AnnouncementController ──► AnnouncementService
    │
CategoryController ──► (直接调用CategoryMapper)
    │
UploadController ──► (文件上传处理)
    │
LibraryLocationController ──► LibraryLocationService
    │
IsbnController ──► (ISBN查询)
    │
HealthController ──► (健康检查)
    │
StatusController ──► (状态常量)
```

### 3.5 前端路由结构

#### 用户端路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/login` | LoginView | 用户登录 |
| `/` | DefaultLayout | 用户端布局 |
| `  /` | HomeView | 首页 |
| `  /books` | BookListView | 图书列表 |
| `  /books/:id` | BookDetailView | 图书详情 |
| `  /books/:id/reviews` | BookReviewView | 图书评价 |
| `  /borrows` | MyBorrowView | 我的借阅 |
| `  /reservations` | MyReservationView | 我的预约 |
| `  /seats` | SeatReservation | 座位预约 |
| `  /my-seats` | MySeatReservationView | 我的座位 |
| `  /inquiries` | InquiryView | 咨询中心 |
| `  /profile` | UserProfileView | 个人中心 |
| `  /notifications` | NotificationView | 消息中心 |
| `  /favorites` | UserFavoritesView | 我的收藏 |
| `  /history` | UserHistoryView | 阅读历史 |
| `  /fines` | UserFinesView | 罚款管理 |
| `  /exports` | UserExportsView | 数据导出 |

#### 管理端路由

| 路径 | 组件 | 说明 |
|------|------|------|
| `/admin` | AdminLayout | 管理端布局 |
| `  /dashboard` | AdminDashboardView | 管理仪表盘 |
| `  /books` | AdminBookView | 图书管理 |
| `  /books/add` | AdminBookAddView | 图书录入 |
| `  /books/import` | AdminBookImportView | 图书批量导入 |
| `  /acquisition` | AdminAcquisitionView | 图书采编 |
| `  /categories` | AdminCategoryView | 分类管理 |
| `  /seats` | AdminSeatView | 座位管理 |
| `  /statistics` | AdminStatisticsView | 统计报表 |
| `  /config` | AdminConfigView | 系统配置 |
| `  /logs` | AdminLogView | 操作日志 |
| `  /users` | AdminUserView | 用户管理 |
| `  /borrows` | AdminBorrowView | 借阅管理 |
| `  /inquiries` | AdminInquiryView | 咨询管理 |

---

## 第四章：数据库ER图及设计说明

### 4.1 数据库基本信息

| 项目 | 值 |
|------|-----|
| 数据库类型 | MySQL 8.0+ |
| 数据库名 | CityLibrary |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |
| 存储引擎 | InnoDB |

### 4.2 ER图（实体关系图）

```
┌──────────────┐       1    N    ┌──────────────────┐
│ user_account │◄───────────────►│  borrow_record    │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  reservation      │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  fine_record      │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  inquiry          │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  notification     │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  seat_reservation │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  purchase_request │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  export_task      │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  book_review      │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  user_favorite    │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  user_behavior    │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  book_recommend.  │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  refresh_token    │
│              │                 └──────────────────┘
│              │       1    N              │
│              │◄───────────────►│  book_import_batch│
│              │                 └──────────────────┘
└──────────────┘
                      │
                      ▼  N    1
              ┌──────────────────┐       1    N    ┌──────────────────┐
              │      book        │◄───────────────►│  borrow_record    │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  reservation      │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  book_review      │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  user_favorite    │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  user_behavior    │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  book_recommend.  │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  book_import_det. │
              │                  │                 └──────────────────┘
              └──────────────────┘
                      ▲
                      │ N    1
              ┌──────────────────┐       1    N    ┌──────────────────┐
              │   category       │                 │  acquisition_task │
              └──────────────────┘                 └──────────────────┘
                      │
                      ▼
              ┌──────────────────┐       1    N    ┌──────────────────┐
              │  reading_room    │◄───────────────►│      seat        │
              │                  │                 └──────────────────┘
              │                  │       1    N              │
              │                  │◄───────────────►│  seat_reservation │
              └──────────────────┘                 └──────────────────┘

  独立表：system_config, operation_log, notification_template, faq,
         announcement, borrow_rule, library_location
```

### 4.3 表结构定义

#### 4.3.1 user_account - 用户账户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| user_type | VARCHAR(20) | 用户类型：READER/STUDENT/TEACHER/INTERNATIONAL |
| real_name | VARCHAR(50) | 真实姓名 |
| phone | VARCHAR(20) | 联系电话 |
| email | VARCHAR(100) | 电子邮箱 |
| id_card | VARCHAR(30) | 身份证件号 |
| student_id | VARCHAR(20) | 学生学号 |
| faculty_id | VARCHAR(20) | 教职工号 |
| user_code | VARCHAR(20) | 用户编号 |
| campus | VARCHAR(100) | 校区 |
| college | VARCHAR(100) | 院系 |
| grade | VARCHAR(20) | 年级 |
| class_name | VARCHAR(50) | 班级 |
| counselor | VARCHAR(50) | 辅导员 |
| institution | VARCHAR(100) | 所属机构 |
| role | VARCHAR(20) | 系统角色 |
| language | VARCHAR(10) | 界面语言（默认zh-CN） |
| status | TINYINT | 状态：0-禁用，1-正常 |
| deleted | TINYINT | 逻辑删除：0-未删除，1-已删除 |
| violation_count | INT | 违约次数 |
| last_violation_time | DATETIME | 最后违约时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_username`、`uk_student_id`、`uk_faculty_id`、`uk_user_code`
- 普通索引：`idx_campus`、`idx_college`、`idx_grade`

#### 4.3.2 book - 图书信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| isbn | VARCHAR(20) | ISBN号 |
| title | VARCHAR(200) | 书名 |
| subtitle | VARCHAR(200) | 副标题 |
| title_en | VARCHAR(200) | 英文书名 |
| author | VARCHAR(100) | 作者 |
| author_en | VARCHAR(100) | 英文作者 |
| translator | VARCHAR(100) | 译者 |
| publisher | VARCHAR(100) | 出版社 |
| publish_year | VARCHAR(10) | 出版年份 |
| edition | VARCHAR(50) | 版次 |
| category | VARCHAR(50) | 分类代码 |
| category_name | VARCHAR(50) | 分类名称 |
| language | VARCHAR(20) | 文献语言（默认zh-CN） |
| pages | INT | 页数 |
| price | DECIMAL(10,2) | 价格 |
| tags | VARCHAR(500) | 标签 |
| cover_url | VARCHAR(500) | 封面图片URL |
| summary | TEXT | 内容简介 |
| summary_en | TEXT | 英文简介 |
| status | VARCHAR(20) | 状态：available/borrowed/reserved/maintenance/deleted |
| total_copies | INT | 总馆藏数量（默认1） |
| available_copies | INT | 可借数量（默认1） |
| location | VARCHAR(100) | 馆藏位置 |
| shelf_no | VARCHAR(50) | 书架号 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 普通索引：`idx_title`、`idx_author`、`idx_category`、`idx_status`

#### 4.3.3 borrow_record - 借阅记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account） |
| book_id | BIGINT | 图书ID（外键→book） |
| book_barcode | VARCHAR(50) | 图书条码 |
| borrow_date | DATETIME | 借阅日期 |
| due_date | DATETIME | 应还日期 |
| return_date | DATETIME | 实际归还日期 |
| renew_count | INT | 续借次数（默认0） |
| status | TINYINT | 状态：0-借阅中，1-已归还，2-逾期，3-丢失 |
| operator_id | BIGINT | 操作员ID |
| remarks | VARCHAR(500) | 备注 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 外键：`fk_borrow_user`（user_id→user_account）、`fk_borrow_book`（book_id→book）
- 普通索引：`idx_user_id`、`idx_book_id`、`idx_status`、`idx_borrow_date`

#### 4.3.4 reservation - 预约记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account） |
| book_id | BIGINT | 图书ID（外键→book） |
| reserve_date | DATETIME | 预约日期 |
| expire_date | DATETIME | 预约过期日期 |
| status | TINYINT | 状态：0-等待中，1-已通知，2-已取消，3-已失效 |
| notify_date | DATETIME | 通知日期 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_reserve_user`、`fk_reserve_book`
- 普通索引：`idx_user_id`、`idx_book_id`、`idx_status`

#### 4.3.5 fine_record - 罚款记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account） |
| borrow_record_id | BIGINT | 借阅记录ID（外键→borrow_record） |
| fine_type | VARCHAR(20) | 罚款类型：OVERDUE/DAMAGE/LOSS |
| amount | DECIMAL(10,2) | 罚款金额 |
| paid_status | TINYINT | 支付状态：0-未支付，1-已支付 |
| paid_date | DATETIME | 支付日期 |
| operator_id | BIGINT | 操作员ID |
| remarks | VARCHAR(500) | 备注 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_fine_user`、`fk_fine_borrow`
- 普通索引：`idx_user_id`、`idx_borrow_record_id`

#### 4.3.6 inquiry - 咨询记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 提问用户ID（外键→user_account） |
| title | VARCHAR(200) | 咨询标题 |
| content | TEXT | 咨询内容 |
| category | VARCHAR(50) | 咨询分类 |
| status | TINYINT | 状态：0-待回复，1-已回复，2-已关闭 |
| reply_content | TEXT | 回复内容 |
| replier_id | BIGINT | 回复人ID |
| reply_date | DATETIME | 回复日期 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 外键：`fk_inquiry_user`
- 普通索引：`idx_user_id`、`idx_status`

#### 4.3.7 system_config - 系统配置表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| config_key | VARCHAR(100) | 配置键，唯一 |
| config_value | VARCHAR(500) | 配置值 |
| config_type | VARCHAR(50) | 配置类型 |
| description | VARCHAR(200) | 配置描述 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_config_key`

#### 4.3.8 operation_log - 操作日志表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 操作用户ID |
| username | VARCHAR(50) | 用户名 |
| operation | VARCHAR(100) | 操作类型 |
| module | VARCHAR(50) | 操作模块 |
| method | VARCHAR(100) | 方法名 |
| params | TEXT | 请求参数 |
| result | TEXT | 返回结果 |
| ip_address | VARCHAR(50) | IP地址 |
| user_agent | VARCHAR(500) | 用户代理 |
| status | TINYINT | 操作状态：0-失败，1-成功 |
| error_message | TEXT | 错误信息 |
| execute_time | BIGINT | 执行时长（ms） |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 普通索引：`idx_user_id`、`idx_create_time`、`idx_operation`

#### 4.3.9 notification - 通知表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 接收用户ID（外键→user_account，CASCADE删除） |
| title | VARCHAR(200) | 通知标题 |
| content | TEXT | 通知内容 |
| type | VARCHAR(50) | 通知类型：SYSTEM/BORROW/RESERVATION/INQUIRY/FINE/RETURN/DUE_REMINDER/OVERDUE |
| is_read | TINYINT | 是否已读：0-未读，1-已读 |
| channel | VARCHAR(20) | 发送渠道：IN_APP/SMS/EMAIL |
| related_id | BIGINT | 关联业务ID |
| related_type | VARCHAR(30) | 关联业务类型 |
| read_time | DATETIME | 阅读时间 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_notification_user`（CASCADE删除）
- 普通索引：`idx_user_id`、`idx_type`、`idx_is_read`、`idx_user_status`、`idx_create_time`

#### 4.3.10 notification_template - 通知模板表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| code | VARCHAR(100) | 模板代码，唯一 |
| channel | VARCHAR(20) | 发送渠道 |
| title | VARCHAR(200) | 模板标题 |
| content | TEXT | 模板内容 |
| variables | VARCHAR(500) | 模板变量说明 |
| status | TINYINT | 状态：0-禁用，1-启用 |
| type | VARCHAR(50) | 通知类型 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_code`
- 普通索引：`idx_channel`、`idx_status`

#### 4.3.11 reading_room - 阅览室表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(100) | 阅览室名称 |
| location | VARCHAR(200) | 位置 |
| capacity | INT | 容量 |
| open_time | TIME | 开放时间 |
| close_time | TIME | 关闭时间 |
| status | TINYINT | 状态：0-关闭，1-开放 |
| total_seats | INT | 总座位数 |
| available_seats | INT | 可用座位数 |
| description | TEXT | 描述说明 |
| image_url | VARCHAR(500) | 阅览室图片 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 4.3.12 seat - 座位表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| room_id | BIGINT | 阅览室ID（外键→reading_room） |
| seat_number | VARCHAR(20) | 座位号 |
| seat_type | VARCHAR(20) | 座位类型：NORMAL/POWER/QUIET/GROUP |
| has_power | TINYINT | 是否有电源 |
| has_lamp | TINYINT | 是否有台灯 |
| row_num | INT | 行号 |
| col_num | INT | 列号 |
| status | TINYINT | 状态：0-可用，1-已预约，2-使用中，3-维护中 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_room_seat`（room_id, seat_number）
- 外键：`fk_seat_room`
- 普通索引：`idx_room_id`、`idx_seat_type`、`idx_status`

#### 4.3.13 seat_reservation - 座位预约表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account） |
| seat_id | BIGINT | 座位ID（外键→seat） |
| room_id | BIGINT | 阅览室ID（外键→reading_room） |
| reserve_date | DATE | 预约日期 |
| start_time | TIME | 开始时间 |
| end_time | TIME | 结束时间 |
| status | TINYINT | 状态：0-已预约，1-已签到，2-已完成，3-已取消，4-违约 |
| check_in_time | DATETIME | 签到时间 |
| check_out_time | DATETIME | 签退时间 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_seat_res_user`、`fk_seat_res_seat`、`fk_seat_reservation_room`
- 普通索引：`idx_user_id`、`idx_seat_id`、`idx_reserve_date`、`idx_status`、`idx_room_date`

#### 4.3.14 purchase_request - 采购申请表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| requester_id | BIGINT | 申请人ID（外键→user_account） |
| book_title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| isbn | VARCHAR(20) | ISBN |
| quantity | INT | 申请数量 |
| publisher | VARCHAR(100) | 出版社 |
| reason | TEXT | 申请理由 |
| status | TINYINT | 状态：0-待审核，1-已批准，2-已拒绝，3-已完成 |
| approve_time | DATETIME | 审批时间 |
| reject_reason | VARCHAR(500) | 驳回理由 |
| remarks | TEXT | 备注 |
| reviewer_id | BIGINT | 审核人ID |
| review_comment | VARCHAR(500) | 审核意见 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 外键：`fk_purchase_user`
- 普通索引：`idx_requester_id`、`idx_status`、`idx_isbn`、`idx_create_time`

#### 4.3.15 export_task - 导出任务表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account） |
| type | VARCHAR(50) | 导出类型：BOOK_EXPORT/BORROW_STATISTICS/USER_STATISTICS |
| file_name | VARCHAR(200) | 文件名 |
| file_path | VARCHAR(500) | 文件路径 |
| file_size | BIGINT | 文件大小（字节） |
| format | VARCHAR(10) | 导出格式：XLSX/PDF/CSV |
| query_params | TEXT | 查询参数（JSON） |
| status | VARCHAR(20) | 状态：PENDING/PROCESSING/COMPLETED/FAILED |
| error_message | VARCHAR(500) | 错误信息 |
| create_time | DATETIME | 创建时间 |
| complete_time | DATETIME | 完成时间 |

**索引：**
- 主键：`id`
- 外键：`fk_export_user`
- 普通索引：`idx_user_id`、`idx_status`、`idx_create_time`

#### 4.3.16 category - 图书分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(50) | 分类名称 |
| code | VARCHAR(20) | 分类编码，唯一 |
| parent_id | BIGINT | 父级分类ID |
| sort_order | INT | 排序值 |
| description | VARCHAR(200) | 分类描述 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_code`
- 普通索引：`idx_parent_id`

#### 4.3.17 acquisition_task - 图书采编任务表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| book_id | BIGINT | 图书ID |
| book_title | VARCHAR(200) | 书名 |
| book_isbn | VARCHAR(20) | ISBN |
| type | VARCHAR(20) | 采编类型：stock_in/transfer/discard |
| source | VARCHAR(100) | 来源 |
| assignee | VARCHAR(50) | 负责人 |
| status | VARCHAR(20) | 状态：pending/processing/completed/discarded |
| operator_id | BIGINT | 操作员ID |
| operator_name | VARCHAR(50) | 操作员姓名 |
| remark | VARCHAR(500) | 备注 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 普通索引：`idx_book_id`、`idx_type`、`idx_status`、`idx_operator_id`

#### 4.3.18 book_import_batch - 图书入库批次表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| batch_no | VARCHAR(50) | 批次号，唯一 |
| operator_id | BIGINT | 操作员ID（外键→user_account） |
| total_count | INT | 总数量 |
| success_count | INT | 成功数量 |
| fail_count | INT | 失败数量 |
| status | TINYINT | 状态：0-待处理，1-处理中，2-已完成 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_batch_no`
- 外键：`fk_batch_user`
- 普通索引：`idx_operator_id`

#### 4.3.19 book_import_detail - 图书导入明细表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| batch_id | BIGINT | 批次ID（外键→book_import_batch，CASCADE删除） |
| row_number | INT | Excel行号 |
| isbn | VARCHAR(20) | ISBN |
| title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| status | VARCHAR(20) | 状态：SUCCESS/FAILED/SKIPPED |
| error_message | VARCHAR(500) | 错误信息 |
| book_id | BIGINT | 关联图书ID（外键→book，SET NULL） |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_import_detail_batch`、`fk_import_detail_book`
- 普通索引：`idx_batch_id`、`idx_batch_status`、`idx_book_id`、`idx_isbn`

#### 4.3.20 book_review - 图书评价表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| book_id | BIGINT | 图书ID（外键→book，CASCADE删除） |
| user_id | BIGINT | 用户ID（外键→user_account，CASCADE删除） |
| rating | TINYINT | 评分1-5星（约束：1-5） |
| content | TEXT | 评价内容 |
| status | TINYINT | 状态：0-隐藏，1-显示 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_book_user`（book_id, user_id）
- 外键：`fk_review_book`、`fk_review_user`
- 普通索引：`idx_book_id`、`idx_user_id`、`idx_rating`
- 约束：`chk_rating`（rating >= 1 AND rating <= 5）

#### 4.3.21 user_favorite - 用户收藏表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account，CASCADE删除） |
| book_id | BIGINT | 图书ID（外键→book，CASCADE删除） |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 唯一索引：`uk_user_book`（user_id, book_id）
- 外键：`fk_favorite_user`、`fk_favorite_book`
- 普通索引：`idx_user_id`、`idx_book_id`

#### 4.3.22 user_behavior - 用户行为表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account，CASCADE删除） |
| book_id | BIGINT | 图书ID（外键→book，SET NULL） |
| action_type | VARCHAR(20) | 行为类型：VIEW/BORROW/RETURN/SEARCH/FAVORITE |
| action_detail | VARCHAR(500) | 行为详情 |
| ip_address | VARCHAR(50) | IP地址 |
| user_agent | VARCHAR(500) | 用户代理 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_behavior_user`、`fk_behavior_book`
- 普通索引：`idx_user_action`、`idx_book_id`、`idx_create_time`

#### 4.3.23 book_recommendation - 图书推荐表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID（外键→user_account，CASCADE删除） |
| book_id | BIGINT | 图书ID（外键→book，CASCADE删除） |
| score | DECIMAL(5,2) | 推荐分数 |
| reason | VARCHAR(500) | 推荐理由 |
| create_time | DATETIME | 创建时间 |

**索引：**
- 主键：`id`
- 外键：`fk_recommendation_user`、`fk_recommendation_book`
- 普通索引：`idx_user_id`、`idx_book_id`

#### 4.3.24 faq - 常见问题表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| question | VARCHAR(500) | 问题 |
| answer | TEXT | 答案 |
| category | VARCHAR(50) | 分类 |
| sort_order | INT | 排序值 |
| view_count | INT | 浏览次数 |
| status | TINYINT | 状态 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 4.3.25 announcement - 公告表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(200) | 公告标题 |
| content | TEXT | 公告内容 |
| type | VARCHAR(50) | 公告类型 |
| priority | INT | 优先级 |
| status | TINYINT | 状态 |
| publisher_id | BIGINT | 发布者ID |
| publisher_name | VARCHAR(50) | 发布者姓名 |
| publish_time | DATETIME | 发布时间 |
| expire_time | DATETIME | 过期时间 |
| view_count | INT | 浏览次数 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 4.3.26 refresh_token - 刷新令牌表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| token | VARCHAR(500) | 令牌值 |
| expiry_date | DATETIME | 过期时间 |
| revoked | TINYINT | 是否已撤销 |
| create_time | DATETIME | 创建时间 |

#### 4.3.27 library_location - 馆藏位置表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| floor | VARCHAR(50) | 楼层 |
| area | VARCHAR(100) | 区域 |
| zone | VARCHAR(50) | 分区 |
| shelf_range | VARCHAR(200) | 书架范围 |
| category_range | VARCHAR(200) | 分类范围 |
| description | TEXT | 描述 |
| image_url | VARCHAR(500) | 图片URL |
| sort_order | INT | 排序值 |
| status | TINYINT | 状态 |
| deleted | TINYINT | 逻辑删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 4.3.28 borrow_rule - 借阅规则表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_type | VARCHAR(20) | 用户类型 |
| max_borrow_count | INT | 最大借阅数量 |
| borrow_days | INT | 借阅天数 |
| renew_limit | INT | 续借次数上限 |
| renew_days | INT | 续借天数 |
| fine_per_day | DECIMAL(10,2) | 每日罚款金额 |
| lost_multiple | DECIMAL(5,2) | 丢失赔偿倍数 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4.4 外键关系说明

| 外键名 | 源表.字段 | 目标表.字段 | 删除策略 | 更新策略 |
|--------|-----------|-------------|----------|----------|
| fk_borrow_user | borrow_record.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_borrow_book | borrow_record.book_id | book.id | RESTRICT | RESTRICT |
| fk_reserve_user | reservation.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_reserve_book | reservation.book_id | book.id | RESTRICT | RESTRICT |
| fk_fine_user | fine_record.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_fine_borrow | fine_record.borrow_record_id | borrow_record.id | SET NULL | RESTRICT |
| fk_inquiry_user | inquiry.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_notification_user | notification.user_id | user_account.id | CASCADE | RESTRICT |
| fk_seat_room | seat.room_id | reading_room.id | RESTRICT | RESTRICT |
| fk_seat_res_user | seat_reservation.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_seat_res_seat | seat_reservation.seat_id | seat.id | RESTRICT | RESTRICT |
| fk_seat_reservation_room | seat_reservation.room_id | reading_room.id | RESTRICT | CASCADE |
| fk_purchase_user | purchase_request.requester_id | user_account.id | RESTRICT | RESTRICT |
| fk_export_user | export_task.user_id | user_account.id | RESTRICT | RESTRICT |
| fk_review_book | book_review.book_id | book.id | CASCADE | RESTRICT |
| fk_review_user | book_review.user_id | user_account.id | CASCADE | RESTRICT |
| fk_favorite_user | user_favorite.user_id | user_account.id | CASCADE | RESTRICT |
| fk_favorite_book | user_favorite.book_id | book.id | CASCADE | RESTRICT |
| fk_behavior_user | user_behavior.user_id | user_account.id | CASCADE | RESTRICT |
| fk_behavior_book | user_behavior.book_id | book.id | SET NULL | RESTRICT |
| fk_recommendation_user | book_recommendation.user_id | user_account.id | CASCADE | RESTRICT |
| fk_recommendation_book | book_recommendation.book_id | book.id | CASCADE | RESTRICT |
| fk_batch_user | book_import_batch.operator_id | user_account.id | RESTRICT | RESTRICT |
| fk_import_detail_batch | book_import_detail.batch_id | book_import_batch.id | CASCADE | RESTRICT |
| fk_import_detail_book | book_import_detail.book_id | book.id | SET NULL | RESTRICT |

### 4.5 索引设计说明

| 索引类型 | 使用场景 | 设计原则 |
|----------|----------|----------|
| 主键索引 | 所有表 | 自增BIGINT，InnoDB聚簇索引 |
| 唯一索引 | 用户账号、配置键、分类编码等 | 保证数据唯一性 |
| 普通索引 | 查询条件列（status、user_id等） | 加速WHERE查询 |
| 复合索引 | 联合查询条件（如 idx_user_status） | 最左前缀原则 |
| 外键索引 | 外键字段 | 自动创建，加速JOIN查询 |

### 4.6 命名规范

| 对象类型 | 命名规范 | 示例 |
|----------|----------|------|
| 表名 | snake_case，名词 | `user_account`, `borrow_record` |
| 字段名 | snake_case | `user_id`, `create_time` |
| 主键 | `id` | `id` |
| 外键 | `fk_源表_目标表` | `fk_borrow_user` |
| 唯一索引 | `uk_字段名` | `uk_username` |
| 普通索引 | `idx_字段名` | `idx_user_id` |
| 复合索引 | `idx_字段1_字段2` | `idx_user_status` |

---

## 第五章：核心业务流程说明

### 5.1 图书借阅流程

```
                    ┌─────────────┐
                    │  用户借书    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 检查借阅权限 │
                    │ (用户类型/规则)│
                    └──────┬──────┘
                           │ 通过
                    ┌──────▼──────┐
                    │ 检查借阅上限 │
                    │ (最大借阅数)  │
                    └──────┬──────┘
                           │ 未超限
                    ┌──────▼──────┐
                    │ 检查图书状态 │
                    │ (available)  │
                    └──────┬──────┘
                           │ 可借
                    ┌──────▼──────┐
                    │ 创建借阅记录 │
                    │ status = 0   │
                    │ (借阅中)      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 更新图书库存 │
                    │ available-1  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 发送通知     │
                    │ (借阅成功)   │
                    └──────┬──────┘
                           ▼
                    ┌─────────────┐
                    │   借阅中     │◄────────────────┐
                    │  status = 0  │                 │
                    └──────┬──────┘                 │
                           │                        │
              ┌────────────┼────────────┐           │
              ▼            ▼            ▼           │
     ┌────────────┐ ┌────────────┐ ┌────────┐      │
     │   已归还    │ │    逾期     │ │  丢失  │      │
     │ status = 1 │ │ status = 2 │ │st = 3  │      │
     └────────────┘ └──────┬─────┘ └────────┘      │
              ▲            │                        │
              │      ┌─────▼──────┐                 │
              │      │ 定时任务检查 │                 │
              │      │ 超期→罚款   │                 │
              │      └────────────┘                 │
              │                                     │
              └────────────── 续借 ─────────────────┘
```

**借阅状态机：**

| 状态 | 值 | 说明 | 可转换状态 |
|------|-----|------|-----------|
| 借阅中 | 0 | 图书在借阅期间 | 已归还(1)、逾期(2)、丢失(3) |
| 已归还 | 1 | 正常归还 | -（终态） |
| 逾期 | 2 | 超过应还日期未还 | 已归还(1)、丢失(3) |
| 丢失 | 3 | 图书丢失 | -（终态） |

### 5.2 图书预约流程

```
                    ┌─────────────┐
                    │  用户预约    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 检查预约资格 │
                    │ (是否已预约)  │
                    └──────┬──────┘
                           │ 通过
                    ┌──────▼──────┐
                    │ 创建预约记录 │
                    │ status = 0   │
                    │ (等待中)      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   等待中     │
                    │  status = 0  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 图书可借/到期 │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 发送通知     │
                    │ status = 1   │
                    │ (已通知)      │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ┌────────────┐ ┌────────────┐ ┌────────┐
     │  用户借阅   │ │  已取消     │ │  已失效 │
     │ (预约完成)  │ │ status = 2 │ │ st = 3 │
     └────────────┘ └────────────┘ └────────┘
```

**预约状态机：**

| 状态 | 值 | 说明 | 可转换状态 |
|------|-----|------|-----------|
| 等待中 | 0 | 预约已提交，等待图书可用 | 已通知(1)、已取消(2)、已失效(3) |
| 已通知 | 1 | 图书已通知用户来取 | 借阅成功(终态)、已取消(2)、已失效(3) |
| 已取消 | 2 | 用户主动取消 | -（终态） |
| 已失效 | 3 | 预约过期未取 | -（终态） |

### 5.3 座位预约流程

```
                    ┌─────────────┐
                    │  选择座位    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ 检查预约条件 │
                    │ (时间/冲突/违约)│
                    └──────┬──────┘
                           │ 通过
                    ┌──────▼──────┐
                    │ 创建座位预约 │
                    │ status = 0   │
                    │ (已预约)      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   已预约     │
                    │  status = 0  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  签到       │
                    │ status = 1   │
                    │ (已签到)      │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  签退       │
                    │ status = 2   │
                    │ (已完成)      │
                    └─────────────┘

              违约路径：
              已预约 → 未按时签到 → 超时释放 → 违约(status=4)
              已签到 → 超时未签退 → 违约处理
```

**座位预约状态机：**

| 状态 | 值 | 说明 | 可转换状态 |
|------|-----|------|-----------|
| 已预约 | 0 | 预约成功，等待签到 | 已签到(1)、已取消(3)、违约(4) |
| 已签到 | 1 | 用户已签到使用 | 已完成(2)、违约(4) |
| 已完成 | 2 | 正常使用完毕 | -（终态） |
| 已取消 | 3 | 用户主动取消 | -（终态） |
| 违约 | 4 | 未签到或超时 | -（终态） |

**预约规则限制：**
- 最多提前7天预约
- 取消需提前30分钟
- 每天最多预约3个时段
- 违约3次封禁7天
- 签到时间窗口：开始前15分钟 ~ 开始后30分钟

### 5.4 罚款计算流程

```
              ┌─────────────────────┐
              │  触发罚款条件       │
              └──────────┬──────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
  ┌────────────┐  ┌────────────┐  ┌────────────┐
  │  逾期罚款   │  │  损坏罚款   │  │  丢失罚款   │
  │ OVERDUE    │  │  DAMAGE    │  │   LOSS     │
  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘
         │               │               │
         ▼               ▼               ▼
  逾期天数 × 0.1元  损坏评估金额    图书价格 × 倍数
  (fine_per_day)   (人工评估)    (lost_multiple)
         │               │               │
         ▼               ▼               ▼
  ┌────────────────────────────────────────────┐
  │          创建罚款记录 (paid_status=0)        │
  └────────────────────────────────────────────┘
```

**罚款类型：**

| 罚款类型 | 计算方式 | 说明 |
|----------|----------|------|
| OVERDUE 逾期 | 逾期天数 × 每日罚款金额 | 默认0.1元/天 |
| DAMAGE 损坏 | 人工评估金额 | 根据损坏程度评估 |
| LOSS 丢失 | 图书价格 × 赔偿倍数 | 根据用户类型确定倍数 |

### 5.5 定时任务流程

#### 5.5.1 OverdueCheckTask - 逾期检查任务

```
触发频率: 每日执行

流程:
1. 查询所有 status=0 (借阅中) 且 due_date < 当前时间 的记录
2. 将记录状态更新为 status=2 (逾期)
3. 创建逾期罚款记录 (fine_type=OVERDUE)
4. 发送逾期通知给用户
5. 记录操作日志
```

#### 5.5.2 ReservationTimeoutTask - 预约超时处理任务

```
触发频率: 定期执行

流程:
1. 查询所有 status=0 (等待中) 且 expire_date < 当前时间 的记录
2. 将记录状态更新为 status=3 (已失效)
3. 可选：通知用户预约已失效
```

#### 5.5.3 SeatReleaseTask - 座位释放任务

```
触发频率: 定期执行

流程:
1. 查询所有 status=0 (已预约) 且超过签到时间窗口 的记录
2. 将记录状态更新为 status=4 (违约)
3. 更新用户违约次数
4. 释放座位状态为可用
5. 若违约次数达到上限，限制用户预约权限
```

---

## 第六章：接口文档

### 6.1 统一响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

#### 分页响应

```json
{
  "code": 200,
  "data": {
    "records": [ ... ],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  }
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

#### 认证方式

所有需要认证的接口均需在请求头中携带JWT Token：

```
Authorization: Bearer <access_token>
```

### 6.2 认证模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录（用户名+密码+验证码） | 否 |
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/captcha` | 获取验证码 | 否 |
| POST | `/api/auth/refresh` | 刷新Access Token | 否 |
| POST | `/api/auth/logout` | 登出（销毁Refresh Token） | 是 |
| GET | `/api/auth/profile` | 获取当前用户信息 | 是 |
| PUT | `/api/auth/password` | 修改密码 | 是 |

**登录请求示例：**

```json
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123",
  "captcha": "ABCD"
}
```

**登录响应示例：**

```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN",
      "userType": "ADMIN"
    }
  }
}
```

### 6.3 图书模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/books` | 图书列表（分页+搜索） | 否 |
| GET | `/api/books/{id}` | 图书详情 | 否 |
| POST | `/api/books` | 新增图书 | 是(ADMIN) |
| PUT | `/api/books/{id}` | 更新图书 | 是(ADMIN) |
| DELETE | `/api/books/{id}` | 删除图书 | 是(ADMIN) |
| GET | `/api/books/categories` | 获取所有分类 | 否 |
| GET | `/api/books/new` | 获取新书（TOP 10） | 否 |
| GET | `/api/books/search/advanced` | 高级搜索 | 否 |
| GET | `/api/books/{id}/reviews` | 获取图书评价 | 否 |

**搜索参数：**
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `category`: 分类代码
- `keyword`: 搜索关键词（标题/作者/ISBN）
- `status`: 图书状态
- `language`: 语言

### 6.4 借阅模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/borrow` | 借书（创建借阅记录） | 是(ADMIN) |
| POST | `/api/borrow/{id}/return` | 归还图书 | 是(ADMIN) |
| POST | `/api/borrow/{id}/renew` | 续借 | 是 |
| GET | `/api/borrow/my` | 我的借阅记录 | 是 |
| GET | `/api/borrow/my/current` | 当前借阅 | 是 |
| GET | `/api/borrow/my/history` | 历史借阅 | 是 |
| GET | `/api/borrow/{id}` | 借阅详情 | 是 |
| GET | `/api/borrow` | 借阅列表（管理端） | 是(ADMIN) |

**借阅参数：**
- `userId`: 用户ID
- `bookId`: 图书ID
- `operatorId`: 操作员ID（管理端操作）

### 6.5 预约模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/reservations` | 预约图书 | 是 |
| DELETE | `/api/reservations/{id}` | 取消预约 | 是 |
| GET | `/api/reservations/my` | 我的预约 | 是 |
| GET | `/api/reservations` | 预约列表（管理端） | 是(ADMIN) |

### 6.6 座位模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/seats/rooms` | 阅览室列表 | 否 |
| GET | `/api/seats/rooms/{id}` | 阅览室详情（含座位） | 否 |
| POST | `/api/seats/rooms` | 新增阅览室 | 是(ADMIN) |
| PUT | `/api/seats/rooms/{id}` | 更新阅览室 | 是(ADMIN) |
| GET | `/api/seats/room/{roomId}` | 获取座位布局 | 否 |
| POST | `/api/seats/reserve` | 预约座位 | 是 |
| POST | `/api/seats/check-in` | 签到 | 是 |
| POST | `/api/seats/check-out` | 签退 | 是 |
| DELETE | `/api/seats/reservation/{id}` | 取消座位预约 | 是 |
| GET | `/api/seats/my` | 我的座位预约 | 是 |
| GET | `/api/seats/reservations` | 预约列表（管理端） | 是(ADMIN) |

### 6.7 用户模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/users` | 用户列表（分页+搜索） | 是(ADMIN) |
| GET | `/api/users/{id}` | 用户详情 | 是 |
| POST | `/api/users` | 新增用户 | 是(ADMIN) |
| PUT | `/api/users/{id}` | 更新用户 | 是(ADMIN) |
| DELETE | `/api/users/{id}` | 删除用户 | 是(ADMIN) |
| PUT | `/api/users/{id}/status` | 更新用户状态 | 是(ADMIN) |
| PUT | `/api/users/{id}/role` | 更新用户角色 | 是(ADMIN) |
| POST | `/api/users/batch` | 批量操作 | 是(ADMIN) |
| POST | `/api/users/import` | 批量导入用户 | 是(ADMIN) |
| GET | `/api/users/export-template` | 下载导入模板 | 是(ADMIN) |

### 6.8 统计模块API

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/statistics/dashboard` | 仪表盘数据 | 是(ADMIN) |
| GET | `/api/statistics/borrow` | 借阅统计 | 是(ADMIN) |
| GET | `/api/statistics/users` | 用户统计 | 是(ADMIN) |
| GET | `/api/statistics/borrow/trend` | 借阅趋势 | 是(ADMIN) |
| GET | `/api/statistics/books/popular` | 热门图书 | 是(ADMIN) |
| GET | `/api/statistics/categories` | 分类统计 | 是(ADMIN) |

### 6.9 其他模块API

| 模块 | 路径 | 主要接口 | 认证 |
|------|------|----------|------|
| 罚款 | `/api/fines` | 查询罚款、支付罚款、我的罚款 | 是 |
| FAQ | `/api/faq` | 获取FAQ列表、按分类查询 | 否 |
| 公告 | `/api/announcements` | 公告CRUD、发布管理 | 部分 |
| 分类 | `/api/categories` | 分类CRUD | 部分 |
| 推荐 | `/api/recommendations` | 相似图书推荐 | 否 |
| 评价 | `/api/reviews` | 评价CRUD、评分 | 是 |
| 导出 | `/api/export` | 创建导出任务、下载文件 | 是 |
| 上传 | `/api/upload` | 文件上传 | 是 |
| 日志 | `/api/logs` | 日志查询、筛选 | 是(ADMIN) |
| 日志流 | `/api/logs/stream` | WebSocket实时日志 | 是(ADMIN) |
| 馆藏位置 | `/api/library-locations` | 位置查询 | 否 |
| ISBN | `/api/isbn` | ISBN信息查询 | 部分 |
| 健康 | `/api/health` | 系统健康检查 | 否 |
| 状态码 | `/api/status` | 状态码字典 | 否 |

---

## 第七章：系统部署架构说明

### 7.1 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | Spring Boot 3.x最低要求 |
| Node.js | 16+ | Vue 3构建需要 |
| MySQL | 8.0+ | 使用InnoDB引擎，utf8mb4字符集 |
| Maven | 3.6+ | 后端构建工具 |
| Git | 2.x+ | 版本控制 |

### 7.2 开发环境部署步骤

#### 后端部署

```bash
# 1. 克隆项目
git clone <repository_url>
cd spring-boot-backend

# 2. 创建数据库
mysql -u root -p
CREATE DATABASE CityLibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 导入数据库结构
mysql -u root -p CityLibrary < src/main/resources/schema.sql

# 4. 修改配置文件
# 编辑 src/main/resources/application.properties
# 修改数据库连接信息

# 5. 构建项目
mvn clean package -DskipTests

# 6. 运行项目
java -jar target/demo-0.0.1-SNAPSHOT.jar

# 或使用Maven直接运行
mvn spring-boot:run
```

#### 前端部署

```bash
# 1. 进入前端目录
cd vue3-project

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 访问应用
# 浏览器打开 http://localhost:5173
```

#### 构建生产版本

```bash
# 前端构建
cd vue3-project
npm run build
# 输出目录：dist/

# 后端构建
cd spring-boot-backend
mvn clean package
# 输出文件：target/demo-0.0.1-SNAPSHOT.jar
```

### 7.3 生产环境部署建议

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Nginx      │         │  Spring Boot │         │    MySQL     │
│  (反向代理)   │ ─────► │  :8080       │ ─────► │   :3306      │
│  :80/:443    │         │  (JAR包)     │         │   InnoDB     │
└──────────────┘         └──────────────┘         └──────────────┘
        │
        ▼
┌──────────────┐
│  静态资源     │
│  (dist目录)   │
└──────────────┘
```

| 建议项 | 说明 |
|--------|------|
| Nginx反向代理 | 前端静态资源 + 后端API代理，统一端口 |
| HTTPS | 使用Let's Encrypt免费证书 |
| 进程管理 | 使用systemd或supervisor管理Spring Boot进程 |
| 数据库备份 | 定时备份 + binlog增量备份 |
| 日志管理 | ELK Stack或阿里云日志服务 |
| 监控 | Prometheus + Grafana监控 |
| 水平扩展 | 多实例 + 负载均衡 |

### 7.4 配置文件说明（application.properties）

```properties
# 服务器端口
server.port=8080

# 应用名称
spring.application.name=CityLibrarySystem

# 数据库配置
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/CityLibrary?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=admin123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP连接池
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.connection-timeout=60000

# MyBatis-Plus配置
mybatis-plus.mapper-locations=classpath:mapper/**/*.xml
mybatis-plus.type-aliases-package=com.example.demo.model
mybatis-plus.configuration.map-underscore-to-camel-case=true

# JWT配置
jwt.secret=your-very-long-secret-key-that-is-at-least-64-bytes-long-for-hs512-algorithm-security
jwt.expiration-ms=86400000  # 24小时

# SQL初始化（生产环境设为never）
spring.sql.init.mode=never

# 文件上传
upload.path=uploads
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=100MB

# 日志配置
logging.file.path=logs
logging.file.name=spring.log
```

### 7.5 日志配置说明（logback-spring.xml）

系统使用Spring Boot默认的logback-spring.xml进行日志配置，支持：
- 控制台输出
- 文件输出（logs/目录下）
- 日志级别配置（开发/生产环境分离）
- 日志滚动策略（按大小/时间）

### 7.6 数据库迁移说明（Flyway）

项目使用Flyway进行数据库版本管理，迁移脚本位于 `src/main/resources/migration/` 目录：

| 版本 | 脚本 | 说明 |
|------|------|------|
| V1.0.1 | `V1.0.1__fix_spelling_errors.sql` | 拼写错误修复 |
| V1.0.2 | `V1.0.2__create_missing_tables.sql` | 创建缺失的表 |
| V1.0.3 | `V1.0.3__create_user_favorite_table.sql` | 创建用户收藏表 |
| V1.0.4 | `V1.0.4__add_missing_columns.sql` | 添加缺失字段 |
| V1.0.5 | `V1.0.5__add_missing_foreign_keys.sql` | 添加外键约束 |
| V1.0.6 | `V1.0.6__create_missing_indexes.sql` | 创建缺失索引 |
| V1.0.7 | `V1.0.7__add_user_identity_fields.sql` | 添加用户身份字段 |
| V1.0.8 | `V1.0.8__migrate_user_identity.sql` | 用户身份数据迁移 |

每个迁移脚本都有对应的回滚脚本（`_rollback.sql`），用于版本回退。

### 7.7 监控与运维建议

| 维度 | 建议 |
|------|------|
| 应用监控 | Spring Boot Actuator暴露/actuator/health、/actuator/metrics端点 |
| 数据库监控 | 慢查询日志、连接池监控、表空间监控 |
| 日志监控 | 日志级别动态调整、错误日志告警 |
| 性能监控 | JVM内存/GC监控、接口响应时间监控 |
| 安全监控 | 登录失败告警、异常IP封禁、SQL注入检测 |
| 备份策略 | 每日全量备份 + binlog增量备份，保留30天 |
| 容灾恢复 | 主从复制、异地备份、定期恢复演练 |

### 7.8 默认账户

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员，拥有所有权限 |

> **安全提醒：** 生产环境部署后请立即修改默认密码！

---

*文档版本: 1.0.0 | 最后更新: 2026-04-23 | 维护者: 项目开发团队*
