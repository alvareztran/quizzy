package com.quizzy.view.component;

import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AdminSidebar {

    public enum NavItem {
        DASHBOARD, TOPIC, QUIZ, QUESTION, ANSWER, USER, RESULT
    }

    private final VBox root = new VBox(8);
    private final UserProfileWidget userProfileWidget = new UserProfileWidget(SessionManager.getCurrentUser());

    private final Button dashBtn = new Button();
    private final Button topicBtn = new Button();
    private final Button quizBtn = new Button();
    private final Button questionBtn = new Button();
    private final Button answerBtn = new Button();
    private final Button userBtn = new Button();
    private final Button resultBtn = new Button();

    public AdminSidebar(NavItem activeItem) {
        createUI(activeItem);
    }

    private void createUI(NavItem activeItem) {
        root.setPrefWidth(260);
        root.setMinWidth(260);
        root.setMaxWidth(260);
        root.getStyleClass().add("sidebar");
        root.setPadding(new Insets(20, 16, 16, 16));

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

        Label managementHeader = new Label("MANAGEMENT");
        managementHeader.setStyle("-fx-text-fill: #767586; -fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 4 0 4 12; -fx-letter-spacing: 0.8px;");

        NavIconHelper.setupNavButton(dashBtn, "Dashboard", "dashboard.png", activeItem == NavItem.DASHBOARD);
        NavIconHelper.setupNavButton(topicBtn, "Topics", "topic_icon.png", activeItem == NavItem.TOPIC);
        NavIconHelper.setupNavButton(quizBtn, "Quizzes", "quiz_icon.png", activeItem == NavItem.QUIZ);
        NavIconHelper.setupNavButton(questionBtn, "Questions", "question_icon.png", activeItem == NavItem.QUESTION);
        NavIconHelper.setupNavButton(answerBtn, "Answers", "answer_icon.png", activeItem == NavItem.ANSWER);
        NavIconHelper.setupNavButton(userBtn, "Users", "user_icon.png", activeItem == NavItem.USER);
        NavIconHelper.setupNavButton(resultBtn, "Results", "result_icon.png", activeItem == NavItem.RESULT);

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(14, 0, 0, 0));
        profileBox.setStyle("-fx-border-color: #c7c4d7; -fx-border-width: 1 0 0 0;");
        profileBox.getChildren().add(userProfileWidget.getRoot());

        root.getChildren().addAll(
                logoContainer,
                managementHeader,
                dashBtn,
                topicBtn,
                quizBtn,
                questionBtn,
                answerBtn,
                userBtn,
                resultBtn,
                spacer,
                profileBox
        );
    }

    public VBox getRoot() {
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
}
