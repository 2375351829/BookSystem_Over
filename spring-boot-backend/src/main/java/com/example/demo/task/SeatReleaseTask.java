package com.example.demo.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.model.Notification;
import com.example.demo.model.Seat;
import com.example.demo.model.SeatReservation;
import com.example.demo.repository.SeatMapper;
import com.example.demo.repository.SeatReservationMapper;
import com.example.demo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class SeatReleaseTask {

    private static final Logger logger = LoggerFactory.getLogger(SeatReleaseTask.class);

    private static final int STATUS_RESERVED = 0;
    private static final int STATUS_CHECKED_IN = 1;
    private static final int STATUS_CHECKED_OUT = 2;
    private static final int STATUS_CANCELLED = 3;
    private static final int STATUS_EXPIRED = 4;

    private static final int SEAT_AVAILABLE = 0;
    private static final int SEAT_OCCUPIED = 1;
    private static final int SEAT_MAINTENANCE = 2;

    private static final int CHECK_IN_TIMEOUT_MINUTES = 30;

    @Autowired
    private SeatReservationMapper seatReservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void releaseTimeoutSeats() {
        logger.info("开始执行座位释放定时任务...");

        try {
            Date now = new Date();

            LambdaQueryWrapper<SeatReservation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SeatReservation::getStatus, STATUS_RESERVED)
                    .eq(SeatReservation::getDeleted, 0);

            List<SeatReservation> reservations = seatReservationMapper.selectList(queryWrapper);
            logger.info("检查{}条座位预约记录", reservations.size());

            int releasedCount = 0;
            int notificationSentCount = 0;

            for (SeatReservation reservation : reservations) {
                try {
                    if (isCheckInTimeout(reservation, now)) {
                        boolean released = releaseSeatReservation(reservation);
                        if (released) {
                            releasedCount++;

                            boolean notificationSent = sendTimeoutNotification(reservation);
                            if (notificationSent) {
                                notificationSentCount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理座位预约超时失败，预约ID: {}, 错误: {}", reservation.getId(), e.getMessage(), e);
                }
            }

            logger.info("座位释放完成。释放座位: {}个，发送通知: {}条", releasedCount, notificationSentCount);

        } catch (Exception e) {
            logger.error("座位释放定时任务执行失败", e);
        }
    }

    private boolean isCheckInTimeout(SeatReservation reservation, Date now) {
        try {
            Date reserveDate = reservation.getReserveDate();
            String startTime = reservation.getStartTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reserveDate);

            String[] timeParts = startTime.split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);

            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            calendar.add(Calendar.MINUTE, CHECK_IN_TIMEOUT_MINUTES);

            Date timeoutThreshold = calendar.getTime();

            return now.after(timeoutThreshold);
        } catch (Exception e) {
            logger.error("判断签到超时失败，预约ID: {}", reservation.getId(), e);
            return false;
        }
    }

    private boolean releaseSeatReservation(SeatReservation reservation) {
        try {
            LambdaUpdateWrapper<SeatReservation> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SeatReservation::getId, reservation.getId())
                    .eq(SeatReservation::getStatus, STATUS_RESERVED)
                    .set(SeatReservation::getStatus, STATUS_EXPIRED);
            
            boolean updated = seatReservationMapper.update(null, updateWrapper) > 0;

            if (updated) {
                Seat seat = seatMapper.selectById(reservation.getSeatId());
                if (seat != null && seat.getStatus() != SEAT_AVAILABLE) {
                    LambdaUpdateWrapper<Seat> seatUpdateWrapper = new LambdaUpdateWrapper<>();
                    seatUpdateWrapper.eq(Seat::getId, seat.getId())
                            .set(Seat::getStatus, SEAT_AVAILABLE);
                    seatMapper.update(null, seatUpdateWrapper);
                    logger.info("释放座位成功，座位ID: {}", seat.getId());
                }
            }

            return updated;
        } catch (Exception e) {
            logger.error("释放座位预约失败，预约ID: {}", reservation.getId(), e);
            return false;
        }
    }

    private boolean sendTimeoutNotification(SeatReservation reservation) {
        try {
            Seat seat = seatMapper.selectById(reservation.getSeatId());
            String seatNumber = seat != null ? seat.getSeatNumber() : "未知座位";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String reserveTimeStr = sdf.format(reservation.getReserveDate()) + " " + reservation.getStartTime();

            String title = "座位预约超时取消通知";
            String content = String.format("您预约的座位%s（预约时间：%s）因超时未签到已自动取消。座位已释放供其他用户预约。",
                    seatNumber, reserveTimeStr);

            notificationService.createNotification(reservation.getUserId(), title, content, Notification.TYPE_SYSTEM);
            logger.info("发送座位超时通知成功，用户ID: {}, 座位: {}", reservation.getUserId(), seatNumber);

            return true;
        } catch (Exception e) {
            logger.error("发送座位超时通知失败，预约ID: {}", reservation.getId(), e);
            return false;
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processExpiredDateReservations() {
        logger.info("开始执行过期日期座位预约处理定时任务...");

        try {
            Date today = clearTime(new Date());

            LambdaUpdateWrapper<SeatReservation> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SeatReservation::getStatus, STATUS_RESERVED)
                    .lt(SeatReservation::getReserveDate, today)
                    .eq(SeatReservation::getDeleted, 0)
                    .set(SeatReservation::getStatus, STATUS_EXPIRED);

            int count = seatReservationMapper.update(null, updateWrapper);
            logger.info("处理过期日期座位预约完成，共{}条", count);

        } catch (Exception e) {
            logger.error("处理过期日期座位预约失败", e);
        }
    }

    private Date clearTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
