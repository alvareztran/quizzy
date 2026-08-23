package com.quizzy.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    /**
     * Hashes password using BCrypt algorithm with a salt factor of 12.
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Checks if plainPassword matches storedPassword.
     * Supports BCrypt hashes ($2a$, $2b$, $2y$) as well as legacy plain text for backward compatibility.
     */
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        
        try {
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                return BCrypt.checkpw(plainPassword, storedPassword);
            }
        } catch (Exception ignored) {
        }
        
        // Backward compatibility for legacy unhashed plain passwords
        return storedPassword.equals(plainPassword);
    }
}
