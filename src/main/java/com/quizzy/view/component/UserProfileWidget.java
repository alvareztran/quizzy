package com.quizzy.view.component;

import com.quizzy.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class UserProfileWidget {

    private final HBox root = new HBox(10);
    private final StackPane avatarPane = new StackPane();
    private final Circle avatarRing = new Circle(18);
    private final ImageView avatarImageView = new ImageView();

    private final VBox textContainer = new VBox(2);
    private final Label nameLabel = new Label("User");
    private final Label roleLabel = new Label("MEMBER");

    private final ContextMenu profileMenu = new ContextMenu();
    private final MenuItem profileInfoItem = new MenuItem("Profile Details");
    private final MenuItem logoutItem = new MenuItem("Sign Out");

    public UserProfileWidget() {
        createUI();
    }

    public UserProfileWidget(User user) {
        createUI();
        setUser(user);
    }

    private void createUI() {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(8, 12, 8, 10));
        root.setMaxWidth(Double.MAX_VALUE);

        String cardBg = "-fx-background-color: #f8f9fb; -fx-background-radius: 8; -fx-border-color: #c7c4d7; -fx-border-radius: 8; -fx-cursor: hand;";
        String cardHoverBg = "-fx-background-color: #e7e8ea; -fx-background-radius: 8; -fx-border-color: #c7c4d7; -fx-border-radius: 8; -fx-cursor: hand;";
        root.setStyle(cardBg);
        root.setOnMouseEntered(e -> root.setStyle(cardHoverBg));
        root.setOnMouseExited(e -> root.setStyle(cardBg));

        avatarRing.setFill(Color.web("#e1e0ff"));
        avatarRing.setStroke(Color.web("#e1e0ff"));
        avatarRing.setStrokeWidth(1.5);

        try {
            Image avatarImg = new Image(getClass().getResourceAsStream("/com/quizzy/images/user-avatar.png"));
            avatarImageView.setImage(avatarImg);
            avatarImageView.setFitWidth(36);
            avatarImageView.setFitHeight(36);
            avatarImageView.setPreserveRatio(false);
            avatarImageView.setSmooth(true);
            avatarImageView.setClip(new Circle(18, 18, 18));
        } catch (Exception ignored) {
        }

        avatarPane.getChildren().setAll(avatarRing, avatarImageView);

        nameLabel.setStyle("-fx-text-fill: #191c1e; -fx-font-weight: 700; -fx-font-size: 14px;");
        roleLabel.setStyle("-fx-text-fill: #767586; -fx-font-size: 12px; -fx-font-weight: 500;");
        textContainer.getChildren().addAll(nameLabel, roleLabel);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        root.getChildren().addAll(avatarPane, textContainer);

        profileInfoItem.setDisable(true);
        profileMenu.getItems().addAll(profileInfoItem, new SeparatorMenuItem(), logoutItem);

        root.setOnMouseClicked(e -> {
            if (!profileMenu.isShowing()) {
                profileMenu.show(root, e.getScreenX() - 10, e.getScreenY() - 70);
            } else {
                profileMenu.hide();
            }
        });
    }

    public void setUser(User user) {
        if (user == null) {
            setUserInfo("Guest User", "Member");
            return;
        }

        String fullName = user.getFullName() != null && !user.getFullName().isBlank() ? user.getFullName() : user.getUserName();
        String role = user.getRole() != null ? user.getRole() : "Player";
        setUserInfo(fullName, role);
    }

    public void setUserInfo(String fullName, String role) {
        nameLabel.setText(fullName);
        roleLabel.setText(role != null ? role.toUpperCase() : "PLAYER");
        profileInfoItem.setText("Signed in as " + fullName);

        if ("ADMIN".equalsIgnoreCase(role)) {
            avatarRing.setStroke(Color.web("#4648d4"));
        } else {
            avatarRing.setStroke(Color.web("#075985"));
        }
    }

    public HBox getRoot() {
        return root;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getRoleLabel() {
        return roleLabel;
    }

    public MenuItem getLogoutItem() {
        return logoutItem;
    }

    public ContextMenu getProfileMenu() {
        return profileMenu;
    }
}
