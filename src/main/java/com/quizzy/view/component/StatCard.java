package com.quizzy.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StatCard {

    private final HBox root = new HBox(14);
    private final Label valueLabel = new Label("0");
    private final Label titleLabel = new Label("Metric");
    private final Label subtextLabel = new Label("Description");

    public StatCard(String iconText, String title, String initialValue, String subtext, String iconBgHex, String iconTextHex) {
        createUI(iconText, title, initialValue, subtext, iconBgHex, iconTextHex);
    }

    private void createUI(String iconText, String title, String initialValue, String subtext, String iconBgHex, String iconTextHex) {
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("card");
        root.setPadding(new Insets(12, 16, 12, 16));
        root.setMinWidth(190);
        root.setPrefWidth(220);

        // Circle Icon Container
        StackPane iconPane = new StackPane();
        Circle circle = new Circle(21);
        circle.setFill(Color.web(iconBgHex));

        Label iconLbl = new Label(iconText);
        iconLbl.setStyle(String.format("-fx-font-size: 14px; -fx-text-fill: %s; -fx-font-weight: 800;", iconTextHex));

        iconPane.getChildren().addAll(circle, iconLbl);

        // Metric Text Column
        VBox textCol = new VBox(1);
        titleLabel.setText(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: #767586;");

        valueLabel.setText(initialValue);
        valueLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #191c1e;");

        subtextLabel.setText(subtext);
        subtextLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #464554;");

        textCol.getChildren().addAll(titleLabel, valueLabel, subtextLabel);

        root.getChildren().addAll(iconPane, textCol);
        HBox.setHgrow(root, Priority.ALWAYS);
    }

    public HBox getRoot() {
        return root;
    }

    public Label getValueLabel() {
        return valueLabel;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getSubtextLabel() {
        return subtextLabel;
    }

}
