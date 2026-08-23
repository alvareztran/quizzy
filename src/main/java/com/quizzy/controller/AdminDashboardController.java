package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Topic;
import com.quizzy.model.User;
import com.quizzy.service.QuestionService;
import com.quizzy.service.QuizService;
import com.quizzy.service.TopicService;
import com.quizzy.service.UserService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.AdminDashboardView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Parent;

public class AdminDashboardController {

    private final AdminDashboardView view;
    private final TopicService topicService = ServiceFactory.getTopicService();
    private final QuizService quizService = ServiceFactory.getQuizService();
    private final QuestionService questionService = ServiceFactory.getQuestionService();
    private final UserService userService = ServiceFactory.getUserService();

    public AdminDashboardController() {
        this.view = new AdminDashboardView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public AdminDashboardView getAdminDashboardView() {
        return view;
    }

    private void initEventHandlers() {
        view.getCreateNewQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getViewAllActivitiesBtn().setOnAction(e -> SceneManager.showTopic());
        view.getViewAllTopicsBtn().setOnAction(e -> SceneManager.showTopic());
    }

    private void initializeData() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isBlank()) {
            view.getGreetingUserLabel().setText(currentUser.getFullName());
        } else if (currentUser != null) {
            view.getGreetingUserLabel().setText(currentUser.getUserName());
        } else {
            view.getGreetingUserLabel().setText("Administrator");
        }

        List<Topic> topics = null;
        List<Quiz> quizzes = null;
        try {
            topics = topicService.getAllTopics();
            int topicCount = topics != null ? topics.size() : 0;
            view.getTotalTopicsCard().getValueLabel().setText(String.valueOf(topicCount));
        } catch (Exception e) {
            view.getTotalTopicsCard().getValueLabel().setText("0");
        }

        try {
            quizzes = quizService.getAllQuizzes();
            int quizCount = quizzes != null ? quizzes.size() : 0;
            view.getTotalQuizzesCard().getValueLabel().setText(String.valueOf(quizCount));

            int totalQuestionCount = 0;
            if (quizzes != null) {
                for (Quiz quiz : quizzes) {
                    totalQuestionCount += quiz.getNumberOfQuestions();
                }
            }
            view.getTotalQuestionsCard().getValueLabel().setText(String.valueOf(totalQuestionCount));
        } catch (Exception e) {
            view.getTotalQuizzesCard().getValueLabel().setText("0");
            view.getTotalQuestionsCard().getValueLabel().setText("0");
        }

        try {
            int userCount = userService.getTotalUsersCount();
            view.getTotalUsersCard().getValueLabel().setText(String.valueOf(userCount));
        } catch (Exception e) {
            view.getTotalUsersCard().getValueLabel().setText("0");
        }

        Map<Integer, Integer> topicQuestionCounts = new HashMap<>();
        if (topics != null && quizzes != null) {
            for (Quiz quiz : quizzes) {
                int topicId = quiz.getTopicId();
                int qCount = quiz.getNumberOfQuestions();
                topicQuestionCounts.put(topicId, topicQuestionCounts.getOrDefault(topicId, 0) + qCount);
            }
        }
        view.renderTopTopics(topics, topicQuestionCounts);

        List<AdminDashboardView.ActivityItemData> activities = new ArrayList<>();
        if (quizzes != null && !quizzes.isEmpty()) {
            Quiz latestQuiz = quizzes.get(quizzes.size() - 1);
            activities.add(new AdminDashboardView.ActivityItemData("+", "New quiz \"" + latestQuiz.getQuizName() + "\" created", "Assessment Ready", "Today", "#e1e0ff", "#4648d4"));
        }

        if (topics != null && !topics.isEmpty()) {
            Topic latestTopic = topics.get(topics.size() - 1);
            activities.add(new AdminDashboardView.ActivityItemData("F", "Topic \"" + latestTopic.getTopicName() + "\" updated", "Question bank updated", "Today", "#fef08a", "#854d0e"));
        }

        User user = SessionManager.getCurrentUser();
        String userName = user != null ? user.getUserName() : "Administrator";
        String role = user != null ? user.getRole() : "ADMIN";
        activities.add(new AdminDashboardView.ActivityItemData("U", "User \"" + userName + "\" active session verified", "Role: " + role, "Active now", "#dcfce7", "#166534"));

        view.renderRecentActivities(activities);
    }
}
