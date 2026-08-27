package com.quizzy.view;

import com.quizzy.model.Topic;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class QuizHistoryView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    private final TextField searchTopicField = new TextField();
    private final ListView<Topic> topicListView = new ListView<>();

    private final Label totalQuizzesValLabel = new Label("0");
    private final Label avgScoreValLabel = new Label("0%");
    private final Label bestScoreValLabel = new Label("0%");
    private final Label daysActiveValLabel = new Label("0");

    private final ComboBox<String> topicFilterComboBox = new ComboBox<>();
    private final ComboBox<String> dateFilterComboBox = new ComboBox<>();

    private final GridPane attemptsGrid = new GridPane();
    private final Label paginationInfoLabel = new Label("Showing 1 to 5 of 5 attempts");
    private final HBox paginationButtonsBox = new HBox(8);

    public QuizHistoryView() {
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

        VBox leftSidebar = new VBox(14);
        leftSidebar.setPrefWidth(280);
        leftSidebar.setMinWidth(260);
        leftSidebar.setMaxWidth(300);
        leftSidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 1px 0 0;");
        leftSidebar.setPadding(new Insets(20, 16, 20, 20));

        VBox topicsHeaderBox = new VBox(4);
        Label topicsHeader = new Label("Learning Topics");
        topicsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        Label topicsSubHeader = new Label("Select a subject to begin");
        topicsSubHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        topicsHeaderBox.getChildren().addAll(topicsHeader, topicsSubHeader);

        searchTopicField.setPromptText("🔍  Search topics...");
        searchTopicField.setPrefHeight(34);
        searchTopicField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 12px; -fx-padding: 0 10; -fx-text-fill: #0f172a;");

        topicListView.getStyleClass().add("topic-list-view");
        topicListView.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(topicListView, Priority.ALWAYS);

        HBox motivCard = new HBox(12);
        motivCard.setAlignment(Pos.CENTER_LEFT);
        motivCard.setPadding(new Insets(12, 12, 12, 12));
        motivCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.03), 6, 0, 0, 2);");

        Label bulbIcon = new Label("💡");
        bulbIcon.setStyle("-fx-font-size: 18px; -fx-background-color: #fef9c3; -fx-padding: 6 8; -fx-background-radius: 10px;");

        VBox motivTextBox = new VBox(2);
        Label motivTitle = new Label("Keep practicing!");
        motivTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #4338ca;");
        Label motivSub = new Label("Consistent practice leads to great results.");
        motivSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-line-spacing: 1px;");
        motivSub.setWrapText(true);
        motivSub.setMaxWidth(150);
        motivTextBox.getChildren().addAll(motivTitle, motivSub);

        motivCard.getChildren().addAll(bulbIcon, motivTextBox);

        leftSidebar.getChildren().addAll(topicsHeaderBox, searchTopicField, topicListView, motivCard);
        root.setLeft(leftSidebar);

        VBox mainContent = new VBox(16);
        mainContent.setPadding(new Insets(18, 36, 20, 36));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        VBox headerTitleBox = new VBox(3);
        Label pageTitleL = new Label("Quiz History");
        pageTitleL.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        Label pageSubTitleL = new Label("Review your past attempts and track your progress over time.");
        pageSubTitleL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        headerTitleBox.getChildren().addAll(pageTitleL, pageSubTitleL);

        HBox statsGrid = new HBox(14);
        statsGrid.setAlignment(Pos.CENTER);

        VBox card1 = createTopStatCard("📑", totalQuizzesValLabel, "Total Quizzes", "#4f46e5", "#eff2fe");
        VBox card2 = createTopStatCard("📈", avgScoreValLabel, "Average Score", "#0284c7", "#e0f2fe");
        VBox card3 = createTopStatCard("🏆", bestScoreValLabel, "Best Score", "#16a34a", "#dcfce7");
        VBox card4 = createTopStatCard("📅", daysActiveValLabel, "Days Active", "#ea580c", "#ffedd5");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsGrid.getChildren().addAll(card1, card2, card3, card4);

        VBox tableCard = new VBox(14);
        tableCard.setPadding(new Insets(18, 20, 16, 20));
        tableCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 8, 0, 0, 2);");

        HBox tableHeaderRow = new HBox(12);
        tableHeaderRow.setAlignment(Pos.CENTER_LEFT);

        Label tableTitle = new Label("Your Quiz Attempts");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

        HBox tableSpacer = new HBox();
        HBox.setHgrow(tableSpacer, Priority.ALWAYS);

        dateFilterComboBox.getItems().setAll("All Time", "Today", "This Week", "This Month", "This Year");
        dateFilterComboBox.setValue("All Time");
        dateFilterComboBox.setPrefWidth(140);
        dateFilterComboBox.setPrefHeight(34);
        dateFilterComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #334155;");

        topicFilterComboBox.setPrefWidth(220);
        topicFilterComboBox.setPrefHeight(34);
        topicFilterComboBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #334155;");

        tableHeaderRow.getChildren().addAll(tableTitle, tableSpacer, dateFilterComboBox, topicFilterComboBox);

        attemptsGrid.getColumnConstraints().clear();

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(32);
        col0.setHalignment(HPos.LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(24);
        col1.setHalignment(HPos.LEFT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(12);
        col2.setHalignment(HPos.CENTER);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(24);
        col3.setHalignment(HPos.LEFT);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(8);
        col4.setHalignment(HPos.CENTER);

        attemptsGrid.getColumnConstraints().addAll(col0, col1, col2, col3, col4);
        attemptsGrid.setHgap(12);
        attemptsGrid.setVgap(0);
        attemptsGrid.setMaxWidth(Double.MAX_VALUE);

        HBox paginationRow = new HBox(16);
        paginationRow.setAlignment(Pos.CENTER_LEFT);
        paginationRow.setPadding(new Insets(16, 8, 4, 8));

        paginationInfoLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

        HBox pSpacer = new HBox();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);

        paginationButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        paginationRow.getChildren().addAll(paginationInfoLabel, pSpacer, paginationButtonsBox);

        tableCard.getChildren().addAll(tableHeaderRow, attemptsGrid, paginationRow);

        mainContent.getChildren().addAll(headerTitleBox, statsGrid, tableCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8fafc;");
        root.setCenter(scrollPane);

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

    private VBox createTopStatCard(String iconStr, Label valLabel, String labelStr, String iconColorHex, String iconBgHex) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 16, 18, 16));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.03), 8, 0, 0, 2);");

        Label iconBox = new Label(iconStr);
        iconBox.setStyle("-fx-font-size: 18px; -fx-text-fill: " + iconColorHex + "; -fx-background-color: " + iconBgHex + "; -fx-padding: 8 10; -fx-background-radius: 8px;");

        valLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

        Label titleL = new Label(labelStr);
        titleL.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #64748b;");

        card.getChildren().addAll(iconBox, valLabel, titleL);
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

    public ListView<Topic> getTopicListView() {
        return topicListView;
    }

    public TextField getSearchTopicField() {
        return searchTopicField;
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

    public Label getDaysActiveValLabel() {
        return daysActiveValLabel;
    }

    public ComboBox<String> getTopicFilterComboBox() {
        return topicFilterComboBox;
    }

    public ComboBox<String> getDateFilterComboBox() {
        return dateFilterComboBox;
    }

    public GridPane getAttemptsGrid() {
        return attemptsGrid;
    }

    public Label getPaginationInfoLabel() {
        return paginationInfoLabel;
    }

    public HBox getPaginationButtonsBox() {
        return paginationButtonsBox;
    }
}
