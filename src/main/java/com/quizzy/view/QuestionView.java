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

    private final Button dashBtn = new Button();
    private final Button topicBtn = new Button();
    private final Button quizBtn = new Button();
    private final Button questionBtn = new Button();
    private final Button answerBtn = new Button();
    private final Button userBtn = new Button();
    private final Button resultBtn = new Button();

    private final Button createQuestionBtn = new Button("+ Add Question");

    private final StatCard totalQuestionsCard = new StatCard("❓", "Total Questions", "0", "In question bank", "#FEF3C7", "#D97706");
    private final StatCard easyQuestionsCard = new StatCard("🟢", "Easy Level", "0", "Basic questions", "#DCFCE7", "#16A34A");
    private final StatCard mediumQuestionsCard = new StatCard("🟡", "Medium Level", "0", "Intermediate items", "#FEF3C7", "#D97706");
    private final StatCard hardQuestionsCard = new StatCard("🔴", "Hard Level", "0", "Advanced questions", "#FEE2E2", "#DC2626");

    private final TextField searchQuestionsField = new TextField();
    private final ComboBox<String> quizFilterComboBox = new ComboBox<>();
    private final ComboBox<String> difficultyFilterComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

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
    private final HBox paginationButtonsBox = new HBox(4);

    public QuestionView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1280, 800);

        // Sidebar Navigation (Full Height)
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20, 16, 16, 16));

        ImageView iconView = new ImageView();
        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            iconView.setImage(iconImg);
            iconView.setFitHeight(30);
            iconView.setFitWidth(30);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
        } catch (Exception e) {
            // Fallback
        }

        Label brandTitle = new Label("QUIZZY");
        brandTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #191c1e; -fx-letter-spacing: 0.5px;");

        HBox logoContainer = new HBox(10, iconView, brandTitle);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(4, 8, 22, 8));

        Label mgmtHeader = new Label("MANAGEMENT");
        mgmtHeader.setStyle("-fx-text-fill: #767586; -fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 4 0 4 12; -fx-letter-spacing: 0.8px;");

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
        profileBox.setPadding(new Insets(14, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #c7c4d7; -fx-border-width: 1 0 0 0;");
        profileBox.getChildren().add(userProfileWidget.getRoot());

        sidebar.getChildren().addAll(
                logoContainer,
                mgmtHeader,
                dashBtn,
                topicBtn, quizBtn, questionBtn, answerBtn, userBtn, resultBtn,
                sidebarSpacer,
                profileBox
        );
        root.setLeft(sidebar);

        VBox mainContent = new VBox(12);
        mainContent.setPadding(new Insets(14, 24, 14, 24));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        HBox pageHeader = new HBox(16);
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleCol = new VBox(2);
        Label titleL = new Label("Question Bank Management");
        titleL.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Manage question bank items, difficulty ratings, and quiz assignments.");
        subtitleL.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        createQuestionBtn.getStyleClass().add("button-primary");
        createQuestionBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");

        pageHeader.getChildren().addAll(titleCol, headerSpacer, createQuestionBtn);

        HBox statCardsRow = new HBox(12);
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

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(8, 14, 8, 14));

        searchQuestionsField.setPromptText("Search questions...");
        searchQuestionsField.setPrefWidth(240);
        searchQuestionsField.setPrefHeight(32);

        quizFilterComboBox.setPromptText("All Quizzes");
        quizFilterComboBox.setPrefHeight(32);

        difficultyFilterComboBox.setPromptText("All Difficulty");
        difficultyFilterComboBox.getItems().setAll("All Difficulty", "Easy", "Medium", "Hard");
        difficultyFilterComboBox.setValue("All Difficulty");
        difficultyFilterComboBox.setPrefHeight(32);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchQuestionsField, quizFilterComboBox, difficultyFilterComboBox, resetFilterBtn, toolbarSpacer);

        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

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

        HBox paginationBar = new HBox(12);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(4, 2, 0, 2));

        paginationInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox pagSpacer = new HBox();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        perPageComboBox.getItems().setAll("10 per page", "25 per page", "50 per page");
        perPageComboBox.setValue("10 per page");
        perPageComboBox.setPrefHeight(30);

        paginationButtonsBox.setAlignment(Pos.CENTER);
        paginationButtonsBox.getChildren().setAll(prevPageBtn, page1Btn, nextPageBtn);

        paginationBar.getChildren().addAll(paginationInfoLabel, pagSpacer, perPageComboBox, paginationButtonsBox);

        mainContent.getChildren().addAll(pageHeader, statCardsRow, toolbar, questionTable, paginationBar);
        root.setCenter(mainContent);
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

    public HBox getPaginationButtonsBox() {
        return paginationButtonsBox;
    }

}
