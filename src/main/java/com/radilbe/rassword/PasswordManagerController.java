package com.radilbe.rassword;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;

public class PasswordManagerController {
    @FXML
    private Hyperlink hyperlink;
    @FXML
    private Button openAddUserDialog;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private Slider passwordLengthSlider;
    @FXML
    private Label passwordLengthLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TableView<User> userTable;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;

    private ObservableList<User> userList;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static SecureRandom random = new SecureRandom();

    public void Hyperlink(Hyperlink hyperlink) {
        this.hyperlink = hyperlink;
    }

    @FXML
    public void initialize() {

        userList = FXCollections.observableArrayList();
        userTable.setItems(userList);

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        passwordColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("*********"); // Show masked password
                }
            }
        });

        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
                    User selectedUser = row.getItem();
                    if (event.getClickCount() == 2) {
                        copyPasswordToClipboard(selectedUser.getPassword());
                    }
                }
            });
            return row;
        });

        // Load users from storage or file
        loadUsers();

        // Check and create users.txt if it doesn't exist
        createUsersFile();
        //
        passwordLengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            passwordLengthLabel.setText(String.valueOf(newVal.intValue()));
        });
        passwordLengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            passwordLengthLabel.setText(String.valueOf(newValue.intValue())); // Int değerini String'e çeviriyoruz
        });

        openAddUserDialog.setOnAction(event -> openAddUserDialog());
    }

    private void createUsersFile() {
        File usersFile = new File("passwordmanager/users.txt");
        if (!usersFile.exists()) {
            try {
                usersFile.getParentFile().mkdirs(); // Create directories if they don't exist
                usersFile.createNewFile(); // Create the file
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while creating users.txt!");
            }
        }
    }


    private void copyPasswordToClipboard(String password) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(password);
        clipboard.setContent(content);
        showAlert("Info", "Password copied to clipboard!");
    }

    public void loadUsers() {
        try {
            List<User> users = Database.loadUsers(); // Şifreleme işlemi kaldırıldı
            userList.addAll(users);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading users!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred while loading users!");
        }
    }

    @FXML
    private void saveUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = generate();  // Automatically generate a password

        if (username == null || username.isEmpty()) {
            showAlert("Error", "Username cannot be empty!");
            return;
        }
        if (email == null || email.isEmpty()) {
            showAlert("Error", "Email cannot be empty!");
            return;
        }

        try {
            // Şifreleme kaldırıldı, kullanıcıyı düz şekilde kaydedin
            User user = new User(username, password, email);
            Database.saveUser(user);
            userList.add(user);
            showAlert("Success", "User saved successfully!");

            usernameField.clear();
            emailField.clear();
            passwordLabel.setText("");  // Clear the password label
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the user.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred while saving the user.");
        }
    }

    @FXML
    private void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userList.remove(selectedUser);
            try {
                Database.deleteUser(selectedUser); // Şifreleme kaldırıldı
                showAlert("Success", "User deleted successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while deleting user!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An unexpected error occurred while deleting user!");
            }
        } else {
            showAlert("Error", "No user selected!");
        }
    }

    private String generate() {
        int passwordLength = (int) passwordLengthSlider.getValue();
        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    @FXML
    private void generatePassword() {
        String password = generate();
        passwordLabel.setText(password);
    }

    public void hyperlink() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/doilbreak"));
    }



    @FXML
    private void openAddUserDialog() {
        try {
            URL fxmlUrl = getClass().getClassLoader().getResource("AddUserDialog.fxml");
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found! Please check the file path.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            AddUserDialogController dialogController = loader.getController();
            dialogController.setMainController(this); // Pass the main controller reference

            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while opening the add user dialog.");
        }
    }

    public void addUser(User user) {
        try {
            Database.saveUser(user); // Save the user
            userList.add(user); // Add to the observable list
            showAlert("Success", "User added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the user!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred while adding the user!");
        }
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
