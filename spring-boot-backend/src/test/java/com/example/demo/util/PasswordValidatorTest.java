package com.example.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password123!");
        assertTrue(result.isValid());
        assertEquals("密码强度符合要求", result.getMessage());
    }

    @Test
    void testNullPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(null);
        assertFalse(result.isValid());
        assertEquals("密码不能为空", result.getMessage());
    }

    @Test
    void testEmptyPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("");
        assertFalse(result.isValid());
        assertEquals("密码不能为空", result.getMessage());
    }

    @Test
    void testTooShortPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Pass1!");
        assertFalse(result.isValid());
        assertEquals("密码长度不能少于8个字符", result.getMessage());
    }

    @Test
    void testNoUppercasePassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("password123!");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个大写字母", result.getMessage());
    }

    @Test
    void testNoLowercasePassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("PASSWORD123!");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个小写字母", result.getMessage());
    }

    @Test
    void testNoDigitPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password!!");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个数字", result.getMessage());
    }

    @Test
    void testNoSpecialCharPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password123");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个特殊字符（!@#$%^&*等）", result.getMessage());
    }

    @Test
    void testCalculateStrength() {
        assertEquals(0, PasswordValidator.calculateStrength(null));
        assertEquals(0, PasswordValidator.calculateStrength(""));
        assertEquals(100, PasswordValidator.calculateStrength("Password123!"));
        assertTrue(PasswordValidator.calculateStrength("password") < 40);
        assertTrue(PasswordValidator.calculateStrength("Password1") >= 70);
    }

    @Test
    void testGetStrengthLabel() {
        assertEquals("弱", PasswordValidator.getStrengthLabel(30));
        assertEquals("中", PasswordValidator.getStrengthLabel(50));
        assertEquals("强", PasswordValidator.getStrengthLabel(80));
        assertEquals("非常强", PasswordValidator.getStrengthLabel(95));
    }
}
