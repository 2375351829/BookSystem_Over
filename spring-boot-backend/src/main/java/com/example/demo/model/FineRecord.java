package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Long borrowRecordId;
    private String fineType;
    private BigDecimal amount;
    private Integer paidStatus;
    private Date paidDate;
    private Long operatorId;
    private String remarks;
    private Integer deleted;
    private Date createTime;

    public static final String TYPE_OVERDUE = "OVERDUE";
    public static final String TYPE_DAMAGE = "DAMAGE";
    public static final String TYPE_LOSS = "LOSS";

    public void setFineTypeEnum(FineType fineType) {
        this.fineType = fineType != null ? fineType.getCode() : null;
    }

    public FineType getFineTypeEnum() {
        return FineType.fromCode(this.fineType);
    }
}