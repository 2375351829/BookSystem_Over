package com.example.demo.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusConstantsTest {

    @Test
    void testSeatStatusColors() {
        assertEquals("#909399", StatusConstants.SeatStatus.COLOR_AVAILABLE);
        assertEquals("#409EFF", StatusConstants.SeatStatus.COLOR_RESERVED);
        assertEquals("#67C23A", StatusConstants.SeatStatus.COLOR_OCCUPIED);
        assertEquals("#E6A23C", StatusConstants.SeatStatus.COLOR_MAINTENANCE);
    }

    @Test
    void testSeatStatusLabels() {
        assertEquals("空闲", StatusConstants.SeatStatus.LABEL_AVAILABLE);
        assertEquals("已预约", StatusConstants.SeatStatus.LABEL_RESERVED);
        assertEquals("使用中", StatusConstants.SeatStatus.LABEL_OCCUPIED);
        assertEquals("维修中", StatusConstants.SeatStatus.LABEL_MAINTENANCE);
    }

    @Test
    void testSeatStatusGetColor() {
        assertEquals("#909399", StatusConstants.SeatStatus.getColor("available"));
        assertEquals("#409EFF", StatusConstants.SeatStatus.getColor("reserved"));
        assertEquals("#67C23A", StatusConstants.SeatStatus.getColor("occupied"));
        assertEquals("#E6A23C", StatusConstants.SeatStatus.getColor("maintenance"));
        assertEquals("#909399", StatusConstants.SeatStatus.getColor("unknown"));
    }

    @Test
    void testSeatStatusGetLabel() {
        assertEquals("空闲", StatusConstants.SeatStatus.getLabel("available"));
        assertEquals("已预约", StatusConstants.SeatStatus.getLabel("reserved"));
        assertEquals("使用中", StatusConstants.SeatStatus.getLabel("occupied"));
        assertEquals("维修中", StatusConstants.SeatStatus.getLabel("maintenance"));
        assertEquals("未知", StatusConstants.SeatStatus.getLabel("unknown"));
    }

    @Test
    void testBookStatusColors() {
        assertEquals("#67C23A", StatusConstants.BookStatus.COLOR_AVAILABLE);
        assertEquals("#F56C6C", StatusConstants.BookStatus.COLOR_BORROWED);
        assertEquals("#409EFF", StatusConstants.BookStatus.COLOR_RESERVED);
        assertEquals("#E6A23C", StatusConstants.BookStatus.COLOR_MAINTENANCE);
    }

    @Test
    void testBookStatusLabels() {
        assertEquals("可借", StatusConstants.BookStatus.LABEL_AVAILABLE);
        assertEquals("已借出", StatusConstants.BookStatus.LABEL_BORROWED);
        assertEquals("预约中", StatusConstants.BookStatus.LABEL_RESERVED);
        assertEquals("维修中", StatusConstants.BookStatus.LABEL_MAINTENANCE);
    }

    @Test
    void testBorrowStatusColors() {
        assertEquals("#409EFF", StatusConstants.BorrowStatus.COLOR_BORROWING);
        assertEquals("#67C23A", StatusConstants.BorrowStatus.COLOR_RETURNED);
        assertEquals("#F56C6C", StatusConstants.BorrowStatus.COLOR_OVERDUE);
    }

    @Test
    void testBorrowStatusLabels() {
        assertEquals("借阅中", StatusConstants.BorrowStatus.LABEL_BORROWING);
        assertEquals("已归还", StatusConstants.BorrowStatus.LABEL_RETURNED);
        assertEquals("逾期", StatusConstants.BorrowStatus.LABEL_OVERDUE);
    }

    @Test
    void testReservationStatusColors() {
        assertEquals("#E6A23C", StatusConstants.ReservationStatus.COLOR_PENDING);
        assertEquals("#409EFF", StatusConstants.ReservationStatus.COLOR_NOTIFIED);
        assertEquals("#67C23A", StatusConstants.ReservationStatus.COLOR_FULFILLED);
        assertEquals("#909399", StatusConstants.ReservationStatus.COLOR_CANCELLED);
        assertEquals("#F56C6C", StatusConstants.ReservationStatus.COLOR_EXPIRED);
    }

    @Test
    void testReservationStatusLabels() {
        assertEquals("等待中", StatusConstants.ReservationStatus.LABEL_PENDING);
        assertEquals("已通知", StatusConstants.ReservationStatus.LABEL_NOTIFIED);
        assertEquals("已完成", StatusConstants.ReservationStatus.LABEL_FULFILLED);
        assertEquals("已取消", StatusConstants.ReservationStatus.LABEL_CANCELLED);
        assertEquals("已过期", StatusConstants.ReservationStatus.LABEL_EXPIRED);
    }

    @Test
    void testFineStatusColors() {
        assertEquals("#F56C6C", StatusConstants.FineStatus.COLOR_UNPAID);
        assertEquals("#67C23A", StatusConstants.FineStatus.COLOR_PAID);
    }

    @Test
    void testFineStatusLabels() {
        assertEquals("未支付", StatusConstants.FineStatus.LABEL_UNPAID);
        assertEquals("已支付", StatusConstants.FineStatus.LABEL_PAID);
    }
}
