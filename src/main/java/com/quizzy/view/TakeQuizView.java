package com.quizzy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TakeQuizView {

    private final BorderPane root = new BorderPane();
    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Label quizNameLabel = new Label("Computer Science Basic");
    private final Label questionNumberLabel = new Label("Question 1 / 10");
    private final Label timerLabel = new Label("⏱ 00:00");
    private final Button backBtn = new Button("Exit Quiz");

    private final Label questionContentLabel = new Label();
    private final Label questionInstructionLabel = new Label("Select the most accurate description from the options below.");
    private final VBox answerBox = new VBox(14);

    private final Button previousButton = new Button("← Previous");
    private final Button nextButton = new Button("Next →");
    private final HBox stepperBox = new HBox(8);

    public TakeQuizView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1140, 720);

        // ==========================================
        // 1. TOP HEADER BAR (Matching media_1787420874972.png)
        // ==========================================
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");
        topBar.setPadding(new Insets(14, 36, 14, 36));

        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            logoImageView.setImage(iconImg);
            logoImageView.setFitHeight(28);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
        } catch (Exception e) {
            // Fallback
        }

        brandNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #6366f1; -fx-letter-spacing: 1px;");
        Label dividerLabel = new Label("|");
        dividerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #cbd5e1;");

        quizNameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        HBox brandHeader = new HBox(12, logoImageView, brandNameLabel, dividerLabel, quizNameLabel);
        brandHeader.setAlignment(Pos.CENTER_LEFT);

        HBox spacerTop = new HBox();
        HBox.setHgrow(spacerTop, Priority.ALWAYS);

        questionNumberLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        timerLabel.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 20px;");

        backBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 16; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        topBar.getChildren().addAll(brandHeader, spacerTop, questionNumberLabel, timerLabel, backBtn);
        root.setTop(topBar);

        // ==========================================
        // 2. CENTER EXAM CANVAS & QUESTION CARD
        // ==========================================
        VBox centerBox = new VBox(22);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(36, 40, 36, 40));
        centerBox.setStyle("-fx-background-color: #f8f9fb;");

        // Question Statement Card Container (820px Width)
        VBox statementCard = new VBox(10);
        statementCard.setMaxWidth(820);
        statementCard.getStyleClass().add("card");
        statementCard.setPadding(new Insets(32, 36, 32, 36));
        statementCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.04), 12, 0, 0, 3);");

        questionContentLabel.setWrapText(true);
        questionContentLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #191c1e; -fx-line-spacing: 4px;");

        questionInstructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        statementCard.getChildren().addAll(questionContentLabel, questionInstructionLabel);

        // Options Vertical Box Container (820px Width)
        answerBox.setMaxWidth(820);
        answerBox.setAlignment(Pos.TOP_LEFT);

        centerBox.getChildren().addAll(statementCard, answerBox);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fb;");
        root.setCenter(scrollPane);

        // ==========================================
        // 3. BOTTOM EXAM CONTROL & STEPPER BAR
        // ==========================================
        HBox bottomBar = new HBox(20);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(18, 52, 18, 52));
        bottomBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1px 0 0 0;");

        previousButton.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9 20; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        nextButton.getStyleClass().add("button-primary");
        nextButton.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 9 24; -fx-font-weight: bold; -fx-background-radius: 8px;");

        HBox btnGroup = new HBox(12, previousButton, nextButton);
        btnGroup.setAlignment(Pos.CENTER_LEFT);

        HBox bottomSpacer = new HBox();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        stepperBox.setAlignment(Pos.CENTER_RIGHT);

        bottomBar.getChildren().addAll(btnGroup, bottomSpacer, stepperBox);
        root.setBottom(bottomBar);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Label getQuizNameLabel() {
        return quizNameLabel;
    }

    public Label getQuestionNumberLabel() {
        return questionNumberLabel;
    }

    public Label getTimerLabel() {
        return timerLabel;
    }

    public Button getBackBtn() {
        return backBtn;
    }

    public Label getQuestionContentLabel() {
        return questionContentLabel;
    }

    public Label getQuestionInstructionLabel() {
        return questionInstructionLabel;
    }

    public VBox getAnswerBox() {
        return answerBox;
    }

    public Button getPreviousButton() {
        return previousButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public HBox getStepperBox() {
        return stepperBox;
    }

}
