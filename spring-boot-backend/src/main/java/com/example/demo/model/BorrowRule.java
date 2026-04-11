package com.example.demo.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BorrowRule {
    private UserType userType;
    private String userTypeCode;
    private String description;
    private int maxBorrowLimit;
    private int borrowDays;
    private int maxRenewCount;
    private int renewDays;
    private BigDecimal fineRatePerDay;

    public BorrowRule() {
    }

    public BorrowRule(UserType userType, int maxBorrowLimit, int borrowDays, 
                      int maxRenewCount, int renewDays, BigDecimal fineRatePerDay) {
        this.userType = userType;
        this.userTypeCode = userType.getCode();
        this.description = userType.getDescription();
        this.maxBorrowLimit = maxBorrowLimit;
        this.borrowDays = borrowDays;
        this.maxRenewCount = maxRenewCount;
        this.renewDays = renewDays;
        this.fineRatePerDay = fineRatePerDay;
    }
}
