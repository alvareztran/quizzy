package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Question;
import com.quizzy.service.AnswerService;
import com.quizzy.service.QuestionService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.AnswerView;
import com.quizzy.view.component.ConfirmDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class AnswerController {

    private final AnswerView view;
    private final AnswerService answerService = ServiceFactory.getAnswerService();
    private final QuestionService questionService = ServiceFactory.getQuestionService();

    private final ObservableList<Answer> displayedAnswerList = FXCollections.observableArrayList();
    private final List<Answer> allAnswers = new ArrayList<>();
    private final List<Question> allQuestions = new ArrayList<>();
    private final Map<Integer, String> questionMap = new HashMap<>();

    public AnswerController() {
        this.view = new AnswerView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public AnswerView getAnswerView() {
        return view;
    }

    private void initEventHandlers() {
        // Navigation Buttons
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> loadData());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showResult());

        // Logout via User Profile ContextMenu Item
        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        // Search & Filters
        view.getSearchAnswersField().textProperty().addListener((obs, oldV, newV) -> filterAnswers());
        view.getQuestionFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterAnswers());
        view.getStatusFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterAnswers());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchAnswersField().clear();
            view.getQuestionFilterComboBox().setValue("All Questions");
            view.getStatusFilterComboBox().setValue("All Correct Status");
            filterAnswers();
        });

        // Question Content Column Factory
        view.getQuestionContentColumn().setCellValueFactory(cellData -> {
            int qId = cellData.getValue().getQuestionId();
            String content = questionMap.getOrDefault(qId, "Question #" + qId);
            return new SimpleStringProperty(content);
        });

        // Setup Actions Column Center Aligned
        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            private final HBox btnBox = new HBox(8, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);
                deleteBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 6 10; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13px; -fx-font-family: 'Segoe UI Emoji', 'Segoe UI Symbol', sans-serif;");

                deleteBtn.setOnAction(e -> {
                    Answer answer = getTableView().getItems().get(getIndex());
                    deleteAnswer(answer);
                });
            }

            @Override
            protected void updateItem(Answer item, boolean empty) {
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
        view.getAnswerTable().setItems(displayedAnswerList);
        loadData();
    }

    private void loadData() {
        try {
            allAnswers.clear();
            allQuestions.clear();
            questionMap.clear();

            List<Question> questions = questionService.getAllQuestions();
            if (questions != null) {
                allQuestions.addAll(questions);
                view.getQuestionFilterComboBox().getItems().setAll("All Questions");
                for (Question q : questions) {
                    questionMap.put(q.getQuestionId(), q.getContent());
                    view.getQuestionFilterComboBox().getItems().add(q.getContent());

                    List<Answer> qAnswers = answerService.getAnswersByQuestionId(q.getQuestionId());
                    if (qAnswers != null) {
                        allAnswers.addAll(qAnswers);
                    }
                }
                view.getQuestionFilterComboBox().setValue("All Questions");
            }

            filterAnswers();
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load answers from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalAnswersCard().getValueLabel().setText(String.valueOf(allAnswers.size()));

        long correctCount = allAnswers.stream().filter(Answer::isIsCorrect).count();
        long incorrectCount = allAnswers.size() - correctCount;

        view.getCorrectAnswersCard().getValueLabel().setText(String.valueOf(correctCount));
        view.getIncorrectAnswersCard().getValueLabel().setText(String.valueOf(incorrectCount));
        view.getQuestionsCoveredCard().getValueLabel().setText(String.valueOf(questionMap.size()));

        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d answers", displayedAnswerList.size(), allAnswers.size())
        );
    }

    private void filterAnswers() {
        String keyword = view.getSearchAnswersField().getText();
        String selectedQuestion = view.getQuestionFilterComboBox().getValue();
        String selectedStatus = view.getStatusFilterComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<Answer> filtered = allAnswers.stream().filter(ans -> {
            boolean matchSearch = (search == null)
                    || (ans.getAnswerContent() != null && ans.getAnswerContent().toLowerCase().contains(search));

            boolean matchQuestion = true;
            if (selectedQuestion != null && !"All Questions".equals(selectedQuestion)) {
                String qContent = questionMap.get(ans.getQuestionId());
                matchQuestion = selectedQuestion.equals(qContent);
            }

            boolean matchStatus = true;
            if ("Correct Only".equals(selectedStatus)) {
                matchStatus = ans.isIsCorrect();
            } else if ("Incorrect Only".equals(selectedStatus)) {
                matchStatus = !ans.isIsCorrect();
            }

            return matchSearch && matchQuestion && matchStatus;
        }).toList();

        displayedAnswerList.setAll(filtered);
        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d answers", displayedAnswerList.size(), allAnswers.size())
        );
    }

    private void deleteAnswer(Answer answer) {
        if (answer == null) return;

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete Answer Choice?",
                "Are you sure you want to delete this answer choice?"
        );

        if (!confirm) return;

        try {
            if (!answerService.deleteAnswer(answer.getAnswerId())) {
                showError("Unable to delete answer choice.");
                return;
            }
            showInfo("Answer choice deleted successfully.");
            loadData();
        } catch (Exception e) {
            showError("Unable to delete answer choice. Database error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Answer Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Answer Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
