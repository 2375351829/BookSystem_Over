package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String content;
    private String type;
    private Integer priority;
    private Integer status;
    private Long publisherId;
    private String publisherName;
    private Date publishTime;
    private Date expireTime;
    private Integer viewCount;
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
}
