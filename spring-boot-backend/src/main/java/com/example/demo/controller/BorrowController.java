package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.OperationLog;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    private Long getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted", 0);
        UserAccount userAccount = userAccountMapper.selectOne(queryWrapper);
        if (userAccount == null) {
            throw new UserNotFoundException("用户不存在");
        }
        return userAccount.getId();
    }

    // 获取我的借阅记录（前端调用 /api/borrows/my）
    @GetMapping("/my")
    public ResponseEntity<?> getMyBorrows(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        Page<BorrowRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> borrowQueryWrapper = new LambdaQueryWrapper<>();
        borrowQueryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getDeleted, 0)
                .orderByDesc(BorrowRecord::getBorrowDate);

        IPage<BorrowRecord> result = borrowRecordMapper.selectPage(pageInfo, borrowQueryWrapper);
        
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (BorrowRecord record : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("bookId", record.getBookId());
            item.put("borrowDate", record.getBorrowDate());
            item.put("dueDate", record.getDueDate());
            item.put("returnDate", record.getReturnDate());
            item.put("status", record.getStatus());
            item.put("renewCount", record.getRenewCount());
            
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                item.put("bookTitle", book.getTitle());
                item.put("bookIsbn", book.getIsbn());
                item.put("bookCover", book.getCoverUrl());
            } else {
                item.put("bookTitle", "未知图书");
                item.put("bookIsbn", "-");
                item.put("bookCover", "");
            }
            records.add(item);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        return ResponseEntity.ok(response);
    }

    // 获取借阅日历（前端调用 /api/borrows/calendar）
    @GetMapping("/calendar")
    public ResponseEntity<?> getBorrowCalendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        if (year == null) year = java.time.Year.now().getValue();
        if (month == null) month = java.time.LocalDate.now().getMonthValue();

        LambdaQueryWrapper<BorrowRecord> borrowQueryWrapper = new LambdaQueryWrapper<>();
        borrowQueryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getDeleted, 0);

        List<BorrowRecord> records = borrowRecordMapper.selectList(borrowQueryWrapper);

        List<Map<String, Object>> calendarData = new java.util.ArrayList<>();
        for (BorrowRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", record.getBorrowDate());
            item.put("type", "borrow");
            item.put("bookId", record.getBookId());
            item.put("status", record.getStatus());
            if (record.getDueDate() != null) {
                item.put("dueDate", record.getDueDate());
            }
            calendarData.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", calendarData);
        return ResponseEntity.ok(response);
    }

    // 图书借阅
    @PostMapping("/borrow")
    @OperationLog(module = "借阅管理", operation = "借书", description = "用户借阅图书")
    public ResponseEntity<Map<String, Object>> borrowBook(
            @RequestParam Long bookId,
            @RequestParam String bookBarcode,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        // 检查图书是否存在且未删除
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图书不存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查图书是否可借
        if (book.getAvailableCopies() <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图书无可用副本");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查用户是否已有未归还的该图书
        LambdaQueryWrapper<BorrowRecord> borrowQueryWrapper = new LambdaQueryWrapper<>();
        borrowQueryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getStatus, 0) // 0表示未归还
                .eq(BorrowRecord::getDeleted, 0);
        if (borrowRecordMapper.selectCount(borrowQueryWrapper) > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "您已借阅该图书，尚未归还");
            return ResponseEntity.badRequest().body(response);
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUserId(userId);
        borrowRecord.setBookId(bookId);
        borrowRecord.setBookBarcode(bookBarcode);
        borrowRecord.setBorrowDate(new Date());
        
        // 计算应还日期（默认30天）
        Date dueDate = new Date();
        dueDate.setTime(dueDate.getTime() + 30L * 24 * 60 * 60 * 1000);
        borrowRecord.setDueDate(dueDate);
        
        borrowRecord.setRenewCount(0);
        borrowRecord.setStatus(0); // 0表示未归还
        borrowRecord.setOperatorId(userId);
        borrowRecord.setDeleted(0);
        borrowRecord.setCreateTime(new Date());
        borrowRecord.setUpdateTime(new Date());

        // 保存借阅记录
        borrowRecordMapper.insert(borrowRecord);

        // 更新图书可用副本数
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setStatus("borrowed");
        bookMapper.updateById(book);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "借阅成功");
        response.put("data", borrowRecord);
        return ResponseEntity.ok(response);
    }

    // 图书归还
    @PostMapping("/return")
    @OperationLog(module = "借阅管理", operation = "还书", description = "用户归还图书")
    public ResponseEntity<Map<String, Object>> returnBook(
            @RequestParam Long recordId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        // 检查借阅记录是否存在
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "借阅记录不存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查是否是当前用户的借阅记录
        if (!borrowRecord.getUserId().equals(userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无权操作此借阅记录");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查是否已归还
        if (borrowRecord.getStatus() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "该图书已归还");
            return ResponseEntity.badRequest().body(response);
        }

        // 更新借阅记录
        borrowRecord.setReturnDate(new Date());
        borrowRecord.setStatus(1); // 1表示已归还
        borrowRecord.setUpdateTime(new Date());
        borrowRecordMapper.updateById(borrowRecord);

        // 更新图书可用副本数
        Book book = bookMapper.selectById(borrowRecord.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setStatus("available");
            bookMapper.updateById(book);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "归还成功");
        return ResponseEntity.ok(response);
    }

    // 图书续借
    @PostMapping("/renew")
    @OperationLog(module = "借阅管理", operation = "续借", description = "用户续借图书")
    public ResponseEntity<Map<String, Object>> renewBook(
            @RequestParam Long recordId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        // 检查借阅记录是否存在
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "借阅记录不存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查是否是当前用户的借阅记录
        if (!borrowRecord.getUserId().equals(userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "无权操作此借阅记录");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查是否已归还
        if (borrowRecord.getStatus() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "该图书已归还，无法续借");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查续借次数（默认最多续借2次）
        if (borrowRecord.getRenewCount() >= 2) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "续借次数已达上限");
            return ResponseEntity.badRequest().body(response);
        }

        // 更新续借信息
        borrowRecord.setRenewCount(borrowRecord.getRenewCount() + 1);
        // 续借30天
        Date newDueDate = new Date(borrowRecord.getDueDate().getTime() + 30L * 24 * 60 * 60 * 1000);
        borrowRecord.setDueDate(newDueDate);
        borrowRecord.setUpdateTime(new Date());
        borrowRecordMapper.updateById(borrowRecord);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "续借成功");
        response.put("newDueDate", newDueDate);
        return ResponseEntity.ok(response);
    }

    // 借阅记录查询（个人）
    @GetMapping("/records")
    public ResponseEntity<?> getBorrowRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        Page<BorrowRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> borrowQueryWrapper = new LambdaQueryWrapper<>();
        borrowQueryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getDeleted, 0);

        // 状态筛选
        if (status != null) {
            borrowQueryWrapper.eq(BorrowRecord::getStatus, status);
        }

        // 按借阅时间倒序
        borrowQueryWrapper.orderByDesc(BorrowRecord::getBorrowDate);

        IPage<BorrowRecord> result = borrowRecordMapper.selectPage(pageInfo, borrowQueryWrapper);
        return ResponseEntity.ok(result);
    }

    // 借阅记录查询（管理员）
    @GetMapping("/admin/records")
    public ResponseEntity<?> getAdminBorrowRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        Page<BorrowRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getDeleted, 0);

        if (userId != null) {
            queryWrapper.eq(BorrowRecord::getUserId, userId);
        }
        if (bookId != null) {
            queryWrapper.eq(BorrowRecord::getBookId, bookId);
        }
        if (status != null) {
            queryWrapper.eq(BorrowRecord::getStatus, status);
        }

        queryWrapper.orderByDesc(BorrowRecord::getBorrowDate);

        IPage<BorrowRecord> result = borrowRecordMapper.selectPage(pageInfo, queryWrapper);

        List<Map<String, Object>> enrichedRecords = new ArrayList<>();
        Set<Long> userIds = new java.util.HashSet<>();
        Set<Long> bookIds = new java.util.HashSet<>();

        for (BorrowRecord record : result.getRecords()) {
            userIds.add(record.getUserId());
            bookIds.add(record.getBookId());
        }

        Map<Long, String> userMap = new java.util.HashMap<>();
        Map<Long, String> userRealNameMap = new java.util.HashMap<>();
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<UserAccount> userQw = new LambdaQueryWrapper<>();
            userQw.in(UserAccount::getId, userIds).eq(UserAccount::getDeleted, 0);
            List<UserAccount> users = userAccountMapper.selectList(userQw);
            for (UserAccount u : users) {
                userMap.put(u.getId(), u.getUsername() != null ? u.getUsername() : "");
                userRealNameMap.put(u.getId(), u.getRealName() != null ? u.getRealName() : "");
            }
        }

        Map<Long, String> bookTitleMap = new java.util.HashMap<>();
        Map<Long, String> bookIsbnMap = new java.util.HashMap<>();
        if (!bookIds.isEmpty()) {
            LambdaQueryWrapper<Book> bookQw = new LambdaQueryWrapper<>();
            bookQw.in(Book::getId, bookIds);
            List<Book> books = bookMapper.selectList(bookQw);
            for (Book b : books) {
                bookTitleMap.put(b.getId(), b.getTitle() != null ? b.getTitle() : "未知图书");
                if (b.getIsbn() != null && !b.getIsbn().isEmpty()) {
                    bookIsbnMap.put(b.getId(), b.getIsbn());
                }
            }
        }

        for (BorrowRecord record : result.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getId());
            item.put("userId", record.getUserId());
            item.put("bookId", record.getBookId());
            item.put("bookBarcode", record.getBookBarcode());
            item.put("borrowDate", record.getBorrowDate());
            item.put("dueDate", record.getDueDate());
            item.put("returnDate", record.getReturnDate());
            item.put("renewCount", record.getRenewCount());
            item.put("maxRenewCount", 3);
            item.put("status", record.getStatus());

            String uname = userMap.getOrDefault(record.getUserId(), "-");

            String rname = userRealNameMap.get(record.getUserId());
            if (rname != null && !rname.isEmpty()) {
                uname = rname + "(" + uname + ")";
            }

            String sid = "";
            UserAccount ua = userAccountMapper.selectById(record.getUserId());
            if (ua != null) {
                sid = ua.getStudentId();
                if (sid == null || sid.isEmpty()) sid = ua.getFacultyId();
                if (sid == null || sid.isEmpty()) sid = ua.getUserId();
            }
            if (sid != null && !sid.isEmpty()) {
                uname += " [" + sid + "]";
            }

            item.put("username", uname);
            item.put("bookTitle", bookTitleMap.getOrDefault(record.getBookId(), "未知图书"));
            item.put("bookIsbn", bookIsbnMap.getOrDefault(record.getBookId(), "-"));

            enrichedRecords.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("records", enrichedRecords);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("pages", result.getPages());
        response.put("size", result.getSize());

        return ResponseEntity.ok(response);
    }

    // 借阅记录高级搜索（管理员）
    @GetMapping("/admin/advanced-search")
    public ResponseEntity<IPage<BorrowRecord>> advancedSearchBorrowRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate,
            @RequestParam(required = false) Long dueStartDate,
            @RequestParam(required = false) Long dueEndDate) {

        Page<BorrowRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getDeleted, 0);

        if (userId != null) {
            queryWrapper.eq(BorrowRecord::getUserId, userId);
        }
        
        if (bookId != null) {
            queryWrapper.eq(BorrowRecord::getBookId, bookId);
        }
        
        if (username != null && !username.isEmpty()) {
            LambdaQueryWrapper<UserAccount> userQuery = new LambdaQueryWrapper<>();
            userQuery.like(UserAccount::getUsername, username).eq(UserAccount::getDeleted, 0);
            List<UserAccount> users = userAccountMapper.selectList(userQuery);
            if (!users.isEmpty()) {
                List<Long> userIds = new java.util.ArrayList<>();
                for (UserAccount user : users) {
                    userIds.add(user.getId());
                }
                queryWrapper.in(BorrowRecord::getUserId, userIds);
            } else {
                queryWrapper.eq(BorrowRecord::getUserId, -1L);
            }
        }
        
        if (bookTitle != null && !bookTitle.isEmpty()) {
            LambdaQueryWrapper<Book> bookQuery = new LambdaQueryWrapper<>();
            bookQuery.like(Book::getTitle, bookTitle).eq(Book::getDeleted, 0);
            List<Book> books = bookMapper.selectList(bookQuery);
            if (!books.isEmpty()) {
                List<Long> bookIds = new java.util.ArrayList<>();
                for (Book book : books) {
                    bookIds.add(book.getId());
                }
                queryWrapper.in(BorrowRecord::getBookId, bookIds);
            } else {
                queryWrapper.eq(BorrowRecord::getBookId, -1L);
            }
        }
        
        if (status != null) {
            queryWrapper.eq(BorrowRecord::getStatus, status);
        }
        
        if (startDate != null) {
            queryWrapper.ge(BorrowRecord::getBorrowDate, new Date(startDate));
        }
        
        if (endDate != null) {
            queryWrapper.le(BorrowRecord::getBorrowDate, new Date(endDate));
        }
        
        if (dueStartDate != null) {
            queryWrapper.ge(BorrowRecord::getDueDate, new Date(dueStartDate));
        }
        
        if (dueEndDate != null) {
            queryWrapper.le(BorrowRecord::getDueDate, new Date(dueEndDate));
        }

        queryWrapper.orderByDesc(BorrowRecord::getBorrowDate);

        IPage<BorrowRecord> result = borrowRecordMapper.selectPage(pageInfo, queryWrapper);
        return ResponseEntity.ok(result);
    }

    // 获取借阅记录详情
    @GetMapping("/records/{id}")
    public ResponseEntity<?> getBorrowRecordById(@PathVariable Long id, Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        BorrowRecord borrowRecord = borrowRecordMapper.selectById(id);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            return ResponseEntity.notFound().build();
        }

        // 检查是否是当前用户的记录或管理员
        if (!borrowRecord.getUserId().equals(userId)) {
            // 这里可以添加管理员权限检查
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(borrowRecord);
    }
}
