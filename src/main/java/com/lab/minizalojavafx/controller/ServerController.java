package com.lab.minizalojavafx.controller;

import com.lab.minizalojavafx.message.AlertMessage;
import com.lab.minizalojavafx.server.Server;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerController {

    @FXML
    private AnchorPane pane;

    @FXML
    private ScrollPane scrollPain;

    @FXML
    private VBox vBox;

    private Server server;
    private static VBox staticVBox;
    private AlertMessage alertMessage;

    public void initialize() {
        alertMessage = new AlertMessage();
        staticVBox = vBox;
        setupUI();
        initializeServer();
    }

    private void setupUI() {
        receiveMessage("Server Starting..");
        vBox.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                scrollPain.setVvalue(scrollPain.getVmax())
        );

        receiveMessage("Server Running..");
        receiveMessage("Waiting for User..");
    }

    private void initializeServer() {
        new Thread(() -> {
            try {
                server = Server.getInstance();
                server.setOnMessageReceived(this::receiveMessage);
                server.makeSocket();
            } catch (IOException e) {
                alertMessage.error("Unable to start server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void addButtonOnAction(ActionEvent actionEvent) {
        openLoginWindow();
    }

    private void openLoginWindow() {
        Stage stage = new Stage();
        stage.setTitle("Mini Zalo");
        stage.centerOnScreen();
        stage.setResizable(false);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            AnchorPane loginPane = fxmlLoader.load();

            LoginController loginController = fxmlLoader.getController();
            loginController.setOnLoginSuccess(() -> openClientWindow(stage));

            stage.setScene(new Scene(loginPane));
            stage.show();
        } catch (IOException e) {
            alertMessage.error("Something went wrong. Can't load login screen.");
            e.printStackTrace();
        }
    }

    private void openClientWindow(Stage loginStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client.fxml"));
            Stage clientStage = new Stage();
            clientStage.setTitle("Client");
            clientStage.centerOnScreen();
            clientStage.setResizable(false);
            clientStage.setScene(new Scene(fxmlLoader.load()));
            clientStage.show();

            loginStage.close();
        } catch (IOException e) {
            alertMessage.error("Can't load client window.");
            e.printStackTrace();
        }
    }

    public void receiveMessage(String msgFromClient) {
        if (msgFromClient.startsWith("IMAGE-")) {
            String[] parts = msgFromClient.split("-");
            String sender = parts[1];
            String filePath = parts[2];
            displayImage(sender, filePath);
        } else {
            addMessageToChatBox(msgFromClient, Pos.CENTER_LEFT, "#abb8c3", Color.BLACK, 14);
        }
    }

    private void displayImage(String sender, String filePath) {
        Image image = new Image("file:" + filePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox(imageView);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().add(hBox);
    }


    private static void addMessageToChatBox(String message, Pos alignment, String bgColor, Color textColor, int fontSize) {
        Platform.runLater(() -> {
            HBox hBox = new HBox();
            hBox.setAlignment(alignment);
            hBox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(message);
            text.setFill(textColor);
            text.setStyle("-fx-font-size: " + fontSize);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 20px; -fx-font-weight: bold");
            textFlow.setPadding(new Insets(5, 10, 5, 10));

            hBox.getChildren().add(textFlow);
            staticVBox.getChildren().add(hBox);
        });
    }
}
