package com.quizzy.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    private static final int LOG_ROUNDS = 12;

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        if (!isHashed(hashedPassword)) {
            return plainPassword.equals(hashedPassword);
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
