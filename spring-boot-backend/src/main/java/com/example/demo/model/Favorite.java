package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long bookId;
    private Date createTime;
}
