package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.ReadingRoom;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReadingRoomMapper extends BaseMapper<ReadingRoom> {
}