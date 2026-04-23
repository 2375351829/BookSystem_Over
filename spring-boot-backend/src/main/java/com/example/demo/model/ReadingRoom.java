package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("reading_room")
public class ReadingRoom {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String location;
    private Integer capacity;
    private String openTime;
    private String closeTime;
    private Integer status;
    private Integer totalSeats;
    private Integer availableSeats;
    private String description;
    private String imageUrl;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}