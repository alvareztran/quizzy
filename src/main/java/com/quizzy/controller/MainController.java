package com.quizzy.controller;

import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.MainView;
import javafx.scene.Parent;

public class MainController {

    private final MainView view;
    private final AdminDashboardController adminDashboardController;

    public MainController() {
        this.view = new MainView();
        this.adminDashboardController = new AdminDashboardController();

        // Place AdminDashboardController view in Center
        view.getRoot().setCenter(adminDashboardController.getView());

        initEventHandlers();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public MainView getMainView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> SceneManager.showUser());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        // Logout via User Profile ContextMenu Item
        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

}
