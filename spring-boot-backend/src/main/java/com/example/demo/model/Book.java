package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String isbn;
    private String title;
    private String subtitle;
    private String titleEn;
    private String author;
    private String authorEn;
    private String translator;
    private String publisher;
    private String publishYear;
    private String edition;
    private String category;
    private String categoryName;
    private String language;
    private Integer pages;
    private BigDecimal price;
    private String tags;
    private String coverUrl;
    private String summary;
    private String summaryEn;

    private String status;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private String shelfNo;

    private Integer deleted;
    private Date createTime;
    private Date updateTime;
}