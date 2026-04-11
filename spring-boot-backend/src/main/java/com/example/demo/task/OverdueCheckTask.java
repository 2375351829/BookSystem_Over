package com.example.demo.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.FineRecord;
import com.example.demo.model.Notification;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.FineRecordMapper;
import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class OverdueCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(OverdueCheckTask.class);

    private static final int STATUS_BORROWING = 0;
    private static final int STATUS_RETURNED = 1;
    private static final int STATUS_OVERDUE = 2;

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.50");

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private FineRecordMapper fineRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkOverdueRecords() {
        logger.info("开始执行逾期检查定时任务...");

        try {
            Date now = new Date();

            LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BorrowRecord::getStatus, STATUS_BORROWING)
                    .lt(BorrowRecord::getDueDate, now)
                    .eq(BorrowRecord::getDeleted, 0);

            List<BorrowRecord> overdueRecords = borrowRecordMapper.selectList(queryWrapper);
            logger.info("发现{}条逾期记录", overdueRecords.size());

            int processedCount = 0;
            int fineCreatedCount = 0;
            int notificationSentCount = 0;

            for (BorrowRecord record : overdueRecords) {
                try {
                    boolean updated = updateRecordStatusToOverdue(record.getId());
                    if (updated) {
                        processedCount++;

                        FineRecord fineRecord = createFineRecord(record);
                        if (fineRecord != null) {
                            fineCreatedCount++;
                        }

                        boolean notificationSent = sendOverdueNotification(record);
                        if (notificationSent) {
                            notificationSentCount++;
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理逾期记录失败，记录ID: {}, 错误: {}", record.getId(), e.getMessage(), e);
                }
            }

            logger.info("逾期检查完成。处理记录: {}条，创建罚款: {}条，发送通知: {}条",
                    processedCount, fineCreatedCount, notificationSentCount);

        } catch (Exception e) {
            logger.error("逾期检查定时任务执行失败", e);
        }
    }

    private boolean updateRecordStatusToOverdue(Long recordId) {
        LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BorrowRecord::getId, recordId)
                .eq(BorrowRecord::getStatus, STATUS_BORROWING)
                .set(BorrowRecord::getStatus, STATUS_OVERDUE)
                .set(BorrowRecord::getUpdateTime, new Date());
        return borrowRecordMapper.update(null, updateWrapper) > 0;
    }

    private FineRecord createFineRecord(BorrowRecord borrowRecord) {
        try {
            Date now = new Date();
            long diffDays = (now.getTime() - borrowRecord.getDueDate().getTime()) / (24 * 60 * 60 * 1000);
            if (diffDays <= 0) {
                return null;
            }

            BigDecimal fineAmount = FINE_PER_DAY.multiply(new BigDecimal(diffDays));

            FineRecord fineRecord = new FineRecord();
            fineRecord.setUserId(borrowRecord.getUserId());
            fineRecord.setBorrowRecordId(borrowRecord.getId());
            fineRecord.setFineType("OVERDUE");
            fineRecord.setAmount(fineAmount);
            fineRecord.setPaidStatus(0);
            fineRecord.setRemarks("逾期罚款，逾期天数: " + diffDays + "天");
            fineRecord.setDeleted(0);
            fineRecord.setCreateTime(now);

            fineRecordMapper.insert(fineRecord);
            logger.info("创建罚款记录成功，用户ID: {}, 金额: {}", borrowRecord.getUserId(), fineAmount);

            return fineRecord;
        } catch (Exception e) {
            logger.error("创建罚款记录失败，借阅记录ID: {}", borrowRecord.getId(), e);
            return null;
        }
    }

    private boolean sendOverdueNotification(BorrowRecord record) {
        try {
            Book book = bookMapper.selectById(record.getBookId());
            String bookTitle = book != null ? book.getTitle() : "未知图书";

            Date now = new Date();
            int overdueDays = (int) ((now.getTime() - record.getDueDate().getTime()) / (24 * 60 * 60 * 1000));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dueDateStr = sdf.format(record.getDueDate());

            String title = "图书逾期通知";
            String content = String.format("您借阅的《%s》已于%s逾期，已逾期%d天。请尽快归还，以免产生更多罚款。当前罚款金额：%.2f元。",
                    bookTitle, dueDateStr, overdueDays, FINE_PER_DAY.multiply(new BigDecimal(overdueDays)));

            notificationService.createNotification(record.getUserId(), title, content, Notification.TYPE_OVERDUE);
            logger.info("发送逾期通知成功，用户ID: {}, 图书: {}", record.getUserId(), bookTitle);

            return true;
        } catch (Exception e) {
            logger.error("发送逾期通知失败，借阅记录ID: {}", record.getId(), e);
            return false;
        }
    }
}
