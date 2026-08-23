package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.User;
import com.quizzy.service.UserService;
import com.quizzy.util.NavIconHelper;
import com.quizzy.util.SceneManager;
import com.quizzy.util.SessionManager;
import com.quizzy.view.UserView;
import com.quizzy.view.component.ConfirmDialog;
import com.quizzy.view.component.UserFormDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class UserController {

    private final UserView view;
    private final UserService userService = ServiceFactory.getUserService();

    private final ObservableList<User> displayedUserList = FXCollections.observableArrayList();
    private final List<User> allUsers = new ArrayList<>();
    private final List<User> currentFilteredList = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    public UserController() {
        this.view = new UserView();
        initEventHandlers();
        initializeData();
    }

    public Parent getView() {
        return view.getRoot();
    }

    public UserView getUserView() {
        return view;
    }

    private void initEventHandlers() {
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> loadUsers());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());
        view.getCreateUserBtn().setOnAction(e -> openCreateUserDialog());

        view.getSearchUsersField().textProperty().addListener((obs, oldV, newV) -> filterUsers());
        view.getRoleFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterUsers());
        view.getSortComboBox().valueProperty().addListener((obs, oldV, newV) -> filterUsers());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchUsersField().clear();
            view.getRoleFilterComboBox().setValue("All Roles");
            view.getSortComboBox().setValue("Sort by: Username");
            filterUsers();
        });

        view.getPerPageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.contains("25")) {
                    pageSize = 25;
                } else if (newVal.contains("50")) {
                    pageSize = 50;
                } else {
                    pageSize = 10;
                }
                currentPage = 1;
                renderPage();
            }
        });

        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = NavIconHelper.createEditActionButton();
            private final Button deleteBtn = NavIconHelper.createDeleteActionButton();
            private final HBox btnBox = new HBox(8, editBtn, deleteBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                btnBox.setMaxWidth(Double.MAX_VALUE);

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    openEditUserDialog(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                    setAlignment(Pos.CENTER);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void initializeData() {
        view.getUserTable().setItems(displayedUserList);
        loadUsers();
    }

    private void loadUsers() {
        try {
            allUsers.clear();
            List<User> users = userService.getAllUsers();
            if (users != null) {
                allUsers.addAll(users);
            }
            filterUsers();
            updateStatCards();
        } catch (Exception e) {
            showError("Failed to load users from database.");
        }
    }

    private void updateStatCards() {
        view.getTotalUsersCard().getValueLabel().setText(String.valueOf(allUsers.size()));
        view.getActiveUsersCard().getValueLabel().setText(String.valueOf(allUsers.size()));

        long adminCount = allUsers.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
        long playerCount = allUsers.stream().filter(u -> "PLAYER".equalsIgnoreCase(u.getRole())).count();

        view.getAdminUsersCard().getValueLabel().setText(String.valueOf(adminCount));
        view.getPlayerUsersCard().getValueLabel().setText(String.valueOf(playerCount));
    }

    private void filterUsers() {
        String keyword = view.getSearchUsersField().getText();
        String selectedRole = view.getRoleFilterComboBox().getValue();
        String sortOption = view.getSortComboBox().getValue();

        final String search = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        List<User> filtered = new java.util.ArrayList<>(allUsers.stream().filter(u -> {
            boolean matchSearch = (search == null)
                    || (u.getUserName() != null && u.getUserName().toLowerCase().contains(search))
                    || (u.getFullName() != null && u.getFullName().toLowerCase().contains(search));

            boolean matchRole = true;
            if (selectedRole != null && !"All Roles".equals(selectedRole)) {
                matchRole = selectedRole.equalsIgnoreCase(u.getRole());
            }

            return matchSearch && matchRole;
        }).toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Sort by: Full Name" -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getFullName() != null ? a.getFullName() : "",
                        b.getFullName() != null ? b.getFullName() : ""
                ));
                case "Sort by: Role" -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getRole() != null ? a.getRole() : "",
                        b.getRole() != null ? b.getRole() : ""
                ));
                default -> filtered.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                        a.getUserName() != null ? a.getUserName() : "",
                        b.getUserName() != null ? b.getUserName() : ""
                )); // Sort by: Username
            }
        }

        currentFilteredList.clear();
        currentFilteredList.addAll(filtered);
        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = currentFilteredList.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        if (fromIndex < total) {
            displayedUserList.setAll(currentFilteredList.subList(fromIndex, toIndex));
            view.getPaginationInfoLabel().setText(
                    String.format("Showing %d to %d of %d users", (fromIndex + 1), toIndex, total)
            );
        } else {
            displayedUserList.clear();
            view.getPaginationInfoLabel().setText("Showing 0 to 0 of 0 users");
        }

        renderPaginationButtons(totalPages);
    }

    private void renderPaginationButtons(int totalPages) {
        HBox box = view.getPaginationButtonsBox();
        box.getChildren().clear();

        Button prevBtn = new Button("<");
        stylePaginationBtn(prevBtn, false);
        prevBtn.setDisable(currentPage <= 1);
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });
        box.getChildren().add(prevBtn);

        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            Button p1 = new Button("1");
            stylePaginationBtn(p1, currentPage == 1);
            p1.setOnAction(e -> {
                currentPage = 1;
                renderPage();
            });
            box.getChildren().add(p1);

            if (startPage > 2) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }
        }

        for (int p = startPage; p <= endPage; p++) {
            final int pageNum = p;
            Button pageBtn = new Button(String.valueOf(pageNum));
            boolean isActive = (pageNum == currentPage);
            stylePaginationBtn(pageBtn, isActive);
            pageBtn.setOnAction(e -> {
                currentPage = pageNum;
                renderPage();
            });
            box.getChildren().add(pageBtn);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label dots = new Label("...");
                dots.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 2 4; -fx-font-weight: bold;");
                box.getChildren().add(dots);
            }

            Button pLast = new Button(String.valueOf(totalPages));
            stylePaginationBtn(pLast, currentPage == totalPages);
            pLast.setOnAction(e -> {
                currentPage = totalPages;
                renderPage();
            });
            box.getChildren().add(pLast);
        }

        Button nextBtn = new Button(">");
        stylePaginationBtn(nextBtn, false);
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
            }
        });
        box.getChildren().add(nextBtn);
    }

    private void stylePaginationBtn(Button btn, boolean isActive) {
        if (isActive) {
            btn.getStyleClass().add("button-primary");
            btn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        } else {
            btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-padding: 4 10; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-min-width: 32px;");
        }
    }

    private void openCreateUserDialog() {
        Optional<User> result = UserFormDialog.showUserDialog(null);
        result.ifPresent(newUser -> {
            try {
                if (!userService.createUser(newUser)) {
                    showError("Cannot create user. Username may already exist.");
                    return;
                }
                showInfo("User account created successfully.");
                loadUsers();
            } catch (Exception e) {
                showError("Unable to create user. Database error.");
            }
        });
    }

    private void openEditUserDialog(User user) {
        if (user == null) return;

        Optional<User> result = UserFormDialog.showUserDialog(user);
        result.ifPresent(updatedUser -> {
            try {
                if (!userService.updateUser(updatedUser)) {
                    showError("Cannot update user. Invalid data.");
                    return;
                }
                showInfo("User account updated successfully.");
                loadUsers();
            } catch (Exception e) {
                showError("Unable to update user. Database error.");
            }
        });
    }

    private void deleteUser(User user) {
        if (user == null) return;

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
            showError("You cannot delete your own active logged in account.");
            return;
        }

        boolean confirm = ConfirmDialog.showDeleteConfirmation(
                "Delete User Account?",
                "Are you sure you want to delete user '" + user.getUserName() + "'? This action cannot be undone."
        );

        if (!confirm) return;

        try {
            if (!userService.deleteUser(user.getUserId())) {
                showError("Unable to delete user.");
                return;
            }
            showInfo("User account deleted successfully.");
            loadUsers();
        } catch (Exception e) {
            showError("Unable to delete user. Database error.");
        }
    }

    private void logout() {
        SessionManager.clear();
        SceneManager.showLogin();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("User Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
