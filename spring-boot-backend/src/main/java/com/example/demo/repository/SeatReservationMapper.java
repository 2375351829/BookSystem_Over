package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.SeatReservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeatReservationMapper extends BaseMapper<SeatReservation> {
}