package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("notification_template")
public class NotificationTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;
    private String channel;
    private String title;
    private String content;
    private String variables;
    private Integer status;
    private String type;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}