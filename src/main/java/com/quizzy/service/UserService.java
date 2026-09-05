package com.quizzy.service;

import com.quizzy.dao.UserDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.User;
import com.quizzy.util.PasswordHasher;
import com.quizzy.util.SessionManager;
import com.quizzy.util.ValidationUtil;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this(DAOFactory.getUserDAO());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getUserById(int userId) {
        SessionManager.requireAdmin();
        return userDAO.findById(userId);
    }

    public User getUserByUsername(String username) {
        SessionManager.requireAdmin();
        return userDAO.findByUsername(username);
    }

    public List<User> getAllUsers() {
        SessionManager.requireAdmin();
        return userDAO.findAll();
    }

    public boolean createUser(User user) {
        SessionManager.requireAdmin();

        if (user == null || user.getUserName() == null || user.getUserName().isBlank()) {
            return false;
        }
        User existing = userDAO.findByUsername(user.getUserName());
        if (existing != null) {
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return false;
        }
        if (!PasswordHasher.isHashed(user.getPassword())) {
            if (!ValidationUtil.isValidPassword(user.getPassword())) {
                return false;
            }
            user.setPassword(PasswordHasher.hash(user.getPassword()));
        }
        return userDAO.insert(user);
    }

    public boolean updateUser(User user) {
        SessionManager.requireAdmin();

        if (user == null || user.getUserId() <= 0) {
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            User existing = userDAO.findById(user.getUserId());
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        } else if (!PasswordHasher.isHashed(user.getPassword())) {
            if (!ValidationUtil.isValidPassword(user.getPassword())) {
                return false;
            }
            user.setPassword(PasswordHasher.hash(user.getPassword()));
        }
        return userDAO.update(user);
    }

    public boolean deleteUser(int userId) {
        SessionManager.requireAdmin();

        if (userId <= 0) {
            return false;
        }
        return userDAO.delete(userId);
    }

    public int getTotalUsersCount() {
        List<User> users = userDAO.findAll();
        return users != null ? users.size() : 0;
    }

}
