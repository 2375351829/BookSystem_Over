package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("fine_record")
public class FineRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    @TableField("borrow_record_id")
    private Long borrowRecordId;
    
    @TableField("fine_type")
    private String fineType;
    
    private BigDecimal amount;
    
    @TableField("paid_status")
    private Integer paidStatus;
    
    @TableField("paid_date")
    private Date paidDate;
    
    private Long operatorId;
    private String remarks;
    private Integer deleted;
    
    @TableField("create_time")
    private Date createTime;
    
    public FineType getFineTypeEnum() {
        return FineType.fromCode(this.fineType);
    }
    
    public void setFineTypeEnum(FineType fineType) {
        this.fineType = fineType.getCode();
    }
    
    public FineStatus getStatusEnum() {
        return FineStatus.fromCode(this.paidStatus);
    }
    
    public void setStatusEnum(FineStatus status) {
        this.paidStatus = status.getCode();
    }
}
