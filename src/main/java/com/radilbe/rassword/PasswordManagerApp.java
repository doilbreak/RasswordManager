package com.radilbe.rassword;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class PasswordManagerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        URL fxmlUrl = getClass().getClassLoader().getResource("hello-view.fxml");
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found! Please check the file path.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 408, 550);

        primaryStage.setTitle("Rassword Manager");
     //   ImageIcon logo = new ImageIcon("resources/rm.png");
        Image image = new Image("icon.png");
        primaryStage.getIcons().add(image);
        primaryStage.initStyle(StageStyle.UNDECORATED); // No window controls
        primaryStage.setScene(scene);
        primaryStage.show(); // Show the stage last
    }

    public static void main(String[] args) {
        launch(args);
    }
}
