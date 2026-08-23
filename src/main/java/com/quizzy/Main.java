package com.quizzy;

import com.quizzy.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setPrimaryStage(stage);
        SceneManager.showHome();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
