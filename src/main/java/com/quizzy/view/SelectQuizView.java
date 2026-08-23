package com.quizzy.view;

import com.quizzy.model.Topic;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SelectQuizView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    private final TextField searchTopicField = new TextField();
    private final ListView<Topic> topicListView = new ListView<>();

    private final Label pageTitleLabel = new Label("Choose a Quiz");
    private final Label pageSubtitleLabel = new Label("Select a topic and choose a quiz to start your practice.");
    private final TextField searchQuizField = new TextField();

    private final HBox quizCardsContainer = new HBox(20);
    private final Button refreshBtn = new Button("🔄  Refresh");

    public SelectQuizView() {
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

        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f46e5; -fx-font-size: 14px; -fx-font-weight: 700; -fx-padding: 8 16 4 16; -fx-cursor: hand;");
        navHistoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 8 16; -fx-cursor: hand;");

        Region activeUnderline = new Region();
        activeUnderline.setPrefHeight(3);
        activeUnderline.setMinHeight(3);
        activeUnderline.setMaxHeight(3);
        activeUnderline.setPrefWidth(54);
        activeUnderline.setMaxWidth(54);
        activeUnderline.setStyle("-fx-background-color: #4f46e5; -fx-background-radius: 3 3 0 0;");

        VBox activeTopicTab = new VBox(2, navTopicsBtn, activeUnderline);
        activeTopicTab.setAlignment(Pos.CENTER);

        HBox navTabs = new HBox(12, activeTopicTab, navHistoryBtn);
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
        leftSidebar.setPrefWidth(260);
        leftSidebar.setMinWidth(240);
        leftSidebar.setMaxWidth(280);
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
        motivSub.setMaxWidth(130);
        motivTextBox.getChildren().addAll(motivTitle, motivSub);

        motivCard.getChildren().addAll(bulbIcon, motivTextBox);

        leftSidebar.getChildren().addAll(topicsHeaderBox, searchTopicField, topicListView, motivCard);
        root.setLeft(leftSidebar);

        VBox mainContent = new VBox(18);
        mainContent.setPadding(new Insets(24, 36, 24, 36));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox headerTitleBox = new VBox(4);
        pageTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        pageSubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        headerTitleBox.getChildren().addAll(pageTitleLabel, pageSubtitleLabel);
        HBox.setHgrow(headerTitleBox, Priority.ALWAYS);

        searchQuizField.setPromptText("🔍  Search quiz name...");
        searchQuizField.setPrefWidth(260);
        searchQuizField.setPrefHeight(38);
        searchQuizField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px; -fx-text-fill: #0f172a; -fx-padding: 0 14;");

        headerRow.getChildren().addAll(headerTitleBox, searchQuizField);

        Region titleDivider = new Region();
        titleDivider.setPrefHeight(1);
        titleDivider.setMaxHeight(1);
        titleDivider.setStyle("-fx-background-color: #e2e8f0;");

        quizCardsContainer.setAlignment(Pos.TOP_LEFT);
        quizCardsContainer.setPadding(new Insets(8, 0, 8, 0));
        VBox.setVgrow(quizCardsContainer, Priority.ALWAYS);

        HBox refreshRow = new HBox(refreshBtn);
        refreshRow.setAlignment(Pos.CENTER_LEFT);
        refreshBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 8 16; -fx-font-size: 13px; -fx-font-weight: 600; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");

        mainContent.getChildren().addAll(headerRow, titleDivider, quizCardsContainer, refreshRow);

        ScrollPane scrollPane = new ScrollPane(mainContent);
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

    public TextField getSearchTopicField() {
        return searchTopicField;
    }

    public ListView<Topic> getTopicListView() {
        return topicListView;
    }

    public Label getPageTitleLabel() {
        return pageTitleLabel;
    }

    public Label getPageSubtitleLabel() {
        return pageSubtitleLabel;
    }

    public TextField getSearchQuizField() {
        return searchQuizField;
    }

    public HBox getQuizCardsContainer() {
        return quizCardsContainer;
    }

    public Button getRefreshBtn() {
        return refreshBtn;
    }
}
