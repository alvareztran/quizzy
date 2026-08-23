package com.quizzy.service;

import com.quizzy.dao.UserDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.User;
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
        
        if (user == null) {
            return null;
        }
        
        if (!user.getPassword().equals(password)) {
            return null;
        }
        
        return user;
    }
    
    public boolean register(String username, String password, String fullName) {
        String result = registerUser(fullName, username, password, password);
        return "SUCCESS".equals(result);
    }

    /**
     * Complete Register business logic with detailed validation messages.
     * Always hardcodes Role to "Player". Never accepts role from client.
     */
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
            
            // Hardcode Role to "Player" for security
            User user = new User(
                    trimmedUsername,
                    password,
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
