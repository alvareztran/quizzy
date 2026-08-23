package com.quizzy.view;

import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SelectQuizView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Top Navbar Controls
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button navDashboardBtn = new Button("Dashboard");
    private final Button navTopicsBtn = new Button("Topics");
    private final Button navHistoryBtn = new Button("History");

    // Sidebar Controls
    private final ListView<Topic> topicListView = new ListView<>();

    // Main Content Controls
    private final Button backToDashboardBtn = new Button("← Back to Dashboard");
    private final Label pageTitleLabel = new Label("Choose a Quiz");
    private final Label pageSubtitleLabel = new Label("Select a topic and choose a quiz to start.");

    // Quiz Cards Grid Container
    private final HBox quizCardsContainer = new HBox(20);

    private final Button refreshBtn = new Button("🔄  Refresh");

    public SelectQuizView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 780);

        // ==========================================
        // 1. TOP NAVBAR (Matching media_1787420830737.png)
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

        // Nav Tabs (Topics active)
        navDashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #464554; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        navTopicsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #4f46e5; -fx-border-width: 0 0 2px 0; -fx-padding: 8 12;");
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
        // 2. LEFT SIDEBAR ("Learning Topics")
        // ==========================================
        VBox leftSidebar = new VBox(16);
        leftSidebar.setPrefWidth(280);
        leftSidebar.setMinWidth(260);
        leftSidebar.getStyleClass().add("sidebar");
        leftSidebar.setPadding(new Insets(24, 20, 24, 28));

        VBox topicsHeaderBox = new VBox(4);
        Label topicsHeader = new Label("Learning Topics");
        topicsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label topicsSubHeader = new Label("Select a subject to begin");
        topicsSubHeader.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        topicsHeaderBox.getChildren().addAll(topicsHeader, topicsSubHeader);

        topicListView.getStyleClass().add("topic-list-view");
        VBox.setVgrow(topicListView, Priority.ALWAYS);

        leftSidebar.getChildren().addAll(topicsHeaderBox, topicListView);
        root.setLeft(leftSidebar);

        // ==========================================
        // 3. MAIN WORKSPACE CONTENT AREA
        // ==========================================
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(28, 36, 28, 36));
        mainContent.setStyle("-fx-background-color: #f8f9fb;");

        // Back to Dashboard Ghost Link
        backToDashboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 0; -fx-cursor: hand;");

        // Header Titles
        VBox headerTitleBox = new VBox(6);
        pageTitleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");
        pageSubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        headerTitleBox.getChildren().addAll(pageTitleLabel, pageSubtitleLabel);

        // Quiz Cards Container (3 Columns Layout)
        quizCardsContainer.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(quizCardsContainer, Priority.ALWAYS);

        // Refresh Bar
        HBox refreshRow = new HBox(refreshBtn);
        refreshRow.setAlignment(Pos.CENTER_LEFT);
        refreshBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #475569; -fx-padding: 8 16; -fx-font-size: 13px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        mainContent.getChildren().addAll(backToDashboardBtn, headerTitleBox, quizCardsContainer, refreshRow);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fb;");
        root.setCenter(scrollPane);

        // ==========================================
        // 4. BOTTOM FOOTER BAR
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

    public ListView<Topic> getTopicListView() {
        return topicListView;
    }

    public Button getBackToDashboardBtn() {
        return backToDashboardBtn;
    }

    public Label getPageTitleLabel() {
        return pageTitleLabel;
    }

    public Label getPageSubtitleLabel() {
        return pageSubtitleLabel;
    }

    public HBox getQuizCardsContainer() {
        return quizCardsContainer;
    }

    public Button getRefreshBtn() {
        return refreshBtn;
    }

}
