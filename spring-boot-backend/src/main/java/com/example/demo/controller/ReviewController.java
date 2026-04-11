package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.repository.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", reviews);
        response.put("total", 0);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<?> submitReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> reviewData) {
        
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "图书不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "评论提交成功");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "评论删除成功");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", reviews);
        response.put("total", 0);
        
        return ResponseEntity.ok(response);
    }
}
