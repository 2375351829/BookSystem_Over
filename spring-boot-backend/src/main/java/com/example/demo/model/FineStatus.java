package com.example.demo.model;

import lombok.Getter;

@Getter
public enum FineStatus {
    UNPAID(0, "未支付"),
    PAID(1, "已支付");

    private final Integer code;
    private final String description;

    FineStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static FineStatus fromCode(Integer code) {
        if (code == null) {
            return UNPAID;
        }
        for (FineStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return UNPAID;
    }
}
