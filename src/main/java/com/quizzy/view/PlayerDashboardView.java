package com.quizzy.view;

import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlayerDashboardView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Top Navigation Items
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navDashboardBtn = new Button("Dashboard");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    // Welcome Greeting Labels
    private final Label welcomeTitleLabel = new Label("Welcome back, User!");
    private final Label welcomeSubtitleLabel = new Label("Ready to continue learning?");

    // 3 Stat Cards Labels
    private final Label totalQuizzesValLabel = new Label("0");
    private final Label avgScoreValLabel = new Label("0%");
    private final Label completedValLabel = new Label("0");

    // Continue Learning Card Elements
    private final Label continueQuizTitleLabel = new Label("Java Programming Basics");
    private final Label continueQuizSubtitleLabel = new Label("Chapter 4: Object-Oriented Principles");
    private final Label progressPercentLabel = new Label("65% Completed");
    private final Button resumeQuizBtn = new Button("Resume Quiz");

    // Right Column Topics Container
    private final VBox topicsListContainer = new VBox(12);
    private final Button viewAllTopicsBtn = new Button("View All");

    public PlayerDashboardView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 780);

        // ==========================================
        // 1. TOP NAVBAR
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

        // Nav Tabs
        navDashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #4f46e5; -fx-border-width: 0 0 2px 0; -fx-padding: 8 12;");
        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #464554; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        navHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #464554; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");

        HBox navTabs = new HBox(16, navDashboardBtn, navTopicsBtn, navHistoryBtn);
        navTabs.setAlignment(Pos.CENTER_LEFT);

        HBox navSpacer = new HBox();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-cursor: hand;");

        HBox userBox = new HBox(16, bellIcon, userProfileWidget.getRoot());
        userBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(logoBrandBox, navTabs, navSpacer, userBox);
        root.setTop(navbar);

        // ==========================================
        // 2. MAIN SCROLLABLE CONTENT CANVAS
        // ==========================================
        VBox contentContainer = new VBox(32);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setPadding(new Insets(36, 52, 40, 52));
        contentContainer.setStyle("-fx-background-color: #f8f9fb;");

        VBox mainBox = new VBox(32);
        mainBox.setMaxWidth(1140);

        // Welcome Header Section
        VBox welcomeHeader = new VBox(6);
        welcomeHeader.setAlignment(Pos.CENTER);

        welcomeTitleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");
        welcomeSubtitleLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748b;");
        welcomeHeader.getChildren().addAll(welcomeTitleLabel, welcomeSubtitleLabel);

        // 3 Stat Cards Row
        HBox statCardsRow = new HBox(20);
        statCardsRow.setAlignment(Pos.CENTER);

        VBox card1 = createStatCard("❓", totalQuizzesValLabel, "QUIZZES");
        VBox card2 = createStatCard("%", avgScoreValLabel, "AVG. SCORE");
        VBox card3 = createStatCard("✓", completedValLabel, "COMPLETED");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);

        statCardsRow.getChildren().addAll(card1, card2, card3);

        // 2-Column Workspace Section
        HBox workspaceRow = new HBox(28);
        workspaceRow.setAlignment(Pos.TOP_LEFT);

        // LEFT COLUMN: Continue Learning Card (60% Width)
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label continueHeading = new Label("Continue Learning");
        continueHeading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        VBox continueCard = new VBox(16);
        continueCard.getStyleClass().add("card");
        continueCard.setPadding(new Insets(28));
        continueCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-border-width: 1px 1px 1px 4px; -fx-border-color: #e5e7eb #e5e7eb #e5e7eb #6366f1; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 14, 0, 0, 3);");

        HBox cardTopRow = new HBox(12);
        cardTopRow.setAlignment(Pos.CENTER_LEFT);

        Label inProgressBadge = new Label("In Progress");
        inProgressBadge.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px;");

        HBox cardSpacer = new HBox();
        HBox.setHgrow(cardSpacer, Priority.ALWAYS);

        Label navArrows = new Label("⟨ ⟩");
        navArrows.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 20px;");

        cardTopRow.getChildren().addAll(inProgressBadge, cardSpacer, navArrows);

        VBox quizTitleBox = new VBox(4);
        continueQuizTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");
        continueQuizSubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        quizTitleBox.getChildren().addAll(continueQuizTitleLabel, continueQuizSubtitleLabel);

        // Progress Bar
        StackPane progressTrack = new StackPane();
        progressTrack.setPrefHeight(8);
        progressTrack.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 4px;");

        HBox fillBar = new HBox();
        fillBar.setPrefHeight(8);
        fillBar.setPrefWidth(280);
        fillBar.setStyle("-fx-background-color: #6366f1; -fx-background-radius: 4px;");
        fillBar.setMaxWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(fillBar, Pos.CENTER_LEFT);
        progressTrack.getChildren().add(fillBar);

        HBox progressBottomRow = new HBox(12);
        progressBottomRow.setAlignment(Pos.CENTER_LEFT);

        progressPercentLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b;");

        HBox bottomSpacer = new HBox();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        resumeQuizBtn.getStyleClass().add("button-primary");
        resumeQuizBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-padding: 10 22; -fx-font-weight: bold; -fx-background-radius: 8px;");

        progressBottomRow.getChildren().addAll(progressPercentLabel, bottomSpacer, resumeQuizBtn);

        continueCard.getChildren().addAll(cardTopRow, quizTitleBox, progressTrack, progressBottomRow);
        leftCol.getChildren().addAll(continueHeading, continueCard);

        // RIGHT COLUMN: Your Topics List (40% Width)
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(380);

        HBox topicsHeaderRow = new HBox(12);
        topicsHeaderRow.setAlignment(Pos.CENTER_LEFT);

        Label topicsHeading = new Label("Your Topics");
        topicsHeading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        HBox topicsSpacer = new HBox();
        HBox.setHgrow(topicsSpacer, Priority.ALWAYS);

        viewAllTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6366f1; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0;");

        topicsHeaderRow.getChildren().addAll(topicsHeading, topicsSpacer, viewAllTopicsBtn);

        rightCol.getChildren().addAll(topicsHeaderRow, topicsListContainer);

        workspaceRow.getChildren().addAll(leftCol, rightCol);
        mainBox.getChildren().addAll(welcomeHeader, statCardsRow, workspaceRow);
        contentContainer.getChildren().add(mainBox);

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

    private VBox createStatCard(String iconStr, Label valLabel, String titleStr) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 10, 0, 0, 3);");

        Label iconLabel = new Label(iconStr);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #6366f1; -fx-background-color: #e0e7ff; -fx-padding: 8 12; -fx-background-radius: 10px;");

        valLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label titleLabel = new Label(titleStr);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-letter-spacing: 1px;");

        card.getChildren().addAll(iconLabel, valLabel, titleLabel);
        return card;
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

    public Label getWelcomeTitleLabel() {
        return welcomeTitleLabel;
    }

    public Label getWelcomeSubtitleLabel() {
        return welcomeSubtitleLabel;
    }

    public Label getTotalQuizzesValLabel() {
        return totalQuizzesValLabel;
    }

    public Label getAvgScoreValLabel() {
        return avgScoreValLabel;
    }

    public Label getCompletedValLabel() {
        return completedValLabel;
    }

    public Label getContinueQuizTitleLabel() {
        return continueQuizTitleLabel;
    }

    public Label getContinueQuizSubtitleLabel() {
        return continueQuizSubtitleLabel;
    }

    public Label getProgressPercentLabel() {
        return progressPercentLabel;
    }

    public Button getResumeQuizBtn() {
        return resumeQuizBtn;
    }

    public VBox getTopicsListContainer() {
        return topicsListContainer;
    }

    public Button getViewAllTopicsBtn() {
        return viewAllTopicsBtn;
    }

}
