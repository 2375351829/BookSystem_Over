package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@TableName("user_behavior")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long bookId;

    @NotNull(message = "行为类型不能为空")
    private String actionType;

    private String actionDetail;

    private String ipAddress;

    private String userAgent;

    @TableField("create_time")
    private Date createTime;
}
