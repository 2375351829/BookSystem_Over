package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;
    private String seatNumber;
    private String seatType;
    private Integer hasPower;
    private Integer hasLamp;
    private Integer rowNum;
    private Integer colNum;
    private Integer status;
    private Integer deleted;
    private Date createTime;
}