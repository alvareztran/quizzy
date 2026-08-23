package com.quizzy.view.component;

import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class SubmitQuizModal {

    private SubmitQuizModal() {
    }

    public static boolean showConfirmation(int answeredCount, int totalQuestions, Map<Integer, Boolean> questionAnsweredMap) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Submit Quiz");
        dialog.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(480);
        dialogPane.setMaxWidth(480);
        dialogPane.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16px; -fx-border-radius: 16px; -fx-border-color: #e5e7eb; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.18), 24, 0, 0, 8);");

        HBox headerBar = new HBox(10);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(16, 20, 16, 20));
        headerBar.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 1px 0;");

        Label iconL = new Label("❓");
        iconL.setStyle("-fx-font-size: 16px; -fx-text-fill: #6366f1;");

        Label titleL = new Label("Submit Quiz?");
        titleL.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        headerBar.getChildren().addAll(iconL, titleL);

        VBox bodyContent = new VBox(18);
        bodyContent.setPadding(new Insets(24, 24, 20, 24));

        HBox warningRow = new HBox(16);
        warningRow.setAlignment(Pos.TOP_LEFT);

        Label warnIcon = new Label("⚠️");
        warnIcon.setAlignment(Pos.CENTER);
        warnIcon.setPrefSize(42, 42);
        warnIcon.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 18px; -fx-background-radius: 21px;");

        VBox textGroup = new VBox(6);
        HBox.setHgrow(textGroup, Priority.ALWAYS);

        Label summaryL = new Label("You have answered " + answeredCount + " of " + totalQuestions + " questions.");
        summaryL.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #191c1e;");

        Label promptL = new Label("Are you sure you want to submit? You cannot return to the quiz once submitted.");
        promptL.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-line-spacing: 3px;");
        promptL.setWrapText(true);

        textGroup.getChildren().addAll(summaryL, promptL);
        warningRow.getChildren().addAll(warnIcon, textGroup);

        HBox stepperRow = new HBox(8);
        stepperRow.setAlignment(Pos.CENTER);
        stepperRow.setPadding(new Insets(6, 0, 0, 0));

        for (int i = 1; i <= totalQuestions; i++) {
            boolean isAns = questionAnsweredMap != null && Boolean.TRUE.equals(questionAnsweredMap.get(i));
            Label circle = new Label(String.valueOf(i));
            circle.setAlignment(Pos.CENTER);
            circle.setPrefSize(28, 28);

            if (isAns) {
                circle.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 14px;");
            } else {
                circle.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-background-radius: 14px; -fx-border-radius: 14px;");
            }
            stepperRow.getChildren().add(circle);
        }

        bodyContent.getChildren().addAll(warningRow, stepperRow);

        VBox mainContainer = new VBox(0, headerBar, bodyContent);
        dialogPane.setContent(mainContainer);

        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submitType = new ButtonType("Submit Quiz →", ButtonBar.ButtonData.OK_DONE);

        dialogPane.getButtonTypes().setAll(cancelType, submitType);

        Button cancelBtn = (Button) dialogPane.lookupButton(cancelType);
        if (cancelBtn != null) {
            cancelBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9 20; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-cursor: hand;");
        }

        Button submitBtn = (Button) dialogPane.lookupButton(submitType);
        if (submitBtn != null) {
            submitBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9 22; -fx-background-radius: 8px; -fx-cursor: hand;");
        }

        dialog.setResultConverter(dialogButton -> dialogButton == submitType);

        return dialog.showAndWait().orElse(false);
    }

}
