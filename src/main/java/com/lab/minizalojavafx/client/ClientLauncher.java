package com.lab.minizalojavafx.client;

import com.lab.minizalojavafx.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientLauncher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client.fxml"));
        ClientController controller = new ClientController();
        fxmlLoader.setController(controller);

        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle("Client");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
