package com.quizzy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HomeView {

    private final BorderPane root = new BorderPane();
    private final ScrollPane scrollPane = new ScrollPane();

    private final ImageView logoImageView = new ImageView();
    private final Label brandNameLabel = new Label("QUIZZY");
    private final Button headerLoginBtn = new Button("Login");
    private final Button headerRegisterBtn = new Button("Register");

    private final Button heroGetStartedBtn = new Button("Get Started →");
    private final Button heroLoginBtn = new Button("Login");

    public HomeView() {
        createUI();
    }

    private void createUI() {
        root.setPrefSize(1240, 760);

        HBox navbar = new HBox(12);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(16, 52, 16, 52));
        navbar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 1px 0;");

        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/quizzy-icon.png"));
            logoImageView.setImage(iconImg);
            logoImageView.setFitHeight(30);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
        } catch (Exception ignored) {
        }

        brandNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #191c1e; -fx-letter-spacing: 1px;");
        HBox logoBrandBox = new HBox(10, logoImageView, brandNameLabel);
        logoBrandBox.setAlignment(Pos.CENTER_LEFT);

        HBox navSpacer = new HBox();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        headerLoginBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #334155; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");

        headerRegisterBtn.getStyleClass().add("button-primary");
        headerRegisterBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-padding: 7 17; -fx-font-weight: bold; -fx-background-radius: 8px;");

        navbar.getChildren().addAll(logoBrandBox, navSpacer, headerLoginBtn, headerRegisterBtn);
        root.setTop(navbar);

        VBox contentContainer = new VBox(40);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setPadding(new Insets(48, 52, 40, 52));
        contentContainer.setStyle("-fx-background-color: #f8f9fb;");

        HBox heroRow = new HBox(48);
        heroRow.setAlignment(Pos.CENTER);
        heroRow.setMaxWidth(1140);

        VBox heroLeftCol = new VBox(22);
        heroLeftCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(heroLeftCol, Priority.ALWAYS);

        VBox titleBox = new VBox(4);
        Label headingLine1 = new Label("Test your knowledge");
        headingLine1.setStyle("-fx-font-size: 44px; -fx-font-weight: 800; -fx-text-fill: #191c1e; -fx-letter-spacing: -0.5px;");

        Label headingLine2 = new Label("Learn - Practice - Improve");
        headingLine2.setStyle("-fx-font-size: 44px; -fx-font-weight: 800; -fx-text-fill: #6366f1; -fx-letter-spacing: -0.5px;");

        titleBox.getChildren().addAll(headingLine1, headingLine2);

        Label subtitleLabel = new Label("Accelerate your learning journey with interactive quizzes, instant analytics, and structured topic mastery.");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #464554; -fx-line-spacing: 4px;");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setMaxWidth(520);

        HBox ctaRow = new HBox(14);
        ctaRow.setAlignment(Pos.CENTER_LEFT);
        ctaRow.setPadding(new Insets(8, 0, 0, 0));

        heroGetStartedBtn.getStyleClass().add("button-primary");
        heroGetStartedBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-padding: 12 26; -fx-font-weight: bold; -fx-background-radius: 8px;");

        heroLoginBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-text-fill: #191c1e; -fx-font-size: 15px; -fx-padding: 12 26; -fx-font-weight: bold;");

        ctaRow.getChildren().addAll(heroGetStartedBtn, heroLoginBtn);

        heroLeftCol.getChildren().addAll(titleBox, subtitleLabel, ctaRow);

        VBox heroRightCol = new VBox();
        heroRightCol.setAlignment(Pos.CENTER);

        VBox quizMockupCard = createQuizMockupCard();
        heroRightCol.getChildren().add(quizMockupCard);

        heroRow.getChildren().addAll(heroLeftCol, heroRightCol);
        contentContainer.getChildren().add(heroRow);

        scrollPane.setContent(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fb;");
        root.setCenter(scrollPane);

        HBox footerBar = new HBox(20);
        footerBar.setAlignment(Pos.CENTER_LEFT);
        footerBar.setPadding(new Insets(18, 52, 18, 52));
        footerBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1px 0 0 0;");

        Label copyrightLabel = new Label("© 2026 QUIZZY Learning Platform");
        copyrightLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748b;");

        HBox footerSpacer = new HBox();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Label privacyLink = new Label("Privacy Policy");
        Label termsLink = new Label("Terms of Service");
        Label helpLink = new Label("Help Center");
        Label contactLink = new Label("Contact Us");

        String linkStyle = "-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-cursor: hand;";
        privacyLink.setStyle(linkStyle);
        termsLink.setStyle(linkStyle);
        helpLink.setStyle(linkStyle);
        contactLink.setStyle(linkStyle);

        HBox linksBox = new HBox(20, privacyLink, termsLink, helpLink, contactLink);
        linksBox.setAlignment(Pos.CENTER_RIGHT);

        footerBar.getChildren().addAll(copyrightLabel, footerSpacer, linksBox);
        root.setBottom(footerBar);
    }

    private VBox createQuizMockupCard() {
        VBox card = new VBox(18);
        card.setPrefWidth(440);
        card.setMaxWidth(440);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 28; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.06), 20, 0, 0, 6);");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label catPill = new Label("Computer Science");
        catPill.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label qCountLabel = new Label("Question 4 of 10");
        qCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        topRow.getChildren().addAll(catPill, spacer, qCountLabel);

        Label qTitle = new Label("What is the primary principle of Object-Oriented Programming?");
        qTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #191c1e; -fx-line-spacing: 4px;");
        qTitle.setWrapText(true);

        HBox opt1 = new HBox(12);
        opt1.setAlignment(Pos.CENTER_LEFT);
        opt1.setPadding(new Insets(12, 16, 12, 16));
        opt1.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label circle1 = new Label("◯");
        circle1.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        Label opt1Text = new Label("Procedural Execution Flow");
        opt1Text.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");
        opt1.getChildren().addAll(circle1, opt1Text);

        HBox opt2 = new HBox(12);
        opt2.setAlignment(Pos.CENTER_LEFT);
        opt2.setPadding(new Insets(12, 16, 12, 16));
        opt2.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #6366f1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label check2 = new Label("✓");
        check2.setStyle("-fx-background-color: #6366f1; -fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 2 5; -fx-background-radius: 9999px;");
        Label opt2Text = new Label("Encapsulation & Inheritance");
        opt2Text.setStyle("-fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-font-size: 14px;");
        opt2.getChildren().addAll(check2, opt2Text);

        HBox opt3 = new HBox(12);
        opt3.setAlignment(Pos.CENTER_LEFT);
        opt3.setPadding(new Insets(12, 16, 12, 16));
        opt3.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label circle3 = new Label("◯");
        circle3.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        Label opt3Text = new Label("Global Memory Allocation");
        opt3Text.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");
        opt3.getChildren().addAll(circle3, opt3Text);

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        Label nextLink = new Label("Next Question >");
        nextLink.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #6366f1; -fx-cursor: hand;");
        bottomRow.getChildren().add(nextLink);

        card.getChildren().addAll(topRow, qTitle, opt1, opt2, opt3, bottomRow);
        return card;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getHeaderLoginBtn() {
        return headerLoginBtn;
    }

    public Button getHeaderRegisterBtn() {
        return headerRegisterBtn;
    }

    public Button getHeroGetStartedBtn() {
        return heroGetStartedBtn;
    }

    public Button getHeroLoginBtn() {
        return heroLoginBtn;
    }

}
