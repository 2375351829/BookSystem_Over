package com.example.demo.constant;

import java.util.HashMap;
import java.util.Map;

public class StatusConstants {

    public static final class SeatStatus {
        public static final String AVAILABLE = "available";
        public static final String RESERVED = "reserved";
        public static final String OCCUPIED = "occupied";
        public static final String MAINTENANCE = "maintenance";
        
        public static final String COLOR_AVAILABLE = "#909399";
        public static final String COLOR_RESERVED = "#409EFF";
        public static final String COLOR_OCCUPIED = "#67C23A";
        public static final String COLOR_MAINTENANCE = "#E6A23C";
        
        public static final String LABEL_AVAILABLE = "空闲";
        public static final String LABEL_RESERVED = "已预约";
        public static final String LABEL_OCCUPIED = "使用中";
        public static final String LABEL_MAINTENANCE = "维修中";
        
        private static final Map<String, String> COLOR_MAP = new HashMap<>();
        private static final Map<String, String> LABEL_MAP = new HashMap<>();
        
        static {
            COLOR_MAP.put(AVAILABLE, COLOR_AVAILABLE);
            COLOR_MAP.put(RESERVED, COLOR_RESERVED);
            COLOR_MAP.put(OCCUPIED, COLOR_OCCUPIED);
            COLOR_MAP.put(MAINTENANCE, COLOR_MAINTENANCE);
            
            LABEL_MAP.put(AVAILABLE, LABEL_AVAILABLE);
            LABEL_MAP.put(RESERVED, LABEL_RESERVED);
            LABEL_MAP.put(OCCUPIED, LABEL_OCCUPIED);
            LABEL_MAP.put(MAINTENANCE, LABEL_MAINTENANCE);
        }
        
        public static String getColor(String status) {
            return COLOR_MAP.getOrDefault(status, "#909399");
        }
        
        public static String getLabel(String status) {
            return LABEL_MAP.getOrDefault(status, "未知");
        }
    }
    
    public static final class BookStatus {
        public static final String AVAILABLE = "available";
        public static final String BORROWED = "borrowed";
        public static final String RESERVED = "reserved";
        public static final String MAINTENANCE = "maintenance";
        
        public static final String COLOR_AVAILABLE = "#67C23A";
        public static final String COLOR_BORROWED = "#F56C6C";
        public static final String COLOR_RESERVED = "#409EFF";
        public static final String COLOR_MAINTENANCE = "#E6A23C";
        
        public static final String LABEL_AVAILABLE = "可借";
        public static final String LABEL_BORROWED = "已借出";
        public static final String LABEL_RESERVED = "预约中";
        public static final String LABEL_MAINTENANCE = "维修中";
        
        private static final Map<String, String> COLOR_MAP = new HashMap<>();
        private static final Map<String, String> LABEL_MAP = new HashMap<>();
        
        static {
            COLOR_MAP.put(AVAILABLE, COLOR_AVAILABLE);
            COLOR_MAP.put(BORROWED, COLOR_BORROWED);
            COLOR_MAP.put(RESERVED, COLOR_RESERVED);
            COLOR_MAP.put(MAINTENANCE, COLOR_MAINTENANCE);
            
            LABEL_MAP.put(AVAILABLE, LABEL_AVAILABLE);
            LABEL_MAP.put(BORROWED, LABEL_BORROWED);
            LABEL_MAP.put(RESERVED, LABEL_RESERVED);
            LABEL_MAP.put(MAINTENANCE, LABEL_MAINTENANCE);
        }
        
        public static String getColor(String status) {
            return COLOR_MAP.getOrDefault(status, "#909399");
        }
        
        public static String getLabel(String status) {
            return LABEL_MAP.getOrDefault(status, "未知");
        }
    }
    
    public static final class BorrowStatus {
        public static final String BORROWING = "borrowing";
        public static final String RETURNED = "returned";
        public static final String OVERDUE = "overdue";
        
        public static final String COLOR_BORROWING = "#409EFF";
        public static final String COLOR_RETURNED = "#67C23A";
        public static final String COLOR_OVERDUE = "#F56C6C";
        
        public static final String LABEL_BORROWING = "借阅中";
        public static final String LABEL_RETURNED = "已归还";
        public static final String LABEL_OVERDUE = "逾期";
        
        private static final Map<String, String> COLOR_MAP = new HashMap<>();
        private static final Map<String, String> LABEL_MAP = new HashMap<>();
        
        static {
            COLOR_MAP.put(BORROWING, COLOR_BORROWING);
            COLOR_MAP.put(RETURNED, COLOR_RETURNED);
            COLOR_MAP.put(OVERDUE, COLOR_OVERDUE);
            
            LABEL_MAP.put(BORROWING, LABEL_BORROWING);
            LABEL_MAP.put(RETURNED, LABEL_RETURNED);
            LABEL_MAP.put(OVERDUE, LABEL_OVERDUE);
        }
        
        public static String getColor(String status) {
            return COLOR_MAP.getOrDefault(status, "#909399");
        }
        
        public static String getLabel(String status) {
            return LABEL_MAP.getOrDefault(status, "未知");
        }
    }
    
    public static final class ReservationStatus {
        public static final String PENDING = "pending";
        public static final String NOTIFIED = "notified";
        public static final String FULFILLED = "fulfilled";
        public static final String CANCELLED = "cancelled";
        public static final String EXPIRED = "expired";
        
        public static final String COLOR_PENDING = "#E6A23C";
        public static final String COLOR_NOTIFIED = "#409EFF";
        public static final String COLOR_FULFILLED = "#67C23A";
        public static final String COLOR_CANCELLED = "#909399";
        public static final String COLOR_EXPIRED = "#F56C6C";
        
        public static final String LABEL_PENDING = "等待中";
        public static final String LABEL_NOTIFIED = "已通知";
        public static final String LABEL_FULFILLED = "已完成";
        public static final String LABEL_CANCELLED = "已取消";
        public static final String LABEL_EXPIRED = "已过期";
        
        private static final Map<String, String> COLOR_MAP = new HashMap<>();
        private static final Map<String, String> LABEL_MAP = new HashMap<>();
        
        static {
            COLOR_MAP.put(PENDING, COLOR_PENDING);
            COLOR_MAP.put(NOTIFIED, COLOR_NOTIFIED);
            COLOR_MAP.put(FULFILLED, COLOR_FULFILLED);
            COLOR_MAP.put(CANCELLED, COLOR_CANCELLED);
            COLOR_MAP.put(EXPIRED, COLOR_EXPIRED);
            
            LABEL_MAP.put(PENDING, LABEL_PENDING);
            LABEL_MAP.put(NOTIFIED, LABEL_NOTIFIED);
            LABEL_MAP.put(FULFILLED, LABEL_FULFILLED);
            LABEL_MAP.put(CANCELLED, LABEL_CANCELLED);
            LABEL_MAP.put(EXPIRED, LABEL_EXPIRED);
        }
        
        public static String getColor(String status) {
            return COLOR_MAP.getOrDefault(status, "#909399");
        }
        
        public static String getLabel(String status) {
            return LABEL_MAP.getOrDefault(status, "未知");
        }
    }
    
    public static final class FineStatus {
        public static final String UNPAID = "unpaid";
        public static final String PAID = "paid";
        
        public static final String COLOR_UNPAID = "#F56C6C";
        public static final String COLOR_PAID = "#67C23A";
        
        public static final String LABEL_UNPAID = "未支付";
        public static final String LABEL_PAID = "已支付";
        
        private static final Map<String, String> COLOR_MAP = new HashMap<>();
        private static final Map<String, String> LABEL_MAP = new HashMap<>();
        
        static {
            COLOR_MAP.put(UNPAID, COLOR_UNPAID);
            COLOR_MAP.put(PAID, COLOR_PAID);
            
            LABEL_MAP.put(UNPAID, LABEL_UNPAID);
            LABEL_MAP.put(PAID, LABEL_PAID);
        }
        
        public static String getColor(String status) {
            return COLOR_MAP.getOrDefault(status, "#909399");
        }
        
        public static String getLabel(String status) {
            return LABEL_MAP.getOrDefault(status, "未知");
        }
    }
}
