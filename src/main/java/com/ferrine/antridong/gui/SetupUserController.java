package com.ferrine.antridong.gui;

import com.ferrine.antridong.database.models.User;
import com.ferrine.antridong.database.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SetupUserController {
    @FXML private TableView<User> table;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleBox;

    private User selectedUser = null;
    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("ADMIN", "PETUGAS");
        roleBox.setValue("PETUGAS");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
        
        refreshTable();
    }

    private void refreshTable() {
        List<User> users = userRepository.query().findList();
        table.getItems().setAll(users);
    }

    private void populateForm(User user) {
        selectedUser = user;
        usernameField.setText(user.getUsername());
        passwordField.setText(""); 
        nameField.setText(user.getName());
        roleBox.setValue(user.getRole());
    }

    @FXML
    private void clearForm() {
        selectedUser = null;
        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        roleBox.setValue("PETUGAS");
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void saveUser() {
        if (usernameField.getText().isEmpty() || nameField.getText().isEmpty()) {
            showAlert("Username and Name are required.");
            return;
        }
        
        User user = selectedUser != null ? selectedUser : new User();
        user.setUsername(usernameField.getText());
        if (!passwordField.getText().isEmpty()) {
            user.setPassword(passwordField.getText());
        } else if (selectedUser == null) {
            showAlert("Password is required for new user.");
            return;
        }
        user.setName(nameField.getText());
        user.setRole(roleBox.getValue());
        
        user.save();
        clearForm();
        refreshTable();
    }

    @FXML
    private void deleteUser() {
        if (selectedUser != null) {
            selectedUser.delete();
            clearForm();
            refreshTable();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.show();
    }
}
