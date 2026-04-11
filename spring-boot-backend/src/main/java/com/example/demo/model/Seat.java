package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("room_id")
    private Long roomId;
    
    @TableField("seat_number")
    private String seatNumber;
    
    @TableField("row_num")
    private Integer rowNum;
    
    @TableField("col_num")
    private Integer colNum;
    
    private Integer status;
    
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("seat_type")
    private String seatType;
    
    @TableField("has_power")
    private Integer hasPower;
    
    @TableField("has_lamp")
    private Integer hasLamp;
}
