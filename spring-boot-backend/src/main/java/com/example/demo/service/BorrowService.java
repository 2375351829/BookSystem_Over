package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.Reservation;
import com.example.demo.model.UserAccount;
import com.example.demo.model.UserType;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class BorrowService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BorrowService.class);

    @Autowired
    private BorrowRuleService borrowRuleService;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReservationService reservationService;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId, String bookBarcode) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("用户状态异常，无法借阅");
        }

        UserType userType = UserType.fromCode(user.getUserType());
        if (!canUserBorrow(userId, userType)) {
            int maxLimit = borrowRuleService.getMaxBorrowLimit(userType);
            throw new RuntimeException("用户借阅数量已达上限（最大" + maxLimit + "本）");
        }

        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            throw new RuntimeException("图书不存在");
        }

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("图书无可用副本");
        }

        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getStatus, 0)
                .eq(BorrowRecord::getDeleted, 0);
        if (borrowRecordMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("您已借阅该图书，尚未归还");
        }

        int borrowDays = borrowRuleService.getBorrowDays(userType);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUserId(userId);
        borrowRecord.setBookId(bookId);
        borrowRecord.setBookBarcode(bookBarcode);
        borrowRecord.setBorrowDate(new Date());

        Date dueDate = new Date(System.currentTimeMillis() + (long) borrowDays * 24 * 60 * 60 * 1000);
        borrowRecord.setDueDate(dueDate);

        borrowRecord.setRenewCount(0);
        borrowRecord.setStatus(0);
        borrowRecord.setOperatorId(userId);
        borrowRecord.setDeleted(0);
        borrowRecord.setCreateTime(new Date());
        borrowRecord.setUpdateTime(new Date());

        borrowRecordMapper.insert(borrowRecord);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() <= 0) {
            book.setStatus("borrowed");
        }
        book.setUpdateTime(new Date());
        bookMapper.updateById(book);

        // 发送借阅成功通知
        try {
            String title = "借阅成功通知";
            String content = String.format("您已成功借阅《%s》，借阅日期：%s，应还日期：%s",
                    book.getTitle(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(borrowRecord.getBorrowDate()),
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(dueDate));
            notificationService.sendNotification(userId, "BORROW", title, content);
            logger.info("借阅成功通知已发送: userId={}, bookId={}", userId, bookId);
        } catch (Exception e) {
            logger.error("发送借阅成功通知失败: userId={}, bookId={}, error={}", userId, bookId, e.getMessage());
        }

        return borrowRecord;
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId, Long userId) {
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!borrowRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此借阅记录");
        }

        if (borrowRecord.getStatus() == 1) {
            throw new RuntimeException("该图书已归还");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        UserType userType = UserType.fromCode(user != null ? user.getUserType() : null);
        BigDecimal fineRate = borrowRuleService.getFineRate(userType);

        Date returnDate = new Date();
        borrowRecord.setReturnDate(returnDate);
        borrowRecord.setStatus(1);
        borrowRecord.setUpdateTime(returnDate);

        BigDecimal fine = calculateFine(borrowRecord.getDueDate(), returnDate, fineRate);
        if (fine.compareTo(BigDecimal.ZERO) > 0) {
            borrowRecord.setRemarks("逾期罚款: " + fine + "元");
        }

        borrowRecordMapper.updateById(borrowRecord);

        Book book = bookMapper.selectById(borrowRecord.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            if (book.getAvailableCopies() > 0) {
                book.setStatus("available");
            }
            book.setUpdateTime(new Date());
            bookMapper.updateById(book);
        }

        // 发送归还成功通知
        try {
            String title = "归还成功通知";
            StringBuilder content = new StringBuilder();
            content.append(String.format("您已成功归还《%s》，归还日期：%s",
                    book != null ? book.getTitle() : "未知图书",
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(returnDate)));
            
            if (fine.compareTo(BigDecimal.ZERO) > 0) {
                content.append(String.format("，逾期罚款：%.2f元", fine));
            }
            
            notificationService.sendNotification(userId, "BORROW", title, content.toString());
            logger.info("归还成功通知已发送: userId={}, bookId={}", userId, borrowRecord.getBookId());
        } catch (Exception e) {
            logger.error("发送归还成功通知失败: userId={}, bookId={}, error={}", userId, borrowRecord.getBookId(), e.getMessage());
        }

        // 检查预约队列并通知下一位预约用户
        if (book != null) {
            try {
                Reservation nextReservation = reservationService.handleReservationNotification(book.getId());
                if (nextReservation != null) {
                    String notifyTitle = "图书可借通知";
                    String notifyContent = String.format("您预约的《%s》已可借阅，请在7天内前往图书馆取书，逾期将自动取消预约。",
                            book.getTitle());
                    notificationService.sendNotification(nextReservation.getUserId(), "RESERVATION", notifyTitle, notifyContent);
                    logger.info("预约通知已发送: userId={}, bookId={}, reservationId={}", 
                            nextReservation.getUserId(), book.getId(), nextReservation.getId());
                }
            } catch (Exception e) {
                logger.error("处理预约通知失败: bookId={}, error={}", book.getId(), e.getMessage());
            }
        }

        return borrowRecord;
    }

    @Transactional
    public BorrowRecord renewBook(Long recordId, Long userId) {
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!borrowRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此借阅记录");
        }

        if (borrowRecord.getStatus() == 1) {
            throw new RuntimeException("该图书已归还，无法续借");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        UserType userType = UserType.fromCode(user != null ? user.getUserType() : null);
        int maxRenewCount = borrowRuleService.getMaxRenewCount(userType);

        if (borrowRecord.getRenewCount() >= maxRenewCount) {
            throw new RuntimeException("续借次数已达上限（最大" + maxRenewCount + "次）");
        }

        if (isOverdue(borrowRecord.getDueDate())) {
            throw new RuntimeException("图书已逾期，无法续借，请先归还");
        }

        int renewDays = borrowRuleService.getRenewDays(userType);

        borrowRecord.setRenewCount(borrowRecord.getRenewCount() + 1);
        Date newDueDate = new Date(borrowRecord.getDueDate().getTime() + (long) renewDays * 24 * 60 * 60 * 1000);
        borrowRecord.setDueDate(newDueDate);
        borrowRecord.setUpdateTime(new Date());
        borrowRecordMapper.updateById(borrowRecord);

        return borrowRecord;
    }

    public IPage<BorrowRecord> getBorrowRecords(Long userId, Integer page, Integer size, Integer status) {
        Page<BorrowRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getDeleted, 0);

        if (status != null) {
            queryWrapper.eq(BorrowRecord::getStatus, status);
        }

        queryWrapper.orderByDesc(BorrowRecord::getBorrowDate);

        return borrowRecordMapper.selectPage(pageInfo, queryWrapper);
    }

    public IPage<BorrowRecord> getBorrowRecordsAdmin(Integer page, Integer size, Long userId, Long bookId, Integer status) {
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

        return borrowRecordMapper.selectPage(pageInfo, queryWrapper);
    }

    public int getCurrentBorrowCount(Long userId) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, 0)
                .eq(BorrowRecord::getDeleted, 0);
        return Math.toIntExact(borrowRecordMapper.selectCount(queryWrapper));
    }

    public boolean canUserBorrow(Long userId, UserType userType) {
        int currentCount = getCurrentBorrowCount(userId);
        int maxLimit = borrowRuleService.getMaxBorrowLimit(userType);
        return currentCount < maxLimit;
    }

    public boolean canUserBorrow(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        UserType userType = UserType.fromCode(user != null ? user.getUserType() : null);
        return canUserBorrow(userId, userType);
    }

    public BorrowRecord getBorrowRecordById(Long recordId) {
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            return null;
        }
        return borrowRecord;
    }

    public boolean isRecordOwner(Long recordId, Long userId) {
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(recordId);
        if (borrowRecord == null || borrowRecord.getDeleted() == 1) {
            return false;
        }
        return borrowRecord.getUserId().equals(userId);
    }

    private BigDecimal calculateFine(Date dueDate, Date returnDate, BigDecimal fineRate) {
        if (returnDate.after(dueDate)) {
            long diffDays = (returnDate.getTime() - dueDate.getTime()) / (24 * 60 * 60 * 1000);
            return fineRate.multiply(new BigDecimal(diffDays));
        }
        return BigDecimal.ZERO;
    }

    private boolean isOverdue(Date dueDate) {
        return new Date().after(dueDate);
    }

    public int getOverdueCount(Long userId) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, 0)
                .eq(BorrowRecord::getDeleted, 0)
                .lt(BorrowRecord::getDueDate, new Date());
        return Math.toIntExact(borrowRecordMapper.selectCount(queryWrapper));
    }

    public boolean hasUnreturnedBook(Long userId, Long bookId) {
        LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getStatus, 0)
                .eq(BorrowRecord::getDeleted, 0);
        return borrowRecordMapper.selectCount(queryWrapper) > 0;
    }

    public int getMaxBorrowLimit(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        UserType userType = UserType.fromCode(user != null ? user.getUserType() : null);
        return borrowRuleService.getMaxBorrowLimit(userType);
    }
}
