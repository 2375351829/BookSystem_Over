package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String username;
    private String operation;
    private String module;
    private String method;
    private String params;
    private String result;
    private String ipAddress;
    private String userAgent;
    private Integer status;
    private String errorMessage;
    private Long executeTime;
    private Date createTime;
}