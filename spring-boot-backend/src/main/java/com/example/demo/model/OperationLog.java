package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private String username;
    private String operation;
    private String module;
    private String method;
    private String params;
    private String result;
    
    @TableField("ip_address")
    private String ipAddress;
    
    @TableField("user_agent")
    private String userAgent;
    
    private Integer status;
    
    @TableField("error_message")
    private String errorMessage;
    
    @TableField("execute_time")
    private Long executeTime;
    
    @TableField("create_time")
    private Date createTime;
}
