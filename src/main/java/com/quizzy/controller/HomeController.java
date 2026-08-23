package com.quizzy.controller;

import com.quizzy.util.SceneManager;
import com.quizzy.view.HomeView;
import javafx.scene.Parent;

public class HomeController {

    private final HomeView view;

    public HomeController() {
        this.view = new HomeView();
        initEventHandlers();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public HomeView getHomeView() {
        return view;
    }

    private void initEventHandlers() {
        view.getHeaderLoginBtn().setOnAction(e -> SceneManager.showLogin());
        view.getHeroLoginBtn().setOnAction(e -> SceneManager.showLogin());
        view.getHeaderRegisterBtn().setOnAction(e -> SceneManager.showRegister());
        view.getHeroGetStartedBtn().setOnAction(e -> SceneManager.showRegister());
    }

}
