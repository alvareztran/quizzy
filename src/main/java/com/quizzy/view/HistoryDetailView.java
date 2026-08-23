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
import javafx.scene.shape.Circle;

public class HistoryDetailView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    private final Button backToHistoryBtn = new Button("←  Back to History");
    private final Label quizTitleInfoLabel = new Label("Java Basic");
    private final Label quizDateInfoLabel = new Label("Aug 23, 2026 • 10:21");
    private final Label quizQuestionsInfoLabel = new Label("10 Questions");
    private final VBox questionsIndexContainer = new VBox(6);

    private final Label percentDisplayLabel = new Label("90%");
    private final Label praiseTitleLabel = new Label("Great Job! 🎉");
    private final Label praiseSubtitleLabel = new Label("You scored 9 out of 10");
    private final Label correctCountLabel = new Label("9");
    private final Label incorrectCountLabel = new Label("1");
    private final Label accuracyPercentLabel = new Label("90%");
    private final Label timeTakenLabel = new Label("10:21");

    private final VBox questionCardsContainer = new VBox(16);
    private final ScrollPane scrollPane = new ScrollPane();

    public HistoryDetailView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1280, 800);
        root.setStyle("-fx-background-color: #f8fafc;");

        HBox navbar = new HBox(24);
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

        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 8 16; -fx-cursor: hand;");
        navHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f46e5; -fx-font-size: 14px; -fx-font-weight: 700; -fx-padding: 8 16 4 16; -fx-cursor: hand;");

        Region activeUnderline = new Region();
        activeUnderline.setPrefHeight(3);
        activeUnderline.setMinHeight(3);
        activeUnderline.setMaxHeight(3);
        activeUnderline.setPrefWidth(54);
        activeUnderline.setMaxWidth(54);
        activeUnderline.setStyle("-fx-background-color: #4f46e5; -fx-background-radius: 3 3 0 0;");

        VBox activeHistoryTab = new VBox(2, navHistoryBtn, activeUnderline);
        activeHistoryTab.setAlignment(Pos.CENTER);

        HBox navTabs = new HBox(12, navTopicsBtn, activeHistoryTab);
        navTabs.setAlignment(Pos.CENTER);

        HBox leftSpacer = new HBox();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        HBox rightSpacer = new HBox();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Label bellIcon = new Label("🔔");
        bellIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b; -fx-cursor: hand;");

        HBox userBox = new HBox(16, bellIcon, userProfileWidget.getRoot());
        userBox.setAlignment(Pos.CENTER_RIGHT);

        navbar.getChildren().addAll(logoBrandBox, leftSpacer, navTabs, rightSpacer, userBox);
        root.setTop(navbar);

        VBox leftSidebar = new VBox(18);
        leftSidebar.setPrefWidth(260);
        leftSidebar.setMinWidth(240);
        leftSidebar.setMaxWidth(280);
        leftSidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 1px 0 0;");
        leftSidebar.setPadding(new Insets(20, 16, 20, 20));

        backToHistoryBtn.setFocusTraversable(false);
        backToHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f46e5; -fx-font-size: 13px; -fx-font-weight: 700; -fx-padding: 0; -fx-cursor: hand;");

        VBox quizInfoCard = new VBox(8);
        quizInfoCard.setPadding(new Insets(14, 14, 14, 14));
        quizInfoCard.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label quizInfoTag = new Label("QUIZ INFO");
        quizInfoTag.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #94a3b8; -fx-letter-spacing: 0.5px;");

        quizTitleInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        HBox dateRow = new HBox(6);
        Label dateIcon = new Label("📅");
        dateIcon.setStyle("-fx-font-size: 11px;");
        quizDateInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        dateRow.getChildren().addAll(dateIcon, quizDateInfoLabel);

        HBox countRow = new HBox(6);
        Label countIcon = new Label("🕒");
        countIcon.setStyle("-fx-font-size: 11px;");
        quizQuestionsInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        countRow.getChildren().addAll(countIcon, quizQuestionsInfoLabel);

        quizInfoCard.getChildren().addAll(quizInfoTag, quizTitleInfoLabel, dateRow, countRow);

        Label questionsTag = new Label("QUESTIONS");
        questionsTag.setStyle("-fx-font-size: 10px; -fx-font-weight: 800; -fx-text-fill: #94a3b8; -fx-letter-spacing: 0.5px; -fx-padding: 4 0 0 0;");

        ScrollPane questionsNavScroll = new ScrollPane(questionsIndexContainer);
        questionsNavScroll.setFitToWidth(true);
        questionsNavScroll.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");
        VBox.setVgrow(questionsNavScroll, Priority.ALWAYS);

        leftSidebar.getChildren().addAll(backToHistoryBtn, quizInfoCard, questionsTag, questionsNavScroll);
        root.setLeft(leftSidebar);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20, 36, 24, 36));
        mainContent.setStyle("-fx-background-color: #f8fafc;");
        mainContent.setAlignment(Pos.TOP_CENTER);

        VBox contentWrapper = new VBox(18);
        contentWrapper.setMaxWidth(960);

        VBox summaryCard = new VBox(14);
        summaryCard.setPadding(new Insets(20, 24, 20, 24));
        summaryCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 2);");

        Label summaryHeader = new Label("Your Result");
        summaryHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        HBox metricsRow = new HBox(18);
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        StackPane circularScoreRing = new StackPane();
        Circle outerRing = new Circle(32);
        outerRing.setStyle("-fx-fill: transparent; -fx-stroke: #4f46e5; -fx-stroke-width: 5px;");
        percentDisplayLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #4f46e5;");
        circularScoreRing.getChildren().addAll(outerRing, percentDisplayLabel);

        VBox praiseBox = new VBox(2);
        praiseTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        praiseSubtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        praiseBox.getChildren().addAll(praiseTitleLabel, praiseSubtitleLabel);

        HBox scoreCol = new HBox(12, circularScoreRing, praiseBox);
        scoreCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(scoreCol, Priority.ALWAYS);

        VBox correctCol = createSummaryStatItem("✓", "#22c55e", "#dcfce7", correctCountLabel, "Correct");
        HBox.setHgrow(correctCol, Priority.ALWAYS);

        VBox incorrectCol = createSummaryStatItem("✕", "#ef4444", "#fee2e2", incorrectCountLabel, "Incorrect");
        HBox.setHgrow(incorrectCol, Priority.ALWAYS);

        VBox accuracyCol = createSummaryStatItem("🎯", "#7c3aed", "#f3e8ff", accuracyPercentLabel, "Accuracy");
        HBox.setHgrow(accuracyCol, Priority.ALWAYS);

        VBox timeCol = createSummaryStatItem("🕒", "#0284c7", "#e0f2fe", timeTakenLabel, "Time Taken");
        HBox.setHgrow(timeCol, Priority.ALWAYS);

        metricsRow.getChildren().addAll(
                scoreCol, createVertDivider(),
                correctCol, createVertDivider(),
                incorrectCol, createVertDivider(),
                accuracyCol, createVertDivider(),
                timeCol
        );

        summaryCard.getChildren().addAll(summaryHeader, metricsRow);

        contentWrapper.getChildren().addAll(summaryCard, questionCardsContainer);
        mainContent.getChildren().add(contentWrapper);

        scrollPane.setContent(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8fafc;");
        root.setCenter(scrollPane);

        // ==========================================
        // 4. BOTTOM FOOTER BAR
        // ==========================================
        HBox footerBar = new HBox(20);
        footerBar.setAlignment(Pos.CENTER_LEFT);
        footerBar.setPadding(new Insets(16, 48, 16, 48));
        footerBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 1px 0 0 0;");

        Label copyrightLabel = new Label("© 2026 QUIZZY Learning Platform");
        copyrightLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

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

        HBox linksBox = new HBox(24, privacyLink, termsLink, helpLink, contactLink);
        linksBox.setAlignment(Pos.CENTER_RIGHT);

        footerBar.getChildren().addAll(copyrightLabel, footerSpacer, linksBox);
        root.setBottom(footerBar);
    }

    private VBox createSummaryStatItem(String iconStr, String iconColor, String iconBg, Label valLabel, String subText) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);

        Label iconL = new Label(iconStr);
        iconL.setAlignment(Pos.CENTER);
        iconL.setPrefSize(26, 26);
        iconL.setStyle("-fx-font-size: 13px; -fx-font-weight: 900; -fx-text-fill: " + iconColor + "; -fx-background-color: " + iconBg + "; -fx-background-radius: 13px;");

        valLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label subL = new Label(subText);
        subL.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

        box.getChildren().addAll(iconL, valLabel, subL);
        return box;
    }

    private Region createVertDivider() {
        Region r = new Region();
        r.setPrefWidth(1);
        r.setMinWidth(1);
        r.setMaxWidth(1);
        r.setPrefHeight(46);
        r.setStyle("-fx-background-color: #f1f5f9;");
        return r;
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

    public Button getBackToHistoryBtn() {
        return backToHistoryBtn;
    }

    public Label getQuizTitleInfoLabel() {
        return quizTitleInfoLabel;
    }

    public Label getQuizDateInfoLabel() {
        return quizDateInfoLabel;
    }

    public Label getQuizQuestionsInfoLabel() {
        return quizQuestionsInfoLabel;
    }

    public VBox getQuestionsIndexContainer() {
        return questionsIndexContainer;
    }

    public Label getPercentDisplayLabel() {
        return percentDisplayLabel;
    }

    public Label getPraiseTitleLabel() {
        return praiseTitleLabel;
    }

    public Label getPraiseSubtitleLabel() {
        return praiseSubtitleLabel;
    }

    public Label getCorrectCountLabel() {
        return correctCountLabel;
    }

    public Label getIncorrectCountLabel() {
        return incorrectCountLabel;
    }

    public Label getAccuracyPercentLabel() {
        return accuracyPercentLabel;
    }

    public Label getTimeTakenLabel() {
        return timeTakenLabel;
    }

    public VBox getQuestionCardsContainer() {
        return questionCardsContainer;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
