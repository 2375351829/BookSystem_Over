package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("purchase_request")
public class PurchaseRequest {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long requesterId;
    private String bookTitle;
    private String author;
    private String isbn;
    private Integer quantity;
    private String publisher;
    private String reason;
    private Integer status;
    private Date approveTime;
    private String rejectReason;
    private String remarks;
    private Long reviewerId;
    private String reviewComment;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}