package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserBehavior> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND action_type = #{actionType} ORDER BY create_time DESC")
    List<UserBehavior> selectByUserIdAndActionType(@Param("userId") Long userId, @Param("actionType") String actionType);
    
    @Select("SELECT * FROM user_behavior WHERE book_id = #{bookId} ORDER BY create_time DESC")
    List<UserBehavior> selectByBookId(@Param("bookId") Long bookId);
    
    @Select("SELECT COUNT(*) FROM user_behavior WHERE user_id = #{userId} AND action_type = #{actionType}")
    Integer countByUserIdAndActionType(@Param("userId") Long userId, @Param("actionType") String actionType);
    
    @Select("SELECT COUNT(DISTINCT user_id) FROM user_behavior WHERE book_id = #{bookId} AND action_type = 'VIEW'")
    Integer countViewUsersByBookId(@Param("bookId") Long bookId);
}
