package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.model.User;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.PaginationButtonRenderer;
import com.quizzy.util.Paginator;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.QuizHistoryView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class QuizHistoryController {

    private final QuizHistoryView view;
    private final ResultService resultService = ServiceFactory.getResultService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final TopicService topicService = ServiceFactory.getTopicService();

    private final ObservableList<Topic> topicList = FXCollections.observableArrayList();
    private final List<Topic> allTopicsList = new ArrayList<>();
    private final List<HistoryItemDTO> allHistoryItems = new ArrayList<>();
    private final Map<Integer, Topic> topicMap = new HashMap<>();
    private final Map<Integer, Quiz> quizMap = new HashMap<>();

    private final Paginator<HistoryItemDTO> paginator = new Paginator<>(5);

    public QuizHistoryController() {
        this.view = new QuizHistoryView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public QuizHistoryView getQuizHistoryView() {
        return view;
    }

    private void initEventHandlers() {
        view.getNavTopicsBtn().setOnAction(e -> SceneManager.showSelectQuiz());
        view.getNavHistoryBtn().setOnAction(e -> SceneManager.showHistory());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getLogoImageView().setOnMouseClicked(e -> SceneManager.showHome());
        view.getBrandNameLabel().setOnMouseClicked(e -> SceneManager.showHome());

        view.getSearchTopicField().textProperty().addListener((obs, oldVal, query) -> filterSidebarTopics(query));

        view.getTopicFilterComboBox().setOnAction(e -> applyFilter());
        view.getDateFilterComboBox().setOnAction(e -> applyFilter());

        view.getTopicListView().getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selectedTopic) -> {
                    if (selectedTopic != null) {
                        view.getTopicFilterComboBox().setValue(selectedTopic.getTopicName());
                    }
                }
        );
    }

    private void initializeData() {

        view.getTopicListView().setItems(topicList);
        view.getTopicListView().setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Topic topic, boolean empty) {
                super.updateItem(topic, empty);
                if (empty || topic == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean isSel = isSelected();
                    HBox box = new HBox();
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setPadding(new Insets(10, 14, 10, 14));

                    Label nameL = new Label(topic.getTopicName());
                    nameL.setWrapText(true);
                    nameL.prefWidthProperty().bind(listView.widthProperty().subtract(36));
                    HBox.setHgrow(nameL, Priority.ALWAYS);
                    box.setMaxWidth(Double.MAX_VALUE);

                    if (isSel) {
                        box.setStyle("-fx-background-color: #eef2ff; -fx-border-color: #4f46e5; -fx-border-width: 0 0 0 3.5px; -fx-border-radius: 0 8 8 0; -fx-background-radius: 0 8 8 0;");
                        nameL.setStyle("-fx-font-weight: 700; -fx-font-size: 14px; -fx-text-fill: #4f46e5;");
                    } else {
                        box.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-background-radius: 8;");
                        nameL.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-text-fill: #334155;");
                    }

                    box.getChildren().add(nameL);
                    setGraphic(box);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 2 0; -fx-cursor: hand;");
                }
            }
        });

        loadAllData();
    }

    private void loadAllData() {
        try {
            topicMap.clear();
            quizMap.clear();
            allHistoryItems.clear();
            allTopicsList.clear();

            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                allTopicsList.addAll(topics);
                view.getTopicFilterComboBox().getItems().setAll("All Topics");
                for (Topic t : topics) {
                    topicMap.put(t.getTopicId(), t);
                    view.getTopicFilterComboBox().getItems().add(t.getTopicName());
                }
                view.getTopicFilterComboBox().setValue("All Topics");
            }
            filterSidebarTopics(view.getSearchTopicField().getText());

            List<Quiz> quizzes = quizService.getAllQuizzes();
            if (quizzes != null) {
                for (Quiz q : quizzes) {
                    quizMap.put(q.getQuizId(), q);
                }
            }

            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                List<Result> results = resultService.getResultsByUserId(currentUser.getUserId());
                if (results != null && !results.isEmpty()) {
                    for (Result r : results) {
                        Quiz quiz = quizMap.get(r.getQuizId());
                        Topic topic = (quiz != null) ? topicMap.get(quiz.getTopicId()) : null;

                        String quizName = (quiz != null) ? quiz.getQuizName() : "General Practice Quiz";
                        String topicName = (topic != null) ? topic.getTopicName() : "General";

                        allHistoryItems.add(new HistoryItemDTO(r, quizName, topicName));
                    }
                }
            }

            allHistoryItems.sort((a, b) -> {
                LocalDateTime dtA = (a.result != null && a.result.getFinishedAt() != null) ? a.result.getFinishedAt() : LocalDateTime.MIN;
                LocalDateTime dtB = (b.result != null && b.result.getFinishedAt() != null) ? b.result.getFinishedAt() : LocalDateTime.MIN;
                return dtB.compareTo(dtA);
            });

            calculateAndDisplayStats();
            applyFilter();

        } catch (Exception e) {
            calculateAndDisplayStats();
            applyFilter();
        }
    }

    private void calculateAndDisplayStats() {
        int total = allHistoryItems.size();
        view.getTotalQuizzesValLabel().setText(String.valueOf(total));

        if (total == 0) {
            view.getAvgScoreValLabel().setText("0%");
            view.getBestScoreValLabel().setText("0%");
            view.getDaysActiveValLabel().setText("0");
            return;
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        Set<LocalDate> activeDays = new HashSet<>();

        for (HistoryItemDTO item : allHistoryItems) {
            if (item.result != null) {
                if (item.result.getScore() != null) {
                    totalScore = totalScore.add(item.result.getScore());
                    if (item.result.getScore().compareTo(maxScore) > 0) {
                        maxScore = item.result.getScore();
                    }
                }
                if (item.result.getFinishedAt() != null) {
                    activeDays.add(item.result.getFinishedAt().toLocalDate());
                }
            }
        }

        BigDecimal avg = totalScore.divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
        double avgPct = (avg.doubleValue() / 10.0) * 100;
        double maxPct = (maxScore.doubleValue() / 10.0) * 100;

        view.getAvgScoreValLabel().setText(String.format("%.0f%%", avgPct));
        view.getBestScoreValLabel().setText(String.format("%.0f%%", maxPct));
        view.getDaysActiveValLabel().setText(String.valueOf(Math.max(activeDays.size(), 1)));
    }

    private void filterSidebarTopics(String query) {
        String search = (query != null) ? query.trim().toLowerCase() : "";
        List<Topic> filtered = new ArrayList<>();
        for (Topic t : allTopicsList) {
            if (search.isEmpty() || (t.getTopicName() != null && t.getTopicName().toLowerCase().contains(search))) {
                filtered.add(t);
            }
        }
        topicList.setAll(filtered);
    }

    private void applyFilter() {
        String selectedTopicFilter = view.getTopicFilterComboBox().getValue();
        String selectedDateFilter = view.getDateFilterComboBox().getValue();

        List<HistoryItemDTO> matched = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (HistoryItemDTO item : allHistoryItems) {
            boolean matchTopic = (selectedTopicFilter == null || selectedTopicFilter.equals("All Topics") || selectedTopicFilter.equalsIgnoreCase(item.topicName));

            boolean matchDate = true;
            if (item.result != null && item.result.getFinishedAt() != null && selectedDateFilter != null && !selectedDateFilter.equals("All Time")) {
                LocalDate itemDate = item.result.getFinishedAt().toLocalDate();
                switch (selectedDateFilter) {
                    case "Today" -> matchDate = itemDate.isEqual(now);
                    case "This Week" -> matchDate = !itemDate.isBefore(now.minusDays(7));
                    case "This Month" -> matchDate = !itemDate.isBefore(now.minusMonths(1));
                    case "This Year" -> matchDate = (itemDate.getYear() == now.getYear());
                }
            }

            if (matchTopic && matchDate) {
                matched.add(item);
            }
        }

        paginator.setItems(matched);
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        GridPane grid = view.getAttemptsGrid();
        grid.getChildren().clear();

        Label colQuiz = new Label("Quiz");
        colQuiz.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #64748b; -fx-padding: 10 14;");

        Label colTopic = new Label("Topic");
        colTopic.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #64748b; -fx-padding: 10 14;");

        Label colScore = new Label("Score");
        colScore.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #64748b; -fx-padding: 10 14; -fx-alignment: CENTER;");

        Label colDate = new Label("Date");
        colDate.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #64748b; -fx-padding: 10 14;");

        Label colAction = new Label("Action");
        colAction.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #64748b; -fx-padding: 10 14; -fx-alignment: CENTER;");

        grid.add(colQuiz, 0, 0);
        grid.add(colTopic, 1, 0);
        grid.add(colScore, 2, 0);
        grid.add(colDate, 3, 0);
        grid.add(colAction, 4, 0);

        Region headerDivider = new Region();
        headerDivider.setPrefHeight(1);
        headerDivider.setMaxHeight(1);
        headerDivider.setStyle("-fx-background-color: #e2e8f0;");
        grid.add(headerDivider, 0, 1, 5, 1);

        List<HistoryItemDTO> currentPageItems = paginator.getCurrentPageItems();
        if (currentPageItems.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(36, 0, 36, 0));
            Label emptyLbl = new Label("No quiz attempts found matching your filter criteria.");
            emptyLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-font-weight: 600;");
            emptyBox.getChildren().add(emptyLbl);
            grid.add(emptyBox, 0, 2, 5, 1);

            view.getPaginationInfoLabel().setText("Showing 0 attempts");
            PaginationButtonRenderer.renderButtons(
                    view.getPaginationButtonsBox(),
                    paginator.getCurrentPage(),
                    paginator.getTotalPages(),
                    page -> {
                        paginator.setPage(page);
                        renderCurrentPage();
                    }
            );
            return;
        }

        view.getPaginationInfoLabel().setText(paginator.getPaginationInfoText("attempts"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy  •  HH:mm");

        int gridRow = 2;
        for (int i = 0; i < currentPageItems.size(); i++) {
            HistoryItemDTO item = currentPageItems.get(i);
            addGridDataRow(grid, item, dtf, gridRow);
            gridRow++;

            if (i < currentPageItems.size() - 1) {
                Region rowDivider = new Region();
                rowDivider.setPrefHeight(1);
                rowDivider.setMaxHeight(1);
                rowDivider.setStyle("-fx-background-color: #f1f5f9;");
                grid.add(rowDivider, 0, gridRow, 5, 1);
                gridRow++;
            }
        }

        PaginationButtonRenderer.renderButtons(
                view.getPaginationButtonsBox(),
                paginator.getCurrentPage(),
                paginator.getTotalPages(),
                page -> {
                    paginator.setPage(page);
                    renderCurrentPage();
                }
        );
    }

    private void addGridDataRow(GridPane grid, HistoryItemDTO item, DateTimeFormatter dtf, int rowIdx) {
        Label quizLbl = new Label(item.quizName);
        quizLbl.setWrapText(true);
        quizLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-padding: 10 14; -fx-cursor: hand;");

        Label topicLbl = new Label(item.topicName);
        topicLbl.setWrapText(true);
        topicLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #64748b; -fx-padding: 10 14; -fx-cursor: hand;");

        double scoreVal = item.result != null && item.result.getScore() != null ? (item.result.getScore().doubleValue() / 10.0) * 100 : 0;
        Label scoreBadge = new Label(String.format("%.0f%%", scoreVal));
        scoreBadge.setAlignment(Pos.CENTER);
        scoreBadge.setPrefWidth(64);

        if (scoreVal >= 80) {
            scoreBadge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 8; -fx-background-radius: 12px;");
        } else if (scoreVal >= 50) {
            scoreBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 8; -fx-background-radius: 12px;");
        } else {
            scoreBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: 800; -fx-padding: 4 8; -fx-background-radius: 12px;");
        }

        HBox scoreBox = new HBox(scoreBadge);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(10, 0, 10, 0));
        scoreBox.setStyle("-fx-cursor: hand;");

        String dateStr = (item.result != null && item.result.getFinishedAt() != null)
                ? item.result.getFinishedAt().format(dtf)
                : LocalDateTime.now().format(dtf);
        Label dateLbl = new Label(dateStr);
        dateLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #64748b; -fx-padding: 10 14; -fx-cursor: hand;");

        Button actionBtn = NavIconHelper.createDetailActionButton();

        HBox actionBox = new HBox(actionBtn);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(6, 0, 6, 0));

        grid.add(quizLbl, 0, rowIdx);
        grid.add(topicLbl, 1, rowIdx);
        grid.add(scoreBox, 2, rowIdx);
        grid.add(dateLbl, 3, rowIdx);
        grid.add(actionBox, 4, rowIdx);

        quizLbl.setOnMouseClicked(e -> openResult(item.result));
        topicLbl.setOnMouseClicked(e -> openResult(item.result));
        scoreBox.setOnMouseClicked(e -> openResult(item.result));
        dateLbl.setOnMouseClicked(e -> openResult(item.result));
        actionBtn.setOnAction(e -> openResult(item.result));
    }

    private void openResult(Result result) {
        if (result != null) {
            SessionManager.setLastResult(result);
        }
        SceneManager.showHistoryDetail();
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private static class HistoryItemDTO {
        Result result;
        String quizName;
        String topicName;

        HistoryItemDTO(Result result, String quizName, String topicName) {
            this.result = result;
            this.quizName = quizName;
            this.topicName = topicName;
        }
    }

}
