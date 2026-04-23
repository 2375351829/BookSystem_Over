package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat_reservation")
public class SeatReservation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long seatId;
    private Long roomId;
    private Date reserveDate;
    private String startTime;
    private String endTime;
    private Integer status;
    private Date checkInTime;
    private Date checkOutTime;
    private Integer deleted;
    private Date createTime;
}