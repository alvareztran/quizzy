package com.quizzy.view.component;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class ConfirmDialog {

    private ConfirmDialog() {
    }

    public static boolean showDeleteConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title != null ? title : "Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "Are you sure you want to delete this item? This action cannot be undone.");

        ButtonType deleteButtonType = new ButtonType("Delete", ButtonType.OK.getButtonData());
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

        Button deleteBtn = (Button) alert.getDialogPane().lookupButton(deleteButtonType);
        if (deleteBtn != null) {
            deleteBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 6;");
        }

        Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelButtonType);
        if (cancelBtn != null) {
            cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-padding: 6 16; -fx-background-radius: 6;");
        }

        return alert.showAndWait().orElse(cancelButtonType) == deleteButtonType;
    }

}
