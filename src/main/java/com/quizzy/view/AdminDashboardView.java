package com.quizzy.view;

import com.quizzy.model.Topic;
import com.quizzy.view.component.StatCard;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class AdminDashboardView {

    private final ScrollPane root = new ScrollPane();
    private final VBox contentBox = new VBox(20);

    private final Label greetingPrefixLabel = new Label("Welcome back, ");
    private final Label greetingUserLabel = new Label("Administrator");
    private final Label greetingSubLabel = new Label("Here's what's happening with your quizzes today.");
    private final Label dateBadgeLabel = new Label();

    private final Button createNewQuizBtn = new Button("+  Create New Quiz");

    private final StatCard totalTopicsCard = new StatCard("F", "Total Topics", "0", "All topics in system", "#e1e0ff", "#4648d4");
    private final StatCard totalQuizzesCard = new StatCard("Q", "Total Quizzes", "0", "All quiz assessments", "#dcfce7", "#166534");
    private final StatCard totalQuestionsCard = new StatCard("?", "Total Questions", "0", "Across all quizzes", "#fef08a", "#854d0e");
    private final StatCard totalUsersCard = new StatCard("U", "Total Users", "0", "Registered accounts", "#e0f2fe", "#075985");

    private final Button viewAllActivitiesBtn = new Button("View all");
    private final VBox activitiesContainer = new VBox(12);

    private final Button viewAllTopicsBtn = new Button("View all");
    private final VBox topTopicsContainer = new VBox(12);

    public AdminDashboardView() {
        createUI();
    }

    private void createUI() {
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fb;");

        contentBox.setPadding(new Insets(26, 32, 26, 32));
        contentBox.setAlignment(Pos.TOP_LEFT);

        HBox header = new HBox(16);
        header.setAlignment(Pos.BOTTOM_LEFT);

        VBox greetingText = new VBox(3);
        HBox greetingLine = new HBox(4);
        greetingLine.setAlignment(Pos.CENTER_LEFT);
        greetingPrefixLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #191c1e;");
        greetingUserLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #4648d4;");
        greetingLine.getChildren().addAll(greetingPrefixLabel, greetingUserLabel);
        greetingSubLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #464554;");
        greetingText.getChildren().addAll(greetingLine, greetingSubLabel);

        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        String currentDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        dateBadgeLabel.setText(currentDateStr);
        dateBadgeLabel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c7c4d7; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #191c1e;");
        header.getChildren().addAll(greetingText, headerSpacer, dateBadgeLabel);

        StackPane heroFrame = new StackPane();
        heroFrame.setMinHeight(138);
        heroFrame.setPrefHeight(138);
        heroFrame.setMaxWidth(Double.MAX_VALUE);
        heroFrame.setStyle("-fx-background-color: #ffffff; -fx-border-color: #4648d4 #c7c4d7 #c7c4d7 #4648d4; -fx-border-width: 0 1 1 4; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.05), 8, 0, 0, 2);");

        HBox heroContent = new HBox(20);
        heroContent.setAlignment(Pos.CENTER_LEFT);
        heroContent.setPadding(new Insets(22, 28, 22, 28));

        VBox heroText = new VBox(8);
        heroText.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        HBox heroTitle = new HBox(6);
        heroTitle.setAlignment(Pos.CENTER_LEFT);
        Label welcomePrefix = new Label("Welcome to ");
        welcomePrefix.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #191c1e;");
        Label welcomeBrand = new Label("Quizzy");
        welcomeBrand.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #4648d4;");
        heroTitle.getChildren().addAll(welcomePrefix, welcomeBrand);

        Label heroSub = new Label("Create, manage and take quizzes in a smarter way.");
        heroSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #464554;");

        createNewQuizBtn.getStyleClass().add("button-primary");
        createNewQuizBtn.setStyle("-fx-font-size: 13px; -fx-padding: 9 20; -fx-font-weight: 700; -fx-background-color: #4648d4;");
        VBox.setMargin(createNewQuizBtn, new Insets(6, 0, 0, 0));
        heroText.getChildren().addAll(heroTitle, heroSub, createNewQuizBtn);

        heroContent.getChildren().addAll(heroText, createHeroGraphic());
        heroFrame.getChildren().add(heroContent);

        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
                totalTopicsCard.getRoot(),
                totalQuizzesCard.getRoot(),
                totalQuestionsCard.getRoot(),
                totalUsersCard.getRoot()
        );
        for (StatCard card : List.of(totalTopicsCard, totalQuizzesCard, totalQuestionsCard, totalUsersCard)) {
            HBox.setHgrow(card.getRoot(), Priority.ALWAYS);
        }

        HBox bottomGrid = new HBox(18);
        bottomGrid.setAlignment(Pos.TOP_LEFT);

        VBox recentActivitiesCard = createPanel("Recent Activities", viewAllActivitiesBtn);
        recentActivitiesCard.getChildren().add(activitiesContainer);

        VBox topTopicsCard = createPanel("Top Topics", viewAllTopicsBtn);
        topTopicsCard.getChildren().add(topTopicsContainer);

        bottomGrid.getChildren().addAll(recentActivitiesCard, topTopicsCard);
        HBox.setHgrow(recentActivitiesCard, Priority.ALWAYS);
        HBox.setHgrow(topTopicsCard, Priority.ALWAYS);

        contentBox.getChildren().addAll(header, heroFrame, statsRow, bottomGrid);
        root.setContent(contentBox);
    }

    private StackPane createHeroGraphic() {
        StackPane pane = new StackPane();
        pane.setPrefWidth(340);
        pane.setMinWidth(260);
        pane.setMaxWidth(Region.USE_PREF_SIZE);
        pane.setAlignment(Pos.CENTER_RIGHT);

        ImageView imageView = new ImageView();
        Image image = loadImage("/com/quizzy/images/admin-dashboard-hero.png");
        if (image == null) {
            image = loadImage("/com/quizzy/images/hero.png");
        }

        if (image != null) {
            imageView.setImage(image);
            imageView.setViewport(new Rectangle2D(80, 270, 864, 360));
            imageView.setFitWidth(320);
            imageView.setFitHeight(134);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Rectangle clip = new Rectangle(320, 134);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            imageView.setClip(clip);
            pane.getChildren().add(imageView);
        }
        return pane;
    }

    private Image loadImage(String resourcePath) {
        try {
            if (getClass().getResource(resourcePath) == null) {
                return null;
            }
            return new Image(getClass().getResourceAsStream(resourcePath));
        } catch (Exception e) {
            return null;
        }
    }

    private VBox createPanel(String title, Button actionButton) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("card");
        panel.setPadding(new Insets(18, 22, 18, 22));
        panel.setMinHeight(235);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #191c1e;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actionButton.setStyle("-fx-background-color: #e1e0ff; -fx-text-fill: #4648d4; -fx-font-weight: 700; -fx-font-size: 12px; -fx-padding: 3 12; -fx-background-radius: 999; -fx-border-color: transparent; -fx-cursor: hand;");
        header.getChildren().addAll(titleLabel, spacer, actionButton);
        panel.getChildren().add(header);
        return panel;
    }

    public void renderTopTopics(List<Topic> topics, Map<Integer, Integer> questionCounts) {
        topTopicsContainer.getChildren().clear();

        if (topics == null || topics.isEmpty()) {
            Label emptyLabel = new Label("No topics found in question bank.");
            emptyLabel.setStyle("-fx-text-fill: #767586; -fx-font-size: 12px; -fx-padding: 10 0;");
            topTopicsContainer.getChildren().add(emptyLabel);
            return;
        }

        int maxQ = 1;
        for (Topic topic : topics) {
            int qCount = questionCounts.getOrDefault(topic.getTopicId(), 0);
            if (qCount > maxQ) {
                maxQ = qCount;
            }
        }

        int count = 0;
        for (Topic topic : topics) {
            if (count >= 5) {
                break;
            }

            int qCount = questionCounts.getOrDefault(topic.getTopicId(), 0);
            double ratio = (double) qCount / maxQ;
            if (ratio < 0.12) {
                ratio = 0.12;
            }

            topTopicsContainer.getChildren().add(createTopicProgressRow(topic.getTopicName(), qCount, ratio));
            count++;
        }
    }

    private VBox createTopicProgressRow(String topicName, int questionsCount, double ratio) {
        VBox row = new VBox(5);

        HBox labelLine = new HBox(12);
        labelLine.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(topicName);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #191c1e;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label(questionsCount + " questions");
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #767586; -fx-font-weight: 500;");
        labelLine.getChildren().addAll(nameLabel, spacer, countLabel);

        StackPane trackPane = new StackPane();
        trackPane.setPrefHeight(7);
        trackPane.setMinHeight(7);
        trackPane.setMaxHeight(7);
        trackPane.setMaxWidth(Double.MAX_VALUE);
        trackPane.setStyle("-fx-background-color: #e7e8ea; -fx-background-radius: 999;");

        HBox fillBar = new HBox();
        fillBar.setPrefHeight(7);
        fillBar.setMinHeight(7);
        fillBar.setMaxHeight(7);
        fillBar.setStyle("-fx-background-color: #4648d4; -fx-background-radius: 999;");
        fillBar.setMaxWidth(Region.USE_PREF_SIZE);
        fillBar.prefWidthProperty().bind(trackPane.widthProperty().multiply(ratio));
        StackPane.setAlignment(fillBar, Pos.CENTER_LEFT);

        trackPane.getChildren().add(fillBar);
        row.getChildren().addAll(labelLine, trackPane);
        return row;
    }

    public void renderRecentActivities(List<ActivityItemData> activities) {
        activitiesContainer.getChildren().clear();

        if (activities == null || activities.isEmpty()) {
            activitiesContainer.getChildren().add(createActivityItem("+", "Topic Management initialized", "System Ready", "Just now", "#e1e0ff", "#4648d4"));
            activitiesContainer.getChildren().add(createActivityItem("Q", "Quiz Engine active", "System Ready", "Just now", "#dcfce7", "#166534"));
            activitiesContainer.getChildren().add(createActivityItem("U", "Admin Session verified", "Active user session", "Just now", "#e0f2fe", "#075985"));
            return;
        }

        for (ActivityItemData activity : activities) {
            activitiesContainer.getChildren().add(createActivityItem(activity.icon, activity.title, activity.sub, activity.time, activity.bgHex, activity.iconHex));
        }
    }

    public HBox createActivityItem(String iconText, String titleText, String subText, String timeText, String bgHex, String iconHex) {
        HBox item = new HBox(14);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(2, 0, 2, 0));

        StackPane iconPane = new StackPane();
        Circle circle = new Circle(17);
        circle.setFill(Color.web(bgHex));

        Label iconLabel = new Label(iconText);
        iconLabel.setStyle(String.format("-fx-font-size: 13px; -fx-text-fill: %s; -fx-font-weight: 800;", iconHex));
        iconPane.getChildren().addAll(circle, iconLabel);

        VBox textCol = new VBox(2);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #191c1e;");

        Label subLabel = new Label(subText);
        subLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #464554;");
        textCol.getChildren().addAll(titleLabel, subLabel);

        Label timeLabel = new Label(timeText);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #767586;");

        item.getChildren().addAll(iconPane, textCol, timeLabel);
        return item;
    }

    public static class ActivityItemData {
        public String icon, title, sub, time, bgHex, iconHex;

        public ActivityItemData(String icon, String title, String sub, String time, String bgHex, String iconHex) {
            this.icon = icon;
            this.title = title;
            this.sub = sub;
            this.time = time;
            this.bgHex = bgHex;
            this.iconHex = iconHex;
        }
    }

    public ScrollPane getRoot() {
        return root;
    }

    public Label getGreetingUserLabel() {
        return greetingUserLabel;
    }

    public Button getCreateNewQuizBtn() {
        return createNewQuizBtn;
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

    public StatCard getTotalUsersCard() {
        return totalUsersCard;
    }

    public Button getViewAllActivitiesBtn() {
        return viewAllActivitiesBtn;
    }

    public Button getViewAllTopicsBtn() {
        return viewAllTopicsBtn;
    }

    public VBox getTopTopicsContainer() {
        return topTopicsContainer;
    }

    public VBox getActivitiesContainer() {
        return activitiesContainer;
    }
}
