package com.quizzy.view;

import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class QuizHistoryView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Top Navbar Controls
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navDashboardBtn = new Button("Dashboard");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    private final TextField searchField = new TextField();

    // Left Sidebar Statistics Card Controls
    private final Label totalQuizzesValLabel = new Label("12");
    private final Label avgScoreValLabel = new Label("85%");
    private final Label bestScoreValLabel = new Label("98%");

    // Right Main History List Container
    private final VBox historyListContainer = new VBox(14);

    public QuizHistoryView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 780);

        // ==========================================
        // 1. TOP NAVBAR (Matching media_1787421029588.png)
        // ==========================================
        HBox navbar = new HBox(24);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(14, 52, 14, 52));
        navbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 1px 0;");

        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            logoImageView.setImage(iconImg);
            logoImageView.setFitHeight(30);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
            logoImageView.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            // Fallback
        }

        brandNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #6366f1; -fx-letter-spacing: 1px; -fx-cursor: hand;");
        HBox logoBrandBox = new HBox(10, logoImageView, brandNameLabel);
        logoBrandBox.setAlignment(Pos.CENTER_LEFT);

        // Nav Tabs (History active)
        navDashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #464554; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #464554; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        navHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #4f46e5; -fx-border-width: 0 0 2px 0; -fx-padding: 8 12;");

        HBox navTabs = new HBox(16, navDashboardBtn, navTopicsBtn, navHistoryBtn);
        navTabs.setAlignment(Pos.CENTER_LEFT);

        HBox navSpacer = new HBox();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        // Search Input Field
        searchField.setPromptText("🔍  Search...");
        searchField.setPrefWidth(220);
        searchField.setPrefHeight(36);
        searchField.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: transparent; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-font-size: 13px;");

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-cursor: hand;");

        HBox userBox = new HBox(16, searchField, bellIcon, userProfileWidget.getRoot());
        userBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(logoBrandBox, navTabs, navSpacer, userBox);
        root.setTop(navbar);

        // ==========================================
        // 2. MAIN 2-COLUMN WORKSPACE CANVAS
        // ==========================================
        VBox contentContainer = new VBox(28);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setPadding(new Insets(36, 52, 40, 52));
        contentContainer.setStyle("-fx-background-color: #f8f9fb;");

        HBox workspaceRow = new HBox(32);
        workspaceRow.setAlignment(Pos.TOP_LEFT);
        workspaceRow.setMaxWidth(1140);
        VBox.setVgrow(workspaceRow, Priority.ALWAYS);

        // LEFT SIDEBAR: Statistics Card (280px Width)
        VBox leftSidebar = new VBox(16);
        leftSidebar.setPrefWidth(280);
        leftSidebar.setMinWidth(260);

        VBox statsCard = new VBox(14);
        statsCard.getStyleClass().add("card");
        statsCard.setPadding(new Insets(24));
        statsCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 10, 0, 0, 3);");

        Label statsTitle = new Label("Statistics");
        statsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        // Stat Row 1: Total Quizzes
        HBox r1 = createStatRow("Total Quizzes", totalQuizzesValLabel, "#191c1e");
        // Stat Row 2: Avg Score
        HBox r2 = createStatRow("Avg. Score", avgScoreValLabel, "#6366f1");
        // Stat Row 3: Best Score
        HBox r3 = createStatRow("Best Score", bestScoreValLabel, "#6366f1");

        statsCard.getChildren().addAll(statsTitle, r1, createDivider(), r2, createDivider(), r3);
        leftSidebar.getChildren().add(statsCard);

        // RIGHT MAIN CONTENT: Quiz History Cards List
        VBox rightMainCol = new VBox(20);
        HBox.setHgrow(rightMainCol, Priority.ALWAYS);

        VBox pageHeaderBox = new VBox(6);
        Label pageTitleL = new Label("Quiz History");
        pageTitleL.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label pageSubTitleL = new Label("Review your past performance and track your progress over time.");
        pageSubTitleL.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        pageHeaderBox.getChildren().addAll(pageTitleL, pageSubTitleL);

        historyListContainer.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(historyListContainer, Priority.ALWAYS);

        rightMainCol.getChildren().addAll(pageHeaderBox, historyListContainer);

        workspaceRow.getChildren().addAll(leftSidebar, rightMainCol);
        contentContainer.getChildren().add(workspaceRow);

        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fb;");
        root.setCenter(scrollPane);

        // ==========================================
        // 3. BOTTOM FOOTER BAR
        // ==========================================
        HBox footerBar = new HBox(20);
        footerBar.setAlignment(Pos.CENTER_LEFT);
        footerBar.setPadding(new Insets(18, 52, 18, 52));
        footerBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1px 0 0 0;");

        Label copyrightLabel = new Label("© 2024 QUIZZY Learning Platform");
        copyrightLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b;");

        HBox footerSpacer = new HBox();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Label privacyLink = new Label("Privacy Policy");
        Label termsLink = new Label("Terms of Service");
        Label helpLink = new Label("Help Center");
        Label contactLink = new Label("Contact Us");

        String linkStyle = "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-cursor: hand;";
        privacyLink.setStyle(linkStyle);
        termsLink.setStyle(linkStyle);
        helpLink.setStyle(linkStyle);
        contactLink.setStyle(linkStyle);

        HBox linksBox = new HBox(20, privacyLink, termsLink, helpLink, contactLink);
        linksBox.setAlignment(Pos.CENTER_RIGHT);

        footerBar.getChildren().addAll(copyrightLabel, footerSpacer, linksBox);
        root.setBottom(footerBar);
    }

    private HBox createStatRow(String labelStr, Label valLabel, String hexColor) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 0, 4, 0));

        Label nameL = new Label(labelStr);
        nameL.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        valLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + hexColor + ";");

        row.getChildren().addAll(nameL, spacer, valLabel);
        return row;
    }

    private HBox createDivider() {
        HBox div = new HBox();
        div.setPrefHeight(1);
        div.setStyle("-fx-background-color: #f1f5f9;");
        return div;
    }

    public BorderPane getRoot() {
        return root;
    }

    public UserProfileWidget getUserProfileWidget() {
        return userProfileWidget;
    }

    public ImageView getLogoImageView() {
        return logoImageView;
    }

    public Label getBrandNameLabel() {
        return brandNameLabel;
    }

    public Button getNavDashboardBtn() {
        return navDashboardBtn;
    }

    public Button getNavTopicsBtn() {
        return navTopicsBtn;
    }

    public Button getNavHistoryBtn() {
        return navHistoryBtn;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Label getTotalQuizzesValLabel() {
        return totalQuizzesValLabel;
    }

    public Label getAvgScoreValLabel() {
        return avgScoreValLabel;
    }

    public Label getBestScoreValLabel() {
        return bestScoreValLabel;
    }

    public VBox getHistoryListContainer() {
        return historyListContainer;
    }

}
