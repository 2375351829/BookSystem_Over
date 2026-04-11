package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.ReadingRoom;
import com.example.demo.model.Seat;
import com.example.demo.model.SeatReservation;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.ReadingRoomMapper;
import com.example.demo.repository.SeatMapper;
import com.example.demo.repository.SeatReservationMapper;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SeatService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SeatService.class);

    @Autowired
    private ReadingRoomMapper readingRoomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private SeatReservationMapper seatReservationMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private NotificationService notificationService;

    private static final int STATUS_RESERVED = 0;
    private static final int STATUS_CHECKED_IN = 1;
    private static final int STATUS_CHECKED_OUT = 2;
    private static final int STATUS_CANCELLED = 3;
    private static final int STATUS_EXPIRED = 4;

    private static final int SEAT_AVAILABLE = 0;
    private static final int SEAT_OCCUPIED = 1;
    private static final int SEAT_MAINTENANCE = 2;
    private static final int SEAT_RESERVED = 1;

    private static final int MAX_ADVANCE_DAYS = 7;
    private static final int CANCEL_ADVANCE_MINUTES = 30;
    private static final int MAX_VIOLATION_COUNT = 3;
    private static final int VIOLATION_BAN_DAYS = 7;
    private static final int MAX_DAILY_RESERVATIONS = 3;
    private static final int CHECKIN_BEFORE_MINUTES = 15;
    private static final int CHECKIN_AFTER_MINUTES = 30;

    public List<ReadingRoom> getReadingRoomList() {
        QueryWrapper<ReadingRoom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .eq("status", 1)
                .orderByAsc("id");
        return readingRoomMapper.selectList(queryWrapper);
    }

    public ReadingRoom getReadingRoomById(Long roomId) {
        return readingRoomMapper.selectById(roomId);
    }

    public List<Map<String, Object>> getRoomSeats(Long roomId, Date date) {
        ReadingRoom room = readingRoomMapper.selectById(roomId);
        if (room == null || room.getDeleted() == 1) {
            throw new RuntimeException("阅览室不存在");
        }

        QueryWrapper<Seat> seatQueryWrapper = new QueryWrapper<>();
        seatQueryWrapper.eq("room_id", roomId)
                .eq("deleted", 0)
                .orderByAsc("row_num")
                .orderByAsc("col_num");
        List<Seat> seats = seatMapper.selectList(seatQueryWrapper);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
        }
        Date dayStart = cal.getTime();
        cal.add(java.util.Calendar.DATE, 1);
        Date dayEnd = cal.getTime();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Seat seat : seats) {
            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("id", seat.getId());
            seatInfo.put("seatNumber", seat.getSeatNumber());
            seatInfo.put("row", seat.getRowNum());
            seatInfo.put("column", seat.getColNum());

            int seatStatus = seat.getStatus();

            if (date != null && seatStatus != SEAT_MAINTENANCE) {
                QueryWrapper<SeatReservation> reservationQueryWrapper = new QueryWrapper<>();
                reservationQueryWrapper.eq("seat_id", seat.getId())
                        .ge("reserve_date", dayStart)
                        .lt("reserve_date", dayEnd)
                        .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                        .eq("deleted", 0);
                long reservedCount = seatReservationMapper.selectCount(reservationQueryWrapper);
                if (reservedCount > 0 && seatStatus == SEAT_AVAILABLE) {
                    seatStatus = SEAT_RESERVED;
                }
            }

            seatInfo.put("status", seatStatus);
            seatInfo.put("isReserved", seatStatus == SEAT_RESERVED || seatStatus == SEAT_OCCUPIED);

            result.add(seatInfo);
        }

        return result;
    }

    @Transactional
    public SeatReservation reserveSeat(Long userId, Long seatId, Date reserveDate, String startTime, String endTime) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getViolationCount() != null && user.getViolationCount() >= MAX_VIOLATION_COUNT) {
            if (user.getLastViolationTime() != null) {
                Calendar banEnd = Calendar.getInstance();
                banEnd.setTime(user.getLastViolationTime());
                banEnd.add(Calendar.DAY_OF_MONTH, VIOLATION_BAN_DAYS);
                if (new Date().before(banEnd.getTime())) {
                    throw new RuntimeException("您因多次违约被禁止预约，禁止期至：" + new SimpleDateFormat("yyyy-MM-dd").format(banEnd.getTime()));
                }
            }
        }

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, MAX_ADVANCE_DAYS);
        if (reserveDate.after(maxDate.getTime())) {
            throw new RuntimeException("仅允许预约未来" + MAX_ADVANCE_DAYS + "天内的座位");
        }

        QueryWrapper<SeatReservation> dailyCountWrapper = new QueryWrapper<>();
        dailyCountWrapper.eq("user_id", userId)
                .eq("reserve_date", reserveDate)
                .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                .eq("deleted", 0);
        long dailyCount = seatReservationMapper.selectCount(dailyCountWrapper);
        if (dailyCount >= MAX_DAILY_RESERVATIONS) {
            throw new RuntimeException("每人每天最多预约" + MAX_DAILY_RESERVATIONS + "个时段");
        }

        Seat seat = seatMapper.selectById(seatId);
        if (seat == null || seat.getDeleted() == 1) {
            throw new RuntimeException("座位不存在");
        }

        if (seat.getStatus() == SEAT_MAINTENANCE) {
            throw new RuntimeException("座位正在维护中，无法预约");
        }

        ReadingRoom room = readingRoomMapper.selectById(seat.getRoomId());
        if (room == null || room.getDeleted() == 1 || room.getStatus() != 1) {
            throw new RuntimeException("阅览室不可用");
        }

        if (!isWithinOpenHours(room, startTime, endTime)) {
            throw new RuntimeException("预约时间不在阅览室开放时间内");
        }

        QueryWrapper<SeatReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seat_id", seatId)
                .eq("reserve_date", reserveDate)
                .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                .eq("deleted", 0);

        List<SeatReservation> existingReservations = seatReservationMapper.selectList(queryWrapper);
        if (hasTimeConflict(existingReservations, startTime, endTime)) {
            throw new RuntimeException("该时间段座位已被预约");
        }

        QueryWrapper<SeatReservation> userReservationWrapper = new QueryWrapper<>();
        userReservationWrapper.eq("user_id", userId)
                .eq("reserve_date", reserveDate)
                .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                .eq("deleted", 0);
        if (seatReservationMapper.selectCount(userReservationWrapper) > 0) {
            throw new RuntimeException("您在该日期已有预约，请先取消现有预约");
        }

        SeatReservation reservation = new SeatReservation();
        reservation.setUserId(userId);
        reservation.setSeatId(seatId);
        reservation.setRoomId(seat.getRoomId());
        reservation.setReserveDate(reserveDate);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(STATUS_RESERVED);
        reservation.setDeleted(0);
        reservation.setCreateTime(new Date());

        seatReservationMapper.insert(reservation);

        Seat updateSeat = new Seat();
        updateSeat.setId(seatId);
        updateSeat.setStatus(SEAT_RESERVED);
        seatMapper.updateById(updateSeat);

        try {
            String title = "座位预约成功";
            String content = String.format("您已成功预约座位 %s，预约日期：%s，时间段：%s-%s",
                    seat.getSeatNumber(),
                    new SimpleDateFormat("yyyy-MM-dd").format(reserveDate),
                    startTime, endTime);
            notificationService.sendNotification(userId, "SYSTEM", title, content);
            logger.info("座位预约成功通知已发送: userId={}, seatId={}", userId, seatId);
        } catch (Exception e) {
            logger.error("发送座位预约成功通知失败: userId={}, seatId={}, error={}", userId, seatId, e.getMessage());
        }

        return reservation;
    }

    @Transactional
    public boolean cancelReservation(Long reservationId, Long userId) {
        SeatReservation reservation = seatReservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消此预约");
        }

        if (reservation.getStatus() != STATUS_RESERVED) {
            throw new RuntimeException("该预约无法取消");
        }

        Date today = clearTime(new Date());
        Date reserveDate = clearTime(reservation.getReserveDate());
        if (reserveDate.equals(today)) {
            String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
            String startTime = reservation.getStartTime();
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date current = sdf.parse(currentTime);
                Date start = sdf.parse(startTime);
                
                long diffMinutes = (start.getTime() - current.getTime()) / (60 * 1000);
                if (diffMinutes < CANCEL_ADVANCE_MINUTES) {
                    throw new RuntimeException("取消预约需提前" + CANCEL_ADVANCE_MINUTES + "分钟");
                }
            } catch (ParseException e) {
                logger.error("时间解析错误: {}", e.getMessage());
            }
        }

        reservation.setStatus(STATUS_CANCELLED);
        int result = seatReservationMapper.updateById(reservation);

        if (result > 0) {
            QueryWrapper<SeatReservation> activeCheck = new QueryWrapper<>();
            activeCheck.eq("seat_id", reservation.getSeatId())
                    .eq("reserve_date", reservation.getReserveDate())
                    .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                    .eq("deleted", 0);
            if (seatReservationMapper.selectCount(activeCheck) == 0) {
                Seat resetSeat = new Seat();
                resetSeat.setId(reservation.getSeatId());
                resetSeat.setStatus(SEAT_AVAILABLE);
                seatMapper.updateById(resetSeat);
            }

            try {
                Seat seat = seatMapper.selectById(reservation.getSeatId());
                String title = "座位预约取消";
                String content = String.format("您已取消座位 %s 的预约，预约日期：%s",
                        seat != null ? seat.getSeatNumber() : "未知",
                        new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReserveDate()));
                notificationService.sendNotification(userId, "SYSTEM", title, content);
                logger.info("座位预约取消通知已发送: userId={}, reservationId={}", userId, reservationId);
            } catch (Exception e) {
                logger.error("发送座位预约取消通知失败: userId={}, reservationId={}, error={}", userId, reservationId, e.getMessage());
            }
        }

        return result > 0;
    }

    @Transactional
    public boolean checkIn(Long reservationId, Long userId) {
        SeatReservation reservation = seatReservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }

        if (reservation.getStatus() != STATUS_RESERVED) {
            throw new RuntimeException("该预约无法签到");
        }

        Date today = clearTime(new Date());
        Date reserveDate = clearTime(reservation.getReserveDate());
        if (!today.equals(reserveDate)) {
            throw new RuntimeException("只能在预约当天签到");
        }

        String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
        String startTime = reservation.getStartTime();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date current = sdf.parse(currentTime);
            Date start = sdf.parse(startTime);
            
            Calendar beforeStart = Calendar.getInstance();
            beforeStart.setTime(start);
            beforeStart.add(Calendar.MINUTE, -CHECKIN_BEFORE_MINUTES);
            
            Calendar afterStart = Calendar.getInstance();
            afterStart.setTime(start);
            afterStart.add(Calendar.MINUTE, CHECKIN_AFTER_MINUTES);
            
            if (current.before(beforeStart.getTime())) {
                throw new RuntimeException("签到时间过早，请在开始时间前" + CHECKIN_BEFORE_MINUTES + "分钟内签到");
            }
            
            if (current.after(afterStart.getTime())) {
                UserAccount user = userAccountMapper.selectById(userId);
                if (user != null) {
                    int violationCount = user.getViolationCount() != null ? user.getViolationCount() : 0;
                    user.setViolationCount(violationCount + 1);
                    user.setLastViolationTime(new Date());
                    userAccountMapper.updateById(user);
                    
                    logger.warn("用户签到超时，违约次数增加: userId={}, violationCount={}", userId, user.getViolationCount());
                }
                throw new RuntimeException("签到超时，已标记为违约");
            }
        } catch (ParseException e) {
            logger.error("时间解析错误: {}", e.getMessage());
        }

        reservation.setStatus(STATUS_CHECKED_IN);
        reservation.setCheckInTime(new Date());
        int result = seatReservationMapper.updateById(reservation);

        if (result > 0) {
            Seat seat = seatMapper.selectById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus(SEAT_OCCUPIED);
                seatMapper.updateById(seat);
            }
            
            try {
                String title = "座位签到成功";
                String content = String.format("您已成功签到座位 %s",
                        seat != null ? seat.getSeatNumber() : "未知");
                notificationService.sendNotification(userId, "SYSTEM", title, content);
                logger.info("座位签到成功通知已发送: userId={}, reservationId={}", userId, reservationId);
            } catch (Exception e) {
                logger.error("发送座位签到成功通知失败: userId={}, reservationId={}, error={}", userId, reservationId, e.getMessage());
            }
        }

        return result > 0;
    }

    @Transactional
    public boolean checkOut(Long reservationId, Long userId) {
        SeatReservation reservation = seatReservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }

        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }

        if (reservation.getStatus() != STATUS_CHECKED_IN) {
            throw new RuntimeException("该预约无法签退");
        }

        reservation.setStatus(STATUS_CHECKED_OUT);
        reservation.setCheckOutTime(new Date());
        int result = seatReservationMapper.updateById(reservation);

        if (result > 0) {
            Seat seat = seatMapper.selectById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus(SEAT_AVAILABLE);
                seatMapper.updateById(seat);
            }
        }

        return result > 0;
    }

    public IPage<SeatReservation> getUserReservations(Long userId, int page, int size) {
        Page<SeatReservation> pageInfo = new Page<>(page, size);
        QueryWrapper<SeatReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("deleted", 0)
                .orderByDesc("create_time");
        return seatReservationMapper.selectPage(pageInfo, queryWrapper);
    }

    public List<SeatReservation> getUserActiveReservations(Long userId) {
        QueryWrapper<SeatReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                .eq("deleted", 0)
                .orderByAsc("reserve_date");
        return seatReservationMapper.selectList(queryWrapper);
    }

    public SeatReservation getReservationById(Long reservationId) {
        return seatReservationMapper.selectById(reservationId);
    }

    public Seat getSeatById(Long seatId) {
        return seatMapper.selectById(seatId);
    }

    public boolean updateSeatStatus(Long seatId, String status) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null || seat.getDeleted() == 1) {
            throw new RuntimeException("座位不存在");
        }

        int statusValue = parseStatusString(status);
        seat.setStatus(statusValue);
        return seatMapper.updateById(seat) > 0;
    }

    private int parseStatusString(String status) {
        if (status == null) return SEAT_AVAILABLE;
        switch (status.toLowerCase()) {
            case "available": return SEAT_AVAILABLE;
            case "reserved":
            case "in_use":
            case "occupied": return SEAT_OCCUPIED;
            case "maintenance": return SEAT_MAINTENANCE;
            default:
                try { return Integer.parseInt(status); }
                catch (NumberFormatException e) { return SEAT_AVAILABLE; }
        }
    }

    public IPage<Seat> getSeatList(Long roomId, int page, int size) {
        Page<Seat> pageInfo = new Page<>(page, size);
        QueryWrapper<Seat> queryWrapper = new QueryWrapper<>();
        if (roomId != null) {
            queryWrapper.eq("room_id", roomId);
        }
        queryWrapper.eq("deleted", 0)
                .orderByAsc("room_id")
                .orderByAsc("row_num")
                .orderByAsc("col_num");
        return seatMapper.selectPage(pageInfo, queryWrapper);
    }

    @Transactional
    public int processExpiredReservations() {
        Date today = clearTime(new Date());
        UpdateWrapper<SeatReservation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("status", STATUS_RESERVED)
                .lt("reserve_date", today)
                .eq("deleted", 0)
                .set("status", STATUS_EXPIRED);
        return seatReservationMapper.update(null, updateWrapper);
    }

    private boolean isWithinOpenHours(ReadingRoom room, String startTime, String endTime) {
        if (room.getOpenTime() == null || room.getCloseTime() == null) {
            return true;
        }
        return startTime.compareTo(room.getOpenTime()) >= 0 
                && endTime.compareTo(room.getCloseTime()) <= 0;
    }

    private boolean hasTimeConflict(List<SeatReservation> reservations, String startTime, String endTime) {
        for (SeatReservation reservation : reservations) {
            if (!(endTime.compareTo(reservation.getStartTime()) <= 0 
                    || startTime.compareTo(reservation.getEndTime()) >= 0)) {
                return true;
            }
        }
        return false;
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

    @Transactional
    public ReadingRoom createReadingRoom(ReadingRoom room) {
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new RuntimeException("阅览室名称不能为空");
        }
        room.setDeleted(0);
        readingRoomMapper.insert(room);
        return room;
    }

    @Transactional
    public ReadingRoom updateReadingRoom(ReadingRoom room) {
        if (room.getId() == null) {
            throw new RuntimeException("阅览室ID不能为空");
        }
        ReadingRoom existing = readingRoomMapper.selectById(room.getId());
        if (existing == null || existing.getDeleted() == 1) {
            throw new RuntimeException("阅览室不存在");
        }
        readingRoomMapper.updateById(room);
        return room;
    }

    @Transactional
    public boolean deleteReadingRoom(Long roomId) {
        ReadingRoom room = readingRoomMapper.selectById(roomId);
        if (room == null || room.getDeleted() == 1) {
            throw new RuntimeException("阅览室不存在");
        }

        QueryWrapper<Seat> seatQueryWrapper = new QueryWrapper<>();
        seatQueryWrapper.eq("room_id", roomId).eq("deleted", 0);
        List<Seat> roomSeats = seatMapper.selectList(seatQueryWrapper);
        
        if (!roomSeats.isEmpty()) {
            List<Long> seatIds = new ArrayList<>();
            for (Seat seat : roomSeats) {
                seatIds.add(seat.getId());
            }
            
            QueryWrapper<SeatReservation> reservationWrapper = new QueryWrapper<>();
            reservationWrapper.in("seat_id", seatIds)
                    .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                    .eq("deleted", 0);
            if (seatReservationMapper.selectCount(reservationWrapper) > 0) {
                throw new RuntimeException("该阅览室存在进行中的预约，无法删除");
            }
        }

        UpdateWrapper<Seat> seatUpdateWrapper = new UpdateWrapper<>();
        seatUpdateWrapper.eq("room_id", roomId).eq("deleted", 0).set("deleted", 1);
        seatMapper.update(null, seatUpdateWrapper);

        room.setDeleted(1);
        return readingRoomMapper.updateById(room) > 0;
    }

    @Transactional
    public int batchCreateSeats(Long roomId, int rows, int cols, String prefix) {
        ReadingRoom room = readingRoomMapper.selectById(roomId);
        if (room == null || room.getDeleted() == 1) {
            throw new RuntimeException("阅览室不存在");
        }

        QueryWrapper<Seat> existingWrapper = new QueryWrapper<>();
        existingWrapper.eq("room_id", roomId).eq("deleted", 0);
        long existingCount = seatMapper.selectCount(existingWrapper);
        if (existingCount > 0) {
            throw new RuntimeException("该阅览室已有座位，请先清空现有座位");
        }

        if (rows <= 0 || rows > 50 || cols <= 0 || cols > 50) {
            throw new RuntimeException("行列数必须在1-50之间");
        }

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Seat seat = new Seat();
                seat.setRoomId(roomId);
                seat.setSeatNumber(prefix + row + "-" + col);
                seat.setRowNum(row);
                seat.setColNum(col);
                seat.setStatus(SEAT_AVAILABLE);
                seat.setDeleted(0);
                seats.add(seat);
            }
        }

        for (Seat seat : seats) {
            seatMapper.insert(seat);
        }

        room.setCapacity(rows * cols);
        readingRoomMapper.updateById(room);

        return seats.size();
    }

    @Transactional
    public boolean deleteSeat(Long seatId) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null || seat.getDeleted() == 1) {
            throw new RuntimeException("座位不存在");
        }

        QueryWrapper<SeatReservation> reservationWrapper = new QueryWrapper<>();
        reservationWrapper.eq("seat_id", seatId)
                .in("status", STATUS_RESERVED, STATUS_CHECKED_IN)
                .eq("deleted", 0);
        if (seatReservationMapper.selectCount(reservationWrapper) > 0) {
            throw new RuntimeException("该座位存在进行中的预约，无法删除");
        }

        seat.setDeleted(1);
        return seatMapper.updateById(seat) > 0;
    }

    public IPage<SeatReservation> getAllReservations(Long roomId, Date date, int page, int size) {
        Page<SeatReservation> pageInfo = new Page<>(page, size);
        QueryWrapper<SeatReservation> queryWrapper = new QueryWrapper<>();
        
        if (roomId != null) {
            List<Long> seatIds = new ArrayList<>();
            QueryWrapper<Seat> seatWrapper = new QueryWrapper<>();
            seatWrapper.eq("room_id", roomId).eq("deleted", 0);
            List<Seat> roomSeats = seatMapper.selectList(seatWrapper);
            for (Seat seat : roomSeats) {
                seatIds.add(seat.getId());
            }
            if (seatIds.isEmpty()) {
                return pageInfo;
            }
            queryWrapper.in("seat_id", seatIds);
        }
        
        if (date != null) {
            queryWrapper.eq("reserve_date", date);
        }
        
        queryWrapper.eq("deleted", 0).orderByDesc("create_time");
        return seatReservationMapper.selectPage(pageInfo, queryWrapper);
    }

    public Map<String, Object> getRoomStats(Long roomId) {
        Map<String, Object> stats = new HashMap<>();
        
        ReadingRoom room = readingRoomMapper.selectById(roomId);
        if (room == null || room.getDeleted() == 1) {
            throw new RuntimeException("阅览室不存在");
        }

        QueryWrapper<Seat> totalSeatWrapper = new QueryWrapper<>();
        totalSeatWrapper.eq("room_id", roomId).eq("deleted", 0);
        int totalSeats = Math.toIntExact(seatMapper.selectCount(totalSeatWrapper));
        stats.put("totalSeats", totalSeats);

        QueryWrapper<Seat> availableSeatWrapper = new QueryWrapper<>();
        availableSeatWrapper.eq("room_id", roomId).eq("status", SEAT_AVAILABLE).eq("deleted", 0);
        int availableSeats = Math.toIntExact(seatMapper.selectCount(availableSeatWrapper));
        stats.put("availableSeats", availableSeats);

        QueryWrapper<Seat> occupiedSeatWrapper = new QueryWrapper<>();
        occupiedSeatWrapper.eq("room_id", roomId).eq("status", SEAT_OCCUPIED).eq("deleted", 0);
        int occupiedSeats = Math.toIntExact(seatMapper.selectCount(occupiedSeatWrapper));
        stats.put("occupiedSeats", occupiedSeats);

        QueryWrapper<Seat> maintenanceSeatWrapper = new QueryWrapper<>();
        maintenanceSeatWrapper.eq("room_id", roomId).eq("status", SEAT_MAINTENANCE).eq("deleted", 0);
        int maintenanceSeats = Math.toIntExact(seatMapper.selectCount(maintenanceSeatWrapper));
        stats.put("maintenanceSeats", maintenanceSeats);

        Date today = clearTime(new Date());
        QueryWrapper<SeatReservation> todayWrapper = new QueryWrapper<>();
        todayWrapper.eq("reserve_date", today).eq("deleted", 0);
        if (roomId != null) {
            List<Long> seatIds = new ArrayList<>();
            QueryWrapper<Seat> seatWrapper = new QueryWrapper<>();
            seatWrapper.eq("room_id", roomId).eq("deleted", 0);
            List<Seat> roomSeats = seatMapper.selectList(seatWrapper);
            for (Seat seat : roomSeats) {
                seatIds.add(seat.getId());
            }
            if (!seatIds.isEmpty()) {
                todayWrapper.in("seat_id", seatIds);
            }
        }
        int todayReservations = Math.toIntExact(seatReservationMapper.selectCount(todayWrapper));
        stats.put("todayReservations", todayReservations);

        stats.put("usageRate", totalSeats > 0 ? Math.round((double) occupiedSeats / totalSeats * 100) : 0);

        return stats;
    }
}
