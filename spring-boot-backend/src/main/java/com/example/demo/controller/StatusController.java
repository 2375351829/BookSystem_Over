package com.example.demo.controller;

import com.example.demo.constant.StatusConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping("/seat")
    public ResponseEntity<?> getSeatStatusConfig() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        addStatusItem(statusList, StatusConstants.SeatStatus.AVAILABLE, 
            StatusConstants.SeatStatus.LABEL_AVAILABLE, 
            StatusConstants.SeatStatus.COLOR_AVAILABLE);
        addStatusItem(statusList, StatusConstants.SeatStatus.RESERVED, 
            StatusConstants.SeatStatus.LABEL_RESERVED, 
            StatusConstants.SeatStatus.COLOR_RESERVED);
        addStatusItem(statusList, StatusConstants.SeatStatus.OCCUPIED, 
            StatusConstants.SeatStatus.LABEL_OCCUPIED, 
            StatusConstants.SeatStatus.COLOR_OCCUPIED);
        addStatusItem(statusList, StatusConstants.SeatStatus.MAINTENANCE, 
            StatusConstants.SeatStatus.LABEL_MAINTENANCE, 
            StatusConstants.SeatStatus.COLOR_MAINTENANCE);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statusList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book")
    public ResponseEntity<?> getBookStatusConfig() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        addStatusItem(statusList, StatusConstants.BookStatus.AVAILABLE, 
            StatusConstants.BookStatus.LABEL_AVAILABLE, 
            StatusConstants.BookStatus.COLOR_AVAILABLE);
        addStatusItem(statusList, StatusConstants.BookStatus.BORROWED, 
            StatusConstants.BookStatus.LABEL_BORROWED, 
            StatusConstants.BookStatus.COLOR_BORROWED);
        addStatusItem(statusList, StatusConstants.BookStatus.RESERVED, 
            StatusConstants.BookStatus.LABEL_RESERVED, 
            StatusConstants.BookStatus.COLOR_RESERVED);
        addStatusItem(statusList, StatusConstants.BookStatus.MAINTENANCE, 
            StatusConstants.BookStatus.LABEL_MAINTENANCE, 
            StatusConstants.BookStatus.COLOR_MAINTENANCE);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statusList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/borrow")
    public ResponseEntity<?> getBorrowStatusConfig() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        addStatusItem(statusList, StatusConstants.BorrowStatus.BORROWING, 
            StatusConstants.BorrowStatus.LABEL_BORROWING, 
            StatusConstants.BorrowStatus.COLOR_BORROWING);
        addStatusItem(statusList, StatusConstants.BorrowStatus.RETURNED, 
            StatusConstants.BorrowStatus.LABEL_RETURNED, 
            StatusConstants.BorrowStatus.COLOR_RETURNED);
        addStatusItem(statusList, StatusConstants.BorrowStatus.OVERDUE, 
            StatusConstants.BorrowStatus.LABEL_OVERDUE, 
            StatusConstants.BorrowStatus.COLOR_OVERDUE);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statusList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservation")
    public ResponseEntity<?> getReservationStatusConfig() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        addStatusItem(statusList, StatusConstants.ReservationStatus.PENDING, 
            StatusConstants.ReservationStatus.LABEL_PENDING, 
            StatusConstants.ReservationStatus.COLOR_PENDING);
        addStatusItem(statusList, StatusConstants.ReservationStatus.NOTIFIED, 
            StatusConstants.ReservationStatus.LABEL_NOTIFIED, 
            StatusConstants.ReservationStatus.COLOR_NOTIFIED);
        addStatusItem(statusList, StatusConstants.ReservationStatus.FULFILLED, 
            StatusConstants.ReservationStatus.LABEL_FULFILLED, 
            StatusConstants.ReservationStatus.COLOR_FULFILLED);
        addStatusItem(statusList, StatusConstants.ReservationStatus.CANCELLED, 
            StatusConstants.ReservationStatus.LABEL_CANCELLED, 
            StatusConstants.ReservationStatus.COLOR_CANCELLED);
        addStatusItem(statusList, StatusConstants.ReservationStatus.EXPIRED, 
            StatusConstants.ReservationStatus.LABEL_EXPIRED, 
            StatusConstants.ReservationStatus.COLOR_EXPIRED);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statusList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fine")
    public ResponseEntity<?> getFineStatusConfig() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        addStatusItem(statusList, StatusConstants.FineStatus.UNPAID, 
            StatusConstants.FineStatus.LABEL_UNPAID, 
            StatusConstants.FineStatus.COLOR_UNPAID);
        addStatusItem(statusList, StatusConstants.FineStatus.PAID, 
            StatusConstants.FineStatus.LABEL_PAID, 
            StatusConstants.FineStatus.COLOR_PAID);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", statusList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStatusConfig() {
        Map<String, Object> allConfig = new HashMap<>();
        allConfig.put("seat", getStatusMap("seat"));
        allConfig.put("book", getStatusMap("book"));
        allConfig.put("borrow", getStatusMap("borrow"));
        allConfig.put("reservation", getStatusMap("reservation"));
        allConfig.put("fine", getStatusMap("fine"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", allConfig);
        return ResponseEntity.ok(response);
    }

    private void addStatusItem(List<Map<String, Object>> list, String status, String label, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("status", status);
        item.put("label", label);
        item.put("color", color);
        list.add(item);
    }

    private List<Map<String, Object>> getStatusMap(String type) {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        switch (type) {
            case "seat":
                addStatusItem(statusList, StatusConstants.SeatStatus.AVAILABLE, 
                    StatusConstants.SeatStatus.LABEL_AVAILABLE, 
                    StatusConstants.SeatStatus.COLOR_AVAILABLE);
                addStatusItem(statusList, StatusConstants.SeatStatus.RESERVED, 
                    StatusConstants.SeatStatus.LABEL_RESERVED, 
                    StatusConstants.SeatStatus.COLOR_RESERVED);
                addStatusItem(statusList, StatusConstants.SeatStatus.OCCUPIED, 
                    StatusConstants.SeatStatus.LABEL_OCCUPIED, 
                    StatusConstants.SeatStatus.COLOR_OCCUPIED);
                addStatusItem(statusList, StatusConstants.SeatStatus.MAINTENANCE, 
                    StatusConstants.SeatStatus.LABEL_MAINTENANCE, 
                    StatusConstants.SeatStatus.COLOR_MAINTENANCE);
                break;
            case "book":
                addStatusItem(statusList, StatusConstants.BookStatus.AVAILABLE, 
                    StatusConstants.BookStatus.LABEL_AVAILABLE, 
                    StatusConstants.BookStatus.COLOR_AVAILABLE);
                addStatusItem(statusList, StatusConstants.BookStatus.BORROWED, 
                    StatusConstants.BookStatus.LABEL_BORROWED, 
                    StatusConstants.BookStatus.COLOR_BORROWED);
                addStatusItem(statusList, StatusConstants.BookStatus.RESERVED, 
                    StatusConstants.BookStatus.LABEL_RESERVED, 
                    StatusConstants.BookStatus.COLOR_RESERVED);
                addStatusItem(statusList, StatusConstants.BookStatus.MAINTENANCE, 
                    StatusConstants.BookStatus.LABEL_MAINTENANCE, 
                    StatusConstants.BookStatus.COLOR_MAINTENANCE);
                break;
            case "borrow":
                addStatusItem(statusList, StatusConstants.BorrowStatus.BORROWING, 
                    StatusConstants.BorrowStatus.LABEL_BORROWING, 
                    StatusConstants.BorrowStatus.COLOR_BORROWING);
                addStatusItem(statusList, StatusConstants.BorrowStatus.RETURNED, 
                    StatusConstants.BorrowStatus.LABEL_RETURNED, 
                    StatusConstants.BorrowStatus.COLOR_RETURNED);
                addStatusItem(statusList, StatusConstants.BorrowStatus.OVERDUE, 
                    StatusConstants.BorrowStatus.LABEL_OVERDUE, 
                    StatusConstants.BorrowStatus.COLOR_OVERDUE);
                break;
            case "reservation":
                addStatusItem(statusList, StatusConstants.ReservationStatus.PENDING, 
                    StatusConstants.ReservationStatus.LABEL_PENDING, 
                    StatusConstants.ReservationStatus.COLOR_PENDING);
                addStatusItem(statusList, StatusConstants.ReservationStatus.NOTIFIED, 
                    StatusConstants.ReservationStatus.LABEL_NOTIFIED, 
                    StatusConstants.ReservationStatus.COLOR_NOTIFIED);
                addStatusItem(statusList, StatusConstants.ReservationStatus.FULFILLED, 
                    StatusConstants.ReservationStatus.LABEL_FULFILLED, 
                    StatusConstants.ReservationStatus.COLOR_FULFILLED);
                addStatusItem(statusList, StatusConstants.ReservationStatus.CANCELLED, 
                    StatusConstants.ReservationStatus.LABEL_CANCELLED, 
                    StatusConstants.ReservationStatus.COLOR_CANCELLED);
                addStatusItem(statusList, StatusConstants.ReservationStatus.EXPIRED, 
                    StatusConstants.ReservationStatus.LABEL_EXPIRED, 
                    StatusConstants.ReservationStatus.COLOR_EXPIRED);
                break;
            case "fine":
                addStatusItem(statusList, StatusConstants.FineStatus.UNPAID, 
                    StatusConstants.FineStatus.LABEL_UNPAID, 
                    StatusConstants.FineStatus.COLOR_UNPAID);
                addStatusItem(statusList, StatusConstants.FineStatus.PAID, 
                    StatusConstants.FineStatus.LABEL_PAID, 
                    StatusConstants.FineStatus.COLOR_PAID);
                break;
        }
        
        return statusList;
    }
}
