package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.model.Book;
import com.example.demo.model.BorrowRecord;
import com.example.demo.repository.BookMapper;
import com.example.demo.repository.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookRecommendationService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    public List<Book> getSimilarBooks(Long bookId, int limit) {
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .ne(Book::getId, bookId)
                   .and(wrapper -> wrapper
                       .eq(Book::getCategory, book.getCategory())
                       .or().eq(Book::getAuthor, book.getAuthor())
                       .or().like(Book::getTags, book.getTags() != null ? book.getTags() : "")
                   )
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        return bookMapper.selectList(queryWrapper);
    }

    public List<Book> getBooksByCategory(String category, int limit) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .eq(Book::getCategory, category)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        return bookMapper.selectList(queryWrapper);
    }

    public List<Book> getBooksByAuthor(String author, int limit) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .like(Book::getAuthor, author)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        return bookMapper.selectList(queryWrapper);
    }

    public List<Book> getPersonalizedRecommendations(Long userId, int limit) {
        LambdaQueryWrapper<BorrowRecord> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BorrowRecord::getUserId, userId)
                    .eq(BorrowRecord::getDeleted, 0)
                    .orderByDesc(BorrowRecord::getBorrowDate)
                    .last("LIMIT 20");

        List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(borrowWrapper);
        
        if (borrowRecords.isEmpty()) {
            return getPopularBooks(limit);
        }

        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> authorCount = new HashMap<>();
        
        for (BorrowRecord record : borrowRecords) {
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null && book.getDeleted() == 0) {
                String category = book.getCategory();
                String author = book.getAuthor();
                
                categoryCount.merge(category, 1, Integer::sum);
                authorCount.merge(author, 1, Integer::sum);
            }
        }

        List<String> topCategories = categoryCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        List<String> topAuthors = authorCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        Set<Long> borrowedBookIds = borrowRecords.stream()
            .map(BorrowRecord::getBookId)
            .collect(Collectors.toSet());

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .notIn(Book::getId, borrowedBookIds)
                   .and(wrapper -> {
                       wrapper.in(Book::getCategory, topCategories)
                             .or().in(Book::getAuthor, topAuthors);
                   })
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        List<Book> recommendations = bookMapper.selectList(queryWrapper);
        
        if (recommendations.size() < limit) {
            List<Book> popularBooks = getPopularBooks(limit - recommendations.size());
            Set<Long> existingIds = recommendations.stream().map(Book::getId).collect(Collectors.toSet());
            for (Book popular : popularBooks) {
                if (!existingIds.contains(popular.getId()) && !borrowedBookIds.contains(popular.getId())) {
                    recommendations.add(popular);
                    if (recommendations.size() >= limit) break;
                }
            }
        }

        return recommendations;
    }

    public List<Book> getPopularBooks(int limit) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .eq(Book::getStatus, "available")
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        return bookMapper.selectList(queryWrapper);
    }

    public List<Book> getNewArrivals(int limit) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getDeleted, 0)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT " + limit);

        return bookMapper.selectList(queryWrapper);
    }
}
