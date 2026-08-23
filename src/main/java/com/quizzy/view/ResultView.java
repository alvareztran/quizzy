package com.quizzy.view;

import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ResultView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Top Navbar Controls
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
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
    private final Button backTopicsBtn = new Button("⊞  Back to Topics");
    private final Button backAdminBtn = new Button("←  Back to Quiz History");

    private final HBox navTabs = new HBox(12);
    private final HBox actionBtnRow = new HBox(16);

    public ResultView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1280, 800);
        root.setStyle("-fx-background-color: #f8fafc;");

        // ==========================================
        // 1. TOP NAVBAR (Matching design screenshot)
        // ==========================================
        HBox navbar = new HBox(20);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(12, 48, 12, 48));
        navbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1px 0;");

        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            logoImageView.setImage(iconImg);
            logoImageView.setFitHeight(28);
            logoImageView.setFitWidth(28);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
            logoImageView.setStyle("-fx-cursor: hand;");
        } catch (Exception ignored) {
        }

        brandNameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #4f46e5; -fx-letter-spacing: 0.5px; -fx-cursor: hand;");
        HBox logoBrandBox = new HBox(8, logoImageView, brandNameLabel);
        logoBrandBox.setAlignment(Pos.CENTER_LEFT);

        // Center Nav Tabs (Topics, History centered)
        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 8 16; -fx-cursor: hand;");
        navHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 8 16; -fx-cursor: hand;");

        navTabs.getChildren().setAll(navTopicsBtn, navHistoryBtn);
        navTabs.setAlignment(Pos.CENTER);

        HBox leftSpacer = new HBox();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        HBox rightSpacer = new HBox();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle("-fx-font-size: 17px; -fx-text-fill: #64748b; -fx-cursor: hand;");

        HBox userBox = new HBox(16, bellIcon, userProfileWidget.getRoot());
        userBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(logoBrandBox, leftSpacer, navTabs, rightSpacer, userBox);
        root.setTop(navbar);

        // ==========================================
        // 2. MAIN RESULT CONTENT
        // ==========================================
        VBox contentContainer = new VBox(22);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setPadding(new Insets(24, 48, 28, 48));
        contentContainer.setStyle("-fx-background-color: #f8fafc;");

        VBox mainBox = new VBox(18);
        mainBox.setMaxWidth(780);
        mainBox.setAlignment(Pos.TOP_CENTER);

        // Header Hero Banner (Trophy + Title + Subtitle)
        VBox headerBox = new VBox(6);
        headerBox.setAlignment(Pos.CENTER);

        StackPane trophyBadge = new StackPane();
        trophyBadge.setPrefSize(50, 50);
        trophyBadge.setMinSize(50, 50);
        trophyBadge.setMaxSize(50, 50);
        trophyBadge.setStyle("-fx-background-color: #e0e7ff; -fx-background-radius: 999px;");

        Label trophyIcon = new Label("🏆");
        trophyIcon.setStyle("-fx-font-size: 22px;");
        trophyBadge.getChildren().add(trophyIcon);

        Label headlineLabel = new Label("Quiz Completed!");
        headlineLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        quizTitleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 500; -fx-text-fill: #64748b;");

        headerBox.getChildren().addAll(trophyBadge, headlineLabel, quizTitleLabel);

        // Performance Metrics Cards Container (Score Card + 2x2 Grid)
        HBox metricsRow = new HBox(18);
        metricsRow.setAlignment(Pos.CENTER);

        // Left Large Score Card
        VBox leftScoreCard = new VBox(8);
        leftScoreCard.setPrefWidth(340);
        leftScoreCard.setAlignment(Pos.CENTER);
        leftScoreCard.getStyleClass().add("card");
        leftScoreCard.setPadding(new Insets(22, 22, 22, 22));
        leftScoreCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 2);");

        Label yourScoreLabel = new Label("YOUR SCORE");
        yourScoreLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #64748b; -fx-letter-spacing: 0.8px;");

        percentDisplayLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: 800; -fx-text-fill: #4338ca;");

        correctRatioBadgeLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 4 16; -fx-background-radius: 999px;");

        leftScoreCard.getChildren().addAll(yourScoreLabel, percentDisplayLabel, correctRatioBadgeLabel);

        // Right 2x2 Grid Stat Cards
        GridPane gridPane = new GridPane();
        gridPane.setHgap(14);
        gridPane.setVgap(14);
        HBox.setHgrow(gridPane, Priority.ALWAYS);

        VBox c1 = createGridStatCard("✓", "Correct", correctValLabel, "#4338ca");
        VBox c2 = createGridStatCard("✕", "Incorrect", incorrectValLabel, "#ef4444");
        VBox c3 = createGridStatCard("🎯", "Accuracy", accuracyValLabel, "#4338ca");
        VBox c4 = createGridStatCard("⏱", "Time", durationValLabel, "#64748b");

        gridPane.add(c1, 0, 0);
        gridPane.add(c2, 1, 0);
        gridPane.add(c3, 0, 1);
        gridPane.add(c4, 1, 1);

        metricsRow.getChildren().addAll(leftScoreCard, gridPane);

        // Question Review Section Container
        VBox reviewSectionCard = new VBox(0);
        reviewSectionCard.getStyleClass().add("card");
        reviewSectionCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 2);");

        HBox reviewHeader = new HBox();
        reviewHeader.setPadding(new Insets(12, 18, 12, 18));
        reviewHeader.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1px 0; -fx-background-color: #f8fafc; -fx-background-radius: 12px 12px 0 0;");
        Label reviewHeaderTitle = new Label("Question Review");
        reviewHeaderTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #334155;");
        reviewHeader.getChildren().add(reviewHeaderTitle);

        reviewSectionCard.getChildren().addAll(reviewHeader, questionReviewBox);

        // Bottom Action Buttons Row
        actionBtnRow.setAlignment(Pos.CENTER);

        reviewAnswersBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 10 24; -fx-font-weight: 700; -fx-background-radius: 8px; -fx-cursor: hand;");
        tryAgainBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-padding: 10 24; -fx-font-weight: 700; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        backTopicsBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-padding: 10 24; -fx-font-weight: 700; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        backAdminBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 10 28; -fx-font-weight: 700; -fx-background-radius: 8px; -fx-cursor: hand;");

        actionBtnRow.getChildren().addAll(reviewAnswersBtn, tryAgainBtn, backTopicsBtn);

        mainBox.getChildren().addAll(headerBox, metricsRow, reviewSectionCard, actionBtnRow);
        contentContainer.getChildren().add(mainBox);

        root.setCenter(contentContainer);
    }

    public void setAdminMode(boolean isAdmin) {
        if (isAdmin) {
            navTabs.setVisible(false);
            navTabs.setManaged(false);
            actionBtnRow.getChildren().setAll(backAdminBtn);
        } else {
            navTabs.setVisible(true);
            navTabs.setManaged(true);
            actionBtnRow.getChildren().setAll(reviewAnswersBtn, tryAgainBtn, backTopicsBtn);
        }
    }

    private VBox createGridStatCard(String iconSymbol, String titleText, Label valLabel, String iconColorHex) {
        VBox card = new VBox(4);
        card.setPrefWidth(190);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.03), 6, 0, 0, 2);");

        HBox titleBox = new HBox(6);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(iconSymbol);
        iconLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + iconColorHex + "; -fx-font-weight: bold;");

        Label titleLbl = new Label(titleText);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        titleBox.getChildren().addAll(iconLbl, titleLbl);

        valLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        card.getChildren().addAll(titleBox, valLabel);
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

    public Button getBackTopicsBtn() {
        return backTopicsBtn;
    }

    public Button getBackAdminBtn() {
        return backAdminBtn;
    }

}
