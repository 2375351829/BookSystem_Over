package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@TableName("book_import_detail")
public class BookImportDetail {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotNull(message = "行号不能为空")
    private Integer rowNumber;

    private String isbn;

    private String title;

    private String author;

    @NotNull(message = "状态不能为空")
    private String status;

    private String errorMessage;

    private Long bookId;

    @TableField("create_time")
    private Date createTime;
}
