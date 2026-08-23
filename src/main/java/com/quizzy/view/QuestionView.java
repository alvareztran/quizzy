package com.quizzy.view;

import com.quizzy.model.Question;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.StatCard;
import com.quizzy.view.component.StatusBadge;
import com.quizzy.view.component.UserProfileWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class QuestionView {

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

    // Page Header Action
    private final Button createQuestionBtn = new Button("+ Add Question");

    // Stat Cards
    private final StatCard totalQuestionsCard = new StatCard("❓", "Total Questions", "0", "In question bank", "#FEF3C7", "#D97706");
    private final StatCard easyQuestionsCard = new StatCard("🟢", "Easy Level", "0", "Basic questions", "#DCFCE7", "#16A34A");
    private final StatCard mediumQuestionsCard = new StatCard("🟡", "Medium Level", "0", "Intermediate items", "#FEF3C7", "#D97706");
    private final StatCard hardQuestionsCard = new StatCard("🔴", "Hard Level", "0", "Advanced questions", "#FEE2E2", "#DC2626");

    // Toolbar Components
    private final TextField searchQuestionsField = new TextField();
    private final ComboBox<String> quizFilterComboBox = new ComboBox<>();
    private final ComboBox<String> difficultyFilterComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

    // TableView Components (Cleaned up: Left-Aligned Data Columns, Center-Aligned ACTIONS)
    private final TableView<Question> questionTable = new TableView<>();
    private final TableColumn<Question, Integer> idColumn = new TableColumn<>("#");
    private final TableColumn<Question, String> contentColumn = new TableColumn<>("QUESTION CONTENT");
    private final TableColumn<Question, String> quizNameColumn = new TableColumn<>("TARGET QUIZ");
    private final TableColumn<Question, String> difficultyColumn = new TableColumn<>("DIFFICULTY");
    private final TableColumn<Question, Question> actionsColumn = new TableColumn<>("ACTIONS");

    // Pagination Controls
    private final Label paginationInfoLabel = new Label("Showing 1 to 0 of 0 questions");
    private final ComboBox<String> perPageComboBox = new ComboBox<>();
    private final Button prevPageBtn = new Button("<");
    private final Button page1Btn = new Button("1");
    private final Button nextPageBtn = new Button(">");

    public QuestionView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 740);

        // Sidebar Navigation (Full Height)
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(240);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20, 16, 16, 16));

        ImageView iconView = new ImageView();
        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            iconView.setImage(iconImg);
            iconView.setFitHeight(32);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
        } catch (Exception e) {
            // Fallback
        }

        Label brandTitle = new Label("QUIZZY");
        brandTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-letter-spacing: 1px;");

        HBox logoContainer = new HBox(10, iconView, brandTitle);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(4, 0, 20, 8));

        Label mgmtHeader = new Label("MANAGEMENT");
        mgmtHeader.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 4 8; -fx-letter-spacing: 1px;");

        NavIconHelper.setupNavButton(dashBtn, "Dashboard", "dashboard.png", false);
        NavIconHelper.setupNavButton(topicBtn, "Topics", "topic_icon.png", false);
        NavIconHelper.setupNavButton(quizBtn, "Quizzes", "quiz_icon.png", false);
        NavIconHelper.setupNavButton(questionBtn, "Questions", "question_icon.png", true);
        NavIconHelper.setupNavButton(answerBtn, "Answers", "answer_icon.png", false);
        NavIconHelper.setupNavButton(userBtn, "Users", "user_icon.png", false);
        NavIconHelper.setupNavButton(resultBtn, "Results", "result_icon.png", false);

        VBox sidebarSpacer = new VBox();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        // Bottom User Profile Widget Frame
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(12, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 1px 0 0 0;");
        profileBox.getChildren().add(userProfileWidget.getRoot());

        sidebar.getChildren().addAll(
                logoContainer,
                dashBtn,
                mgmtHeader,
                topicBtn, quizBtn, questionBtn, answerBtn, userBtn, resultBtn,
                sidebarSpacer,
                profileBox
        );
        root.setLeft(sidebar);

        // Main Workspace Content (Full height from top)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        // Page Header
        HBox pageHeader = new HBox(16);
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleCol = new VBox(4);
        Label titleL = new Label("Question Bank Management");
        titleL.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Manage question bank items, difficulty ratings, and quiz assignments.");
        subtitleL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        createQuestionBtn.getStyleClass().add("button-primary");
        createQuestionBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 22; -fx-font-weight: bold;");

        pageHeader.getChildren().addAll(titleCol, headerSpacer, createQuestionBtn);

        // 4 Stat Cards Row
        HBox statCardsRow = new HBox(16);
        HBox.setHgrow(totalQuestionsCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(easyQuestionsCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(mediumQuestionsCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(hardQuestionsCard.getRoot(), Priority.ALWAYS);

        statCardsRow.getChildren().addAll(
                totalQuestionsCard.getRoot(),
                easyQuestionsCard.getRoot(),
                mediumQuestionsCard.getRoot(),
                hardQuestionsCard.getRoot()
        );

        // Search & Filter Toolbar
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(12, 16, 12, 16));

        searchQuestionsField.setPromptText("Search questions...");
        searchQuestionsField.setPrefWidth(260);
        searchQuestionsField.setPrefHeight(36);

        quizFilterComboBox.setPromptText("All Quizzes");
        quizFilterComboBox.setPrefHeight(36);

        difficultyFilterComboBox.setPromptText("All Difficulty");
        difficultyFilterComboBox.getItems().setAll("All Difficulty", "Easy", "Medium", "Hard");
        difficultyFilterComboBox.setValue("All Difficulty");
        difficultyFilterComboBox.setPrefHeight(36);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 7 14;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchQuestionsField, quizFilterComboBox, difficultyFilterComboBox, resetFilterBtn, toolbarSpacer);

        // Full Width Data TableView (Left-Aligned Data Columns, Center-Aligned ACTIONS)
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.setMaxWidth(60);
        idColumn.setMinWidth(45);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));

        contentColumn.setPrefWidth(480);
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        quizNameColumn.setPrefWidth(240);

        difficultyColumn.setPrefWidth(140);
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        difficultyColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String diff, boolean empty) {
                super.updateItem(diff, empty);
                if (empty || diff == null) {
                    setGraphic(null);
                } else {
                    setGraphic(StatusBadge.createDifficultyBadge(diff));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        actionsColumn.setPrefWidth(120);
        actionsColumn.setMaxWidth(130);
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.getStyleClass().add("column-center");

        questionTable.getColumns().addAll(
                idColumn, contentColumn, quizNameColumn,
                difficultyColumn, actionsColumn
        );
        VBox.setVgrow(questionTable, Priority.ALWAYS);

        // Pagination Bar
        HBox paginationBar = new HBox(12);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(10, 4, 4, 4));

        paginationInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox pagSpacer = new HBox();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        perPageComboBox.getItems().setAll("10 per page", "25 per page", "50 per page");
        perPageComboBox.setValue("10 per page");
        perPageComboBox.setPrefHeight(32);

        prevPageBtn.setStyle("-fx-padding: 4 10;");
        page1Btn.getStyleClass().add("button-primary");
        page1Btn.setStyle("-fx-padding: 4 10; -fx-font-weight: bold;");
        nextPageBtn.setStyle("-fx-padding: 4 10;");

        HBox pagBtns = new HBox(4, prevPageBtn, page1Btn, nextPageBtn);
        pagBtns.setAlignment(Pos.CENTER);

        paginationBar.getChildren().addAll(paginationInfoLabel, pagSpacer, perPageComboBox, pagBtns);

        mainContent.getChildren().addAll(pageHeader, statCardsRow, toolbar, questionTable, paginationBar);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8fafc;");
        root.setCenter(scrollPane);
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

    public Button getCreateQuestionBtn() {
        return createQuestionBtn;
    }

    public StatCard getTotalQuestionsCard() {
        return totalQuestionsCard;
    }

    public StatCard getEasyQuestionsCard() {
        return easyQuestionsCard;
    }

    public StatCard getMediumQuestionsCard() {
        return mediumQuestionsCard;
    }

    public StatCard getHardQuestionsCard() {
        return hardQuestionsCard;
    }

    public TextField getSearchQuestionsField() {
        return searchQuestionsField;
    }

    public ComboBox<String> getQuizFilterComboBox() {
        return quizFilterComboBox;
    }

    public ComboBox<String> getDifficultyFilterComboBox() {
        return difficultyFilterComboBox;
    }

    public Button getResetFilterBtn() {
        return resetFilterBtn;
    }

    public TableView<Question> getQuestionTable() {
        return questionTable;
    }

    public TableColumn<Question, Integer> getIdColumn() {
        return idColumn;
    }

    public TableColumn<Question, String> getContentColumn() {
        return contentColumn;
    }

    public TableColumn<Question, String> getQuizNameColumn() {
        return quizNameColumn;
    }

    public TableColumn<Question, String> getDifficultyColumn() {
        return difficultyColumn;
    }

    public TableColumn<Question, Question> getActionsColumn() {
        return actionsColumn;
    }

    public Label getPaginationInfoLabel() {
        return paginationInfoLabel;
    }

    public ComboBox<String> getPerPageComboBox() {
        return perPageComboBox;
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
