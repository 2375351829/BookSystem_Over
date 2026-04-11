package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.annotation.OperationLog;
import com.example.demo.model.ReadingRoom;
import com.example.demo.model.Seat;
import com.example.demo.model.SeatReservation;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import com.example.demo.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private static final Logger logger = LoggerFactory.getLogger(SeatController.class);

    @Autowired
    private SeatService seatService;
    
    @Autowired
    private UserAccountMapper userAccountMapper;
    
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted", 0);
        UserAccount user = userAccountMapper.selectOne(queryWrapper);
        return user != null ? user.getId() : null;
    }

    @GetMapping("/rooms")
    public Map<String, Object> getReadingRoomList() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ReadingRoom> rooms = seatService.getReadingRoomList();
            result.put("success", true);
            result.put("data", rooms);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/room/{roomId}")
    public Map<String, Object> getRoomSeats(
            @PathVariable Long roomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (date == null) {
                date = new Date();
            }
            ReadingRoom room = seatService.getReadingRoomById(roomId);
            if (room == null) {
                result.put("success", false);
                result.put("message", "阅览室不存在");
                return result;
            }
            List<Map<String, Object>> seats = seatService.getRoomSeats(roomId, date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("room", room);
            data.put("seats", seats);
            data.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/reserve")
    @OperationLog(module = "座位管理", operation = "预约座位", description = "用户预约座位")
    public Map<String, Object> reserveSeat(
            @RequestParam Long seatId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date reserveDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            SeatReservation reservation = seatService.reserveSeat(userId, seatId, reserveDate, startTime, endTime);

            Map<String, Object> reservationInfo = new HashMap<>();
            reservationInfo.put("id", reservation.getId());
            reservationInfo.put("seatId", reservation.getSeatId());
            reservationInfo.put("reserveDate", new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReserveDate()));
            reservationInfo.put("startTime", reservation.getStartTime());
            reservationInfo.put("endTime", reservation.getEndTime());
            reservationInfo.put("status", reservation.getStatus());
            reservationInfo.put("checkInTime", reservation.getCheckInTime());
            reservationInfo.put("checkOutTime", reservation.getCheckOutTime());
            reservationInfo.put("createTime", reservation.getCreateTime());

            Seat seat = seatService.getSeatById(reservation.getSeatId());
            if (seat != null) {
                reservationInfo.put("seatNumber", seat.getSeatNumber());
                ReadingRoom room = seatService.getReadingRoomById(seat.getRoomId());
                if (room != null) {
                    reservationInfo.put("roomId", room.getId());
                    reservationInfo.put("roomName", room.getName());
                    reservationInfo.put("roomLocation", room.getLocation());
                }
            }

            result.put("success", true);
            result.put("data", reservationInfo);
            result.put("message", "预约成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/reserve/{id}")
    @OperationLog(module = "座位管理", operation = "取消预约", description = "用户取消座位预约")
    public Map<String, Object> cancelReservation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            boolean success = seatService.cancelReservation(id, userId);
            result.put("success", success);
            result.put("message", success ? "取消预约成功" : "取消预约失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/check-in/{id}")
    public Map<String, Object> checkIn(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            boolean success = seatService.checkIn(id, userId);
            result.put("success", success);
            result.put("message", success ? "签到成功" : "签到失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/check-out/{id}")
    public Map<String, Object> checkOut(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            boolean success = seatService.checkOut(id, userId);
            result.put("success", success);
            result.put("message", success ? "签退成功" : "签退失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/my-reservations")
    public Map<String, Object> getUserReservations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            IPage<SeatReservation> reservations = seatService.getUserReservations(userId, page, size);
            
            List<Map<String, Object>> reservationList = new ArrayList<>();
            for (SeatReservation reservation : reservations.getRecords()) {
                Map<String, Object> reservationInfo = new HashMap<>();
                reservationInfo.put("id", reservation.getId());
                reservationInfo.put("seatId", reservation.getSeatId());
                reservationInfo.put("reserveDate", new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReserveDate()));
                reservationInfo.put("startTime", reservation.getStartTime());
                reservationInfo.put("endTime", reservation.getEndTime());
                reservationInfo.put("status", reservation.getStatus());
                reservationInfo.put("checkInTime", reservation.getCheckInTime());
                reservationInfo.put("checkOutTime", reservation.getCheckOutTime());
                reservationInfo.put("createTime", reservation.getCreateTime());

                Seat seat = seatService.getSeatById(reservation.getSeatId());
                if (seat != null) {
                    reservationInfo.put("seatNumber", seat.getSeatNumber());
                    reservationInfo.put("row", seat.getRowNum());
                    reservationInfo.put("column", seat.getColNum());
                    
                    ReadingRoom room = seatService.getReadingRoomById(seat.getRoomId());
                    if (room != null) {
                        reservationInfo.put("roomId", room.getId());
                        reservationInfo.put("roomName", room.getName());
                        reservationInfo.put("roomLocation", room.getLocation());
                    }
                }

                reservationList.add(reservationInfo);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("records", reservationList);
            data.put("total", reservations.getTotal());
            data.put("current", reservations.getCurrent());
            data.put("pages", reservations.getPages());

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/my-active-reservations")
    public Map<String, Object> getUserActiveReservations() {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }
            List<SeatReservation> reservations = seatService.getUserActiveReservations(userId);
            
            List<Map<String, Object>> reservationList = new ArrayList<>();
            for (SeatReservation reservation : reservations) {
                Map<String, Object> reservationInfo = new HashMap<>();
                reservationInfo.put("id", reservation.getId());
                reservationInfo.put("seatId", reservation.getSeatId());
                reservationInfo.put("reserveDate", new SimpleDateFormat("yyyy-MM-dd").format(reservation.getReserveDate()));
                reservationInfo.put("startTime", reservation.getStartTime());
                reservationInfo.put("endTime", reservation.getEndTime());
                reservationInfo.put("status", reservation.getStatus());
                reservationInfo.put("checkInTime", reservation.getCheckInTime());
                reservationInfo.put("checkOutTime", reservation.getCheckOutTime());
                reservationInfo.put("createTime", reservation.getCreateTime());

                Seat seat = seatService.getSeatById(reservation.getSeatId());
                if (seat != null) {
                    reservationInfo.put("seatNumber", seat.getSeatNumber());
                    
                    ReadingRoom room = seatService.getReadingRoomById(seat.getRoomId());
                    if (room != null) {
                        reservationInfo.put("roomName", room.getName());
                        reservationInfo.put("roomLocation", room.getLocation());
                    }
                }

                reservationList.add(reservationInfo);
            }

            result.put("success", true);
            result.put("data", reservationList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> getSeatList(
            @RequestParam(required = false) Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<Seat> seats = seatService.getSeatList(roomId, page, size);
            result.put("success", true);
            result.put("data", seats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/reservation/{id}")
    public Map<String, Object> getReservationDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            SeatReservation reservation = seatService.getReservationById(id);
            if (reservation == null) {
                result.put("success", false);
                result.put("message", "预约记录不存在");
                return result;
            }

            Map<String, Object> reservationInfo = new HashMap<>();
            reservationInfo.put("id", reservation.getId());
            reservationInfo.put("userId", reservation.getUserId());
            reservationInfo.put("seatId", reservation.getSeatId());
            reservationInfo.put("reserveDate", reservation.getReserveDate());
            reservationInfo.put("startTime", reservation.getStartTime());
            reservationInfo.put("endTime", reservation.getEndTime());
            reservationInfo.put("status", reservation.getStatus());
            reservationInfo.put("checkInTime", reservation.getCheckInTime());
            reservationInfo.put("checkOutTime", reservation.getCheckOutTime());
            reservationInfo.put("createTime", reservation.getCreateTime());

            Seat seat = seatService.getSeatById(reservation.getSeatId());
            if (seat != null) {
                reservationInfo.put("seatNumber", seat.getSeatNumber());
                reservationInfo.put("row", seat.getRowNum());
                reservationInfo.put("column", seat.getColNum());
                
                ReadingRoom room = seatService.getReadingRoomById(seat.getRoomId());
                if (room != null) {
                    reservationInfo.put("roomId", room.getId());
                    reservationInfo.put("roomName", room.getName());
                    reservationInfo.put("roomLocation", room.getLocation());
                }
            }

            result.put("success", true);
            result.put("data", reservationInfo);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/seat/{seatId}/status")
    public Map<String, Object> updateSeatStatus(
            @PathVariable Long seatId,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            String status = body.get("status");
            boolean success = seatService.updateSeatStatus(seatId, status);
            result.put("success", success);
            result.put("message", success ? "状态更新成功" : "状态更新失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms")
    public Map<String, Object> createReadingRoom(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            ReadingRoom room = new ReadingRoom();
            room.setName((String) body.get("name"));
            room.setLocation((String) body.get("location"));
            room.setCapacity(body.get("capacity") != null ? ((Number) body.get("capacity")).intValue() : 0);
            room.setOpenTime((String) body.get("openTime"));
            room.setCloseTime((String) body.get("closeTime"));
            room.setStatus(body.get("status") != null ? ((Number) body.get("status")).intValue() : 1);
            room.setDeleted(0);
            
            ReadingRoom created = seatService.createReadingRoom(room);
            result.put("success", true);
            result.put("data", created);
            result.put("message", "阅览室创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/rooms/{id}")
    public Map<String, Object> updateReadingRoom(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            ReadingRoom room = seatService.getReadingRoomById(id);
            if (room == null) {
                result.put("success", false);
                result.put("message", "阅览室不存在");
                return result;
            }
            
            if (body.get("name") != null) room.setName((String) body.get("name"));
            if (body.get("location") != null) room.setLocation((String) body.get("location"));
            if (body.get("capacity") != null) room.setCapacity(((Number) body.get("capacity")).intValue());
            if (body.get("openTime") != null) room.setOpenTime((String) body.get("openTime"));
            if (body.get("closeTime") != null) room.setCloseTime((String) body.get("closeTime"));
            if (body.get("status") != null) room.setStatus(((Number) body.get("status")).intValue());
            
            ReadingRoom updated = seatService.updateReadingRoom(room);
            result.put("success", true);
            result.put("data", updated);
            result.put("message", "阅览室更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/rooms/{id}")
    public Map<String, Object> deleteReadingRoom(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = seatService.deleteReadingRoom(id);
            result.put("success", success);
            result.put("message", success ? "阅览室删除成功" : "阅览室删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms/{roomId}/seats")
    public Map<String, Object> batchCreateSeats(
            @PathVariable Long roomId,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = body.get("rows") != null ? ((Number) body.get("rows")).intValue() : 5;
            int cols = body.get("cols") != null ? ((Number) body.get("cols")).intValue() : 8;
            String prefix = (String) body.getOrDefault("prefix", "");
            
            int count = seatService.batchCreateSeats(roomId, rows, cols, prefix);
            result.put("success", true);
            result.put("data", Map.of("count", count));
            result.put("message", "成功创建 " + count + " 个座位");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/seats/{id}")
    public Map<String, Object> deleteSeat(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = seatService.deleteSeat(id);
            result.put("success", success);
            result.put("message", success ? "座位删除成功" : "座位删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reservations")
    public Map<String, Object> getAllReservations(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<SeatReservation> reservations = seatService.getAllReservations(roomId, date, page, size);
            
            List<Map<String, Object>> reservationList = new ArrayList<>();
            for (SeatReservation reservation : reservations.getRecords()) {
                Map<String, Object> reservationInfo = new HashMap<>();
                reservationInfo.put("id", reservation.getId());
                reservationInfo.put("userId", reservation.getUserId());
                reservationInfo.put("seatId", reservation.getSeatId());
                reservationInfo.put("reserveDate", reservation.getReserveDate());
                reservationInfo.put("startTime", reservation.getStartTime());
                reservationInfo.put("endTime", reservation.getEndTime());
                reservationInfo.put("status", reservation.getStatus());
                reservationInfo.put("checkInTime", reservation.getCheckInTime());
                reservationInfo.put("checkOutTime", reservation.getCheckOutTime());
                reservationInfo.put("createTime", reservation.getCreateTime());

                Seat seat = seatService.getSeatById(reservation.getSeatId());
                if (seat != null) {
                    reservationInfo.put("seatNumber", seat.getSeatNumber());
                    
                    ReadingRoom room = seatService.getReadingRoomById(seat.getRoomId());
                    if (room != null) {
                        reservationInfo.put("roomId", room.getId());
                        reservationInfo.put("roomName", room.getName());
                    }
                }

                reservationList.add(reservationInfo);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("records", reservationList);
            data.put("total", reservations.getTotal());
            data.put("current", reservations.getCurrent());
            data.put("pages", reservations.getPages());

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rooms/{roomId}/stats")
    public Map<String, Object> getRoomStats(@PathVariable Long roomId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = seatService.getRoomStats(roomId);
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
