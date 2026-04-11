package com.example.demo.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 100;
    
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");

    public static ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "密码不能为空");
        }

        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, "密码长度不能少于" + MIN_LENGTH + "个字符");
        }

        if (password.length() > MAX_LENGTH) {
            return new ValidationResult(false, "密码长度不能超过" + MAX_LENGTH + "个字符");
        }

        if (!LETTER_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个字母");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个数字");
        }

        return new ValidationResult(true, "密码强度符合要求");
    }

    public static int calculateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int strength = 0;

        if (password.length() >= 6) {
            strength += 20;
        }
        if (password.length() >= 8) {
            strength += 10;
        }
        if (password.length() >= 12) {
            strength += 10;
        }
        if (Pattern.compile("[a-z]").matcher(password).find()) {
            strength += 15;
        }
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            strength += 15;
        }
        if (DIGIT_PATTERN.matcher(password).find()) {
            strength += 15;
        }
        if (Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) {
            strength += 15;
        }

        return Math.min(strength, 100);
    }

    public static String getStrengthLabel(int strength) {
        if (strength < 40) {
            return "弱";
        } else if (strength < 70) {
            return "中";
        } else if (strength < 90) {
            return "强";
        } else {
            return "非常强";
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
