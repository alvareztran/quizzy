package com.quizzy.view.component;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class PasswordInputField extends StackPane {

    private static final String EYE_SHOW = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
    private static final String EYE_HIDE = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.44-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";

    private static final String COLOR_DEFAULT = "#94a3b8";
    private static final String COLOR_HOVER_DEFAULT = "#475569";
    private static final String COLOR_ACTIVE = "#6366f1";
    private static final String COLOR_HOVER_ACTIVE = "#4338ca";

    private final PasswordField passwordField = new PasswordField();
    private final TextField plainTextField = new TextField();
    private final Button toggleButton = new Button();
    private final SVGPath iconPath = new SVGPath();
    private boolean isPasswordVisible = false;

    public PasswordInputField() {
        this("Enter your password");
    }

    public PasswordInputField(String promptText) {
        initUI(promptText);
    }

    private void initUI(String promptText) {
        String baseStyle = "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10 40 10 14; -fx-font-size: 14px;";

        setPrefHeight(42);
        passwordField.setPromptText(promptText);
        passwordField.setStyle(baseStyle);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.prefHeightProperty().bind(prefHeightProperty());

        plainTextField.setPromptText(promptText);
        plainTextField.setStyle(baseStyle);
        plainTextField.setMaxWidth(Double.MAX_VALUE);
        plainTextField.setVisible(false);
        plainTextField.setManaged(false);
        plainTextField.prefHeightProperty().bind(prefHeightProperty());

        plainTextField.textProperty().bindBidirectional(passwordField.textProperty());

        iconPath.setContent(EYE_SHOW);
        iconPath.setFill(Color.web(COLOR_DEFAULT));
        iconPath.setScaleX(0.85);
        iconPath.setScaleY(0.85);

        toggleButton.getStyleClass().clear();
        toggleButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-background-insets: 0; -fx-border-width: 0; -fx-padding: 0 14 0 0; -fx-cursor: hand;");
        toggleButton.setFocusTraversable(false);
        toggleButton.setGraphic(iconPath);
        toggleButton.setTooltip(new Tooltip("Show password"));

        toggleButton.setOnAction(e -> toggleVisibility());

        toggleButton.setOnMouseEntered(e -> {
            iconPath.setFill(Color.web(isPasswordVisible ? COLOR_HOVER_ACTIVE : COLOR_HOVER_DEFAULT));
        });

        toggleButton.setOnMouseExited(e -> {
            iconPath.setFill(Color.web(isPasswordVisible ? COLOR_ACTIVE : COLOR_DEFAULT));
        });

        setAlignment(Pos.CENTER_RIGHT);
        getChildren().addAll(passwordField, plainTextField, toggleButton);
    }

    public void toggleVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            plainTextField.setVisible(true);
            plainTextField.setManaged(true);
            plainTextField.requestFocus();
            plainTextField.positionCaret(plainTextField.getText().length());

            iconPath.setContent(EYE_HIDE);
            iconPath.setFill(Color.web(COLOR_ACTIVE));
            toggleButton.setTooltip(new Tooltip("Hide password"));
        } else {
            plainTextField.setVisible(false);
            plainTextField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());

            iconPath.setContent(EYE_SHOW);
            iconPath.setFill(Color.web(COLOR_DEFAULT));
            toggleButton.setTooltip(new Tooltip("Show password"));
        }
    }

    public String getText() {
        return passwordField.getText();
    }

    public void setText(String value) {
        passwordField.setText(value);
    }

    public StringProperty textProperty() {
        return passwordField.textProperty();
    }

    public void setPromptText(String prompt) {
        passwordField.setPromptText(prompt);
        plainTextField.setPromptText(prompt);
    }

    public void setFieldStyle(String style) {
        passwordField.setStyle(style);
        plainTextField.setStyle(style);
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        passwordField.setOnAction(value);
        plainTextField.setOnAction(value);
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public TextField getPlainTextField() {
        return plainTextField;
    }

    public Button getToggleButton() {
        return toggleButton;
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }
}
