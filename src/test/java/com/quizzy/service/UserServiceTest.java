package com.quizzy.service;

import com.quizzy.dao.UserDAO;
import com.quizzy.model.User;
import com.quizzy.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserDAO mockUserDAO;
    private UserService userService;
    private List<User> savedUsers;

    @BeforeEach
    void setUp() {
        savedUsers = new ArrayList<>();
        // Set an admin user in session so SessionManager.requireAdmin() passes
        User admin = new User(1, "admin", "HashedPass123@", "Admin User", "Admin", null);
        SessionManager.setCurrentUser(admin);

        mockUserDAO = new UserDAO() {
            @Override
            public User findById(int userId) {
                return savedUsers.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
            }

            @Override
            public User findByUsername(String username) {
                return savedUsers.stream().filter(u -> u.getUserName().equalsIgnoreCase(username)).findFirst().orElse(null);
            }

            @Override
            public List<User> findAll() {
                return new ArrayList<>(savedUsers);
            }

            @Override
            public boolean insert(User user) {
                savedUsers.add(user);
                return true;
            }

            @Override
            public boolean update(User user) {
                return true;
            }

            @Override
            public boolean delete(int userId) {
                return true;
            }
        };

        userService = new UserService(mockUserDAO);
    }

    @Test
    @DisplayName("Admin creating user with valid password regex should succeed")
    void testCreateUserWithValidPassword() {
        User validUser = new User("newplayer", "Secret@123", "New Player", "Player");
        boolean created = userService.createUser(validUser);
        assertTrue(created);
    }

    @Test
    @DisplayName("Admin creating user with invalid password regex should fail")
    void testCreateUserWithInvalidPassword() {
        User invalidUser = new User("badplayer", "123456", "Bad Player", "Player");
        boolean created = userService.createUser(invalidUser);
        assertFalse(created);

        User emptyPassUser = new User("emptyplayer", "", "Empty Pass", "Player");
        assertFalse(userService.createUser(emptyPassUser));

        User nullPassUser = new User("nullplayer", null, "Null Pass", "Player");
        assertFalse(userService.createUser(nullPassUser));
    }
}
