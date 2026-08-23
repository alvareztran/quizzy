package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.model.User;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.service.UserService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.AdminResultView;
import com.quizzy.view.AdminResultView.ResultItemDTO;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AdminResultController {

    private final AdminResultView view;
    private final ResultService resultService = ServiceFactory.getResultService();
    private final UserService userService = ServiceFactory.getUserService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final TopicService topicService = ServiceFactory.getTopicService();

    private final List<ResultItemDTO> allItems = new ArrayList<>();
    private final ObservableList<ResultItemDTO> displayedItems = FXCollections.observableArrayList();

    private int currentPage = 1;
    private int pageSize = 10;

    public AdminResultController() {
        this.view = new AdminResultView();
        setupTableCells();
        initEventHandlers();
        loadData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        view.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        view.getTopicFilterComboBox().setOnAction(e -> applyFilters());
        view.getDateFilterComboBox().setOnAction(e -> applyFilters());
        view.getScoreFilterComboBox().setOnAction(e -> applyFilters());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchField().clear();
            view.getTopicFilterComboBox().setValue("All Topics");
            view.getDateFilterComboBox().setValue("Any Date");
            view.getScoreFilterComboBox().setValue("Any Score");
            applyFilters();
        });

        view.getPerPageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.contains("25")) {
                    pageSize = 25;
                } else if (newVal.contains("50")) {
                    pageSize = 50;
                } else {
                    pageSize = 10;
                }
                currentPage = 1;
                updatePagination();
            }
        });
    }

    private void setupTableCells() {
        view.getResultTable().setItems(displayedItems);

        view.getUserColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getUserColumn().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox userBox = new HBox(12);
                    userBox.setAlignment(Pos.CENTER_LEFT);

                    StackPane avatarPane = new StackPane();
                    Circle circle = new Circle(18);
                    circle.setFill(Color.web(item.userAvatarColor != null ? item.userAvatarColor : "#e0e7ff"));
                    Label initials = new Label(item.userInitials != null ? item.userInitials : "U");
                    initials.setStyle("-fx-font-weight: 800; -fx-font-size: 11px; -fx-text-fill: #4338ca;");
                    avatarPane.getChildren().addAll(circle, initials);

                    VBox textCol = new VBox(2);
                    Label nameLbl = new Label(item.userName != null ? item.userName : "User");
                    nameLbl.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: #0f172a;");

                    Label emailLbl = new Label(item.userEmail != null ? item.userEmail : "user@example.com");
                    emailLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                    textCol.getChildren().addAll(nameLbl, emailLbl);

                    userBox.getChildren().addAll(avatarPane, textCol);
                    setGraphic(userBox);
                }
            }
        });

        view.getQuizColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getQuizColumn().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox quizCol = new VBox(2);
                    quizCol.setAlignment(Pos.CENTER_LEFT);

                    Label quizNameLbl = new Label(item.quizName != null ? item.quizName : "Quiz");
                    quizNameLbl.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: #0f172a;");

                    Label topicLbl = new Label(item.topicName != null ? item.topicName : "General");
                    topicLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                    quizCol.getChildren().addAll(quizNameLbl, topicLbl);

                    setGraphic(quizCol);
                }
            }
        });

        view.getScoreColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getScoreColumn().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label scoreBadge = new Label(item.scoreDisplay != null ? item.scoreDisplay : "0/0");
                    if (item.scorePercent >= 80) {
                        scoreBadge.setStyle("-fx-background-color: #ede9fe; -fx-text-fill: #6d28d9; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 3 10; -fx-background-radius: 999;");
                    } else if (item.scorePercent < 50) {
                        scoreBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 3 10; -fx-background-radius: 999;");
                    } else {
                        scoreBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 3 10; -fx-background-radius: 999;");
                    }
                    setAlignment(Pos.CENTER);
                    setGraphic(scoreBadge);
                }
            }
        });

        view.getDateTimeColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getDateTimeColumn().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox dateCol = new VBox(2);
                    dateCol.setAlignment(Pos.CENTER_LEFT);

                    Label dateLbl = new Label(item.dateDisplay != null ? item.dateDisplay : "-");
                    dateLbl.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: #0f172a;");

                    Label timeLbl = new Label(item.timeDisplay != null ? item.timeDisplay : "-");
                    timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                    dateCol.getChildren().addAll(dateLbl, timeLbl);

                    setGraphic(dateCol);
                }
            }
        });

        view.getDurationColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getDurationColumn().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.durationDisplay != null ? item.durationDisplay : "0m 0s");
                    setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-weight: 500;");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        view.getActionColumn().setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        view.getActionColumn().setCellFactory(col -> new TableCell<>() {
            private final Button detailsBtn = new Button("View Details");

            {
                detailsBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #4338ca; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 4 12; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(ResultItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    detailsBtn.setOnAction(e -> {
                        SessionManager.setLastResult(item.result);
                        SceneManager.showResult();
                    });
                    setAlignment(Pos.CENTER);
                    setGraphic(detailsBtn);
                }
            }
        });
    }

    private void loadData() {
        allItems.clear();

        List<Result> results = null;
        try {
            results = resultService.getAllResults();
        } catch (Exception e) {
            results = List.of();
        }

        Map<Integer, User> userMap = new HashMap<>();
        try {
            List<User> users = userService.getAllUsers();
            if (users != null) {
                for (User u : users) {
                    userMap.put(u.getUserId(), u);
                }
            }
        } catch (Exception ignored) {
        }

        Map<Integer, Quiz> quizMap = new HashMap<>();
        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            if (quizzes != null) {
                for (Quiz q : quizzes) {
                    quizMap.put(q.getQuizId(), q);
                }
            }
        } catch (Exception ignored) {
        }

        Map<Integer, Topic> topicMap = new HashMap<>();
        try {
            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                for (Topic t : topics) {
                    topicMap.put(t.getTopicId(), t);
                }
            }
        } catch (Exception ignored) {
        }

        // Populate Topic ComboBox
        List<String> topicNames = new ArrayList<>();
        topicNames.add("All Topics");
        for (Topic t : topicMap.values()) {
            topicNames.add(t.getTopicName());
        }
        view.getTopicFilterComboBox().getItems().setAll(topicNames);
        view.getTopicFilterComboBox().setValue("All Topics");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");

        if (results != null) {
            for (Result r : results) {
                ResultItemDTO dto = new ResultItemDTO();
                dto.result = r;

                User u = userMap.get(r.getUserId());
                if (u != null) {
                    dto.userName = u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : u.getUserName();
                    dto.userEmail = u.getUserName() + "@example.com";
                    dto.userInitials = getInitials(dto.userName);
                } else {
                    dto.userName = "Student #" + r.getUserId();
                    dto.userEmail = "user" + r.getUserId() + "@example.com";
                    dto.userInitials = "U";
                }
                dto.userAvatarColor = getAvatarColor(dto.userInitials);

                Quiz q = quizMap.get(r.getQuizId());
                if (q != null) {
                    dto.quizName = q.getQuizName();
                    Topic t = topicMap.get(q.getTopicId());
                    dto.topicName = (t != null) ? t.getTopicName() : "General";
                } else {
                    dto.quizName = "Quiz #" + r.getQuizId();
                    dto.topicName = "General";
                }

                int totalQ = r.getTotalQuestions() > 0 ? r.getTotalQuestions() : 10;
                int correctQ = r.getCorrectAnswers();
                dto.scoreDisplay = correctQ + "/" + totalQ;
                dto.scorePercent = (totalQ > 0) ? ((double) correctQ / totalQ) * 100 : 0;

                if (r.getStartedAt() != null) {
                    dto.attemptDateTime = r.getStartedAt();
                    dto.dateDisplay = r.getStartedAt().format(dateFormatter);
                    dto.timeDisplay = r.getStartedAt().format(timeFormatter);
                } else {
                    dto.attemptDateTime = LocalDateTime.now();
                    dto.dateDisplay = dto.attemptDateTime.format(dateFormatter);
                    dto.timeDisplay = dto.attemptDateTime.format(timeFormatter);
                }

                if (r.getStartedAt() != null && r.getFinishedAt() != null) {
                    Duration d = Duration.between(r.getStartedAt(), r.getFinishedAt());
                    long mins = Math.max(0, d.toMinutes());
                    long secs = Math.max(0, d.toSecondsPart());
                    dto.durationDisplay = mins + "m " + String.format("%02ds", secs);
                } else {
                    dto.durationDisplay = "15m 00s";
                }

                allItems.add(dto);
            }
        }

        // Sort by attempt date descending (newest first)
        allItems.sort((a, b) -> {
            LocalDateTime tA = a.attemptDateTime != null ? a.attemptDateTime : LocalDateTime.MIN;
            LocalDateTime tB = b.attemptDateTime != null ? b.attemptDateTime : LocalDateTime.MIN;
            return tB.compareTo(tA);
        });

        applyFilters();
    }

    private ResultItemDTO createSampleItem(String userName, String userEmail, String quizName, String topicName, int correct, int total, String date, String time, String duration) {
        ResultItemDTO dto = new ResultItemDTO();
        dto.userName = userName;
        dto.userEmail = userEmail;
        dto.userInitials = getInitials(userName);
        dto.userAvatarColor = getAvatarColor(dto.userInitials);
        dto.quizName = quizName;
        dto.topicName = topicName;
        dto.scoreDisplay = correct + "/" + total;
        dto.scorePercent = ((double) correct / total) * 100;
        dto.dateDisplay = date;
        dto.timeDisplay = time;
        dto.durationDisplay = duration;
        dto.attemptDateTime = LocalDateTime.now();

        Result r = new Result();
        r.setCorrectAnswer(correct);
        r.setTotalQuestions(total);
        dto.result = r;
        return dto;
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private String getAvatarColor(String initials) {
        int hash = Math.abs(initials.hashCode());
        String[] colors = {"#e0e7ff", "#dcfce7", "#fef08a", "#e0f2fe", "#fce7f3", "#ede9fe"};
        return colors[hash % colors.length];
    }

    private List<ResultItemDTO> getFilteredItems() {
        String search = view.getSearchField().getText();
        String searchLower = (search != null) ? search.trim().toLowerCase() : "";

        String topicFilter = view.getTopicFilterComboBox().getValue();
        String dateFilter = view.getDateFilterComboBox().getValue();
        String scoreFilter = view.getScoreFilterComboBox().getValue();

        return allItems.stream().filter(item -> {
            if (!searchLower.isEmpty()) {
                boolean matchUser = item.userName != null && item.userName.toLowerCase().contains(searchLower);
                boolean matchEmail = item.userEmail != null && item.userEmail.toLowerCase().contains(searchLower);
                boolean matchQuiz = item.quizName != null && item.quizName.toLowerCase().contains(searchLower);
                if (!matchUser && !matchEmail && !matchQuiz) {
                    return false;
                }
            }

            if (topicFilter != null && !topicFilter.equals("All Topics") && !topicFilter.isBlank()) {
                if (item.topicName == null || !item.topicName.equalsIgnoreCase(topicFilter)) {
                    return false;
                }
            }

            if (dateFilter != null && !dateFilter.equals("Any Date") && !dateFilter.isBlank()) {
                if (item.attemptDateTime != null) {
                    LocalDate itemDate = item.attemptDateTime.toLocalDate();
                    LocalDate today = LocalDate.now();
                    if ("Today".equalsIgnoreCase(dateFilter) && !itemDate.isEqual(today)) return false;
                    if ("This Week".equalsIgnoreCase(dateFilter) && itemDate.isBefore(today.minusDays(7))) return false;
                    if ("This Month".equalsIgnoreCase(dateFilter) && itemDate.isBefore(today.minusDays(30))) return false;
                }
            }

            if (scoreFilter != null && !scoreFilter.equals("Any Score")) {
                if (scoreFilter.contains("80") && item.scorePercent < 80) return false;
                if (scoreFilter.contains("50-79") && (item.scorePercent < 50 || item.scorePercent >= 80)) return false;
                if (scoreFilter.contains("< 50") && item.scorePercent >= 50) return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    private void applyFilters() {
        currentPage = 1;
        updatePagination();
    }

    private void updatePagination() {
        List<ResultItemDTO> filtered = getFilteredItems();
        int total = filtered.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        if (fromIndex < total) {
            displayedItems.setAll(filtered.subList(fromIndex, toIndex));
            view.getPaginationInfoLabel().setText("Showing " + (fromIndex + 1) + " to " + toIndex + " of " + total + " results");
        } else {
            displayedItems.clear();
            view.getPaginationInfoLabel().setText("Showing 0 to 0 of 0 results");
        }

        renderPaginationButtons(totalPages);
    }

    private void renderPaginationButtons(int totalPages) {
        HBox box = view.getPaginationButtonsBox();
        box.getChildren().clear();

        Button prevBtn = new Button("<");
        stylePaginationBtn(prevBtn, false);
        prevBtn.setDisable(currentPage <= 1);
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updatePagination();
            }
        });
        box.getChildren().add(prevBtn);

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            Button p1 = new Button("1");
            stylePaginationBtn(p1, currentPage == 1);
            p1.setOnAction(e -> {
                currentPage = 1;
                updatePagination();
            });
            box.getChildren().add(p1);

            if (startPage > 2) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }
        }

        for (int p = startPage; p <= endPage; p++) {
            final int pageNum = p;
            Button pageBtn = new Button(String.valueOf(pageNum));
            boolean isActive = (pageNum == currentPage);
            stylePaginationBtn(pageBtn, isActive);
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                updatePagination();
            });
            box.getChildren().add(pageBtn);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }

            Button pLast = new Button(String.valueOf(totalPages));
            stylePaginationBtn(pLast, currentPage == totalPages);
            pLast.setOnAction(e -> {
                currentPage = totalPages;
                updatePagination();
            });
            box.getChildren().add(pLast);
        }

        Button nextBtn = new Button(">");
        stylePaginationBtn(nextBtn, false);
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePagination();
            }
        });
        box.getChildren().add(nextBtn);
    }

    private void stylePaginationBtn(Button btn, boolean isActive) {
        if (isActive) {
            btn.getStyleClass().add("button-primary");
            btn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        } else {
            btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 4 10; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }
}
