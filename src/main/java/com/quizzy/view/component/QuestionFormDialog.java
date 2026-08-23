package com.quizzy.view.component;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Answer;
import com.quizzy.model.Question;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class QuestionFormDialog {

    public static class QuestionFormResult {
        public Question question;
        public List<Answer> answers;

        public QuestionFormResult(Question question, List<Answer> answers) {
            this.question = question;
            this.answers = answers;
        }
    }

    private QuestionFormDialog() {
    }

    public static Optional<QuestionFormResult> showQuestionDialog(Question existingQuestion, List<Answer> existingAnswers, List<Quiz> availableQuizzes) {
        List<Topic> availableTopics;
        try {
            availableTopics = ServiceFactory.getTopicService().getAllTopics();
        } catch (Exception e) {
            availableTopics = new ArrayList<>();
        }
        return showQuestionDialog(existingQuestion, existingAnswers, availableTopics, availableQuizzes);
    }

    public static Optional<QuestionFormResult> showQuestionDialog(Question existingQuestion, List<Answer> existingAnswers, List<Topic> availableTopics, List<Quiz> availableQuizzes) {
        Dialog<QuestionFormResult> dialog = new Dialog<>();
        boolean isEdit = (existingQuestion != null);

        dialog.setTitle(isEdit ? "Edit Question" : "Create Question");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType(isEdit ? "Update Question" : "Create Question", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().getStyleClass().add("card");
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");

        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(500);

        Label headerTitle = new Label(isEdit ? "Edit Question" : "Add New Question");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label topicLabel = new Label("Target Topic *");
        topicLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        ComboBox<Topic> topicComboBox = new ComboBox<>();
        topicComboBox.setMaxWidth(Double.MAX_VALUE);
        topicComboBox.setPromptText("Select a topic");
        topicComboBox.setPrefHeight(38);
        setupTopicComboBox(topicComboBox);

        if (availableTopics != null) {
            topicComboBox.getItems().addAll(availableTopics);
        }

        Label quizLabel = new Label("Target Quiz *");
        quizLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        ComboBox<Quiz> quizComboBox = new ComboBox<>();
        quizComboBox.setMaxWidth(Double.MAX_VALUE);
        quizComboBox.setPromptText("Select topic first, then choose quiz");
        quizComboBox.setPrefHeight(38);
        setupQuizComboBox(quizComboBox);

        topicComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newTopic) -> {
            quizComboBox.getItems().clear();
            if (newTopic != null && availableQuizzes != null) {
                List<Quiz> filtered = availableQuizzes.stream()
                        .filter(q -> q.getTopicId() == newTopic.getTopicId())
                        .toList();
                quizComboBox.getItems().addAll(filtered);
                if (!filtered.isEmpty()) {
                    quizComboBox.setPromptText("Select target quiz (" + filtered.size() + " available)");
                } else {
                    quizComboBox.setPromptText("No quizzes under this topic");
                }
            } else {
                quizComboBox.setPromptText("Select topic first, then choose quiz");
            }
        });

        Label contentLabel = new Label("Question Content *");
        contentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter question text...");
        contentArea.setPrefRowCount(3);
        contentArea.setWrapText(true);

        Label diffLabel = new Label("Difficulty Level *");
        diffLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");
        ComboBox<String> diffComboBox = new ComboBox<>();
        diffComboBox.setMaxWidth(Double.MAX_VALUE);
        diffComboBox.getItems().addAll("Easy", "Medium", "Hard");
        diffComboBox.setValue("Easy");
        diffComboBox.setPrefHeight(38);

        Label answerHeaderLabel = new Label("Answer Choices (Select Correct Answer) *");
        answerHeaderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0f172a; -fx-padding: 10 0 0 0;");

        ToggleGroup correctGroup = new ToggleGroup();

        TextField optionAField = new TextField();
        optionAField.setPromptText("Option A text...");
        RadioButton radioA = new RadioButton("Correct A");
        radioA.setToggleGroup(correctGroup);
        radioA.setSelected(true);
        HBox rowA = new HBox(8, optionAField, radioA);
        HBox.setHgrow(optionAField, Priority.ALWAYS);
        rowA.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TextField optionBField = new TextField();
        optionBField.setPromptText("Option B text...");
        RadioButton radioB = new RadioButton("Correct B");
        radioB.setToggleGroup(correctGroup);
        HBox rowB = new HBox(8, optionBField, radioB);
        HBox.setHgrow(optionBField, Priority.ALWAYS);
        rowB.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TextField optionCField = new TextField();
        optionCField.setPromptText("Option C text...");
        RadioButton radioC = new RadioButton("Correct C");
        radioC.setToggleGroup(correctGroup);
        HBox rowC = new HBox(8, optionCField, radioC);
        HBox.setHgrow(optionCField, Priority.ALWAYS);
        rowC.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TextField optionDField = new TextField();
        optionDField.setPromptText("Option D text...");
        RadioButton radioD = new RadioButton("Correct D");
        radioD.setToggleGroup(correctGroup);
        HBox rowD = new HBox(8, optionDField, radioD);
        HBox.setHgrow(optionDField, Priority.ALWAYS);
        rowD.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        if (isEdit) {
            contentArea.setText(existingQuestion.getContent());
            diffComboBox.setValue(existingQuestion.getDifficulty());

            if (availableQuizzes != null) {
                for (Quiz q : availableQuizzes) {
                    if (q.getQuizId() == existingQuestion.getQuizId()) {
                        if (availableTopics != null) {
                            for (Topic t : availableTopics) {
                                if (t.getTopicId() == q.getTopicId()) {
                                    topicComboBox.setValue(t);
                                    break;
                                }
                            }
                        }
                        quizComboBox.setValue(q);
                        break;
                    }
                }
            }

            if (existingAnswers != null && existingAnswers.size() >= 4) {
                optionAField.setText(existingAnswers.get(0).getAnswerContent());
                if (existingAnswers.get(0).isIsCorrect()) radioA.setSelected(true);

                optionBField.setText(existingAnswers.get(1).getAnswerContent());
                if (existingAnswers.get(1).isIsCorrect()) radioB.setSelected(true);

                optionCField.setText(existingAnswers.get(2).getAnswerContent());
                if (existingAnswers.get(2).isIsCorrect()) radioC.setSelected(true);

                optionDField.setText(existingAnswers.get(3).getAnswerContent());
                if (existingAnswers.get(3).isIsCorrect()) radioD.setSelected(true);
            }
        }

        contentBox.getChildren().addAll(
                headerTitle,
                topicLabel, topicComboBox,
                quizLabel, quizComboBox,
                contentLabel, contentArea,
                diffLabel, diffComboBox,
                answerHeaderLabel,
                rowA, rowB, rowC, rowD,
                errorLabel
        );

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(520);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        dialog.getDialogPane().setContent(scrollPane);

        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Topic selectedTopic = topicComboBox.getValue();
            Quiz selectedQuiz = quizComboBox.getValue();
            String text = contentArea.getText();

            if (selectedTopic == null) {
                showErr(errorLabel, "Please select a target topic.");
                event.consume();
                return;
            }

            if (selectedQuiz == null) {
                showErr(errorLabel, "Please select a target quiz.");
                event.consume();
                return;
            }

            if (text == null || text.isBlank()) {
                showErr(errorLabel, "Question content is required.");
                event.consume();
                return;
            }

            if (optionAField.getText().isBlank() || optionBField.getText().isBlank()
                    || optionCField.getText().isBlank() || optionDField.getText().isBlank()) {
                showErr(errorLabel, "All 4 answer options (A, B, C, D) are required.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Quiz selectedQuiz = quizComboBox.getValue();
                String text = contentArea.getText().trim();
                String diff = diffComboBox.getValue();

                Question question;
                if (isEdit) {
                    question = new Question(existingQuestion.getQuestionId(), selectedQuiz.getQuizId(), text, diff, existingQuestion.getCreatedAt());
                } else {
                    question = new Question(selectedQuiz.getQuizId(), text, diff);
                }

                List<Answer> answers = new ArrayList<>();
                int qId = isEdit ? existingQuestion.getQuestionId() : 0;

                if (isEdit && existingAnswers != null && existingAnswers.size() >= 4) {
                    answers.add(new Answer(existingAnswers.get(0).getAnswerId(), qId, optionAField.getText().trim(), radioA.isSelected()));
                    answers.add(new Answer(existingAnswers.get(1).getAnswerId(), qId, optionBField.getText().trim(), radioB.isSelected()));
                    answers.add(new Answer(existingAnswers.get(2).getAnswerId(), qId, optionCField.getText().trim(), radioC.isSelected()));
                    answers.add(new Answer(existingAnswers.get(3).getAnswerId(), qId, optionDField.getText().trim(), radioD.isSelected()));
                } else {
                    answers.add(new Answer(qId, optionAField.getText().trim(), radioA.isSelected()));
                    answers.add(new Answer(qId, optionBField.getText().trim(), radioB.isSelected()));
                    answers.add(new Answer(qId, optionCField.getText().trim(), radioC.isSelected()));
                    answers.add(new Answer(qId, optionDField.getText().trim(), radioD.isSelected()));
                }

                return new QuestionFormResult(question, answers);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static void setupTopicComboBox(ComboBox<Topic> comboBox) {
        StringConverter<Topic> converter = new StringConverter<>() {
            @Override
            public String toString(Topic topic) {
                return topic != null ? topic.getTopicName() : "";
            }

            @Override
            public Topic fromString(String string) {
                return null;
            }
        };
        comboBox.setConverter(converter);
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTopicName());
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTopicName());
            }
        });
    }

    private static void setupQuizComboBox(ComboBox<Quiz> comboBox) {
        StringConverter<Quiz> converter = new StringConverter<>() {
            @Override
            public String toString(Quiz quiz) {
                return quiz != null ? quiz.getQuizName() : "";
            }

            @Override
            public Quiz fromString(String string) {
                return null;
            }
        };
        comboBox.setConverter(converter);
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuizName());
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuizName());
            }
        });
    }

    private static void showErr(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

}
