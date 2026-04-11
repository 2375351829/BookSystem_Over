package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.Faq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FaqMapper extends BaseMapper<Faq> {
    
    @Select("SELECT * FROM faq WHERE deleted = 0 AND status = 1 ORDER BY sort_order ASC, create_time DESC")
    List<Faq> findAllActive();
    
    @Select("SELECT * FROM faq WHERE deleted = 0 AND category = #{category} AND status = 1 ORDER BY sort_order ASC")
    List<Faq> findByCategory(@Param("category") String category);
    
    @Select("SELECT DISTINCT category FROM faq WHERE deleted = 0 AND status = 1")
    List<String> findAllCategories();
    
    @Select("SELECT * FROM faq WHERE deleted = 0 AND (question LIKE CONCAT('%', #{keyword}, '%') OR answer LIKE CONCAT('%', #{keyword}, '%')) ORDER BY sort_order ASC")
    List<Faq> searchByKeyword(@Param("keyword") String keyword);
}
