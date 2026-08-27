package com.quizzy.util;

import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public final class PaginationButtonRenderer {

    private PaginationButtonRenderer() {
    }

    public static void renderButtons(HBox container, int currentPage, int totalPages, Consumer<Integer> onPageSelected) {
        if (container == null) {
            return;
        }
        container.getChildren().clear();

        Button prevBtn = new Button("<");
        stylePaginationBtn(prevBtn, false);
        prevBtn.setDisable(currentPage <= 1);
        prevBtn.setOnAction(e -> {
            if (currentPage > 1 && onPageSelected != null) {
                onPageSelected.accept(currentPage - 1);
            }
        });
        container.getChildren().add(prevBtn);

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            Button p1 = new Button("1");
            stylePaginationBtn(p1, currentPage == 1);
            p1.setOnAction(e -> {
                if (onPageSelected != null) {
                    onPageSelected.accept(1);
                }
            });
            container.getChildren().add(p1);

            if (startPage > 2) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                container.getChildren().add(dots);
            }
        }

        for (int p = startPage; p <= endPage; p++) {
            final int pageNum = p;
            Button pageBtn = new Button(String.valueOf(pageNum));
            boolean isActive = (pageNum == currentPage);
            stylePaginationBtn(pageBtn, isActive);
            pageBtn.setOnAction(e -> {
                if (onPageSelected != null) {
                    onPageSelected.accept(pageNum);
                }
            });
            container.getChildren().add(pageBtn);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                container.getChildren().add(dots);
            }

            Button pLast = new Button(String.valueOf(totalPages));
            stylePaginationBtn(pLast, currentPage == totalPages);
            pLast.setOnAction(e -> {
                if (onPageSelected != null) {
                    onPageSelected.accept(totalPages);
                }
            });
            container.getChildren().add(pLast);
        }

        Button nextBtn = new Button(">");
        stylePaginationBtn(nextBtn, false);
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages && onPageSelected != null) {
                onPageSelected.accept(currentPage + 1);
            }
        });
        container.getChildren().add(nextBtn);
    }

    public static void stylePaginationBtn(Button btn, boolean isActive) {
        if (isActive) {
            btn.getStyleClass().add("button-primary");
            btn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        } else {
            btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 4 10; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        }
    }
}
