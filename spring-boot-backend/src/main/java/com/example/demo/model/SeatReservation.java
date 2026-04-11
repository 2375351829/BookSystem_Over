package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat_reservation")
public class SeatReservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("seat_id")
    private Long seatId;

    @TableField("room_id")
    private Long roomId;

    @TableField("reserve_date")
    private Date reserveDate;
    
    @TableField("start_time")
    private String startTime;
    
    @TableField("end_time")
    private String endTime;
    
    private Integer status;
    
    @TableField("check_in_time")
    private Date checkInTime;
    
    @TableField("check_out_time")
    private Date checkOutTime;
    
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
}
