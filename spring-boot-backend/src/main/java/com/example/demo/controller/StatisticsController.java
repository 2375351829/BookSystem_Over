package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        LambdaQueryWrapper<Book> bookQueryWrapper = new LambdaQueryWrapper<>();
        bookQueryWrapper.eq(Book::getDeleted, 0);
        long totalBooks = bookMapper.selectCount(bookQueryWrapper);
        dashboard.put("totalBooks", totalBooks);

        LambdaQueryWrapper<Book> availableQueryWrapper = new LambdaQueryWrapper<>();
        availableQueryWrapper.eq(Book::getDeleted, 0).gt(Book::getAvailableCopies, 0);
        long availableBooks = bookMapper.selectCount(availableQueryWrapper);
        dashboard.put("availableBooks", availableBooks);

        long borrowedBooks = totalBooks - availableBooks;
        dashboard.put("borrowedBooks", borrowedBooks);

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date startOfDay = todayStart.getTime();
        todayStart.add(Calendar.DAY_OF_YEAR, 1);
        Date endOfDay = todayStart.getTime();

        LambdaQueryWrapper<BorrowRecord> todayBorrowWrapper = new LambdaQueryWrapper<>();
        todayBorrowWrapper.eq(BorrowRecord::getDeleted, 0)
                .ge(BorrowRecord::getBorrowDate, startOfDay)
                .lt(BorrowRecord::getBorrowDate, endOfDay);
        long todayBorrows = borrowRecordMapper.selectCount(todayBorrowWrapper);
        dashboard.put("todayBorrows", todayBorrows);

        LambdaQueryWrapper<BorrowRecord> todayReturnWrapper = new LambdaQueryWrapper<>();
        todayReturnWrapper.eq(BorrowRecord::getDeleted, 0)
                .ge(BorrowRecord::getReturnDate, startOfDay)
                .lt(BorrowRecord::getReturnDate, endOfDay);
        long todayReturns = borrowRecordMapper.selectCount(todayReturnWrapper);
        dashboard.put("todayReturns", todayReturns);

        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getDeleted, 0)
                .eq(BorrowRecord::getStatus, 0)
                .lt(BorrowRecord::getDueDate, new Date());
        long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);
        dashboard.put("overdueCount", overdueCount);

        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        List<Long> activeUserIds = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getDeleted, 0)
                        .ge(BorrowRecord::getBorrowDate, thirtyDaysAgo)
                        .select(BorrowRecord::getUserId)
        ).stream()
                .map(BorrowRecord::getUserId)
                .distinct()
                .collect(Collectors.toList());
        dashboard.put("activeUsers", activeUserIds.size());

        LambdaQueryWrapper<UserAccount> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(UserAccount::getDeleted, 0);
        long totalUsers = userAccountMapper.selectCount(userQueryWrapper);
        dashboard.put("totalUsers", totalUsers);

        return ResponseEntity.ok(dashboard);
    }

    // 图书统计
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/books")
    public ResponseEntity<List<Map<String, Object>>> getBookStatistics() {
        // 分类统计 - 返回前端期望的格式
        LambdaQueryWrapper<Book> bookQueryWrapper = new LambdaQueryWrapper<>();
        bookQueryWrapper.eq(Book::getDeleted, 0);
        List<Book> allBooks = bookMapper.selectList(bookQueryWrapper);
        
        Map<String, Long> categoryCount = allBooks.stream()
                .collect(Collectors.groupingBy(book -> book.getCategory() != null ? book.getCategory() : "未分类", Collectors.counting()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        categoryCount.forEach((name, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("count", count);
            result.add(item);
        });
        
        // 按数量降序排序
        result.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));

        return ResponseEntity.ok(result);
    }

    // 借阅统计
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrow")
    public ResponseEntity<Map<String, Object>> getBorrowStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "thisMonth") String range) {
        Map<String, Object> statistics = new HashMap<>();

        // 总借阅次数
        LambdaQueryWrapper<BorrowRecord> borrowQueryWrapper = new LambdaQueryWrapper<>();
        borrowQueryWrapper.eq(BorrowRecord::getDeleted, 0);
        long totalBorrows = borrowRecordMapper.selectCount(borrowQueryWrapper);
        statistics.put("totalBorrows", totalBorrows);

        // 未归还数量
        LambdaQueryWrapper<BorrowRecord> unreturnedQueryWrapper = new LambdaQueryWrapper<>();
        unreturnedQueryWrapper.eq(BorrowRecord::getDeleted, 0).eq(BorrowRecord::getStatus, 0);
        long unreturnedCount = borrowRecordMapper.selectCount(unreturnedQueryWrapper);
        statistics.put("unreturnedCount", unreturnedCount);

        // 已归还数量
        long returnedCount = totalBorrows - unreturnedCount;
        statistics.put("returnedCount", returnedCount);

        // 借阅趋势（最近6个月）
        List<String> months = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        for (int i = 5; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -i);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date monthStart = calendar.getTime();
            
            calendar.add(Calendar.MONTH, 1);
            Date monthEnd = calendar.getTime();
            
            String monthLabel = String.format("%d月", calendar.get(Calendar.MONTH) == 0 ? 12 : calendar.get(Calendar.MONTH));
            months.add(monthLabel);

            LambdaQueryWrapper<BorrowRecord> monthlyQueryWrapper = new LambdaQueryWrapper<>();
            monthlyQueryWrapper.eq(BorrowRecord::getDeleted, 0)
                    .ge(BorrowRecord::getBorrowDate, monthStart)
                    .lt(BorrowRecord::getBorrowDate, monthEnd);
            long borrowCount = borrowRecordMapper.selectCount(monthlyQueryWrapper);
            values.add(borrowCount);
        }
        
        statistics.put("months", months);
        statistics.put("values", values);

        return ResponseEntity.ok(statistics);
    }

    // 用户统计
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总用户数
        LambdaQueryWrapper<UserAccount> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(UserAccount::getDeleted, 0);
        long totalUsers = userAccountMapper.selectCount(userQueryWrapper);
        statistics.put("totalUsers", totalUsers);

        // 活跃用户数（最近30天有借阅记录的用户）
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        List<Long> activeUserIds = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getDeleted, 0)
                        .ge(BorrowRecord::getBorrowDate, thirtyDaysAgo)
                        .select(BorrowRecord::getUserId)
        ).stream()
                .map(BorrowRecord::getUserId)
                .distinct()
                .collect(Collectors.toList());
        statistics.put("activeUsers", activeUserIds.size());

        // 用户类型统计
        Map<String, Long> userTypeStatistics = userAccountMapper.selectList(userQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(UserAccount::getRole, Collectors.counting()));
        statistics.put("userTypeStatistics", userTypeStatistics);

        return ResponseEntity.ok(statistics);
    }

    // 热门图书榜单 / 借阅明细 / 分类统计
    @GetMapping("/hot-books")
    public ResponseEntity<Map<String, Object>> getHotBooks(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String range) {
        
        Map<String, Object> result = new HashMap<>();
        
        if ("borrow".equals(type)) {
            // 借阅明细 - 按月份统计
            List<Map<String, Object>> borrowDetails = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            
            for (int i = 5; i >= 0; i--) {
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -i);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date monthStart = calendar.getTime();
                
                calendar.add(Calendar.MONTH, 1);
                Date monthEnd = calendar.getTime();
                
                String monthLabel = String.format("%d-%02d", 
                    calendar.get(Calendar.YEAR), 
                    calendar.get(Calendar.MONTH) == 0 ? 12 : calendar.get(Calendar.MONTH));

                // 借阅量
                LambdaQueryWrapper<BorrowRecord> borrowWrapper = new LambdaQueryWrapper<>();
                borrowWrapper.eq(BorrowRecord::getDeleted, 0)
                        .ge(BorrowRecord::getBorrowDate, monthStart)
                        .lt(BorrowRecord::getBorrowDate, monthEnd);
                long borrowCount = borrowRecordMapper.selectCount(borrowWrapper);

                // 归还量
                LambdaQueryWrapper<BorrowRecord> returnWrapper = new LambdaQueryWrapper<>();
                returnWrapper.eq(BorrowRecord::getDeleted, 0)
                        .ge(BorrowRecord::getReturnDate, monthStart)
                        .lt(BorrowRecord::getReturnDate, monthEnd);
                long returnCount = borrowRecordMapper.selectCount(returnWrapper);

                // 逾期数
                LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
                overdueWrapper.eq(BorrowRecord::getDeleted, 0)
                        .eq(BorrowRecord::getStatus, 0)
                        .lt(BorrowRecord::getDueDate, monthEnd);
                long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);

                // 新增用户
                LambdaQueryWrapper<UserAccount> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(UserAccount::getDeleted, 0)
                        .ge(UserAccount::getCreateTime, monthStart)
                        .lt(UserAccount::getCreateTime, monthEnd);
                long newUsers = userAccountMapper.selectCount(userWrapper);

                Map<String, Object> detail = new HashMap<>();
                detail.put("month", monthLabel);
                detail.put("borrowCount", borrowCount);
                detail.put("returnCount", returnCount);
                detail.put("overdueCount", overdueCount);
                detail.put("newUsers", newUsers);
                borrowDetails.add(detail);
            }
            
            result.put("list", borrowDetails);
            result.put("total", borrowDetails.size());
            
        } else if ("user".equals(type)) {
            // 用户排行
            List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>()
                            .eq(BorrowRecord::getDeleted, 0)
            );

            Map<Long, Long> userBorrowCount = borrowRecords.stream()
                    .collect(Collectors.groupingBy(BorrowRecord::getUserId, Collectors.counting()));

            List<Map.Entry<Long, Long>> sortedEntries = userBorrowCount.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .collect(Collectors.toList());

            List<Map<String, Object>> userList = new ArrayList<>();
            int rank = 1;
            for (Map.Entry<Long, Long> entry : sortedEntries) {
                UserAccount user = userAccountMapper.selectById(entry.getKey());
                if (user != null && user.getDeleted() == 0) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("rank", rank++);
                    userInfo.put("username", user.getUsername());
                    userInfo.put("realName", user.getRealName() != null ? user.getRealName() : user.getUsername());
                    userInfo.put("borrowCount", entry.getValue());
                    userInfo.put("activeDays", 1);
                    userList.add(userInfo);
                }
            }
            
            result.put("list", userList);
            result.put("total", userList.size());
            
        } else if ("category".equals(type)) {
            // 分类统计
            LambdaQueryWrapper<Book> bookQueryWrapper = new LambdaQueryWrapper<>();
            bookQueryWrapper.eq(Book::getDeleted, 0);
            List<Book> allBooks = bookMapper.selectList(bookQueryWrapper);
            
            // 统计每个分类的图书数量
            Map<String, Long> categoryBookCount = allBooks.stream()
                    .collect(Collectors.groupingBy(book -> book.getCategory() != null ? book.getCategory() : "未分类", Collectors.counting()));
            
            // 统计每个分类的借阅次数
            List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>()
                            .eq(BorrowRecord::getDeleted, 0)
            );
            
            long totalBorrows = borrowRecords.size();
            
            // 计算分类借阅统计
            Map<String, Long> categoryBorrowCount = new HashMap<>();
            for (BorrowRecord record : borrowRecords) {
                Book book = bookMapper.selectById(record.getBookId());
                if (book != null) {
                    String category = book.getCategory() != null ? book.getCategory() : "未分类";
                    categoryBorrowCount.merge(category, 1L, Long::sum);
                }
            }
            
            List<Map<String, Object>> categoryList = new ArrayList<>();
            for (Map.Entry<String, Long> entry : categoryBookCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("code", entry.getKey().substring(0, Math.min(2, entry.getKey().length())));
                item.put("bookCount", entry.getValue());
                item.put("borrowCount", categoryBorrowCount.getOrDefault(entry.getKey(), 0L));
                double percentage = totalBorrows > 0 ? 
                    (double) categoryBorrowCount.getOrDefault(entry.getKey(), 0L) / totalBorrows * 100 : 0;
                item.put("percentage", Math.round(percentage * 10) / 10.0);
                categoryList.add(item);
            }
            
            categoryList.sort((a, b) -> Long.compare((Long) b.get("borrowCount"), (Long) a.get("borrowCount")));
            
            result.put("list", categoryList);
            result.put("total", categoryList.size());
            
        } else {
            // 默认：热门图书
            List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                    new LambdaQueryWrapper<BorrowRecord>()
                            .eq(BorrowRecord::getDeleted, 0)
            );

            Map<Long, Long> bookBorrowCount = borrowRecords.stream()
                    .collect(Collectors.groupingBy(BorrowRecord::getBookId, Collectors.counting()));

            List<Map.Entry<Long, Long>> sortedEntries = bookBorrowCount.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(limit)
                    .collect(Collectors.toList());

            List<Map<String, Object>> hotBooks = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : sortedEntries) {
                Book book = bookMapper.selectById(entry.getKey());
                if (book != null && book.getDeleted() == 0) {
                    Map<String, Object> bookInfo = new HashMap<>();
                    bookInfo.put("id", book.getId());
                    bookInfo.put("title", book.getTitle());
                    bookInfo.put("author", book.getAuthor());
                    bookInfo.put("category", book.getCategory());
                    bookInfo.put("borrowCount", entry.getValue());
                    bookInfo.put("availableCopies", book.getAvailableCopies());
                    hotBooks.add(bookInfo);
                }
            }
            
            result.put("list", hotBooks);
            result.put("total", hotBooks.size());
        }

        return ResponseEntity.ok(result);
    }

    // 热门用户榜单
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hot-users")
    public ResponseEntity<List<Map<String, Object>>> getHotUsers(
            @RequestParam(defaultValue = "10") int limit) {
        // 统计用户借阅次数
        List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getDeleted, 0)
        );

        Map<Long, Long> userBorrowCount = borrowRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getUserId, Collectors.counting()));

        // 按借阅次数排序
        List<Map.Entry<Long, Long>> sortedEntries = userBorrowCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // 构建热门用户列表
        List<Map<String, Object>> hotUsers = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, Long> entry : sortedEntries) {
            UserAccount user = userAccountMapper.selectById(entry.getKey());
            if (user != null && user.getDeleted() == 0) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("rank", rank++);
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("realName", user.getRealName() != null ? user.getRealName() : user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole());
                userInfo.put("borrowCount", entry.getValue());
                userInfo.put("activeDays", 1); // 简化处理
                hotUsers.add(userInfo);
            }
        }

        return ResponseEntity.ok(hotUsers);
    }
}
