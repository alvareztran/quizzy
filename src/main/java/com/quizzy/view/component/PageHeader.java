package com.quizzy.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PageHeader {

    private final HBox root = new HBox(16);
    private final Label titleLabel = new Label();
    private final Label descriptionLabel = new Label();
    private final VBox textContainer = new VBox(4);
    private Button primaryActionButton;

    public PageHeader(String title, String description) {
        this(title, description, null);
    }

    public PageHeader(String title, String description, Button primaryActionButton) {
        this.primaryActionButton = primaryActionButton;
        createUI(title, description);
    }

    private void createUI(String title, String description) {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(0, 0, 16, 0));

        titleLabel.setText(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        descriptionLabel.setText(description);
        descriptionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        textContainer.getChildren().addAll(titleLabel, descriptionLabel);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(textContainer, spacer);

        if (primaryActionButton != null) {
            primaryActionButton.getStyleClass().add("button-primary");
            primaryActionButton.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");
            root.getChildren().add(primaryActionButton);
        }
    }

    public HBox getRoot() {
        return root;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }

    public Button getPrimaryActionButton() {
        return primaryActionButton;
    }

    public void setPrimaryActionButton(Button button) {
        if (this.primaryActionButton != null) {
            root.getChildren().remove(this.primaryActionButton);
        }
        this.primaryActionButton = button;
        if (button != null) {
            button.getStyleClass().add("button-primary");
            button.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");
            root.getChildren().add(button);
        }
    }

}
