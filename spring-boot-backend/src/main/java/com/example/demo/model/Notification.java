package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private String channel;
    private Long relatedId;
    private String relatedType;
    private Date readTime;
    private Integer deleted;
    private Date createTime;

    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_BORROW = "BORROW";
    public static final String TYPE_RESERVATION = "RESERVATION";
    public static final String TYPE_INQUIRY = "INQUIRY";
    public static final String TYPE_FINE = "FINE";
    public static final String TYPE_RETURN = "RETURN";
    public static final String TYPE_DUE_REMINDER = "DUE_REMINDER";
    public static final String TYPE_OVERDUE = "OVERDUE";
}