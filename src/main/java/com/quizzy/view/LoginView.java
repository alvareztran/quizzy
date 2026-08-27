package com.quizzy.view;

import com.quizzy.view.component.PasswordInputField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class LoginView {

    private final VBox root = new VBox(22);
    private final Button backHomeBtn = new Button("← Back to Home");
    private final ImageView logoImageView = new ImageView();
    private final Label titleLabel = new Label("Welcome back !");
    private final Label subtitleLabel = new Label("Sign in to continue learning");
    private final TextField usernameField = new TextField();
    private final PasswordInputField passwordField = new PasswordInputField("Enter your password");
    private final Button forgotPasswordBtn = new Button("Forgot password?");
    private final Button loginButton = new Button("Login →");
    private final Button registerLinkButton = new Button("Register");

    public LoginView() {
        createUI();
    }

    private void createUI() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 44, 40, 44));
        root.getStyleClass().add("login-box");
        root.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.06), 20, 0, 0, 6);");
        root.setMaxWidth(440);
        root.setPrefWidth(440);
        root.setMaxHeight(Region.USE_PREF_SIZE);

        backHomeBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4px 6px;");
        HBox backHomeBox = new HBox(backHomeBtn);
        backHomeBox.setAlignment(Pos.CENTER_LEFT);
        backHomeBox.setMaxWidth(350);

        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-logo.png"));
            logoImageView.setImage(logoImg);
            logoImageView.setFitWidth(140);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
            logoImageView.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {

        }

        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        VBox headerBox = new VBox(8, logoImageView, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        VBox usernameBox = new VBox(6);
        usernameBox.setMaxWidth(350);
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(42);
        usernameField.setStyle(
                "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 14; -fx-font-size: 14px;");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        VBox passwordBox = new VBox(6);
        passwordBox.setMaxWidth(350);

        HBox passHeader = new HBox();
        passHeader.setAlignment(Pos.CENTER_LEFT);

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #334155;");

        HBox passSpacer = new HBox();
        HBox.setHgrow(passSpacer, Priority.ALWAYS);

        forgotPasswordBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 4px 6px; -fx-cursor: hand;");

        passHeader.getChildren().addAll(passLabel, passSpacer, forgotPasswordBtn);

        passwordBox.getChildren().addAll(passHeader, passwordField);

        loginButton.setMaxWidth(350);
        loginButton.setPrefHeight(44);
        loginButton.getStyleClass().add("button-primary");
        loginButton.setStyle(
                "-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-padding: 12 16; -fx-font-weight: bold; -fx-background-radius: 8px;");

        HBox footerBox = new HBox(5);
        footerBox.setAlignment(Pos.CENTER);
        Label accountPromptL = new Label("Don't have an account?");
        accountPromptL.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        registerLinkButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 4px 6px; -fx-cursor: hand;");
        footerBox.getChildren().addAll(accountPromptL, registerLinkButton);

        root.getChildren().addAll(backHomeBox, headerBox, usernameBox, passwordBox, loginButton, footerBox);
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

    public Label getSubtitleLabel() {
        return subtitleLabel;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordInputField getPasswordField() {
        return passwordField;
    }

    public Button getForgotPasswordBtn() {
        return forgotPasswordBtn;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getRegisterLinkButton() {
        return registerLinkButton;
    }

}
