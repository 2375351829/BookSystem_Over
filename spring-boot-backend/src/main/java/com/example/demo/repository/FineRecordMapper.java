package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.FineRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FineRecordMapper extends BaseMapper<FineRecord> {
}