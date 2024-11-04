package com.lab.minizalojavafx.controller;

import com.lab.minizalojavafx.client.ClientHandler;
import com.lab.minizalojavafx.emoji.EmojiPicker;
import com.lab.minizalojavafx.message.AlertMessage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Setter;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientController {
    @FXML
    private Button emojiButton;
    @FXML
    private Button attachedButton;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPain;
    @FXML
    private Text txtLabel;
    @FXML
    private TextField txtMsg;
    @FXML
    private VBox vBox;

    @FXML
    private ComboBox<String> cbClientOnline;

    @FXML
    private ListView<String> onlineUsersList;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName;
    private AlertMessage alertMessage;

    @Setter
    private ClientHandler clientHandler;

    @FXML
    void attachedButtonOnAction(ActionEvent event) {
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        dialog.dispose();
        sendImage(file);
        System.out.println(file + " chosen.");
    }

    @FXML
    void sendButtonOnAction(ActionEvent event) {
        String message = txtMsg.getText().trim();
        String recipient = cbClientOnline.getValue();

        if (recipient != null && !recipient.isEmpty()) {
            sendMsg(message, recipient);
        } else {
            alertMessage.error("Vui lòng chọn người nhận.");
        }
    }

    @FXML
    void txtMsgOnAction(ActionEvent event) {
        sendButtonOnAction(event);
    }

    public void initialize() {
        txtLabel.setText(clientName);
        connectToServer();

        cbClientOnline.getItems().clear();
        vBox.heightProperty().addListener((ChangeListener<Number>) (observableValue, oldValue, newValue) -> {
            scrollPain.setVvalue((Double) newValue);
        });

        emoji();
    }


    private void connectToServer() {
        new Thread(() -> {
            try {
                alertMessage = new AlertMessage();
                socket = new Socket("localhost", 3001);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(clientName);
                System.out.println("Client connected");

                while (socket.isConnected()) {
                    String receivingMsg = dataInputStream.readUTF();
                    receiveMessage(receivingMsg);
                }
            } catch (IOException e) {
                alertMessage.error("Could not connect to server at localhost:3001. Please ensure the server is running.");
                e.printStackTrace();
            }
        }).start();
    }

    public void shutdown() {
        try {
            if (socket != null && !socket.isClosed()) {
                dataOutputStream.writeUTF("REMOVE_USER-" + clientName);
                dataOutputStream.flush();
                dataOutputStream.writeUTF(clientName + " has left the chat.");
                dataOutputStream.flush();
                socket.close();
                dataInputStream.close();
                dataOutputStream.close();
                System.out.println(clientName + " left.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emoji() {
        EmojiPicker emojiPicker = new EmojiPicker();
        VBox emojiBox = new VBox(emojiPicker);
        emojiBox.setPrefSize(150, 300);
        emojiBox.setLayoutX(400);
        emojiBox.setLayoutY(175);
        emojiBox.setStyle("-fx-font-size: 30");
        pane.getChildren().add(emojiBox);
        emojiBox.setVisible(false);

        emojiButton.setOnAction(event -> emojiBox.setVisible(!emojiBox.isVisible()));

        emojiPicker.getEmojiListView().setOnMouseClicked(event -> {
            String selectedEmoji = emojiPicker.getEmojiListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                txtMsg.setText(txtMsg.getText() + selectedEmoji);
            }
            emojiBox.setVisible(false);
        });
    }

    private void sendMsg(String msgToSend, String recipient) {
        if (dataOutputStream != null && socket.isConnected() && !msgToSend.isEmpty()) {
            HBox hBox = createMessageHBox(msgToSend, Pos.CENTER_RIGHT, "#0693e3");
            vBox.getChildren().add(hBox);

            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Text timeText = new Text(time);
            timeText.setStyle("-fx-font-size: 8");
            HBox hBoxTime = new HBox(timeText);
            hBoxTime.setAlignment(Pos.CENTER_RIGHT);
            hBoxTime.setPadding(new Insets(0, 5, 5, 10));
            vBox.getChildren().add(hBoxTime);

            try {
                dataOutputStream.writeUTF(recipient + "-" + clientName + "-" + msgToSend);
                dataOutputStream.flush();
            } catch (IOException e) {
                alertMessage.error("Failed to send message to server.");
                e.printStackTrace();
            }
            txtMsg.clear();
        } else if (dataOutputStream == null) {
            alertMessage.error("Cannot send message. Not connected to server.");
        }
    }



    private void sendImage(String filePath) {
        Image image = new Image(filePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox(imageView);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(hBox);
        alertMessage = new AlertMessage();

        try {
            dataOutputStream.writeUTF(clientName + "-" + filePath);
            dataOutputStream.flush();
        } catch (IOException e) {
            alertMessage.error("Failed to send image to server.");
            e.printStackTrace();
        }
    }

    public void receiveMessage(String msg) {
        Platform.runLater(() -> {
            if (msg.startsWith("USER_LIST-")) {
                updateClientOnline(msg);
            } else {
                String[] parts = msg.split("-");
                if (parts.length >= 3) {
                    String recipient = parts[0];
                    String name = parts[1];
                    String content = parts[2];

                    if (recipient.equals(clientName)) {
                        HBox hBox = createMessageHBox(content, Pos.CENTER_LEFT, "#abb8c3");
                        vBox.getChildren().add(hBox);
                    }
                } else {
                    System.err.println("Received malformed message: " + msg);
                }
            }
        });
    }


    public void setClientName(String name) {
        this.clientName = name;
        txtLabel.setText(name);
    }


    private static HBox createMessageHBox(String msg, Pos alignment, String color) {
        HBox hBox = new HBox();
        hBox.setAlignment(alignment);
        hBox.setPadding(new Insets(5, 5, 0, 10));

        Text text = new Text(msg);
        text.setStyle("-fx-font-size: 14");
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 20px; -fx-font-weight: bold");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(1, 1, 1));

        hBox.getChildren().add(textFlow);
        return hBox;
    }


    public void updateClientOnline(String userListMessage) {
        String[] parts = userListMessage.split("-");
        if (parts.length > 1 && parts[0].equals("USER_LIST")) {
            String[] onlineUsers = parts[1].split(",");
            Platform.runLater(() -> {
                onlineUsersList.getItems().clear();
                onlineUsersList.getItems().addAll(onlineUsers);
                cbClientOnline.getItems().clear();
                cbClientOnline.getItems().addAll(onlineUsers);
            });
        }
    }

}
