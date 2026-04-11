package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_account")
public class UserAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String userType;
    private String realName;
    private String phone;
    private String email;
    private String idCard;
    private String institution;
    private String role;
    private String language;
    
    private String studentId;
    private String facultyId;

    @TableField("user_code")
    private String userId;
    
    private String campus;
    private String college;
    private String grade;
    
    @TableField("class_name")
    private String className;
    
    private String counselor;
    
    private Integer status;
    private Integer deleted;
    private Integer violationCount;
    private Date lastViolationTime;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
}
