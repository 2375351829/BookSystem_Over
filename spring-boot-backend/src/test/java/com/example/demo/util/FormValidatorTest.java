package com.example.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FormValidatorTest {

    @Test
    void testValidRegistration() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            "testuser", "Password123!", "test@example.com", 
            "13800138000", "Test User", "110101199001011234", 
            "Test Institution"
        );
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testRegistrationWithNullUsername() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            null, "Password123!", "test@example.com", 
            "13800138000", "Test User", null, null
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("用户名不能为空"));
    }

    @Test
    void testRegistrationWithInvalidEmail() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            "testuser", "Password123!", "invalid-email", 
            "13800138000", "Test User", null, null
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("邮箱格式不正确"));
    }

    @Test
    void testRegistrationWithInvalidPhone() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            "testuser", "Password123!", "test@example.com", 
            "12345", "Test User", null, null
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("手机号格式不正确"));
    }

    @Test
    void testRegistrationWithNullEmail() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            "testuser", "Password123!", null, 
            "13800138000", "Test User", null, null
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("邮箱不能为空"));
    }

    @Test
    void testRegistrationWithNullPhone() {
        FormValidator.ValidationResult result = FormValidator.validateRegistration(
            "testuser", "Password123!", "test@example.com", 
            null, "Test User", null, null
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("手机号不能为空"));
    }

    @Test
    void testValidLogin() {
        FormValidator.ValidationResult result = FormValidator.validateLogin("testuser", "Password123!");
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testLoginWithNullUsername() {
        FormValidator.ValidationResult result = FormValidator.validateLogin(null, "Password123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("用户名不能为空"));
    }

    @Test
    void testLoginWithNullPassword() {
        FormValidator.ValidationResult result = FormValidator.validateLogin("testuser", null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("密码不能为空"));
    }

    @Test
    void testValidBook() {
        FormValidator.ValidationResult result = FormValidator.validateBook(
            "9787111234567", "Test Book", "Test Author", 
            "Test Publisher", "Fiction", "A1-01", 
            "Shelf A"
        );
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testBookWithNullTitle() {
        FormValidator.ValidationResult result = FormValidator.validateBook(
            "9787111234567", null, "Test Author", 
            "Test Publisher", "Fiction", "A1-01", 
            "Shelf A"
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("书名不能为空"));
    }

    @Test
    void testBookWithNullCategory() {
        FormValidator.ValidationResult result = FormValidator.validateBook(
            "9787111234567", "Test Book", "Test Author", 
            "Test Publisher", null, "A1-01", 
            "Shelf A"
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("分类不能为空"));
    }

    @Test
    void testBookWithInvalidShelfNo() {
        FormValidator.ValidationResult result = FormValidator.validateBook(
            "9787111234567", "Test Book", "Test Author", 
            "Test Publisher", "Fiction", "invalid", 
            "Shelf A"
        );
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("书架号格式不正确"));
    }

    @Test
    void testIsValidIsbn10() {
        assertTrue(FormValidator.isValidIsbn("0471958697"));
        assertTrue(FormValidator.isValidIsbn("0-471-95869-7"));
        assertTrue(FormValidator.isValidIsbn("047195869X"));
    }

    @Test
    void testIsValidIsbn13() {
        assertTrue(FormValidator.isValidIsbn("9780471958697"));
        assertTrue(FormValidator.isValidIsbn("978-0-471-95869-7"));
    }

    @Test
    void testInvalidIsbn() {
        assertFalse(FormValidator.isValidIsbn("123"));
        assertFalse(FormValidator.isValidIsbn("invalid-isbn"));
    }

    @Test
    void testIsValidEmail() {
        assertTrue(FormValidator.isValidEmail("test@example.com"));
        assertTrue(FormValidator.isValidEmail("user.name@domain.co.uk"));
        assertFalse(FormValidator.isValidEmail("invalid-email"));
        assertFalse(FormValidator.isValidEmail(null));
    }

    @Test
    void testIsValidPhone() {
        assertTrue(FormValidator.isValidPhone("13800138000"));
        assertTrue(FormValidator.isValidPhone("15912345678"));
        assertFalse(FormValidator.isValidPhone("12345678901"));
        assertFalse(FormValidator.isValidPhone(null));
    }

    @Test
    void testIsValidShelfNo() {
        assertTrue(FormValidator.isValidShelfNo("A1-01"));
        assertTrue(FormValidator.isValidShelfNo("Z999-999"));
        assertFalse(FormValidator.isValidShelfNo("invalid"));
        assertFalse(FormValidator.isValidShelfNo(null));
    }
}
