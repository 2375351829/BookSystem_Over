package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("export_task")
public class ExportTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String type;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String format;
    private String queryParams;
    private String status;
    private String errorMessage;
    private Date createTime;
    private Date completeTime;

    public static final String TYPE_BOOK_EXPORT = "BOOK_EXPORT";
    public static final String TYPE_BORROW_STATISTICS = "BORROW_STATISTICS";
    public static final String TYPE_USER_STATISTICS = "USER_STATISTICS";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
}