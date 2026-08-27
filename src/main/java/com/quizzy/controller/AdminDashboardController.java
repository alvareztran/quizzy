package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.Quiz;
import com.quizzy.model.Result;
import com.quizzy.model.Topic;
import com.quizzy.model.User;
import com.quizzy.service.QuestionService;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.service.UserService;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.AdminDashboardView;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ResultService resultService = ServiceFactory.getResultService();

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
        view.getViewAllActivitiesBtn().setOnAction(e -> SceneManager.showAdminResult());
        view.getViewAllTopicsBtn().setOnAction(e -> SceneManager.showTopic());
    }

    private void initializeData() {
        User currentUser = SessionManager.getCurrentUser();
        String name = "Administrator";
        if (currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isBlank()) {
            name = currentUser.getFullName();
        } else if (currentUser != null && currentUser.getUserName() != null && !currentUser.getUserName().isBlank()) {
            name = currentUser.getUserName();
        }
        view.getGreetingUserLabel().setText(name + "!");

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

        Map<Integer, String> userMap = new HashMap<>();
        try {
            List<User> allUsers = userService.getAllUsers();
            if (allUsers != null) {
                for (User u : allUsers) {
                    userMap.put(u.getUserId(), u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : u.getUserName());
                }
            }
        } catch (Exception ignored) {
        }

        Map<Integer, String> quizMap = new HashMap<>();
        if (quizzes != null) {
            for (Quiz q : quizzes) {
                quizMap.put(q.getQuizId(), q.getQuizName());
            }
        }

        List<AdminDashboardView.ActivityItemData> activities = new ArrayList<>();

        try {
            List<Result> recentResults = resultService.getAllResults();
            if (recentResults != null && !recentResults.isEmpty()) {
                recentResults.sort((a, b) -> {
                    LocalDateTime t1 = a.getFinishedAt() != null ? a.getFinishedAt() : a.getStartedAt();
                    LocalDateTime t2 = b.getFinishedAt() != null ? b.getFinishedAt() : b.getStartedAt();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                });

                int count = Math.min(recentResults.size(), 3);
                for (int i = 0; i < count; i++) {
                    Result res = recentResults.get(i);
                    String uName = userMap.getOrDefault(res.getUserId(), "Player #" + res.getUserId());
                    String qName = quizMap.getOrDefault(res.getQuizId(), "Quiz #" + res.getQuizId());
                    LocalDateTime time = res.getFinishedAt() != null ? res.getFinishedAt() : res.getStartedAt();
                    String timeStr = formatRelativeTime(time);

                    double percent = (res.getTotalQuestions() > 0)
                            ? ((double) res.getCorrectAnswers() / res.getTotalQuestions()) * 100.0
                            : 0;

                    String bgHex = percent >= 80 ? "#ede9fe" : (percent < 50 ? "#fee2e2" : "#f1f5f9");
                    String iconHex = percent >= 80 ? "#6d28d9" : (percent < 50 ? "#b91c1c" : "#475569");

                    String title = uName + " completed \"" + qName + "\"";
                    String sub = "Score: " + res.getCorrectAnswers() + "/" + res.getTotalQuestions()
                            + " (" + Math.round(percent) + "%)";

                    activities.add(new AdminDashboardView.ActivityItemData("🎯", title, sub, timeStr, bgHex, iconHex));
                }
            }
        } catch (Exception ignored) {
        }

        if (quizzes != null && !quizzes.isEmpty()) {
            Quiz latestQuiz = quizzes.get(quizzes.size() - 1);
            String timeStr = formatRelativeTime(latestQuiz.getCreatedAt());
            activities.add(new AdminDashboardView.ActivityItemData(
                    "+",
                    "New quiz \"" + latestQuiz.getQuizName() + "\" created",
                    latestQuiz.getNumberOfQuestions() + " Questions | " + latestQuiz.getTimeLimit() + " mins",
                    timeStr,
                    "#e1e0ff",
                    "#4648d4"
            ));
        }

        User user = SessionManager.getCurrentUser();
        String userName = user != null ? (user.getFullName() != null ? user.getFullName() : user.getUserName()) : "Administrator";
        String role = user != null ? user.getRole() : "ADMIN";
        activities.add(new AdminDashboardView.ActivityItemData("👤", "Admin session: " + userName, "Role: " + role, "Active now", "#dcfce7", "#166534"));

        view.renderRecentActivities(activities);
    }

    private String formatRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Recently";
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        if (seconds < 0) {
            return "Just now";
        }
        if (seconds < 60) {
            return "Just now";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m ago";
        }
        long hours = minutes / 60;
        if (hours < 24 && dateTime.toLocalDate().isEqual(now.toLocalDate())) {
            return hours + "h ago";
        }
        long days = duration.toDays();
        if (days == 0 || dateTime.toLocalDate().isEqual(now.toLocalDate())) {
            return "Today";
        }
        if (days == 1 || dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1))) {
            return "Yesterday";
        }
        if (days < 7) {
            return days + "d ago";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
