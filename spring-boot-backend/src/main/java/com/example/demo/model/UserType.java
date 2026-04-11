package com.example.demo.model;

import lombok.Getter;

@Getter
public enum UserType {
    READER("READER", "普通读者"),
    STUDENT("STUDENT", "学生"),
    TEACHER("TEACHER", "教师"),
    INTERNATIONAL("INTERNATIONAL", "国际读者");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserType fromCode(String code) {
        if (code == null) {
            return READER;
        }
        for (UserType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return READER;
    }
}
