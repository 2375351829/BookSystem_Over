package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.BookReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookReviewMapper extends BaseMapper<BookReview> {
    
    @Select("SELECT * FROM book_review WHERE book_id = #{bookId} AND status = 1 ORDER BY create_time DESC")
    List<BookReview> selectByBookId(@Param("bookId") Long bookId);
    
    @Select("SELECT * FROM book_review WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<BookReview> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM book_review WHERE book_id = #{bookId} AND user_id = #{userId} LIMIT 1")
    BookReview selectByBookIdAndUserId(@Param("bookId") Long bookId, @Param("userId") Long userId);
    
    @Select("SELECT AVG(rating) FROM book_review WHERE book_id = #{bookId} AND status = 1")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);
    
    @Select("SELECT COUNT(*) FROM book_review WHERE book_id = #{bookId} AND status = 1")
    Integer countByBookId(@Param("bookId") Long bookId);
}
