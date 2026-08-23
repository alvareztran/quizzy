package com.quizzy.view;

import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AdminResultView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    // Sidebar Navigation Buttons
    private final Button dashBtn = new Button();
    private final Button topicBtn = new Button();
    private final Button quizBtn = new Button();
    private final Button questionBtn = new Button();
    private final Button answerBtn = new Button();
    private final Button userBtn = new Button();
    private final Button resultBtn = new Button();

    // Toolbar Filter Controls
    private final TextField searchField = new TextField();
    private final ComboBox<String> topicFilterComboBox = new ComboBox<>();
    private final ComboBox<String> dateFilterComboBox = new ComboBox<>();
    private final ComboBox<String> scoreFilterComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

    // TableView and Columns
    private final TableView<ResultItemDTO> resultTable = new TableView<>();
    private final TableColumn<ResultItemDTO, ResultItemDTO> userColumn = new TableColumn<>("User");
    private final TableColumn<ResultItemDTO, ResultItemDTO> quizColumn = new TableColumn<>("Quiz Name");
    private final TableColumn<ResultItemDTO, ResultItemDTO> scoreColumn = new TableColumn<>("Score");
    private final TableColumn<ResultItemDTO, ResultItemDTO> dateTimeColumn = new TableColumn<>("Date & Time");
    private final TableColumn<ResultItemDTO, ResultItemDTO> durationColumn = new TableColumn<>("Duration");
    private final TableColumn<ResultItemDTO, ResultItemDTO> actionColumn = new TableColumn<>("Action");

    // Pagination Controls
    private final Label paginationInfoLabel = new Label("Showing 0 to 0 of 0 results");
    private final Button prevPageBtn = new Button("‹");
    private final Button page1Btn = new Button("1");
    private final Button nextPageBtn = new Button("›");

    public AdminResultView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 780);
        root.setStyle("-fx-background-color: #f8fafc;");

        // ==========================================
        // 1. SIDEBAR (Full-height from top)
        // ==========================================
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 16, 16));

        ImageView iconView = new ImageView();
        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            iconView.setImage(iconImg);
            iconView.setFitHeight(32);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
        } catch (Exception ignored) {
        }

        Label brandTitle = new Label("QUIZZY");
        brandTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: 800; -fx-text-fill: #191c1e;");

        HBox logoContainer = new HBox(8, iconView, brandTitle);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(0, 8, 28, 8));

        Label managementHeader = new Label("MANAGEMENT");
        managementHeader.setStyle("-fx-text-fill: #767586; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 2 0 4 16;");

        // Setup navigation buttons with PNG icons
        NavIconHelper.setupNavButton(dashBtn, "Dashboard", "dashboard.png", false);
        NavIconHelper.setupNavButton(topicBtn, "Topics", "topic_icon.png", false);
        NavIconHelper.setupNavButton(quizBtn, "Quizzes", "quiz_icon.png", false);
        NavIconHelper.setupNavButton(questionBtn, "Questions", "question_icon.png", false);
        NavIconHelper.setupNavButton(answerBtn, "Answers", "answer_icon.png", false);
        NavIconHelper.setupNavButton(userBtn, "Users", "user_icon.png", false);
        NavIconHelper.setupNavButton(resultBtn, "Results", "result_icon.png", true);

        VBox sidebarSpacer = new VBox();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(16, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #c7c4d7; -fx-border-width: 1 0 0 0;");
        profileBox.getChildren().add(userProfileWidget.getRoot());

        sidebar.getChildren().addAll(
                logoContainer,
                managementHeader,
                dashBtn, topicBtn, quizBtn, questionBtn, answerBtn, userBtn, resultBtn,
                sidebarSpacer,
                profileBox
        );
        root.setLeft(sidebar);

        // ==========================================
        // 2. MAIN WORKSPACE CONTENT AREA
        // ==========================================
        VBox mainContent = new VBox(14);
        mainContent.setPadding(new Insets(16, 28, 16, 28));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        // Page Header
        VBox titleCol = new VBox(3);
        Label titleL = new Label("Quiz History Management");
        titleL.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Track and analyze all quiz attempts across the platform.");
        subtitleL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        // Search & Filter Toolbar
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(10, 16, 10, 16));

        searchField.setPromptText("🔍  Search by user or quiz...");
        searchField.setPrefWidth(260);
        searchField.setPrefHeight(34);

        topicFilterComboBox.setPromptText("All Topics");
        topicFilterComboBox.setPrefHeight(34);
        topicFilterComboBox.setPrefWidth(160);

        dateFilterComboBox.setPromptText("Any Date");
        dateFilterComboBox.getItems().setAll("Any Date", "Today", "This Week", "This Month");
        dateFilterComboBox.setValue("Any Date");
        dateFilterComboBox.setPrefHeight(34);
        dateFilterComboBox.setPrefWidth(140);

        scoreFilterComboBox.setPromptText("Any Score");
        scoreFilterComboBox.getItems().setAll("Any Score", "High (≥ 80%)", "Medium (50-79%)", "Low (< 50%)");
        scoreFilterComboBox.setValue("Any Score");
        scoreFilterComboBox.setPrefHeight(34);
        scoreFilterComboBox.setPrefWidth(140);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 6 14;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(
                searchField, topicFilterComboBox, dateFilterComboBox,
                scoreFilterComboBox, resetFilterBtn, toolbarSpacer
        );

        // Data TableView
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userColumn.setPrefWidth(240);
        quizColumn.setPrefWidth(260);
        scoreColumn.setPrefWidth(100);
        scoreColumn.setStyle("-fx-alignment: CENTER;");
        dateTimeColumn.setPrefWidth(160);
        durationColumn.setPrefWidth(120);
        actionColumn.setPrefWidth(130);
        actionColumn.setMaxWidth(140);
        actionColumn.setStyle("-fx-alignment: CENTER;");

        resultTable.getColumns().addAll(
                userColumn, quizColumn, scoreColumn,
                dateTimeColumn, durationColumn, actionColumn
        );
        VBox.setVgrow(resultTable, Priority.ALWAYS);

        // Pagination Bar
        HBox paginationBar = new HBox(12);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(4, 2, 0, 2));

        paginationInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox pagSpacer = new HBox();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        prevPageBtn.setStyle("-fx-padding: 3 9;");
        page1Btn.getStyleClass().add("button-primary");
        page1Btn.setStyle("-fx-padding: 3 9; -fx-font-weight: bold;");
        nextPageBtn.setStyle("-fx-padding: 3 9;");

        HBox pagBtns = new HBox(4, prevPageBtn, page1Btn, nextPageBtn);
        pagBtns.setAlignment(Pos.CENTER);

        paginationBar.getChildren().addAll(paginationInfoLabel, pagSpacer, pagBtns);

        mainContent.getChildren().addAll(titleCol, toolbar, resultTable, paginationBar);
        root.setCenter(mainContent);
    }

    public static class ResultItemDTO {
        public Result result;
        public String userName;
        public String userEmail;
        public String userInitials;
        public String userAvatarColor;
        public String quizName;
        public String topicName;
        public String scoreDisplay;
        public double scorePercent;
        public String dateDisplay;
        public String timeDisplay;
        public String durationDisplay;
    }

    public BorderPane getRoot() {
        return root;
    }

    public UserProfileWidget getUserProfileWidget() {
        return userProfileWidget;
    }

    public Button getDashBtn() {
        return dashBtn;
    }

    public Button getTopicBtn() {
        return topicBtn;
    }

    public Button getQuizBtn() {
        return quizBtn;
    }

    public Button getQuestionBtn() {
        return questionBtn;
    }

    public Button getAnswerBtn() {
        return answerBtn;
    }

    public Button getUserBtn() {
        return userBtn;
    }

    public Button getResultBtn() {
        return resultBtn;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public ComboBox<String> getTopicFilterComboBox() {
        return topicFilterComboBox;
    }

    public ComboBox<String> getDateFilterComboBox() {
        return dateFilterComboBox;
    }

    public ComboBox<String> getScoreFilterComboBox() {
        return scoreFilterComboBox;
    }

    public Button getResetFilterBtn() {
        return resetFilterBtn;
    }

    public TableView<ResultItemDTO> getResultTable() {
        return resultTable;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getUserColumn() {
        return userColumn;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getQuizColumn() {
        return quizColumn;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getScoreColumn() {
        return scoreColumn;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getDateTimeColumn() {
        return dateTimeColumn;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getDurationColumn() {
        return durationColumn;
    }

    public TableColumn<ResultItemDTO, ResultItemDTO> getActionColumn() {
        return actionColumn;
    }

    public Label getPaginationInfoLabel() {
        return paginationInfoLabel;
    }

    public Button getPrevPageBtn() {
        return prevPageBtn;
    }

    public Button getPage1Btn() {
        return page1Btn;
    }

    public Button getNextPageBtn() {
        return nextPageBtn;
    }
}
