package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    
    @Select("SELECT * FROM announcement WHERE deleted = 0 AND status = 1 AND (expire_time IS NULL OR expire_time > NOW()) ORDER BY priority DESC, publish_time DESC")
    List<Announcement> findActiveAnnouncements();
    
    @Select("SELECT * FROM announcement WHERE deleted = 0 AND type = #{type} AND status = 1 ORDER BY priority DESC, publish_time DESC")
    List<Announcement> findByType(@Param("type") String type);
}
