package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("faq")
public class Faq {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String question;
    private String answer;
    private String category;
    private Integer sortOrder;
    private Integer viewCount;
    private Integer status;
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
}
