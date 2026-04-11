package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.Book;
import com.example.demo.repository.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/isbn")
public class IsbnController {

    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/search/{isbn}")
    public ResponseEntity<?> searchByISBN(@PathVariable String isbn) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getIsbn, isbn).eq(Book::getDeleted, 0);
        Book book = bookMapper.selectOne(queryWrapper);
        
        if (book == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "未找到该ISBN对应的图书");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", book);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch-search")
    public ResponseEntity<?> batchSearch(@RequestBody Map<String, Object> request) {
        List<String> isbns = (List<String>) request.get("isbns");
        
        if (isbns == null || isbns.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "ISBN列表不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Book::getIsbn, isbns).eq(Book::getDeleted, 0);
        List<Book> books = bookMapper.selectList(queryWrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", books);
        
        return ResponseEntity.ok(response);
    }
}
