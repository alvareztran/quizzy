package com.quizzy.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Secret123@",
            "MyP@ssw0rd",
            "Admin@2026",
            "Str0ng!Pass",
            "Valid$Password1"
    })
    @DisplayName("Valid passwords meeting all regex criteria should return true")
    void testValidPasswords(String password) {
        assertTrue(ValidationUtil.isValidPassword(password), "Expected password to be valid: " + password);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                    // Empty
            "Short1!",             // Less than 8 characters
            "alllowercase123@",    // Missing uppercase
            "ALLUPPERCASE123@",    // Missing lowercase
            "NoNumbersHere!@",     // Missing digit
            "NoSpecialChar123",    // Missing special character
            "   "                  // Whitespace
    })
    @DisplayName("Invalid passwords failing regex criteria should return false")
    void testInvalidPasswords(String password) {
        assertFalse(ValidationUtil.isValidPassword(password), "Expected password to be invalid: " + password);
    }

    @Test
    @DisplayName("Null password should return false")
    void testNullPassword() {
        assertFalse(ValidationUtil.isValidPassword(null));
    }
}
