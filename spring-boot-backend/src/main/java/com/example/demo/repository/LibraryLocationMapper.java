package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.LibraryLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LibraryLocationMapper extends BaseMapper<LibraryLocation> {
    
    @Select("SELECT * FROM library_location WHERE deleted = 0 AND status = 1 ORDER BY sort_order ASC")
    List<LibraryLocation> findAllActive();
    
    @Select("SELECT * FROM library_location WHERE deleted = 0 AND floor = #{floor} ORDER BY sort_order ASC")
    List<LibraryLocation> findByFloor(@Param("floor") String floor);
    
    @Select("SELECT DISTINCT floor FROM library_location WHERE deleted = 0 AND status = 1 ORDER BY floor ASC")
    List<String> findAllFloors();
}
