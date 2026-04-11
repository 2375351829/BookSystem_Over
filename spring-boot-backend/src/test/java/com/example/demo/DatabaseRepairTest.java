package com.example.demo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseRepairTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private FineRecordMapper fineRecordMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    private static Long testUserId;
    private static Long testBookId;
    private static Long testCategoryId;

    @Test
    @Order(1)
    @DisplayName("1. 数据库表结构验证 - 验证所有表是否存在")
    void testDatabaseTablesExist() {
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"
        );
        
        assertFalse(tables.isEmpty(), "数据库表不应为空");
        
        String[] expectedTables = {
            "user_account", "book", "borrow_record", "reservation",
            "user_favorite", "notification", "category", "fine_record",
            "operation_log", "system_config", "notification_template",
            "inquiry", "reading_room", "seat", "seat_reservation",
            "purchase_request", "book_import_batch", "export_task", "acquisition_task"
        };
        
        for (String tableName : expectedTables) {
            boolean tableExists = tables.stream()
                .anyMatch(t -> t.get("TABLE_NAME").toString().equalsIgnoreCase(tableName));
            assertTrue(tableExists, "表 " + tableName + " 应该存在");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. UserAccount实体类映射测试")
    void testUserAccountEntityMapping() {
        UserAccount user = new UserAccount();
        user.setUsername("testuser_" + System.currentTimeMillis());
        user.setPassword("password123");
        user.setUserType("STUDENT");
        user.setRealName("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setIdCard("110101199001011234");
        user.setInstitution("测试大学");
        user.setRole("USER");
        user.setLanguage("zh_CN");
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int result = userAccountMapper.insert(user);
        assertEquals(1, result, "插入用户应该成功");
        assertNotNull(user.getId(), "用户ID应该自动生成");
        testUserId = user.getId();

        UserAccount retrieved = userAccountMapper.selectById(user.getId());
        assertNotNull(retrieved, "应该能查询到用户");
        assertEquals("testuser", retrieved.getUsername().substring(0, 8), "用户名应该匹配");
        assertEquals("测试用户", retrieved.getRealName(), "真实姓名应该匹配");
        assertEquals("STUDENT", retrieved.getUserType(), "用户类型应该匹配");
        assertEquals("USER", retrieved.getRole(), "角色应该匹配");
        assertEquals(1, retrieved.getStatus(), "状态应该匹配");
    }

    @Test
    @Order(3)
    @DisplayName("3. Book实体类映射测试")
    void testBookEntityMapping() {
        Book book = new Book();
        book.setIsbn("978-7-111-12345-6");
        book.setTitle("测试图书");
        book.setSubtitle("副标题");
        book.setTitleEn("Test Book");
        book.setAuthor("测试作者");
        book.setAuthorEn("Test Author");
        book.setTranslator("测试译者");
        book.setPublisher("测试出版社");
        book.setPublishYear("2024");
        book.setEdition("第1版");
        book.setCategory("CS");
        book.setCategoryName("计算机科学");
        book.setLanguage("zh");
        book.setPages(300);
        book.setPrice(new BigDecimal("59.90"));
        book.setTags("编程,测试");
        book.setCoverUrl("http://example.com/cover.jpg");
        book.setSummary("这是一本测试图书");
        book.setStatus("available");
        book.setTotalCopies(10);
        book.setAvailableCopies(8);
        book.setLocation("A区1排");
        book.setShelfNo("A1-001");
        book.setDeleted(0);
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        int result = bookMapper.insert(book);
        assertEquals(1, result, "插入图书应该成功");
        assertNotNull(book.getId(), "图书ID应该自动生成");
        testBookId = book.getId();

        Book retrieved = bookMapper.selectById(book.getId());
        assertNotNull(retrieved, "应该能查询到图书");
        assertEquals("测试图书", retrieved.getTitle(), "书名应该匹配");
        assertEquals("测试作者", retrieved.getAuthor(), "作者应该匹配");
        assertEquals("available", retrieved.getStatus(), "状态应该匹配");
        assertEquals(10, retrieved.getTotalCopies(), "总副本数应该匹配");
        assertEquals(8, retrieved.getAvailableCopies(), "可用副本数应该匹配");
    }

    @Test
    @Order(4)
    @DisplayName("4. Category实体类映射测试")
    void testCategoryEntityMapping() {
        Category category = new Category();
        category.setName("计算机科学");
        category.setCode("CS");
        category.setParentId(null);
        category.setSortOrder(1);
        category.setDescription("计算机科学类图书");
        category.setDeleted(0);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());

        int result = categoryMapper.insert(category);
        assertEquals(1, result, "插入分类应该成功");
        assertNotNull(category.getId(), "分类ID应该自动生成");
        testCategoryId = category.getId();

        Category retrieved = categoryMapper.selectById(category.getId());
        assertNotNull(retrieved, "应该能查询到分类");
        assertEquals("计算机科学", retrieved.getName(), "分类名称应该匹配");
        assertEquals("CS", retrieved.getCode(), "分类代码应该匹配");
    }

    @Test
    @Order(5)
    @DisplayName("5. BorrowRecord实体类映射测试")
    void testBorrowRecordEntityMapping() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookBarcode("BAR001");
        record.setBorrowDate(new Date());
        record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        record.setRenewCount(0);
        record.setStatus(0);
        record.setDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        int result = borrowRecordMapper.insert(record);
        assertEquals(1, result, "插入借阅记录应该成功");
        assertNotNull(record.getId(), "借阅记录ID应该自动生成");

        BorrowRecord retrieved = borrowRecordMapper.selectById(record.getId());
        assertNotNull(retrieved, "应该能查询到借阅记录");
        assertEquals(userId, retrieved.getUserId(), "用户ID应该匹配");
        assertEquals(bookId, retrieved.getBookId(), "图书ID应该匹配");
        assertEquals(0, retrieved.getStatus(), "状态应该匹配");
    }

    @Test
    @Order(6)
    @DisplayName("6. Reservation实体类映射测试")
    void testReservationEntityMapping() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReserveDate(new Date());
        reservation.setExpireDate(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000));
        reservation.setStatus(0);
        reservation.setDeleted(0);
        reservation.setCreateTime(new Date());

        int result = reservationMapper.insert(reservation);
        assertEquals(1, result, "插入预约记录应该成功");
        assertNotNull(reservation.getId(), "预约记录ID应该自动生成");

        Reservation retrieved = reservationMapper.selectById(reservation.getId());
        assertNotNull(retrieved, "应该能查询到预约记录");
        assertEquals(userId, retrieved.getUserId(), "用户ID应该匹配");
        assertEquals(bookId, retrieved.getBookId(), "图书ID应该匹配");
        assertEquals(0, retrieved.getStatus(), "状态应该匹配");
    }

    @Test
    @Order(7)
    @DisplayName("7. Favorite实体类映射测试")
    void testFavoriteEntityMapping() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(new Date());

        int result = favoriteMapper.insert(favorite);
        assertEquals(1, result, "插入收藏记录应该成功");
        assertNotNull(favorite.getId(), "收藏记录ID应该自动生成");

        Favorite retrieved = favoriteMapper.selectById(favorite.getId());
        assertNotNull(retrieved, "应该能查询到收藏记录");
        assertEquals(userId, retrieved.getUserId(), "用户ID应该匹配");
        assertEquals(bookId, retrieved.getBookId(), "图书ID应该匹配");
    }

    @Test
    @Order(8)
    @DisplayName("8. Notification实体类映射测试")
    void testNotificationEntityMapping() {
        Long userId = createTestUser();

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("测试通知");
        notification.setContent("这是一条测试通知内容");
        notification.setType(Notification.TYPE_SYSTEM);
        notification.setIsRead(0);
        notification.setDeleted(0);
        notification.setCreateTime(new Date());

        int result = notificationMapper.insert(notification);
        assertEquals(1, result, "插入通知应该成功");
        assertNotNull(notification.getId(), "通知ID应该自动生成");

        Notification retrieved = notificationMapper.selectById(notification.getId());
        assertNotNull(retrieved, "应该能查询到通知");
        assertEquals("测试通知", retrieved.getTitle(), "标题应该匹配");
        assertEquals(Notification.TYPE_SYSTEM, retrieved.getType(), "类型应该匹配");
        assertEquals(0, retrieved.getIsRead(), "已读状态应该匹配");
    }

    @Test
    @Order(9)
    @DisplayName("9. FineRecord实体类映射测试")
    void testFineRecordEntityMapping() {
        Long userId = createTestUser();
        Long borrowRecordId = createTestBorrowRecord(userId);

        FineRecord fineRecord = new FineRecord();
        fineRecord.setUserId(userId);
        fineRecord.setBorrowRecordId(borrowRecordId);
        fineRecord.setFineType("OVERDUE");
        fineRecord.setAmount(new BigDecimal("5.00"));
        fineRecord.setPaidStatus(0);
        fineRecord.setDeleted(0);
        fineRecord.setCreateTime(new Date());

        int result = fineRecordMapper.insert(fineRecord);
        assertEquals(1, result, "插入罚款记录应该成功");
        assertNotNull(fineRecord.getId(), "罚款记录ID应该自动生成");

        FineRecord retrieved = fineRecordMapper.selectById(fineRecord.getId());
        assertNotNull(retrieved, "应该能查询到罚款记录");
        assertEquals(userId, retrieved.getUserId(), "用户ID应该匹配");
        assertEquals("OVERDUE", retrieved.getFineType(), "罚款类型应该匹配");
        assertEquals(new BigDecimal("5.00"), retrieved.getAmount(), "罚款金额应该匹配");
    }

    @Test
    @Order(10)
    @DisplayName("10. OperationLog实体类映射测试")
    void testOperationLogEntityMapping() {
        Long userId = createTestUser();

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername("testuser");
        log.setOperation("测试操作");
        log.setModule("测试模块");
        log.setMethod("testMethod");
        log.setParams("{\"key\":\"value\"}");
        log.setResult("成功");
        log.setIpAddress("127.0.0.1");
        log.setUserAgent("TestAgent");
        log.setStatus(1);
        log.setExecuteTime(100L);
        log.setCreateTime(new Date());

        int result = operationLogMapper.insert(log);
        assertEquals(1, result, "插入操作日志应该成功");
        assertNotNull(log.getId(), "操作日志ID应该自动生成");

        OperationLog retrieved = operationLogMapper.selectById(log.getId());
        assertNotNull(retrieved, "应该能查询到操作日志");
        assertEquals("测试操作", retrieved.getOperation(), "操作应该匹配");
        assertEquals("测试模块", retrieved.getModule(), "模块应该匹配");
        assertEquals(1, retrieved.getStatus(), "状态应该匹配");
    }

    @Test
    @Order(11)
    @DisplayName("11. UserAccount CRUD操作测试")
    void testUserAccountCRUD() {
        UserAccount user = new UserAccount();
        user.setUsername("crud_test_" + System.currentTimeMillis());
        user.setPassword("password123");
        user.setRealName("CRUD测试用户");
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int insertResult = userAccountMapper.insert(user);
        assertEquals(1, insertResult, "创建用户应该成功");
        Long userId = user.getId();
        assertNotNull(userId, "用户ID应该自动生成");

        UserAccount readUser = userAccountMapper.selectById(userId);
        assertNotNull(readUser, "读取用户应该成功");
        assertEquals("CRUD测试用户", readUser.getRealName(), "用户信息应该匹配");

        readUser.setRealName("更新后的用户名");
        readUser.setUpdateTime(new Date());
        int updateResult = userAccountMapper.updateById(readUser);
        assertEquals(1, updateResult, "更新用户应该成功");

        UserAccount updatedUser = userAccountMapper.selectById(userId);
        assertEquals("更新后的用户名", updatedUser.getRealName(), "更新后的用户名应该匹配");

        int deleteResult = userAccountMapper.deleteById(userId);
        assertEquals(1, deleteResult, "删除用户应该成功");

        UserAccount deletedUser = userAccountMapper.selectById(userId);
        assertNull(deletedUser, "删除后应该查询不到用户");
    }

    @Test
    @Order(12)
    @DisplayName("12. Book CRUD操作测试")
    void testBookCRUD() {
        Book book = new Book();
        book.setIsbn("ISBN" + (System.currentTimeMillis() % 1000000000L));
        book.setTitle("CRUD测试图书");
        book.setAuthor("CRUD测试作者");
        book.setStatus("available");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        book.setDeleted(0);
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        int insertResult = bookMapper.insert(book);
        assertEquals(1, insertResult, "创建图书应该成功");
        Long bookId = book.getId();
        assertNotNull(bookId, "图书ID应该自动生成");

        Book readBook = bookMapper.selectById(bookId);
        assertNotNull(readBook, "读取图书应该成功");
        assertEquals("CRUD测试图书", readBook.getTitle(), "图书信息应该匹配");

        readBook.setTitle("更新后的图书标题");
        readBook.setAvailableCopies(3);
        readBook.setUpdateTime(new Date());
        int updateResult = bookMapper.updateById(readBook);
        assertEquals(1, updateResult, "更新图书应该成功");

        Book updatedBook = bookMapper.selectById(bookId);
        assertEquals("更新后的图书标题", updatedBook.getTitle(), "更新后的标题应该匹配");
        assertEquals(3, updatedBook.getAvailableCopies(), "更新后的可用副本数应该匹配");

        int deleteResult = bookMapper.deleteById(bookId);
        assertEquals(1, deleteResult, "删除图书应该成功");

        Book deletedBook = bookMapper.selectById(bookId);
        assertNull(deletedBook, "删除后应该查询不到图书");
    }

    @Test
    @Order(13)
    @DisplayName("13. BorrowRecord CRUD操作测试")
    void testBorrowRecordCRUD() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookBarcode("CRUD-BAR-001");
        record.setBorrowDate(new Date());
        record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        record.setRenewCount(0);
        record.setStatus(0);
        record.setDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        int insertResult = borrowRecordMapper.insert(record);
        assertEquals(1, insertResult, "创建借阅记录应该成功");
        Long recordId = record.getId();
        assertNotNull(recordId, "借阅记录ID应该自动生成");

        BorrowRecord readRecord = borrowRecordMapper.selectById(recordId);
        assertNotNull(readRecord, "读取借阅记录应该成功");
        assertEquals(userId, readRecord.getUserId(), "借阅记录用户ID应该匹配");

        readRecord.setStatus(1);
        readRecord.setReturnDate(new Date());
        readRecord.setUpdateTime(new Date());
        int updateResult = borrowRecordMapper.updateById(readRecord);
        assertEquals(1, updateResult, "更新借阅记录应该成功");

        BorrowRecord updatedRecord = borrowRecordMapper.selectById(recordId);
        assertEquals(1, updatedRecord.getStatus(), "更新后的状态应该匹配");
        assertNotNull(updatedRecord.getReturnDate(), "归还日期应该已设置");

        int deleteResult = borrowRecordMapper.deleteById(recordId);
        assertEquals(1, deleteResult, "删除借阅记录应该成功");

        BorrowRecord deletedRecord = borrowRecordMapper.selectById(recordId);
        assertNull(deletedRecord, "删除后应该查询不到借阅记录");
    }

    @Test
    @Order(14)
    @DisplayName("14. Reservation CRUD操作测试")
    void testReservationCRUD() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReserveDate(new Date());
        reservation.setExpireDate(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000));
        reservation.setStatus(0);
        reservation.setDeleted(0);
        reservation.setCreateTime(new Date());

        int insertResult = reservationMapper.insert(reservation);
        assertEquals(1, insertResult, "创建预约记录应该成功");
        Long reservationId = reservation.getId();
        assertNotNull(reservationId, "预约记录ID应该自动生成");

        Reservation readReservation = reservationMapper.selectById(reservationId);
        assertNotNull(readReservation, "读取预约记录应该成功");
        assertEquals(userId, readReservation.getUserId(), "预约记录用户ID应该匹配");

        readReservation.setStatus(1);
        int updateResult = reservationMapper.updateById(readReservation);
        assertEquals(1, updateResult, "更新预约记录应该成功");

        Reservation updatedReservation = reservationMapper.selectById(reservationId);
        assertEquals(1, updatedReservation.getStatus(), "更新后的状态应该匹配");

        int deleteResult = reservationMapper.deleteById(reservationId);
        assertEquals(1, deleteResult, "删除预约记录应该成功");

        Reservation deletedReservation = reservationMapper.selectById(reservationId);
        assertNull(deletedReservation, "删除后应该查询不到预约记录");
    }

    @Test
    @Order(15)
    @DisplayName("15. Favorite CRUD操作测试")
    void testFavoriteCRUD() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(new Date());

        int insertResult = favoriteMapper.insert(favorite);
        assertEquals(1, insertResult, "创建收藏记录应该成功");
        Long favoriteId = favorite.getId();
        assertNotNull(favoriteId, "收藏记录ID应该自动生成");

        Favorite readFavorite = favoriteMapper.selectById(favoriteId);
        assertNotNull(readFavorite, "读取收藏记录应该成功");
        assertEquals(userId, readFavorite.getUserId(), "收藏记录用户ID应该匹配");

        int deleteResult = favoriteMapper.deleteById(favoriteId);
        assertEquals(1, deleteResult, "删除收藏记录应该成功");

        Favorite deletedFavorite = favoriteMapper.selectById(favoriteId);
        assertNull(deletedFavorite, "删除后应该查询不到收藏记录");
    }

    @Test
    @Order(16)
    @DisplayName("16. Notification CRUD操作测试")
    void testNotificationCRUD() {
        Long userId = createTestUser();

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("CRUD测试通知");
        notification.setContent("CRUD测试通知内容");
        notification.setType(Notification.TYPE_SYSTEM);
        notification.setIsRead(0);
        notification.setDeleted(0);
        notification.setCreateTime(new Date());

        int insertResult = notificationMapper.insert(notification);
        assertEquals(1, insertResult, "创建通知应该成功");
        Long notificationId = notification.getId();
        assertNotNull(notificationId, "通知ID应该自动生成");

        Notification readNotification = notificationMapper.selectById(notificationId);
        assertNotNull(readNotification, "读取通知应该成功");
        assertEquals("CRUD测试通知", readNotification.getTitle(), "通知标题应该匹配");

        readNotification.setIsRead(1);
        int updateResult = notificationMapper.updateById(readNotification);
        assertEquals(1, updateResult, "更新通知应该成功");

        Notification updatedNotification = notificationMapper.selectById(notificationId);
        assertEquals(1, updatedNotification.getIsRead(), "更新后的已读状态应该匹配");

        int deleteResult = notificationMapper.deleteById(notificationId);
        assertEquals(1, deleteResult, "删除通知应该成功");

        Notification deletedNotification = notificationMapper.selectById(notificationId);
        assertNull(deletedNotification, "删除后应该查询不到通知");
    }

    @Test
    @Order(17)
    @DisplayName("17. 用户名唯一性约束测试")
    void testUsernameUniqueConstraint() {
        String username = "unique_test_" + System.currentTimeMillis();
        
        UserAccount user1 = new UserAccount();
        user1.setUsername(username);
        user1.setPassword("password1");
        user1.setStatus(1);
        user1.setDeleted(0);
        user1.setCreateTime(new Date());
        user1.setUpdateTime(new Date());
        
        int result1 = userAccountMapper.insert(user1);
        assertEquals(1, result1, "第一次插入用户应该成功");

        UserAccount user2 = new UserAccount();
        user2.setUsername(username);
        user2.setPassword("password2");
        user2.setStatus(1);
        user2.setDeleted(0);
        user2.setCreateTime(new Date());
        user2.setUpdateTime(new Date());

        assertThrows(Exception.class, () -> {
            userAccountMapper.insert(user2);
        }, "重复用户名应该抛出异常");
    }

    @Test
    @Order(18)
    @DisplayName("18. 分类代码唯一性约束测试")
    void testCategoryCodeUniqueConstraint() {
        String code = "UNIQUE_CODE_" + System.currentTimeMillis();
        
        Category category1 = new Category();
        category1.setName("测试分类1");
        category1.setCode(code);
        category1.setDeleted(0);
        category1.setCreateTime(new Date());
        category1.setUpdateTime(new Date());
        
        int result1 = categoryMapper.insert(category1);
        assertEquals(1, result1, "第一次插入分类应该成功");

        Category category2 = new Category();
        category2.setName("测试分类2");
        category2.setCode(code);
        category2.setDeleted(0);
        category2.setCreateTime(new Date());
        category2.setUpdateTime(new Date());

        assertThrows(Exception.class, () -> {
            categoryMapper.insert(category2);
        }, "重复分类代码应该抛出异常");
    }

    @Test
    @Order(19)
    @DisplayName("19. 查询操作测试 - 条件查询")
    void testConditionalQuery() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        for (int i = 0; i < 3; i++) {
            BorrowRecord record = new BorrowRecord();
            record.setUserId(userId);
            record.setBookId(bookId);
            record.setBorrowDate(new Date());
            record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
            record.setStatus(i % 2);
            record.setDeleted(0);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            borrowRecordMapper.insert(record);
        }

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .eq(BorrowRecord::getStatus, 0);
        
        List<BorrowRecord> records = borrowRecordMapper.selectList(wrapper);
        assertFalse(records.isEmpty(), "应该能查询到记录");
        records.forEach(r -> {
            assertEquals(userId, r.getUserId(), "用户ID应该匹配");
            assertEquals(0, r.getStatus(), "状态应该匹配");
        });
    }

    @Test
    @Order(20)
    @DisplayName("20. 分页查询测试")
    void testPaginationQuery() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        for (int i = 0; i < 15; i++) {
            BorrowRecord record = new BorrowRecord();
            record.setUserId(userId);
            record.setBookId(bookId);
            record.setBorrowDate(new Date());
            record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
            record.setStatus(0);
            record.setDeleted(0);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            borrowRecordMapper.insert(record);
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BorrowRecord> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        
        com.baomidou.mybatisplus.core.metadata.IPage<BorrowRecord> result = 
            borrowRecordMapper.selectPage(page, wrapper);
        
        assertEquals(15, result.getTotal(), "总记录数应该为15");
        assertEquals(10, result.getRecords().size(), "第一页应该有10条记录");
        assertEquals(2, result.getPages(), "应该有2页");
    }

    @Test
    @Order(21)
    @DisplayName("21. 批量插入测试")
    void testBatchInsert() {
        Long userId = createTestUser();

        for (int i = 0; i < 5; i++) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle("批量测试通知_" + i);
            notification.setContent("批量测试内容_" + i);
            notification.setType(Notification.TYPE_SYSTEM);
            notification.setIsRead(0);
            notification.setDeleted(0);
            notification.setCreateTime(new Date());
            notificationMapper.insert(notification);
        }

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        
        List<Notification> notifications = notificationMapper.selectList(wrapper);
        assertEquals(5, notifications.size(), "应该有5条通知记录");
    }

    @Test
    @Order(22)
    @DisplayName("22. 逻辑删除测试")
    void testLogicalDelete() {
        Book book = new Book();
        book.setIsbn("LOGICAL-DELETE-ISBN");
        book.setTitle("逻辑删除测试图书");
        book.setStatus("available");
        book.setTotalCopies(1);
        book.setAvailableCopies(1);
        book.setDeleted(0);
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());

        bookMapper.insert(book);
        Long bookId = book.getId();

        Book beforeDelete = bookMapper.selectById(bookId);
        assertNotNull(beforeDelete, "删除前应该能查询到图书");
        assertEquals(0, beforeDelete.getDeleted(), "删除标记应该为0");

        book.setDeleted(1);
        bookMapper.updateById(book);

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getId, bookId).eq(Book::getDeleted, 0);
        Book afterDelete = bookMapper.selectOne(wrapper);
        assertNull(afterDelete, "逻辑删除后不应该查询到图书(deleted=0条件)");
    }

    @Test
    @Order(23)
    @DisplayName("23. 字段默认值测试")
    void testFieldDefaultValues() {
        UserAccount user = new UserAccount();
        user.setUsername("default_test_" + System.currentTimeMillis());
        user.setPassword("password");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userAccountMapper.insert(user);
        Long userId = user.getId();

        UserAccount retrieved = userAccountMapper.selectById(userId);
        assertNotNull(retrieved, "应该能查询到用户");
        assertEquals("USER", retrieved.getRole(), "角色默认值应该是USER");
        assertEquals("zh_CN", retrieved.getLanguage(), "语言默认值应该是zh_CN");
        assertEquals(1, retrieved.getStatus(), "状态默认值应该是1");
        assertEquals(0, retrieved.getDeleted(), "删除标记默认值应该是0");
    }

    @Test
    @Order(24)
    @DisplayName("24. 日期字段测试")
    void testDateFields() {
        Date now = new Date();
        
        UserAccount user = new UserAccount();
        user.setUsername("date_test_" + System.currentTimeMillis());
        user.setPassword("password");
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        userAccountMapper.insert(user);
        
        UserAccount retrieved = userAccountMapper.selectById(user.getId());
        assertNotNull(retrieved.getCreateTime(), "创建时间不应为空");
        assertNotNull(retrieved.getUpdateTime(), "更新时间不应为空");
    }

    @Test
    @Order(25)
    @DisplayName("25. 外键关系模拟测试 - BorrowRecord关联User和Book")
    void testBorrowRecordForeignKeyRelation() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(new Date());
        record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        record.setStatus(0);
        record.setDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        borrowRecordMapper.insert(record);

        BorrowRecord retrieved = borrowRecordMapper.selectById(record.getId());
        assertNotNull(retrieved, "借阅记录应该存在");

        UserAccount user = userAccountMapper.selectById(retrieved.getUserId());
        assertNotNull(user, "关联的用户应该存在");

        Book book = bookMapper.selectById(retrieved.getBookId());
        assertNotNull(book, "关联的图书应该存在");
    }

    @Test
    @Order(26)
    @DisplayName("26. 外键关系模拟测试 - Reservation关联User和Book")
    void testReservationForeignKeyRelation() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReserveDate(new Date());
        reservation.setStatus(0);
        reservation.setDeleted(0);
        reservation.setCreateTime(new Date());

        reservationMapper.insert(reservation);

        Reservation retrieved = reservationMapper.selectById(reservation.getId());
        assertNotNull(retrieved, "预约记录应该存在");

        UserAccount user = userAccountMapper.selectById(retrieved.getUserId());
        assertNotNull(user, "关联的用户应该存在");

        Book book = bookMapper.selectById(retrieved.getBookId());
        assertNotNull(book, "关联的图书应该存在");
    }

    @Test
    @Order(27)
    @DisplayName("27. 外键关系模拟测试 - Notification关联User")
    void testNotificationForeignKeyRelation() {
        Long userId = createTestUser();

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("外键测试通知");
        notification.setType(Notification.TYPE_SYSTEM);
        notification.setIsRead(0);
        notification.setDeleted(0);
        notification.setCreateTime(new Date());

        notificationMapper.insert(notification);

        Notification retrieved = notificationMapper.selectById(notification.getId());
        assertNotNull(retrieved, "通知应该存在");

        UserAccount user = userAccountMapper.selectById(retrieved.getUserId());
        assertNotNull(user, "关联的用户应该存在");
    }

    @Test
    @Order(28)
    @DisplayName("28. 外键关系模拟测试 - FineRecord关联User和BorrowRecord")
    void testFineRecordForeignKeyRelation() {
        Long userId = createTestUser();
        Long borrowRecordId = createTestBorrowRecord(userId);

        FineRecord fineRecord = new FineRecord();
        fineRecord.setUserId(userId);
        fineRecord.setBorrowRecordId(borrowRecordId);
        fineRecord.setFineType("OVERDUE");
        fineRecord.setAmount(new BigDecimal("10.00"));
        fineRecord.setPaidStatus(0);
        fineRecord.setDeleted(0);
        fineRecord.setCreateTime(new Date());

        fineRecordMapper.insert(fineRecord);

        FineRecord retrieved = fineRecordMapper.selectById(fineRecord.getId());
        assertNotNull(retrieved, "罚款记录应该存在");

        UserAccount user = userAccountMapper.selectById(retrieved.getUserId());
        assertNotNull(user, "关联的用户应该存在");

        BorrowRecord borrowRecord = borrowRecordMapper.selectById(retrieved.getBorrowRecordId());
        assertNotNull(borrowRecord, "关联的借阅记录应该存在");
    }

    @Test
    @Order(29)
    @DisplayName("29. 复合查询测试 - 多表关联模拟")
    void testComplexQuerySimulation() {
        Long userId = createTestUser();
        Long bookId = createTestBook();

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(new Date());
        record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        record.setStatus(0);
        record.setDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        borrowRecordMapper.insert(record);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle("借书成功通知");
        notification.setContent("您已成功借阅图书");
        notification.setType(Notification.TYPE_BORROW);
        notification.setIsRead(0);
        notification.setDeleted(0);
        notification.setCreateTime(new Date());
        notificationMapper.insert(notification);

        List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
            new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getUserId, userId)
        );
        assertFalse(borrowRecords.isEmpty(), "用户应该有借阅记录");

        List<Notification> notifications = notificationMapper.selectList(
            new LambdaQueryWrapper<Notification>().eq(Notification::getUserId, userId)
        );
        assertFalse(notifications.isEmpty(), "用户应该有通知记录");
    }

    @Test
    @Order(30)
    @DisplayName("30. 数据完整性测试 - 必填字段验证")
    void testRequiredFieldsValidation() {
        UserAccount userWithoutRequired = new UserAccount();
        userWithoutRequired.setCreateTime(new Date());
        userWithoutRequired.setUpdateTime(new Date());

        assertThrows(Exception.class, () -> {
            userAccountMapper.insert(userWithoutRequired);
        }, "缺少必填字段应该抛出异常");

        Book bookWithoutTitle = new Book();
        bookWithoutTitle.setIsbn("NO-TITLE-ISBN");
        bookWithoutTitle.setDeleted(0);
        bookWithoutTitle.setCreateTime(new Date());
        bookWithoutTitle.setUpdateTime(new Date());

        assertThrows(Exception.class, () -> {
            bookMapper.insert(bookWithoutTitle);
        }, "缺少书名应该抛出异常");
    }

    private Long createTestUser() {
        UserAccount user = new UserAccount();
        user.setUsername("test_user_" + System.currentTimeMillis() + "_" + Math.random());
        user.setPassword("password123");
        user.setRealName("测试用户");
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userAccountMapper.insert(user);
        return user.getId();
    }

    private Long createTestBook() {
        Book book = new Book();
        book.setIsbn("ISBN" + (System.currentTimeMillis() % 1000000000L));
        book.setTitle("测试图书_" + System.currentTimeMillis());
        book.setAuthor("测试作者");
        book.setStatus("available");
        book.setTotalCopies(10);
        book.setAvailableCopies(10);
        book.setDeleted(0);
        book.setCreateTime(new Date());
        book.setUpdateTime(new Date());
        bookMapper.insert(book);
        return book.getId();
    }

    private Long createTestBorrowRecord(Long userId) {
        Long bookId = createTestBook();
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(new Date());
        record.setDueDate(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        record.setStatus(0);
        record.setDeleted(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        borrowRecordMapper.insert(record);
        return record.getId();
    }
}
