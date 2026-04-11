package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("refresh_token")
public class RefreshToken {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("token")
    private String token;
    
    @TableField("expiry_date")
    private Date expiryDate;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("revoked")
    private Integer revoked;
}
