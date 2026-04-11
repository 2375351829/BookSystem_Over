package com.example.demo.model;

import lombok.Getter;

@Getter
public enum FineType {
    OVERDUE("OVERDUE", "逾期罚款"),
    DAMAGE("DAMAGE", "损坏罚款"),
    LOSS("LOSS", "丢失罚款");

    private final String code;
    private final String description;

    FineType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static FineType fromCode(String code) {
        if (code == null) {
            return OVERDUE;
        }
        for (FineType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return OVERDUE;
    }
}
