package com.quizzy.view.component;

import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class QuizFormDialog {

    private QuizFormDialog() {
    }

    public static Optional<Quiz> showQuizDialog(Quiz existingQuiz, List<Topic> availableTopics) {
        Dialog<Quiz> dialog = new Dialog<>();
        boolean isEdit = (existingQuiz != null);

        dialog.setTitle(isEdit ? "Edit Quiz" : "Create Quiz");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType(isEdit ? "Update Quiz" : "Create Quiz", ButtonBar.ButtonData.OK_DONE);
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

        Label headerTitle = new Label(isEdit ? "Edit Quiz Assessment" : "Create New Quiz");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label nameLabel = new Label("Quiz Title *");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Java Fundamentals Quiz");
        nameField.setPrefHeight(38);

        Label topicLabel = new Label("Target Topic *");
        topicLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        ComboBox<Topic> topicComboBox = new ComboBox<>();
        topicComboBox.setMaxWidth(Double.MAX_VALUE);
        topicComboBox.setPromptText("Select topic category");
        topicComboBox.setPrefHeight(38);
        javafx.util.StringConverter<Topic> topicConverter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(Topic topic) {
                return topic != null ? topic.getTopicName() : "";
            }

            @Override
            public Topic fromString(String string) {
                return null;
            }
        };
        topicComboBox.setConverter(topicConverter);
        topicComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTopicName());
            }
        });
        topicComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTopicName());
            }
        });
        if (availableTopics != null) {
            topicComboBox.getItems().addAll(availableTopics);
        }

        Label qCountLabel = new Label("Number of Questions *");
        qCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextField qCountField = new TextField();
        qCountField.setPromptText("e.g. 10");
        qCountField.setPrefHeight(38);

        Label timeLabel = new Label("Time Limit (minutes) *");
        timeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g. 15");
        timeField.setPrefHeight(38);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (isEdit) {
            nameField.setText(existingQuiz.getQuizName());
            qCountField.setText(String.valueOf(existingQuiz.getNumberOfQuestions()));
            timeField.setText(String.valueOf(existingQuiz.getTimeLimit()));

            if (availableTopics != null) {
                for (Topic t : availableTopics) {
                    if (t.getTopicId() == existingQuiz.getTopicId()) {
                        topicComboBox.setValue(t);
                        break;
                    }
                }
            }
        }

        contentBox.getChildren().addAll(
                headerTitle,
                nameLabel, nameField,
                topicLabel, topicComboBox,
                qCountLabel, qCountField,
                timeLabel, timeField,
                errorLabel
        );
        dialog.getDialogPane().setContent(contentBox);

        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String title = nameField.getText();
            Topic selectedTopic = topicComboBox.getValue();
            String qStr = qCountField.getText();
            String timeStr = timeField.getText();

            if (title == null || title.isBlank()) {
                showErr(errorLabel, "Quiz title is required.");
                event.consume();
                return;
            }

            if (selectedTopic == null) {
                showErr(errorLabel, "Please select a target topic.");
                event.consume();
                return;
            }

            try {
                int qCount = Integer.parseInt(qStr.trim());
                int time = Integer.parseInt(timeStr.trim());
                if (qCount <= 0 || time <= 0) {
                    showErr(errorLabel, "Questions and time limit must be positive numbers.");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                showErr(errorLabel, "Please enter valid numeric values for questions and time limit.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String title = nameField.getText().trim();
                Topic selectedTopic = topicComboBox.getValue();
                int qCount = Integer.parseInt(qCountField.getText().trim());
                int time = Integer.parseInt(timeField.getText().trim());

                if (isEdit) {
                    return new Quiz(existingQuiz.getQuizId(), selectedTopic.getTopicId(), title, qCount, time, existingQuiz.getCreatedAt());
                } else {
                    return new Quiz(selectedTopic.getTopicId(), title, qCount, time);
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
