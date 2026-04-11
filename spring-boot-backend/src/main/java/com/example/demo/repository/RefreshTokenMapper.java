package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
    
    RefreshToken findByToken(@Param("token") String token);
    
    RefreshToken findByUserId(@Param("userId") Long userId);
    
    int deleteByUserId(@Param("userId") Long userId);
}
