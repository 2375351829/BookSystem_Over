package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long bookId;
    private String bookBarcode;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private Integer renewCount;
    private Integer status;
    private Long operatorId;
    private String remarks;
    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}