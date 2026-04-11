package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private BookRecommendationService recommendationService;

    @GetMapping("/similar/{bookId}")
    public ResponseEntity<?> getSimilarBooks(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Book> books = recommendationService.getSimilarBooks(bookId, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getBooksByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Book> books = recommendationService.getBooksByCategory(category, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author")
    public ResponseEntity<?> getBooksByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Book> books = recommendationService.getBooksByAuthor(author, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/personalized")
    public ResponseEntity<?> getPersonalizedRecommendations(
            @RequestParam(defaultValue = "10") Integer limit,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<Book> books = recommendationService.getPersonalizedRecommendations(userId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularBooks(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Book> books = recommendationService.getPopularBooks(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<?> getNewArrivals(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<Book> books = recommendationService.getNewArrivals(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return 1L;
    }
}
