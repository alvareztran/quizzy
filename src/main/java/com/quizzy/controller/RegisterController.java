package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.service.AuthService;
import com.quizzy.util.SceneManager;
import com.quizzy.view.RegisterView;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public class RegisterController {

    private final RegisterView view;
    private final StackPane container = new StackPane();
    private final AuthService authService = ServiceFactory.getAuthService();

    public RegisterController() {
        this.view = new RegisterView();
        initUI();
        initEventHandlers();
    }

    private void initUI() {
        container.setStyle("-fx-background-color: #f8fafc;");
        container.getChildren().add(view.getRoot());
        StackPane.setAlignment(view.getRoot(), Pos.CENTER);
    }

    public Parent getView() {
        return container;
    }

    public RegisterView getRegisterView() {
        return view;
    }

    private void initEventHandlers() {
        view.getRegisterButton().setOnAction(e -> handleRegister());
        view.getConfirmPasswordField().setOnAction(e -> handleRegister());
        view.getLoginLinkButton().setOnAction(e -> SceneManager.showLogin());
        view.getBackHomeBtn().setOnAction(e -> SceneManager.showHome());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
    }

    private void handleRegister() {
        String fullName = view.getFullNameField().getText();
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();
        String confirmPassword = view.getConfirmPasswordField().getText();

        if (fullName == null || fullName.isBlank()) {
            showError("Full Name is required.");
            return;
        }

        if (username == null || username.isBlank()) {
            showError("Username is required.");
            return;
        }

        if (password == null || password.isBlank()) {
            showError("Password is required.");
            return;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            showError("Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character (@$!%*?&).");
            return;
        }
        
        if (confirmPassword == null || confirmPassword.isBlank()) {
            showError("Confirm Password is required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            String result = authService.registerUser(fullName.trim(), username.trim(), password, confirmPassword);

            if ("SUCCESS".equals(result)) {
                showSuccess("Account created successfully.");
                
                // Show Login screen with prefilled username
                LoginController loginController = new LoginController();
                loginController.getLoginView().getUsernameField().setText(username.trim());
                SceneManager.switchScene(loginController.getView(), "Quizzy - Login");
            } else {
                showError(result);
            }
        } catch (Exception e) {
            showError("Unable to create account. Please try again.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
