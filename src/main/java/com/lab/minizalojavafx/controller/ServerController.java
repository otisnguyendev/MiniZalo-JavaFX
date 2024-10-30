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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                server.makeSocket();
            } catch (IOException e) {
                alertMessage.error("Server Error\", \"Unable to start server: " + e.getMessage());
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
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(pane.getScene().getWindow());
        stage.setTitle("EChat");
        stage.centerOnScreen();
        stage.setResizable(false);

        try {
            AnchorPane loginPane = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(loginPane));
            stage.show();
        } catch (IOException e) {
            alertMessage.error("Load Error\", \"Something went wrong. Can't load login screen.");
            e.printStackTrace();
        }
    }

    private void sendMsg(String msgToSend) {
        if (msgToSend == null || msgToSend.isEmpty()) return;

        addMessageToChatBox(msgToSend, Pos.CENTER_RIGHT, "#0693e3", Color.WHITE, 14);
        addTimeToChatBox(Pos.CENTER_RIGHT, 8);
    }

    public void receiveMessage(String msgFromClient) {
        addMessageToChatBox(msgFromClient, Pos.CENTER_LEFT, "#abb8c3", Color.BLACK, 14);
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

    private static void addTimeToChatBox(Pos alignment, int fontSize) {
        Platform.runLater(() -> {
            HBox hBoxTime = new HBox();
            hBoxTime.setAlignment(alignment);
            hBoxTime.setPadding(new Insets(0, 5, 5, 10));

            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text timeText = new Text(currentTime);
            timeText.setStyle("-fx-font-size: " + fontSize);

            hBoxTime.getChildren().add(timeText);
            staticVBox.getChildren().add(hBoxTime);
        });
    }
}
