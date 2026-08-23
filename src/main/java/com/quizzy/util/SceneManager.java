package com.quizzy.util;

import com.quizzy.controller.AnswerController;
import com.quizzy.controller.HomeController;
import com.quizzy.controller.LoginController;
import com.quizzy.controller.MainController;
import com.quizzy.controller.QuestionController;
import com.quizzy.controller.QuizController;
import com.quizzy.controller.RegisterController;
import com.quizzy.controller.ResultController;
import com.quizzy.controller.SelectQuizController;
import com.quizzy.controller.TakeQuizController;
import com.quizzy.controller.TopicController;
import com.quizzy.controller.UserController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;
    private static final double DEFAULT_WIDTH = 960;
    private static final double DEFAULT_HEIGHT = 600;
    private static final String CSS_PATH = "/com/quizzy/css/style.css";
    private static final String ICON_PATH = "/com/quizzy/images/quizzy-icon.png";

    private SceneManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        if (primaryStage != null) {
            primaryStage.setTitle("Quizzy");
            try {
                if (SceneManager.class.getResource(ICON_PATH) != null) {
                    Image icon = new Image(SceneManager.class.getResourceAsStream(ICON_PATH));
                    primaryStage.getIcons().setAll(icon);
                }
            } catch (Exception e) {
                // Graceful fallback
            }
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(Parent root, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set.");
        }

        Scene currentScene = primaryStage.getScene();
        double width = currentScene != null ? currentScene.getWidth() : DEFAULT_WIDTH;
        double height = currentScene != null ? currentScene.getHeight() : DEFAULT_HEIGHT;

        Scene newScene = new Scene(root, width, height);

        if (SceneManager.class.getResource(CSS_PATH) != null) {
            newScene.getStylesheets().add(SceneManager.class.getResource(CSS_PATH).toExternalForm());
        }

        primaryStage.setScene(newScene);

        if (title != null && !title.isBlank()) {
            primaryStage.setTitle(title);
        } else {
            primaryStage.setTitle("Quizzy");
        }

        primaryStage.show();
    }

    public static void showHome() {
        HomeController controller = new HomeController();
        switchScene(controller.getView(), "Quizzy - Home");
    }

    public static void showLogin() {
        LoginController controller = new LoginController();
        switchScene(controller.getView(), "Quizzy - Login");
    }

    public static void showRegister() {
        RegisterController controller = new RegisterController();
        switchScene(controller.getView(), "Quizzy - Register");
    }

    public static void showMain() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        MainController controller = new MainController();
        switchScene(controller.getView(), "Quizzy - Dashboard");
    }

    public static void showPlayerDashboard() {
        showSelectQuiz();
    }

    public static void showTopic() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        TopicController controller = new TopicController();
        switchScene(controller.getView(), "Quizzy - Topic Management");
    }

    public static void showQuiz() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        QuizController controller = new QuizController();
        switchScene(controller.getView(), "Quizzy - Quiz Management");
    }

    public static void showQuestion() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        QuestionController controller = new QuestionController();
        switchScene(controller.getView(), "Quizzy - Question Management");
    }

    public static void showAnswer() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        AnswerController controller = new AnswerController();
        switchScene(controller.getView(), "Quizzy - Answer Management");
    }

    public static void showUser() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showSelectQuiz(); return; }
        UserController controller = new UserController();
        switchScene(controller.getView(), "Quizzy - User Management");
    }

    public static void showSelectQuiz() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        SelectQuizController controller = new SelectQuizController();
        switchScene(controller.getView(), "Quizzy - Select Quiz");
    }

    public static void showTakeQuiz() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        TakeQuizController controller = new TakeQuizController();
        switchScene(controller.getView(), "Quizzy - Take Quiz");
    }

    public static void showResult() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        ResultController controller = new ResultController();
        switchScene(controller.getView(), "Quizzy - Quiz Result");
    }

    public static void showAdminResult() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        if (!SessionManager.isAdmin()) { showResult(); return; }
        com.quizzy.controller.AdminResultController controller = new com.quizzy.controller.AdminResultController();
        switchScene(controller.getView(), "Quizzy - Quiz History Management");
    }

    public static void showHistory() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        com.quizzy.controller.QuizHistoryController controller = new com.quizzy.controller.QuizHistoryController();
        switchScene(controller.getView(), "Quizzy - Quiz History");
    }

    public static void showHistoryDetail() {
        if (!SessionManager.isLoggedIn()) { showLogin(); return; }
        com.quizzy.controller.HistoryDetailController controller = new com.quizzy.controller.HistoryDetailController();
        switchScene(controller.getView(), "Quizzy - Quiz Attempt Review");
    }

}
