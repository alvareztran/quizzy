package com.quizzy.view.component;

import com.quizzy.model.Topic;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TopicFormDialog {

    private TopicFormDialog() {
    }

    public static Optional<Topic> showTopicDialog(Topic existingTopic) {
        Dialog<Topic> dialog = new Dialog<>();
        boolean isEdit = (existingTopic != null);

        dialog.setTitle(isEdit ? "Edit Topic" : "Create Topic");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType(isEdit ? "Update Topic" : "Create Topic", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().getStyleClass().add("card");
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8;");

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-padding: 8 18; -fx-background-radius: 8;");

        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(400);

        Label headerTitle = new Label(isEdit ? "Edit Topic" : "Create Topic");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label nameLabel = new Label("Topic Name *");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Java Programming");
        nameField.setPrefHeight(38);

        Label descLabel = new Label("Description");
        descLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Brief description of this topic category...");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (isEdit) {
            nameField.setText(existingTopic.getTopicName());
            descArea.setText(existingTopic.getDescription());
        }

        contentBox.getChildren().addAll(headerTitle, nameLabel, nameField, descLabel, descArea, errorLabel);
        dialog.getDialogPane().setContent(contentBox);

        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String topicName = nameField.getText();
            if (topicName == null || topicName.isBlank()) {
                errorLabel.setText("Topic name is required.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String topicName = nameField.getText().trim();
                String description = descArea.getText() != null ? descArea.getText().trim() : null;
                if (description != null && description.isBlank()) {
                    description = null;
                }

                if (isEdit) {
                    return new Topic(existingTopic.getTopicId(), topicName, description);
                } else {
                    return new Topic(topicName, description);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
