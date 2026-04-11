package com.example.demo.util;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class FormValidator {

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int PASSWORD_MAX_LENGTH = 100;
    private static final int REAL_NAME_MAX_LENGTH = 50;
    private static final int PHONE_MAX_LENGTH = 20;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int ID_CARD_MAX_LENGTH = 20;
    private static final int INSTITUTION_MAX_LENGTH = 100;
    
    private static final int ISBN_MAX_LENGTH = 20;
    private static final int TITLE_MAX_LENGTH = 200;
    private static final int AUTHOR_MAX_LENGTH = 100;
    private static final int PUBLISHER_MAX_LENGTH = 100;
    private static final int SHELF_NO_MAX_LENGTH = 20;
    private static final int LOCATION_MAX_LENGTH = 100;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^(?:\\d{9}[\\dXx]|\\d-[\\d]{4}-[\\d]{4}-[\\dXx])$");
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^(?:\\d{13}|\\d{3}-\\d-[\\d]{4}-[\\d]{4}-\\d)$");
    private static final Pattern SHELF_NO_PATTERN = Pattern.compile("^[A-Za-z]\\d{1,3}-\\d{1,3}$");

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }

    public static ValidationResult validateRegistration(String username, String password, String email, 
                                                        String phone, String realName, String idCard, 
                                                        String institution) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("用户名不能为空");
        } else if (username.length() < USERNAME_MIN_LENGTH) {
            errors.add("用户名长度不能少于" + USERNAME_MIN_LENGTH + "个字符");
        } else if (username.length() > USERNAME_MAX_LENGTH) {
            errors.add("用户名长度不能超过" + USERNAME_MAX_LENGTH + "个字符");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.add("密码不能为空");
        } else if (password.length() > PASSWORD_MAX_LENGTH) {
            errors.add("密码长度不能超过" + PASSWORD_MAX_LENGTH + "个字符");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.add("邮箱不能为空");
        } else if (email.length() > EMAIL_MAX_LENGTH) {
            errors.add("邮箱长度不能超过" + EMAIL_MAX_LENGTH + "个字符");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("邮箱格式不正确");
        }

        if (phone == null || phone.trim().isEmpty()) {
            errors.add("手机号不能为空");
        } else if (phone.length() > PHONE_MAX_LENGTH) {
            errors.add("手机号长度不能超过" + PHONE_MAX_LENGTH + "个字符");
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            errors.add("手机号格式不正确");
        }

        if (realName != null && realName.length() > REAL_NAME_MAX_LENGTH) {
            errors.add("真实姓名长度不能超过" + REAL_NAME_MAX_LENGTH + "个字符");
        }

        if (idCard != null && !idCard.trim().isEmpty()) {
            if (idCard.length() > ID_CARD_MAX_LENGTH) {
                errors.add("身份证号长度不能超过" + ID_CARD_MAX_LENGTH + "个字符");
            } else if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
                errors.add("身份证号格式不正确");
            }
        }

        if (institution != null && institution.length() > INSTITUTION_MAX_LENGTH) {
            errors.add("所属机构长度不能超过" + INSTITUTION_MAX_LENGTH + "个字符");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateLogin(String username, String password) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("用户名不能为空");
        } else if (username.length() > USERNAME_MAX_LENGTH) {
            errors.add("用户名长度不能超过" + USERNAME_MAX_LENGTH + "个字符");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.add("密码不能为空");
        } else if (password.length() > PASSWORD_MAX_LENGTH) {
            errors.add("密码长度不能超过" + PASSWORD_MAX_LENGTH + "个字符");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateBook(String isbn, String title, String author, 
                                                String publisher, String category, 
                                                String shelfNo, String location) {
        List<String> errors = new ArrayList<>();

        if (isbn != null && !isbn.trim().isEmpty()) {
            if (isbn.length() > ISBN_MAX_LENGTH) {
                errors.add("ISBN长度不能超过" + ISBN_MAX_LENGTH + "个字符");
            } else if (!isValidIsbn(isbn)) {
                errors.add("ISBN格式不正确，应为10位或13位数字");
            }
        }

        if (title == null || title.trim().isEmpty()) {
            errors.add("书名不能为空");
        } else if (title.length() > TITLE_MAX_LENGTH) {
            errors.add("书名长度不能超过" + TITLE_MAX_LENGTH + "个字符");
        }

        if (author != null && author.length() > AUTHOR_MAX_LENGTH) {
            errors.add("作者长度不能超过" + AUTHOR_MAX_LENGTH + "个字符");
        }

        if (publisher != null && publisher.length() > PUBLISHER_MAX_LENGTH) {
            errors.add("出版社长度不能超过" + PUBLISHER_MAX_LENGTH + "个字符");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.add("分类不能为空");
        }

        if (shelfNo != null && !shelfNo.trim().isEmpty()) {
            if (shelfNo.length() > SHELF_NO_MAX_LENGTH) {
                errors.add("书架号长度不能超过" + SHELF_NO_MAX_LENGTH + "个字符");
            } else if (!SHELF_NO_PATTERN.matcher(shelfNo).matches()) {
                errors.add("书架号格式不正确，应为如 A1-01 格式");
            }
        }

        if (location != null && location.length() > LOCATION_MAX_LENGTH) {
            errors.add("馆藏位置长度不能超过" + LOCATION_MAX_LENGTH + "个字符");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static boolean isValidIsbn(String isbn) {
        String cleanIsbn = isbn.replaceAll("[-\\s]", "");
        
        if (cleanIsbn.length() == 10) {
            return ISBN_10_PATTERN.matcher(isbn).matches() || validateIsbn10Checksum(cleanIsbn);
        } else if (cleanIsbn.length() == 13) {
            return ISBN_13_PATTERN.matcher(isbn).matches() || validateIsbn13Checksum(cleanIsbn);
        }
        
        return false;
    }

    private static boolean validateIsbn10Checksum(String isbn) {
        if (isbn.length() != 10) return false;
        
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) return false;
            sum += (c - '0') * (10 - i);
        }
        
        char last = isbn.charAt(9);
        if (last == 'X' || last == 'x') {
            sum += 10;
        } else if (Character.isDigit(last)) {
            sum += (last - '0');
        } else {
            return false;
        }
        
        return sum % 11 == 0;
    }

    private static boolean validateIsbn13Checksum(String isbn) {
        if (isbn.length() != 13) return false;
        
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) return false;
            int digit = c - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        char last = isbn.charAt(12);
        if (!Character.isDigit(last)) return false;
        
        int checksum = (10 - (sum % 10)) % 10;
        return (last - '0') == checksum;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidIdCard(String idCard) {
        return idCard != null && ID_CARD_PATTERN.matcher(idCard).matches();
    }

    public static boolean isValidShelfNo(String shelfNo) {
        return shelfNo != null && SHELF_NO_PATTERN.matcher(shelfNo).matches();
    }
}
