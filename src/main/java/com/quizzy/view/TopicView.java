package com.quizzy.view;

import com.quizzy.model.Topic;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SessionManager;
import com.quizzy.view.component.StatCard;
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

public class TopicView {

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
    private final Button createTopicBtn = new Button("+ Create Topic");

    // Stat Cards
    private final StatCard totalTopicsCard = new StatCard("📁", "Total Topics", "0", "All topics in system", "#EEF2FF", "#4F46E5");
    private final StatCard totalQuizzesCard = new StatCard("📝", "Total Quizzes", "0", "Across all topics", "#DCFCE7", "#16A34A");
    private final StatCard totalQuestionsCard = new StatCard("❓", "Total Questions", "0", "Across all topics", "#FEF3C7", "#D97706");
    private final StatCard activeTopicsCard = new StatCard("👥", "Active Topics", "0", "Active topics", "#E0F2FE", "#0284C7");

    // Toolbar Components
    private final TextField searchTopicsField = new TextField();
    private final ComboBox<String> statusFilterComboBox = new ComboBox<>();
    private final ComboBox<String> sortComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

    // TableView Components
    private final TableView<Topic> topicTable = new TableView<>();
    private final TableColumn<Topic, Integer> idColumn = new TableColumn<>("#");
    private final TableColumn<Topic, String> nameColumn = new TableColumn<>("TOPIC NAME");
    private final TableColumn<Topic, String> descriptionColumn = new TableColumn<>("DESCRIPTION");
    private final TableColumn<Topic, Topic> actionsColumn = new TableColumn<>("ACTIONS");

    // Pagination Controls
    private final Label paginationInfoLabel = new Label("Showing 1 to 0 of 0 topics");
    private final ComboBox<String> perPageComboBox = new ComboBox<>();
    private final Button prevPageBtn = new Button("<");
    private final Button page1Btn = new Button("1");
    private final Button nextPageBtn = new Button(">");

    public TopicView() {
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
        NavIconHelper.setupNavButton(topicBtn, "Topics", "topic_icon.png", true);
        NavIconHelper.setupNavButton(quizBtn, "Quizzes", "quiz_icon.png", false);
        NavIconHelper.setupNavButton(questionBtn, "Questions", "question_icon.png", false);
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

        // Main Content Area (Full height from top)
        VBox mainContent = new VBox(12);
        mainContent.setPadding(new Insets(14, 24, 14, 24));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        // Page Header
        HBox pageHeader = new HBox(16);
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleCol = new VBox(2);
        Label titleL = new Label("Topic Management");
        titleL.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Manage quiz topics and organize your question bank.");
        subtitleL.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        createTopicBtn.getStyleClass().add("button-primary");
        createTopicBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");

        pageHeader.getChildren().addAll(titleCol, headerSpacer, createTopicBtn);

        // 4 Stat Cards Row (Expanding 100% Width)
        HBox statCardsRow = new HBox(12);
        HBox.setHgrow(totalTopicsCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(totalQuizzesCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(totalQuestionsCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(activeTopicsCard.getRoot(), Priority.ALWAYS);

        statCardsRow.getChildren().addAll(
                totalTopicsCard.getRoot(),
                totalQuizzesCard.getRoot(),
                totalQuestionsCard.getRoot(),
                activeTopicsCard.getRoot()
        );

        // Search & Filter Toolbar Card
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(8, 14, 8, 14));

        searchTopicsField.setPromptText("Search topics...");
        searchTopicsField.setPrefWidth(240);
        searchTopicsField.setPrefHeight(32);

        statusFilterComboBox.setPromptText("All Status");
        statusFilterComboBox.getItems().setAll("All Status", "Active", "Inactive");
        statusFilterComboBox.setValue("All Status");
        statusFilterComboBox.setPrefHeight(32);

        sortComboBox.setPromptText("Sort by: Newest");
        sortComboBox.getItems().setAll("Sort by: Newest", "Sort by: Name A-Z", "Sort by: ID");
        sortComboBox.setValue("Sort by: Newest");
        sortComboBox.setPrefHeight(32);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchTopicsField, statusFilterComboBox, sortComboBox, resetFilterBtn, toolbarSpacer);

        // Full Width Data TableView (All data columns Left Aligned, ACTIONS Center Aligned)
        topicTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.setMaxWidth(60);
        idColumn.setMinWidth(45);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("topicId"));

        nameColumn.setPrefWidth(280);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("topicName"));
        nameColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #4f46e5;");
                }
            }
        });

        descriptionColumn.setPrefWidth(540);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        actionsColumn.setPrefWidth(120);
        actionsColumn.setMaxWidth(130);
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.getStyleClass().add("column-center");

        topicTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn, actionsColumn);
        VBox.setVgrow(topicTable, Priority.ALWAYS);

        // Table Pagination Footer
        HBox paginationBar = new HBox(12);
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(4, 2, 0, 2));

        paginationInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        HBox pagSpacer = new HBox();
        HBox.setHgrow(pagSpacer, Priority.ALWAYS);

        perPageComboBox.getItems().setAll("10 per page", "25 per page", "50 per page");
        perPageComboBox.setValue("10 per page");
        perPageComboBox.setPrefHeight(30);

        prevPageBtn.setStyle("-fx-padding: 3 9;");
        page1Btn.getStyleClass().add("button-primary");
        page1Btn.setStyle("-fx-padding: 3 9; -fx-font-weight: bold;");
        nextPageBtn.setStyle("-fx-padding: 3 9;");

        HBox pagBtns = new HBox(4, prevPageBtn, page1Btn, nextPageBtn);
        pagBtns.setAlignment(Pos.CENTER);

        paginationBar.getChildren().addAll(paginationInfoLabel, pagSpacer, perPageComboBox, pagBtns);

        mainContent.getChildren().addAll(pageHeader, statCardsRow, toolbar, topicTable, paginationBar);
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

    public Button getCreateTopicBtn() {
        return createTopicBtn;
    }

    public StatCard getTotalTopicsCard() {
        return totalTopicsCard;
    }

    public StatCard getTotalQuizzesCard() {
        return totalQuizzesCard;
    }

    public StatCard getTotalQuestionsCard() {
        return totalQuestionsCard;
    }

    public StatCard getActiveTopicsCard() {
        return activeTopicsCard;
    }

    public TextField getSearchTopicsField() {
        return searchTopicsField;
    }

    public ComboBox<String> getStatusFilterComboBox() {
        return statusFilterComboBox;
    }

    public ComboBox<String> getSortComboBox() {
        return sortComboBox;
    }

    public Button getResetFilterBtn() {
        return resetFilterBtn;
    }

    public TableView<Topic> getTopicTable() {
        return topicTable;
    }

    public TableColumn<Topic, Integer> getIdColumn() {
        return idColumn;
    }

    public TableColumn<Topic, String> getNameColumn() {
        return nameColumn;
    }

    public TableColumn<Topic, String> getDescriptionColumn() {
        return descriptionColumn;
    }

    public TableColumn<Topic, Topic> getActionsColumn() {
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
