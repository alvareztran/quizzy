package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.User;
import com.quizzy.service.AuthService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.LoginView;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public class LoginController {

    private final LoginView view;
    private final StackPane container = new StackPane();
    private final AuthService authService = ServiceFactory.getAuthService();

    public LoginController() {
        this.view = new LoginView();
        initUI();
        initEventHandlers();
    }

    private void initUI() {
        container.setStyle("-fx-background-color: #f8f9fb;");
        container.getChildren().add(view.getRoot());
        StackPane.setAlignment(view.getRoot(), Pos.CENTER);
    }

    public Parent getView() {
        return container;
    }

    public LoginView getLoginView() {
        return view;
    }

    private void initEventHandlers() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getPasswordField().setOnAction(e -> handleLogin());
        view.getForgotPasswordBtn().setOnAction(e -> handleForgotPassword());
        view.getRegisterLinkButton().setOnAction(e -> SceneManager.showRegister());
        view.getBackHomeBtn().setOnAction(e -> SceneManager.showHome());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();

        if (username == null || username.isBlank()) {
            showError("Username or email is required.");
            return;
        }

        if (password == null || password.isBlank()) {
            showError("Password is required.");
            return;
        }

        try {
            User user = authService.login(username.trim(), password);

            if (user == null) {
                showError("Username or password is incorrect.");
                return;
            }

            SessionManager.setCurrentUser(user);

            if (SessionManager.isAdmin()) {
                SceneManager.showMain();
            } else {
                SceneManager.showSelectQuiz();
            }
        } catch (Exception e) {
            showError("System error during login. Please check database connection.");
        }
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Recovery");
        alert.setHeaderText("Forgot your password?");
        alert.setContentText("Please contact your Quizzy system administrator to reset your account password.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
