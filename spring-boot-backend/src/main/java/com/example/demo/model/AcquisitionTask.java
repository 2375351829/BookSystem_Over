package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("acquisition_task")
public class AcquisitionTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("book_id")
    private Long bookId;

    @TableField("book_title")
    private String bookTitle;

    @TableField("book_isbn")
    private String bookIsbn;

    private String type;

    private String source;

    private String assignee;

    private String status;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    private String remark;

    private Integer deleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
