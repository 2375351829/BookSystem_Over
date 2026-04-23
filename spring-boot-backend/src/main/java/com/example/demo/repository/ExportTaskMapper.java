package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.ExportTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExportTaskMapper extends BaseMapper<ExportTask> {
}