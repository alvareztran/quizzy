package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.TopicView;
import com.quizzy.view.component.ConfirmDialog;
import com.quizzy.view.component.TopicFormDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class TopicController {

    private final TopicView view;
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuizService quizService = ServiceFactory.getQuizService();

    private final ObservableList<Topic> displayedTopicList = FXCollections.observableArrayList();
    private final List<Topic> allTopics = new ArrayList<>();
    private final List<Topic> currentFilteredList = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    public TopicController() {
        this.view = new TopicView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public TopicView getTopicView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> loadTopics());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getCreateTopicBtn().setOnAction(e -> openCreateTopicDialog());

        view.getSearchTopicsField().textProperty().addListener(
                (observable, oldValue, newValue) -> filterTopics(newValue)
        );
        view.getSortComboBox().valueProperty().addListener(
                (observable, oldValue, newValue) -> filterTopics(view.getSearchTopicsField().getText())
        );

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchTopicsField().clear();
            view.getStatusFilterComboBox().setValue("All Status");
            view.getSortComboBox().setValue("Sort by: Newest");
            filterTopics(null);
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
                renderPage();
            }
        });

        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = NavIconHelper.createEditActionButton();
            private final Button deleteBtn = NavIconHelper.createDeleteActionButton();
            private final HBox btnBox = new HBox(8, editBtn, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                editBtn.setOnAction(e -> {
                    Topic topic = getTableView().getItems().get(getIndex());
                    openEditTopicDialog(topic);
                });

                deleteBtn.setOnAction(e -> {
                    Topic topic = getTableView().getItems().get(getIndex());
                    deleteTopic(topic);
                });
            }

            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void initializeData() {
        view.getTopicTable().setItems(displayedTopicList);
        loadTopics();
    }

    private void loadTopics() {
        try {
            allTopics.clear();
            List<Topic> topics = topicService.getAllTopics();
            if (topics != null) {
                allTopics.addAll(topics);
            }
            filterTopics(view.getSearchTopicsField().getText());
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load topics from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalTopicsCard().getValueLabel().setText(String.valueOf(allTopics.size()));
        view.getActiveTopicsCard().getValueLabel().setText(String.valueOf(allTopics.size()));

        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();
            int quizCount = quizzes != null ? quizzes.size() : 0;
            view.getTotalQuizzesCard().getValueLabel().setText(String.valueOf(quizCount));

            int qCount = 0;
            if (quizzes != null) {
                for (Quiz q : quizzes) {
                    qCount += q.getNumberOfQuestions();
                }
            }
            view.getTotalQuestionsCard().getValueLabel().setText(String.valueOf(qCount));
        } catch (Exception e) {
            view.getTotalQuizzesCard().getValueLabel().setText("0");
            view.getTotalQuestionsCard().getValueLabel().setText("0");
        }
    }

    private void filterTopics(String keyword) {
        String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;
        String sortOption = view.getSortComboBox().getValue();

        List<Topic> filtered = new ArrayList<>(allTopics.stream()
                .filter(t -> search == null
                        || (t.getTopicName() != null && t.getTopicName().toLowerCase().contains(search))
                        || (t.getDescription() != null && t.getDescription().toLowerCase().contains(search)))
                .toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Sort by: Oldest" -> filtered.sort(java.util.Comparator.comparingInt(Topic::getTopicId));
                case "Sort by: Name A-Z", "Name: A - Z" -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getTopicName() != null ? a.getTopicName() : "",
                        b.getTopicName() != null ? b.getTopicName() : ""
                ));
                case "Sort by: Name Z-A", "Name: Z - A" -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        b.getTopicName() != null ? b.getTopicName() : "",
                        a.getTopicName() != null ? a.getTopicName() : ""
                ));
                default -> filtered.sort((a, b) -> Integer.compare(b.getTopicId(), a.getTopicId())); // Newest by default
            }
        }

        currentFilteredList.clear();
        currentFilteredList.addAll(filtered);
        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = currentFilteredList.size();
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
            displayedTopicList.setAll(currentFilteredList.subList(fromIndex, toIndex));
            view.getPaginationInfoLabel().setText(
                    String.format("Showing %d to %d of %d topics", (fromIndex + 1), toIndex, total)
            );
        } else {
            displayedTopicList.clear();
            view.getPaginationInfoLabel().setText("Showing 0 to 0 of 0 topics");
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
                renderPage();
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
                renderPage();
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
                renderPage();
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
                renderPage();
            });
            box.getChildren().add(pLast);
        }

        Button nextBtn = new Button(">");
        stylePaginationBtn(nextBtn, false);
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
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

    private void openCreateTopicDialog() {
        Optional<Topic> result = TopicFormDialog.showTopicDialog(null);
        result.ifPresent(newTopic -> {
            try {
                if (!topicService.createTopic(newTopic)) {
                    showError("Cannot create topic. Topic name may already exist.");
                    return;
                }
                showInfo("Topic created successfully.");
                loadTopics();
            } catch (Exception e) {
                showError("Unable to create topic. Database connection error.");
            }
        });
    }

    private void openEditTopicDialog(Topic topic) {
        if (topic == null) return;

        Optional<Topic> result = TopicFormDialog.showTopicDialog(topic);
        result.ifPresent(updatedTopic -> {
            try {
                if (!topicService.updateTopic(updatedTopic)) {
                    showError("Cannot update topic. Topic name may already exist.");
                    return;
                }
                showInfo("Topic updated successfully.");
                loadTopics();
            } catch (Exception e) {
                showError("Unable to update topic. Database connection error.");
            }
        });
    }

    private void deleteTopic(Topic topic) {
        if (topic == null) return;

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Topic?",
                "Are you sure you want to delete topic '" + topic.getTopicName() + "'? This action cannot be undone."
        );

        if (!confirm) return;

        try {
            if (!topicService.deleteTopic(topic.getTopicId())) {
                showError("Unable to delete topic. Please make sure no quizzes are assigned to this topic.");
                return;
            }
            showInfo("Topic deleted successfully.");
            loadTopics();
        } catch (Exception e) {
            showError("Unable to delete topic. Database connection error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Topic Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Topic Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
