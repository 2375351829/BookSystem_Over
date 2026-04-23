package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private Long parentId;
    private Integer sortOrder;
    private String description;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}