package com.quizzy.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EmptyStateView {

    private final VBox root = new VBox(12);
    private final Label titleLabel = new Label();
    private final Label messageLabel = new Label();
    private Button actionButton;

    public EmptyStateView(String title, String message) {
        this(title, message, null);
    }

    public EmptyStateView(String title, String message, Button actionButton) {
        this.actionButton = actionButton;
        createUI(title, message);
    }

    private void createUI(String title, String message) {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40, 24, 40, 24));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8;");

        titleLabel.setText(title != null ? title : "No Data Found");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        messageLabel.setText(message != null ? message : "There are no records to display at this time.");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        root.getChildren().addAll(titleLabel, messageLabel);

        if (actionButton != null) {
            actionButton.getStyleClass().add("button-primary");
            actionButton.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");
            root.getChildren().add(actionButton);
        }
    }

    public VBox getRoot() {
        return root;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public Button getActionButton() {
        return actionButton;
    }

}
