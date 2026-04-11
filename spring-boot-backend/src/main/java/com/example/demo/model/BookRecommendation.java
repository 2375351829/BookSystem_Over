package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("book_recommendation")
public class BookRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    private BigDecimal score;

    private String reason;

    @TableField("create_time")
    private Date createTime;
}
