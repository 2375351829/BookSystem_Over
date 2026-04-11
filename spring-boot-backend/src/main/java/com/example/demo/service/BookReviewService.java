package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.BookReview;
import com.example.demo.repository.BookReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookReviewService {

    private static final Logger logger = LoggerFactory.getLogger(BookReviewService.class);

    @Autowired
    private BookReviewMapper bookReviewMapper;

    public boolean addReview(BookReview review) {
        logger.info("添加图书评价: bookId={}, userId={}, rating={}", review.getBookId(), review.getUserId(), review.getRating());
        
        BookReview existingReview = bookReviewMapper.selectByBookIdAndUserId(review.getBookId(), review.getUserId());
        if (existingReview != null) {
            logger.warn("用户已评价过该图书: bookId={}, userId={}", review.getBookId(), review.getUserId());
            return false;
        }
        
        review.setStatus(1);
        review.setCreateTime(new Date());
        int result = bookReviewMapper.insert(review);
        
        if (result > 0) {
            logger.info("图书评价添加成功: reviewId={}", review.getId());
        }
        return result > 0;
    }

    public boolean updateReview(BookReview review) {
        logger.info("更新图书评价: reviewId={}", review.getId());
        
        review.setUpdateTime(new Date());
        int result = bookReviewMapper.updateById(review);
        
        if (result > 0) {
            logger.info("图书评价更新成功: reviewId={}", review.getId());
        }
        return result > 0;
    }

    public boolean deleteReview(Long id) {
        logger.info("删除图书评价: reviewId={}", id);
        
        int result = bookReviewMapper.deleteById(id);
        
        if (result > 0) {
            logger.info("图书评价删除成功: reviewId={}", id);
        }
        return result > 0;
    }

    public BookReview getReviewById(Long id) {
        return bookReviewMapper.selectById(id);
    }

    public List<BookReview> getReviewsByBookId(Long bookId) {
        return bookReviewMapper.selectByBookId(bookId);
    }

    public List<BookReview> getReviewsByUserId(Long userId) {
        return bookReviewMapper.selectByUserId(userId);
    }

    public BookReview getReviewByBookIdAndUserId(Long bookId, Long userId) {
        return bookReviewMapper.selectByBookIdAndUserId(bookId, userId);
    }

    public Double getAverageRating(Long bookId) {
        return bookReviewMapper.getAverageRatingByBookId(bookId);
    }

    public Integer getReviewCount(Long bookId) {
        return bookReviewMapper.countByBookId(bookId);
    }

    public IPage<BookReview> getReviewsByPage(int page, int size) {
        Page<BookReview> pageInfo = new Page<>(page, size);
        QueryWrapper<BookReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1).orderByDesc("create_time");
        return bookReviewMapper.selectPage(pageInfo, queryWrapper);
    }

    public boolean updateReviewStatus(Long id, Integer status) {
        logger.info("更新图书评价状态: reviewId={}, status={}", id, status);
        
        BookReview review = bookReviewMapper.selectById(id);
        if (review != null) {
            review.setStatus(status);
            review.setUpdateTime(new Date());
            int result = bookReviewMapper.updateById(review);
            
            if (result > 0) {
                logger.info("图书评价状态更新成功: reviewId={}", id);
            }
            return result > 0;
        }
        return false;
    }
}
