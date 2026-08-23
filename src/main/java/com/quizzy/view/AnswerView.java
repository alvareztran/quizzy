package com.quizzy.view;

import com.quizzy.model.Answer;
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

public class AnswerView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final Button dashBtn = new Button();
    private final Button topicBtn = new Button();
    private final Button quizBtn = new Button();
    private final Button questionBtn = new Button();
    private final Button answerBtn = new Button();
    private final Button userBtn = new Button();
    private final Button resultBtn = new Button();

    private final StatCard totalAnswersCard = new StatCard("💬", "Total Answers", "0", "In answer bank", "#EEF2FF", "#4F46E5");
    private final StatCard correctAnswersCard = new StatCard("✅", "Correct Answers", "0", "Valid answer keys", "#DCFCE7", "#16A34A");
    private final StatCard incorrectAnswersCard = new StatCard("❌", "Option Distractors", "0", "Incorrect choices", "#FEF3C7", "#D97706");
    private final StatCard questionsCoveredCard = new StatCard("❓", "Questions Covered", "0", "Questions with options", "#E0F2FE", "#0284C7");

    private final TextField searchAnswersField = new TextField();
    private final ComboBox<String> questionFilterComboBox = new ComboBox<>();
    private final ComboBox<String> statusFilterComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

    private final TableView<Answer> answerTable = new TableView<>();
    private final TableColumn<Answer, Integer> idColumn = new TableColumn<>("#");
    private final TableColumn<Answer, String> questionContentColumn = new TableColumn<>("QUESTION");
    private final TableColumn<Answer, String> answerContentColumn = new TableColumn<>("ANSWER CONTENT");
    private final TableColumn<Answer, Boolean> statusColumn = new TableColumn<>("IS CORRECT?");
    private final TableColumn<Answer, Answer> actionsColumn = new TableColumn<>("ACTIONS");

    // Pagination Controls
    private final Label paginationInfoLabel = new Label("Showing 1 to 0 of 0 answers");
    private final ComboBox<String> perPageComboBox = new ComboBox<>();
    private final Button prevPageBtn = new Button("<");
    private final Button page1Btn = new Button("1");
    private final Button nextPageBtn = new Button(">");
    private final HBox paginationButtonsBox = new HBox(4);

    public AnswerView() {
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
        NavIconHelper.setupNavButton(questionBtn, "Questions", "question_icon.png", false);
        NavIconHelper.setupNavButton(answerBtn, "Answers", "answer_icon.png", true);
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
        Label titleL = new Label("Answer Bank Management");
        titleL.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Manage answer options, distractors, and correct answer keys.");
        subtitleL.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        pageHeader.getChildren().add(titleCol);

        HBox statCardsRow = new HBox(12);
        HBox.setHgrow(totalAnswersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(correctAnswersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(incorrectAnswersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(questionsCoveredCard.getRoot(), Priority.ALWAYS);

        statCardsRow.getChildren().addAll(
                totalAnswersCard.getRoot(),
                correctAnswersCard.getRoot(),
                incorrectAnswersCard.getRoot(),
                questionsCoveredCard.getRoot()
        );

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(8, 14, 8, 14));

        searchAnswersField.setPromptText("Search answers...");
        searchAnswersField.setPrefWidth(240);
        searchAnswersField.setPrefHeight(32);

        questionFilterComboBox.setPromptText("All Questions");
        questionFilterComboBox.setPrefWidth(300);
        questionFilterComboBox.setPrefHeight(32);

        statusFilterComboBox.setPromptText("All Correct Status");
        statusFilterComboBox.getItems().setAll("All Correct Status", "Correct Only", "Incorrect Only");
        statusFilterComboBox.setValue("All Correct Status");
        statusFilterComboBox.setPrefWidth(150);
        statusFilterComboBox.setPrefHeight(32);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchAnswersField, questionFilterComboBox, statusFilterComboBox, resetFilterBtn, toolbarSpacer);

        answerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        idColumn.setMaxWidth(60);
        idColumn.setMinWidth(45);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("answerId"));

        questionContentColumn.setPrefWidth(480);

        answerContentColumn.setPrefWidth(220);
        answerContentColumn.setCellValueFactory(new PropertyValueFactory<>("answerContent"));
        answerContentColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");
                }
            }
        });

        statusColumn.setPrefWidth(140);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("isCorrect"));
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean isCorrect, boolean empty) {
                super.updateItem(isCorrect, empty);
                if (empty || isCorrect == null) {
                    setGraphic(null);
                } else {
                    if (isCorrect) {
                        setGraphic(StatusBadge.createCustomBadge("● Correct", "#dcfce7", "#15803d"));
                    } else {
                        setGraphic(StatusBadge.createCustomBadge("○ Incorrect", "#f1f5f9", "#64748b"));
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        actionsColumn.setPrefWidth(120);
        actionsColumn.setMaxWidth(130);
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.getStyleClass().add("column-center");

        answerTable.getColumns().addAll(
                idColumn, questionContentColumn, answerContentColumn,
                statusColumn, actionsColumn
        );
        VBox.setVgrow(answerTable, Priority.ALWAYS);

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

        mainContent.getChildren().addAll(pageHeader, statCardsRow, toolbar, answerTable, paginationBar);
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

    public StatCard getTotalAnswersCard() {
        return totalAnswersCard;
    }

    public StatCard getCorrectAnswersCard() {
        return correctAnswersCard;
    }

    public StatCard getIncorrectAnswersCard() {
        return incorrectAnswersCard;
    }

    public StatCard getQuestionsCoveredCard() {
        return questionsCoveredCard;
    }

    public TextField getSearchAnswersField() {
        return searchAnswersField;
    }

    public ComboBox<String> getQuestionFilterComboBox() {
        return questionFilterComboBox;
    }

    public ComboBox<String> getStatusFilterComboBox() {
        return statusFilterComboBox;
    }

    public Button getResetFilterBtn() {
        return resetFilterBtn;
    }

    public TableView<Answer> getAnswerTable() {
        return answerTable;
    }

    public TableColumn<Answer, Integer> getIdColumn() {
        return idColumn;
    }

    public TableColumn<Answer, String> getQuestionContentColumn() {
        return questionContentColumn;
    }

    public TableColumn<Answer, String> getAnswerContentColumn() {
        return answerContentColumn;
    }

    public TableColumn<Answer, Boolean> getStatusColumn() {
        return statusColumn;
    }

    public TableColumn<Answer, Answer> getActionsColumn() {
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
