package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.OperationLog;
import com.example.demo.model.Book;
import com.example.demo.repository.BookMapper;
import com.example.demo.util.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookMapper bookMapper;
    
    // 图书列表查询
    @GetMapping
    public ResponseEntity<IPage<Book>> getBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String language) {

        Page<Book> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();

        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Book::getCategory, category);
        }

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like(Book::getTitle, keyword)
                .or().like(Book::getAuthor, keyword)
                .or().like(Book::getIsbn, keyword)
            );
        }

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Book::getStatus, status);
        }

        if (language != null && !language.isEmpty()) {
            queryWrapper.eq(Book::getLanguage, language);
        }

        queryWrapper.eq(Book::getDeleted, 0);

        IPage<Book> result = bookMapper.selectPage(pageInfo, queryWrapper);
        return ResponseEntity.ok(result);
    }
    
    // 图书详情查询
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }
    
    // 图书添加（管理员）
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @OperationLog(module = "图书管理", operation = "添加图书", description = "管理员添加新图书")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        FormValidator.ValidationResult validation = FormValidator.validateBook(
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublisher(),
            book.getCategory(),
            book.getShelfNo(),
            book.getLocation()
        );
        
        if (!validation.isValid()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", validation.getErrorMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        book.setDeleted(0);
        book.setAvailableCopies(book.getTotalCopies());
        book.setStatus("available");
        bookMapper.insert(book);
        return ResponseEntity.ok(book);
    }
    
    // 图书修改（管理员）
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @OperationLog(module = "图书管理", operation = "修改图书", description = "管理员修改图书信息")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book existingBook = bookMapper.selectById(id);
        if (existingBook == null || existingBook.getDeleted() == 1) {
            return ResponseEntity.notFound().build();
        }
        
        FormValidator.ValidationResult validation = FormValidator.validateBook(
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublisher(),
            book.getCategory(),
            book.getShelfNo(),
            book.getLocation()
        );
        
        if (!validation.isValid()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", validation.getErrorMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        book.setId(id);
        book.setDeleted(existingBook.getDeleted());
        book.setCreateTime(existingBook.getCreateTime());
        book.setUpdateTime(new java.util.Date());
        bookMapper.updateById(book);
        return ResponseEntity.ok(book);
    }
    
    // 图书删除（管理员）
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @OperationLog(module = "图书管理", operation = "删除图书", description = "管理员删除图书")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) {
            return ResponseEntity.notFound().build();
        }
        
        // 软删除
        book.setDeleted(1);
        bookMapper.updateById(book);
        return ResponseEntity.noContent().build();
    }

    // 批量删除图书（管理员）
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteBooks(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请选择要删除的图书"));
        }

        int deletedCount = 0;
        for (Long id : ids) {
            Book book = bookMapper.selectById(id);
            if (book != null && book.getDeleted() == 0) {
                book.setDeleted(1);
                book.setUpdateTime(new java.util.Date());
                bookMapper.updateById(book);
                deletedCount++;
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "成功删除 " + deletedCount + " 本图书"));
    }

    @OperationLog(module = "图书管理", operation = "批量编辑", description = "批量修改图书信息")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/batch-update")
    public ResponseEntity<Map<String, Object>> batchUpdateBooks(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请选择要修改的图书"));
        }
        
        if (data == null || data.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "没有要修改的字段"));
        }
        
        int updatedCount = 0;
        for (Integer id : ids) {
            Book book = bookMapper.selectById(id);
            if (book != null && book.getDeleted() == 0) {
                if (data.containsKey("category")) book.setCategory((String) data.get("category"));
                if (data.containsKey("language")) book.setLanguage((String) data.get("language"));
                if (data.containsKey("status")) book.setStatus((String) data.get("status"));
                if (data.containsKey("location")) book.setLocation((String) data.get("location"));
                if (data.containsKey("coverUrl")) book.setCoverUrl((String) data.get("coverUrl"));
                book.setUpdateTime(new java.util.Date());
                bookMapper.updateById(book);
                updatedCount++;
            }
        }
        
        return ResponseEntity.ok(Map.of("success", true, "message", "成功修改 " + updatedCount + " 本图书"));
    }

    // 批量修改图书状态（管理员）
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch-update-status")
    public ResponseEntity<Map<String, Object>> batchUpdateBookStatus(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请选择要修改的图书"));
        }
        
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "请指定要修改的状态"));
        }

        int updatedCount = 0;
        for (Long id : ids) {
            Book book = bookMapper.selectById(id);
            if (book != null && book.getDeleted() == 0) {
                book.setStatus(status);
                book.setUpdateTime(new java.util.Date());
                bookMapper.updateById(book);
                updatedCount++;
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "成功修改 " + updatedCount + " 本图书状态"));
    }
    
    // 高级搜索图书
    @GetMapping("/advanced-search")
    public ResponseEntity<IPage<Book>> advancedSearchBooks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String publishYearStart,
            @RequestParam(required = false) String publishYearEnd,
            @RequestParam(required = false) java.math.BigDecimal priceMin,
            @RequestParam(required = false) java.math.BigDecimal priceMax,
            @RequestParam(required = false) String tags) {
        
        Page<Book> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(Book::getDeleted, 0);
        
        if (title != null && !title.isEmpty()) {
            queryWrapper.like(Book::getTitle, title);
        }
        
        if (author != null && !author.isEmpty()) {
            queryWrapper.like(Book::getAuthor, author);
        }
        
        if (isbn != null && !isbn.isEmpty()) {
            queryWrapper.like(Book::getIsbn, isbn);
        }
        
        if (publisher != null && !publisher.isEmpty()) {
            queryWrapper.like(Book::getPublisher, publisher);
        }
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Book::getCategory, category);
        }
        
        if (language != null && !language.isEmpty()) {
            queryWrapper.eq(Book::getLanguage, language);
        }
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Book::getStatus, status);
        }
        
        if (publishYearStart != null && !publishYearStart.isEmpty()) {
            queryWrapper.ge(Book::getPublishYear, publishYearStart);
        }
        
        if (publishYearEnd != null && !publishYearEnd.isEmpty()) {
            queryWrapper.le(Book::getPublishYear, publishYearEnd);
        }
        
        if (priceMin != null) {
            queryWrapper.ge(Book::getPrice, priceMin);
        }
        
        if (priceMax != null) {
            queryWrapper.le(Book::getPrice, priceMax);
        }
        
        if (tags != null && !tags.isEmpty()) {
            queryWrapper.like(Book::getTags, tags);
        }
        
        queryWrapper.orderByDesc(Book::getCreateTime);
        
        IPage<Book> result = bookMapper.selectPage(pageInfo, queryWrapper);
        return ResponseEntity.ok(result);
    }
    
    // 获取所有图书分类
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Book::getCategory).eq(Book::getDeleted, 0);

        List<Book> books = bookMapper.selectList(queryWrapper);
        List<String> categories = books.stream()
                .map(Book::getCategory)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    // 获取热门图书（使用创建时间排序作为示例，实际可改为借阅量）
    @GetMapping("/hot")
    public ResponseEntity<List<Book>> getHotBooks() {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT 10");
        List<Book> books = bookMapper.selectList(queryWrapper);
        return ResponseEntity.ok(books);
    }

    // 获取新书
    @GetMapping("/new")
    public ResponseEntity<List<Book>> getNewBooks() {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT 10");
        List<Book> books = bookMapper.selectList(queryWrapper);
        return ResponseEntity.ok(books);
    }
    
    // 图书搜索功能
    @GetMapping("/search")
    public ResponseEntity<IPage<Book>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<Book> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.and(wrapper -> wrapper
            .like(Book::getTitle, keyword)
            .or().like(Book::getTitleEn, keyword)
            .or().like(Book::getAuthor, keyword)
            .or().like(Book::getAuthorEn, keyword)
            .or().like(Book::getIsbn, keyword)
            .or().like(Book::getSummary, keyword)
            .or().like(Book::getSummaryEn, keyword)
        );
        
        queryWrapper.eq(Book::getDeleted, 0);
        
        IPage<Book> result = bookMapper.selectPage(pageInfo, queryWrapper);
        return ResponseEntity.ok(result);
    }
}