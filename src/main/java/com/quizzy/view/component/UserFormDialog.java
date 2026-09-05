package com.quizzy.view.component;

import com.quizzy.model.User;
import com.quizzy.util.ValidationUtil;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UserFormDialog {

    private UserFormDialog() {
    }

    public static Optional<User> showUserDialog(User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        boolean isEdit = (existingUser != null);

        dialog.setTitle(isEdit ? "Edit User" : "Create User");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType(isEdit ? "Update User" : "Create User", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().getStyleClass().add("card");
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8;");

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-padding: 8 18; -fx-background-radius: 8;");

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(420);

        Label headerTitle = new Label(isEdit ? "Edit User Account" : "Create New User");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label usernameLabel = new Label("Username *");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("e.g. john_doe");
        usernameField.setPrefHeight(38);

        Label fullnameLabel = new Label("Full Name *");
        fullnameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextField fullnameField = new TextField();
        fullnameField.setPromptText("e.g. John Doe");
        fullnameField.setPrefHeight(38);

        Label passLabel = new Label(isEdit ? "Password (leave blank to keep current)" : "Password *");
        passLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        PasswordInputField passField = new PasswordInputField("Account password");
        passField.setPrefHeight(38);

        Label passHintLabel = new Label("Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char (@$!%*?&)");
        passHintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        passHintLabel.setWrapText(true);

        Label roleLabel = new Label("Role *");
        roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.getItems().addAll("Admin", "Player");
        roleComboBox.setValue("Player");
        roleComboBox.setPrefHeight(38);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(400);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (isEdit) {
            usernameField.setText(existingUser.getUserName());
            usernameField.setDisable(true);
            fullnameField.setText(existingUser.getFullName());
            roleComboBox.setValue(existingUser.getRole());
        }

        contentBox.getChildren().addAll(
                headerTitle,
                usernameLabel, usernameField,
                fullnameLabel, fullnameField,
                passLabel, passField, passHintLabel,
                roleLabel, roleComboBox,
                errorLabel
        );
        dialog.getDialogPane().setContent(contentBox);

        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String uName = usernameField.getText();
            String fName = fullnameField.getText();
            String pass = passField.getText();

            if (uName == null || uName.isBlank()) {
                showErr(errorLabel, "Username is required.");
                event.consume();
                return;
            }

            if (fName == null || fName.isBlank()) {
                showErr(errorLabel, "Full name is required.");
                event.consume();
                return;
            }

            if (!isEdit) {
                if (pass == null || pass.isBlank()) {
                    showErr(errorLabel, "Password is required.");
                    event.consume();
                    return;
                }
                if (!ValidationUtil.isValidPassword(pass)) {
                    showErr(errorLabel, ValidationUtil.PASSWORD_REQUIREMENT_MESSAGE);
                    event.consume();
                    return;
                }
            } else {
                if (pass != null && !pass.isBlank() && !ValidationUtil.isValidPassword(pass)) {
                    showErr(errorLabel, ValidationUtil.PASSWORD_REQUIREMENT_MESSAGE);
                    event.consume();
                    return;
                }
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String uName = usernameField.getText().trim();
                String fName = fullnameField.getText().trim();
                String pass = passField.getText().trim();
                String role = roleComboBox.getValue();

                if (isEdit) {
                    return new User(existingUser.getUserId(), uName, pass, fName, role, existingUser.getCreatedAt());
                } else {
                    return new User(uName, pass, fName, role);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static void showErr(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

}
