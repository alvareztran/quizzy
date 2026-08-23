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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ResultView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Top Navbar Controls
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navDashboardBtn = new Button("Dashboard");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    // Header Hero Controls
    private final Label quizTitleLabel = new Label("Computer Science Basic");

    // Left Large Score Card Controls
    private final Label percentDisplayLabel = new Label("82%");
    private final Label correctRatioBadgeLabel = new Label("8 / 10 Correct");

    // Right 2x2 Grid Stat Cards Controls
    private final Label correctValLabel = new Label("8");
    private final Label incorrectValLabel = new Label("2");
    private final Label accuracyValLabel = new Label("80%");
    private final Label durationValLabel = new Label("12:34");

    // Question Review Container
    private final VBox questionReviewBox = new VBox(0);

    // Bottom Action Buttons Controls
    private final Button reviewAnswersBtn = new Button("👁  Review Answers");
    private final Button tryAgainBtn = new Button("🔄  Try Again");
    private final Button backDashboardBtn = new Button("⊞  Back to Dashboard");

    public ResultView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 780);

        // ==========================================
        // 1. TOP NAVBAR (Matching media_1787421007569.png)
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

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-cursor: hand;");

        HBox userBox = new HBox(16, bellIcon, userProfileWidget.getRoot());
        userBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(logoBrandBox, navTabs, navSpacer, userBox);
        root.setTop(navbar);

        // ==========================================
        // 2. MAIN RESULT WORKSPACE CANVAS
        // ==========================================
        VBox contentContainer = new VBox(28);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setPadding(new Insets(32, 52, 40, 52));
        contentContainer.setStyle("-fx-background-color: #f8f9fb;");

        VBox mainBox = new VBox(28);
        mainBox.setMaxWidth(780);

        // Header Hero Banner
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label trophyBadge = new Label("🏆");
        trophyBadge.setAlignment(Pos.CENTER);
        trophyBadge.setPrefSize(48, 48);
        trophyBadge.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 22px; -fx-background-radius: 24px;");

        Label headlineLabel = new Label("Quiz Completed!");
        headlineLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        quizTitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b;");

        headerBox.getChildren().addAll(trophyBadge, headlineLabel, quizTitleLabel);

        // Performance Metrics Cards Container (Score Card + 2x2 Grid)
        HBox metricsRow = new HBox(20);
        metricsRow.setAlignment(Pos.CENTER);

        // Left Large Score Card
        VBox leftScoreCard = new VBox(12);
        leftScoreCard.setPrefWidth(340);
        leftScoreCard.setAlignment(Pos.CENTER);
        leftScoreCard.getStyleClass().add("card");
        leftScoreCard.setPadding(new Insets(28, 24, 28, 24));
        leftScoreCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 10, 0, 0, 3);");

        Label yourScoreLabel = new Label("YOUR SCORE");
        yourScoreLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-letter-spacing: 1px;");

        percentDisplayLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");

        correctRatioBadgeLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 14; -fx-background-radius: 12px;");

        leftScoreCard.getChildren().addAll(yourScoreLabel, percentDisplayLabel, correctRatioBadgeLabel);

        // Right 2x2 Grid Stat Cards
        GridPane gridPane = new GridPane();
        gridPane.setHgap(16);
        gridPane.setVgap(16);
        HBox.setHgrow(gridPane, Priority.ALWAYS);

        VBox c1 = createGridStatCard("✓  Correct", correctValLabel, "#10b981");
        VBox c2 = createGridStatCard("✕  Incorrect", incorrectValLabel, "#ef4444");
        VBox c3 = createGridStatCard("🎯  Accuracy", accuracyValLabel, "#6366f1");
        VBox c4 = createGridStatCard("⏱  Time", durationValLabel, "#64748b");

        gridPane.add(c1, 0, 0);
        gridPane.add(c2, 1, 0);
        gridPane.add(c3, 0, 1);
        gridPane.add(c4, 1, 1);

        metricsRow.getChildren().addAll(leftScoreCard, gridPane);

        // Question Review Section Container
        VBox reviewSectionCard = new VBox(0);
        reviewSectionCard.getStyleClass().add("card");
        reviewSectionCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 10, 0, 0, 3);");

        HBox reviewHeader = new HBox();
        reviewHeader.setPadding(new Insets(16, 20, 16, 20));
        reviewHeader.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 1px 0; -fx-background-color: #f8f9fb; -fx-background-radius: 16px 16px 0 0;");
        Label reviewHeaderTitle = new Label("Question Review");
        reviewHeaderTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        reviewHeader.getChildren().add(reviewHeaderTitle);

        reviewSectionCard.getChildren().addAll(reviewHeader, questionReviewBox);

        // Bottom Action Buttons Row (3 Buttons)
        HBox actionBtnRow = new HBox(14);
        actionBtnRow.setAlignment(Pos.CENTER);

        reviewAnswersBtn.getStyleClass().add("button-primary");
        reviewAnswersBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 11 22; -fx-font-weight: bold; -fx-background-radius: 8px;");

        tryAgainBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #191c1e; -fx-font-size: 14px; -fx-padding: 11 22; -fx-font-weight: bold; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        backDashboardBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #191c1e; -fx-font-size: 14px; -fx-padding: 11 22; -fx-font-weight: bold; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        actionBtnRow.getChildren().addAll(reviewAnswersBtn, tryAgainBtn, backDashboardBtn);

        mainBox.getChildren().addAll(headerBox, metricsRow, reviewSectionCard, actionBtnRow);
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

    private VBox createGridStatCard(String headerStr, Label valLabel, String textHex) {
        VBox card = new VBox(6);
        card.setPrefWidth(190);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label headerL = new Label(headerStr);
        headerL.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + textHex + ";");

        valLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        card.getChildren().addAll(headerL, valLabel);
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

    public Label getQuizTitleLabel() {
        return quizTitleLabel;
    }

    public Label getPercentDisplayLabel() {
        return percentDisplayLabel;
    }

    public Label getCorrectRatioBadgeLabel() {
        return correctRatioBadgeLabel;
    }

    public Label getCorrectValLabel() {
        return correctValLabel;
    }

    public Label getIncorrectValLabel() {
        return incorrectValLabel;
    }

    public Label getAccuracyValLabel() {
        return accuracyValLabel;
    }

    public Label getDurationValLabel() {
        return durationValLabel;
    }

    public VBox getQuestionReviewBox() {
        return questionReviewBox;
    }

    public Button getReviewAnswersBtn() {
        return reviewAnswersBtn;
    }

    public Button getTryAgainBtn() {
        return tryAgainBtn;
    }

    public Button getBackDashboardBtn() {
        return backDashboardBtn;
    }

}
