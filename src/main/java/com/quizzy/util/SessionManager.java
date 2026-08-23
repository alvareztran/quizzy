package com.quizzy.util;

import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.User;
import java.time.LocalDateTime;

public class SessionManager {

    private static User currentUser;
    private static Quiz selectedQuiz;
    private static Result lastResult;
    private static LocalDateTime quizStartTime;

    private SessionManager() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isPlayer() {
        return currentUser != null && "Player".equalsIgnoreCase(currentUser.getRole());
    }

    public static void requireLoggedIn() {
        if (!isLoggedIn()) {
            throw new SecurityException("Authentication required. Please log in first.");
        }
    }

    public static void requireAdmin() {
        requireLoggedIn();
        if (!isAdmin()) {
            throw new SecurityException("Access denied: Admin role required.");
        }
    }

    public static void setSelectedQuiz(Quiz quiz) {
        selectedQuiz = quiz;
    }

    public static Quiz getSelectedQuiz() {
        return selectedQuiz;
    }

    public static void setLastResult(Result result) {
        lastResult = result;
    }

    public static Result getLastResult() {
        return lastResult;
    }

    public static void setQuizStartTime(LocalDateTime startTime) {
        quizStartTime = startTime;
    }

    public static LocalDateTime getQuizStartTime() {
        return quizStartTime;
    }

    public static void clear() {
        currentUser = null;
        selectedQuiz = null;
        lastResult = null;
        quizStartTime = null;
    }

}
