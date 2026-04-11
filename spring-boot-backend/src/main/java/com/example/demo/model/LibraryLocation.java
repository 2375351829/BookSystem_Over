package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("library_location")
public class LibraryLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String floor;
    private String area;
    private String zone;
    private String shelfRange;
    private String categoryRange;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    private Integer status;
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
}
