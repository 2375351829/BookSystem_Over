package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.BookRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookRecommendationMapper extends BaseMapper<BookRecommendation> {
    
    @Select("SELECT * FROM book_recommendation WHERE user_id = #{userId} ORDER BY score DESC, create_time DESC")
    List<BookRecommendation> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM book_recommendation WHERE user_id = #{userId} ORDER BY score DESC, create_time DESC LIMIT #{limit}")
    List<BookRecommendation> selectTopByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Select("SELECT * FROM book_recommendation WHERE book_id = #{bookId} ORDER BY score DESC")
    List<BookRecommendation> selectByBookId(@Param("bookId") Long bookId);
    
    @Select("SELECT * FROM book_recommendation WHERE user_id = #{userId} AND book_id = #{bookId} LIMIT 1")
    BookRecommendation selectByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    @Select("SELECT COUNT(*) FROM book_recommendation WHERE user_id = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);
}
