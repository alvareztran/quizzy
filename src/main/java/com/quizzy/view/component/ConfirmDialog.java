package com.quizzy.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfirmDialog {

    private ConfirmDialog() {
    }

    public static boolean showDeleteConfirmation(String title, String message) {
        return showDeleteConfirmation(title, message, null);
    }

    public static boolean showDeleteConfirmation(String title, String message, String warningDetail) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title != null ? title : "Confirm Deletion");
        alert.setHeaderText(null);

        ButtonType deleteButtonType = new ButtonType("Delete Permanently", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(16, 20, 16, 20));
        contentBox.setPrefWidth(440);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 22px; -fx-background-color: #FEE2E2; -fx-padding: 8 12; -fx-background-radius: 50%; -fx-text-fill: #DC2626;");

        VBox titleBox = new VBox(3);
        Label titleLabel = new Label(title != null ? title : "Confirm Deletion");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitleLabel = new Label("Please review the impact before proceeding.");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        headerRow.getChildren().addAll(iconLabel, titleBox);

        Label messageLabel = new Label(message != null ? message : "Are you sure you want to delete this item?");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155; -fx-font-weight: 500; -fx-line-spacing: 2px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);

        contentBox.getChildren().addAll(headerRow, messageLabel);

        if (warningDetail != null && !warningDetail.isBlank()) {
            VBox warningBox = new VBox(4);
            warningBox.setPadding(new Insets(10, 12, 10, 12));
            warningBox.setStyle("-fx-background-color: #FFF1F2; -fx-border-color: #FECDD3; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            Label warningHeader = new Label("⚠️ Data Loss Warning:");
            warningHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #9F1239;");

            Label warningText = new Label(warningDetail);
            warningText.setStyle("-fx-font-size: 12px; -fx-text-fill: #BE123C; -fx-line-spacing: 2px;");
            warningText.setWrapText(true);
            warningText.setMaxWidth(380);

            warningBox.getChildren().addAll(warningHeader, warningText);
            contentBox.getChildren().add(warningBox);
        }

        alert.getDialogPane().setContent(contentBox);

        Button deleteBtn = (Button) alert.getDialogPane().lookupButton(deleteButtonType);
        if (deleteBtn != null) {
            deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        }

        Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelButtonType);
        if (cancelBtn != null) {
            cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #E2E8F0; -fx-text-fill: #334155; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        }

        return alert.showAndWait().orElse(cancelButtonType) == deleteButtonType;
    }

    public static void showCannotDeleteAlert(String title, String message, String reasonDetail) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title != null ? title : "Cannot Delete Item");
        alert.setHeaderText(null);

        ButtonType okButtonType = new ButtonType("I Understand", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButtonType);
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(16, 20, 16, 20));
        contentBox.setPrefWidth(440);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("🚫");
        iconLabel.setStyle("-fx-font-size: 22px; -fx-background-color: #FEE2E2; -fx-padding: 8 12; -fx-background-radius: 50%; -fx-text-fill: #DC2626;");

        VBox titleBox = new VBox(3);
        Label titleLabel = new Label(title != null ? title : "Action Prohibited");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitleLabel = new Label("This item is currently referenced by other system records.");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        headerRow.getChildren().addAll(iconLabel, titleBox);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155; -fx-font-weight: 500; -fx-line-spacing: 2px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);

        contentBox.getChildren().addAll(headerRow, messageLabel);

        if (reasonDetail != null && !reasonDetail.isBlank()) {
            VBox reasonBox = new VBox(4);
            reasonBox.setPadding(new Insets(10, 12, 10, 12));
            reasonBox.setStyle("-fx-background-color: #FEF3C7; -fx-border-color: #FDE68A; -fx-border-radius: 8px; -fx-background-radius: 8px;");

            Label reasonHeader = new Label("⚠️ Dependency Constraint:");
            reasonHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #92400E;");

            Label reasonText = new Label(reasonDetail);
            reasonText.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350F; -fx-line-spacing: 2px;");
            reasonText.setWrapText(true);
            reasonText.setMaxWidth(380);

            reasonBox.getChildren().addAll(reasonHeader, reasonText);
            contentBox.getChildren().add(reasonBox);
        }

        alert.getDialogPane().setContent(contentBox);

        Button okBtn = (Button) alert.getDialogPane().lookupButton(okButtonType);
        if (okBtn != null) {
            okBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        }

        alert.showAndWait();
    }

}
