package com.quizzy.dao;

import com.quizzy.model.User;
import java.util.List;

public interface UserDAO {
    
    User findById(int userId);
    User findByUsername(String username);
    List<User> findAll();
    boolean insert(User user);
    boolean update(User user);
    boolean delete(int userId);
    
}
