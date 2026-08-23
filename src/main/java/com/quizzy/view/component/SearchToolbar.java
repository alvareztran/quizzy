package com.quizzy.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SearchToolbar {

    private final HBox root = new HBox(12);
    private final TextField searchField = new TextField();
    private final Button refreshButton = new Button("Refresh");
    private final HBox filterContainer = new HBox(8);

    public SearchToolbar(String searchPlaceholder) {
        createUI(searchPlaceholder);
    }

    private void createUI(String searchPlaceholder) {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(12, 16, 12, 16));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8;");

        searchField.setPromptText(searchPlaceholder != null ? searchPlaceholder : "Search...");
        searchField.setPrefWidth(280);
        searchField.setPrefHeight(36);

        filterContainer.setAlignment(Pos.CENTER_LEFT);

        refreshButton.getStyleClass().add("button");
        refreshButton.setStyle("-fx-font-size: 13px; -fx-padding: 6 14;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(searchField, filterContainer, refreshButton, spacer);
    }

    public void addFilterNode(Node node) {
        filterContainer.getChildren().add(node);
    }

    public HBox getRoot() {
        return root;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getRefreshButton() {
        return refreshButton;
    }

    public HBox getFilterContainer() {
        return filterContainer;
    }

}
