package com.quizzy.view;

import com.quizzy.model.User;
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

public class UserView {

    private final BorderPane root = new BorderPane();
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final Button dashBtn = new Button();
    private final Button topicBtn = new Button();
    private final Button quizBtn = new Button();
    private final Button questionBtn = new Button();
    private final Button answerBtn = new Button();
    private final Button userBtn = new Button();
    private final Button resultBtn = new Button();

    private final Button createUserBtn = new Button("+ Create User");

    private final StatCard totalUsersCard = new StatCard("👥", "Total Users", "0", "All system accounts", "#E0F2FE", "#0284C7");
    private final StatCard adminUsersCard = new StatCard("🛡️", "Admins", "0", "System administrators", "#EEF2FF", "#4F46E5");
    private final StatCard playerUsersCard = new StatCard("🎯", "Players", "0", "Registered quiz takers", "#DCFCE7", "#16A34A");
    private final StatCard activeUsersCard = new StatCard("🟢", "Active Accounts", "0", "Verified users", "#FEF3C7", "#D97706");

    private final TextField searchUsersField = new TextField();
    private final ComboBox<String> roleFilterComboBox = new ComboBox<>();
    private final ComboBox<String> sortComboBox = new ComboBox<>();
    private final Button resetFilterBtn = new Button("🔄  Reset");

    private final TableView<User> userTable = new TableView<>();
    private final TableColumn<User, Integer> idColumn = new TableColumn<>("#");
    private final TableColumn<User, String> fullNameColumn = new TableColumn<>("FULL NAME");
    private final TableColumn<User, String> usernameColumn = new TableColumn<>("USERNAME");
    private final TableColumn<User, String> roleColumn = new TableColumn<>("ROLE");
    private final TableColumn<User, User> actionsColumn = new TableColumn<>("ACTIONS");

    private final Label paginationInfoLabel = new Label("Showing 1 to 0 of 0 users");
    private final ComboBox<String> perPageComboBox = new ComboBox<>();
    private final Button prevPageBtn = new Button("<");
    private final Button page1Btn = new Button("1");
    private final Button nextPageBtn = new Button(">");
    private final HBox paginationButtonsBox = new HBox(4);

    public UserView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1280, 800);

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
        NavIconHelper.setupNavButton(answerBtn, "Answers", "answer_icon.png", false);
        NavIconHelper.setupNavButton(userBtn, "Users", "user_icon.png", true);
        NavIconHelper.setupNavButton(resultBtn, "Results", "result_icon.png", false);

        VBox sidebarSpacer = new VBox();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

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
        Label titleL = new Label("User Management");
        titleL.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subtitleL = new Label("Manage user accounts, roles, and administrative access permissions.");
        subtitleL.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        titleCol.getChildren().addAll(titleL, subtitleL);

        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        createUserBtn.getStyleClass().add("button-primary");
        createUserBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8 18; -fx-font-weight: bold;");

        pageHeader.getChildren().addAll(titleCol, headerSpacer, createUserBtn);

        HBox statCardsRow = new HBox(12);
        HBox.setHgrow(totalUsersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(adminUsersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(playerUsersCard.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(activeUsersCard.getRoot(), Priority.ALWAYS);

        statCardsRow.getChildren().addAll(
                totalUsersCard.getRoot(),
                adminUsersCard.getRoot(),
                playerUsersCard.getRoot(),
                activeUsersCard.getRoot()
        );

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("card");
        toolbar.setPadding(new Insets(8, 14, 8, 14));

        searchUsersField.setPromptText("Search users...");
        searchUsersField.setPrefWidth(240);
        searchUsersField.setPrefHeight(32);

        roleFilterComboBox.setPromptText("All Roles");
        roleFilterComboBox.getItems().setAll("All Roles", "Admin", "Player");
        roleFilterComboBox.setValue("All Roles");
        roleFilterComboBox.setPrefHeight(32);

        sortComboBox.setPromptText("Sort by: ID");
        sortComboBox.getItems().setAll("Sort by: ID", "Sort by: Username", "Sort by: Full Name", "Sort by: Role");
        sortComboBox.setValue("Sort by: ID");
        sortComboBox.setPrefHeight(32);

        resetFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");

        HBox toolbarSpacer = new HBox();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchUsersField, roleFilterComboBox, sortComboBox, resetFilterBtn, toolbarSpacer);

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        idColumn.setId("id-col");
        idColumn.setMaxWidth(60);
        idColumn.setMinWidth(45);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idColumn.getStyleClass().add("column-center");

        fullNameColumn.setPrefWidth(340);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        usernameColumn.setPrefWidth(260);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        usernameColumn.setCellFactory(col -> new TableCell<>() {
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

        roleColumn.setPrefWidth(160);
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setGraphic(null);
                } else {
                    setGraphic(StatusBadge.createRoleBadge(role));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        actionsColumn.setId("actions-col");
        actionsColumn.setPrefWidth(120);
        actionsColumn.setMaxWidth(130);
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.getStyleClass().add("column-center");

        userTable.getColumns().addAll(
                idColumn, fullNameColumn, usernameColumn,
                roleColumn, actionsColumn
        );
        VBox.setVgrow(userTable, Priority.ALWAYS);

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

        mainContent.getChildren().addAll(pageHeader, statCardsRow, toolbar, userTable, paginationBar);
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

    public Button getCreateUserBtn() {
        return createUserBtn;
    }

    public StatCard getTotalUsersCard() {
        return totalUsersCard;
    }

    public StatCard getAdminUsersCard() {
        return adminUsersCard;
    }

    public StatCard getPlayerUsersCard() {
        return playerUsersCard;
    }

    public StatCard getActiveUsersCard() {
        return activeUsersCard;
    }

    public TextField getSearchUsersField() {
        return searchUsersField;
    }

    public ComboBox<String> getRoleFilterComboBox() {
        return roleFilterComboBox;
    }

    public ComboBox<String> getSortComboBox() {
        return sortComboBox;
    }

    public Button getResetFilterBtn() {
        return resetFilterBtn;
    }

    public TableView<User> getUserTable() {
        return userTable;
    }

    public TableColumn<User, Integer> getIdColumn() {
        return idColumn;
    }

    public TableColumn<User, String> getUsernameColumn() {
        return usernameColumn;
    }

    public TableColumn<User, String> getFullNameColumn() {
        return fullNameColumn;
    }

    public TableColumn<User, String> getRoleColumn() {
        return roleColumn;
    }

    public TableColumn<User, User> getActionsColumn() {
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
