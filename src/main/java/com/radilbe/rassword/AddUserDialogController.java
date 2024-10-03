package com.radilbe.rassword;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserDialogController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    private PasswordManagerController mainController;

    public void setMainController(PasswordManagerController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void saveUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            // Show an alert if any field is empty
            mainController.showAlert("Error", "Please fill in all fields!");
            return;
        }

        // Create a new User object
        User newUser = new User(username, password, email); // Consider encrypting if necessary
        mainController.addUser(newUser); // Add the user to the main table

        // Close the dialog
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel() {
        // Close the dialog without saving
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
