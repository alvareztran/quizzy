package com.quizzy.view.component;

import javafx.scene.control.Label;

public class StatusBadge {

    private StatusBadge() {
    }

    public static Label createRoleBadge(String role) {
        String roleStr = role != null ? role.toUpperCase() : "PLAYER";
        Label badge = new Label(roleStr);
        badge.getStyleClass().add("badge-pill");

        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            badge.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        } else {
            badge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        }
        return badge;
    }

    public static Label createDifficultyBadge(String difficulty) {
        String diffStr = difficulty != null ? difficulty.toUpperCase() : "EASY";
        Label badge = new Label(diffStr);
        badge.getStyleClass().add("badge-pill");

        if ("EASY".equalsIgnoreCase(diffStr)) {
            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        } else if ("HARD".equalsIgnoreCase(diffStr)) {
            badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        } else {
            badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #b45309; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        }
        return badge;
    }

    public static Label createCorrectnessBadge(boolean isCorrect) {
        Label badge = new Label(isCorrect ? "CORRECT" : "INCORRECT");
        badge.getStyleClass().add("badge-pill");

        if (isCorrect) {
            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        } else {
            badge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
        }
        return badge;
    }

    public static Label createCustomBadge(String text, String bgHex, String textHex) {
        Label badge = new Label(text);
        badge.getStyleClass().add("badge-pill");
        badge.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;", bgHex, textHex));
        return badge;
    }

}
