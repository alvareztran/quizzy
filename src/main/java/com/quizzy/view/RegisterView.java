package com.quizzy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class RegisterView {

    private final VBox root = new VBox(18);
    private final Button backHomeBtn = new Button("← Back to Home");
    private final ImageView logoImageView = new ImageView();
    private final Label titleLabel = new Label("Create your account");

    private final TextField fullNameField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();

    private final Button registerButton = new Button("Register");
    private final Button loginLinkButton = new Button("Login");

    public RegisterView() {
        createUI();
    }

    private void createUI() {
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(18, 44, 20, 44));
        root.getStyleClass().add("login-box");
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.06), 20, 0, 0, 6);");
        root.setMaxWidth(440);
        root.setPrefWidth(440);
        root.setMaxHeight(Region.USE_PREF_SIZE);

        // Top Back to Home Button Row
        backHomeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4px 6px;");
        HBox backHomeBox = new HBox(backHomeBtn);
        backHomeBox.setAlignment(Pos.CENTER_LEFT);
        backHomeBox.setMaxWidth(350);

        // Brand Logo Container
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-logo.png"));
            logoImageView.setImage(logoImg);
            logoImageView.setFitWidth(100);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
            logoImageView.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            // Fallback
        }

        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        VBox headerBox = new VBox(8, logoImageView, titleLabel);
        headerBox.setAlignment(Pos.CENTER);

        // Full Name Field Group
        VBox fullNameBox = new VBox(5);
        fullNameBox.setMaxWidth(350);
        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");
        fullNameField.setPromptText("Enter your full name");
        fullNameField.setPrefHeight(42);
        fullNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 14px;");
        fullNameBox.getChildren().addAll(nameLabel, fullNameField);

        // Username / Email Field Group
        VBox usernameBox = new VBox(5);
        usernameBox.setMaxWidth(350);
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");
        usernameField.setPromptText("name@example.com");
        usernameField.setPrefHeight(42);
        usernameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 14px;");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        // Password Field Group
        VBox passwordBox = new VBox(5);
        passwordBox.setMaxWidth(350);
        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");
        passwordField.setPromptText("Create a strong password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 14px;");
        passwordBox.getChildren().addAll(passLabel, passwordField);

        // Confirm Password Field Group
        VBox confirmPasswordBox = new VBox(5);
        confirmPasswordBox.setMaxWidth(350);
        Label confirmPassLabel = new Label("Confirm Password");
        confirmPassLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setPrefHeight(42);
        confirmPasswordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 14px;");
        confirmPasswordBox.getChildren().addAll(confirmPassLabel, confirmPasswordField);

        // Primary Register Button
        registerButton.setMaxWidth(350);
        registerButton.setPrefHeight(44);
        registerButton.getStyleClass().add("button-primary");
        registerButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-padding: 12 16; -fx-font-weight: bold; -fx-background-radius: 8px;");

        // Footer Login Link
        HBox footerBox = new HBox(5);
        footerBox.setAlignment(Pos.CENTER);
        Label accountPromptL = new Label("Already have an account?");
        accountPromptL.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        loginLinkButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 4px 6px; -fx-cursor: hand;");
        footerBox.getChildren().addAll(accountPromptL, loginLinkButton);

        root.getChildren().addAll(
                backHomeBox,
                headerBox,
                fullNameBox,
                usernameBox,
                passwordBox,
                confirmPasswordBox,
                registerButton,
                footerBox
        );
    }

    public VBox getRoot() {
        return root;
    }

    public Button getBackHomeBtn() {
        return backHomeBtn;
    }

    public ImageView getLogoImageView() {
        return logoImageView;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public TextField getFullNameField() {
        return fullNameField;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public PasswordField getConfirmPasswordField() {
        return confirmPasswordField;
    }

    public Button getRegisterButton() {
        return registerButton;
    }

    public Button getLoginLinkButton() {
        return loginLinkButton;
    }

}
