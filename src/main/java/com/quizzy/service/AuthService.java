package com.quizzy.service;

import com.quizzy.dao.UserDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.User;
import com.quizzy.util.PasswordHasher;
import java.util.Objects;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this(DAOFactory.getUserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO);
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank()) {
            return null;
        }

        if (password == null || password.isBlank()) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null || user.getPassword() == null) {
            return null;
        }

        if (PasswordHasher.checkPassword(password, user.getPassword())) {
            if (!PasswordHasher.isHashed(user.getPassword())) {
                String upgradedHash = PasswordHasher.hash(password);
                user.setPassword(upgradedHash);
                userDAO.update(user);
            }
            return user;
        }

        return null;
    }

    public boolean register(String username, String password, String fullName) {
        String result = registerUser(fullName, username, password, password);
        return "SUCCESS".equals(result);
    }

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

    public String registerUser(String fullName, String username, String password, String confirmPassword) {
        if (fullName == null || fullName.isBlank()) {
            return "Full Name is required.";
        }

        if (username == null || username.isBlank()) {
            return "Username is required.";
        }

        if (password == null || password.isBlank()) {
            return "Password is required.";
        }

        if (!password.matches(PASSWORD_REGEX)) {
            return "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character (@$!%*?&).";
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            return "Confirm Password is required.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        String trimmedUsername = username.trim();

        try {
            User existingUser = userDAO.findByUsername(trimmedUsername);
            if (existingUser != null) {
                return "Username already exists.";
            }

            String hashedPassword = PasswordHasher.hash(password);
            User user = new User(
                    trimmedUsername,
                    hashedPassword,
                    fullName.trim(),
                    "Player"
            );

            boolean inserted = userDAO.insert(user);
            if (inserted) {
                return "SUCCESS";
            } else {
                return "Unable to create account. Please try again.";
            }
        } catch (Exception e) {
            System.out.println("Registration service error: " + e.getMessage());
            return "Unable to create account. Please try again.";
        }
    }

}
