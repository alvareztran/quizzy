package com.quizzy.controller;

import com.quizzy.factory.ServiceFactory;
import com.quizzy.model.User;
import com.quizzy.service.UserService;
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
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class UserController {

    private final UserView view;
    private final UserService userService = ServiceFactory.getUserService();

    private final ObservableList<User> displayedUserList = FXCollections.observableArrayList();
    private final List<User> allUsers = new ArrayList<>();

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
        // Navigation Buttons
        view.getDashBtn().setOnAction(e -> SceneManager.showMain());
        view.getTopicBtn().setOnAction(e -> SceneManager.showTopic());
        view.getQuizBtn().setOnAction(e -> SceneManager.showQuiz());
        view.getQuestionBtn().setOnAction(e -> SceneManager.showQuestion());
        view.getAnswerBtn().setOnAction(e -> SceneManager.showAnswer());
        view.getUserBtn().setOnAction(e -> loadUsers());
        view.getResultBtn().setOnAction(e -> SceneManager.showAdminResult());

        // Logout via User Profile ContextMenu Item
        view.getUserProfileWidget().getLogoutItem().setOnAction(e -> logout());

        // Header Action
        view.getCreateUserBtn().setOnAction(e -> openCreateUserDialog());

        // Search & Filters
        view.getSearchUsersField().textProperty().addListener((obs, oldV, newV) -> filterUsers());
        view.getRoleFilterComboBox().valueProperty().addListener((obs, oldV, newV) -> filterUsers());
        view.getSortComboBox().valueProperty().addListener((obs, oldV, newV) -> filterUsers());

        view.getResetFilterBtn().setOnAction(e -> {
            view.getSearchUsersField().clear();
            view.getRoleFilterComboBox().setValue("All Roles");
            view.getSortComboBox().setValue("Sort by: Username");
            filterUsers();
        });

        // Setup Actions Column Center Aligned
        view.getActionsColumn().setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = com.quizzy.util.NavIconHelper.createEditActionButton();
            private final Button deleteBtn = com.quizzy.util.NavIconHelper.createDeleteActionButton();
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

        long adminCount = allUsers.stream().filter(u -> "Admin".equalsIgnoreCase(u.getRole())).count();
        long playerCount = allUsers.stream().filter(u -> "Player".equalsIgnoreCase(u.getRole())).count();

        view.getAdminUsersCard().getValueLabel().setText(String.valueOf(adminCount));
        view.getPlayerUsersCard().getValueLabel().setText(String.valueOf(playerCount));

        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d users", displayedUserList.size(), allUsers.size())
        );
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

        displayedUserList.setAll(filtered);
        view.getPaginationInfoLabel().setText(
                String.format("Showing 1 to %d of %d users", displayedUserList.size(), allUsers.size())
        );
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
