package com.example.demo.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.Book;
import com.example.demo.model.Notification;
import com.example.demo.model.Reservation;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.ReservationMapper;
import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ReservationTimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(ReservationTimeoutTask.class);

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_READY_FOR_PICKUP = 1;
    private static final int STATUS_CANCELLED = 2;
    private static final int STATUS_COMPLETED = 3;
    private static final int STATUS_EXPIRED = 4;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void processExpiredReservations() {
        logger.info("开始执行预约超时处理定时任务...");

        try {
            Date now = new Date();

            LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Reservation::getStatus, STATUS_PENDING, STATUS_READY_FOR_PICKUP)
                    .lt(Reservation::getExpireDate, now)
                    .eq(Reservation::getDeleted, 0);

            List<Reservation> expiredReservations = reservationMapper.selectList(queryWrapper);
            logger.info("发现{}条过期预约", expiredReservations.size());

            int processedCount = 0;
            int notificationSentCount = 0;

            for (Reservation reservation : expiredReservations) {
                try {
                    boolean updated = updateReservationStatusToExpired(reservation.getId());
                    if (updated) {
                        processedCount++;

                        boolean notificationSent = sendExpirationNotification(reservation);
                        if (notificationSent) {
                            notificationSentCount++;
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理过期预约失败，预约ID: {}, 错误: {}", reservation.getId(), e.getMessage(), e);
                }
            }

            logger.info("预约超时处理完成。处理记录: {}条，发送通知: {}条", processedCount, notificationSentCount);

        } catch (Exception e) {
            logger.error("预约超时处理定时任务执行失败", e);
        }
    }

    private boolean updateReservationStatusToExpired(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return false;
        }

        reservation.setStatus(STATUS_EXPIRED);
        return reservationMapper.updateById(reservation) > 0;
    }

    private boolean sendExpirationNotification(Reservation reservation) {
        try {
            Book book = bookMapper.selectById(reservation.getBookId());
            String bookTitle = book != null ? book.getTitle() : "未知图书";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String expireDateStr = sdf.format(reservation.getExpireDate());

            String title = "图书预约过期通知";
            String content;

            if (reservation.getStatus() == STATUS_READY_FOR_PICKUP) {
                content = String.format("您预约的《%s》已于%s过期，取书期限已到，预约已自动取消。如仍需借阅，请重新预约。",
                        bookTitle, expireDateStr);
            } else {
                content = String.format("您预约的《%s》已于%s过期，预约已自动取消。如仍需借阅，请重新预约。",
                        bookTitle, expireDateStr);
            }

            notificationService.createNotification(reservation.getUserId(), title, content, Notification.TYPE_SYSTEM);
            logger.info("发送预约过期通知成功，用户ID: {}, 图书: {}", reservation.getUserId(), bookTitle);

            return true;
        } catch (Exception e) {
            logger.error("发送预约过期通知失败，预约ID: {}", reservation.getId(), e);
            return false;
        }
    }
}
